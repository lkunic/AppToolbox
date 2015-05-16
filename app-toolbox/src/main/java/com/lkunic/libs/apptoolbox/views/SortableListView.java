package com.lkunic.libs.apptoolbox.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.lkunic.libs.apptoolbox.R;

/**
 * Copyright (c) Luka Kunic 2015 / "SortableListView.java"
 * Created by lkunic on 06/05/2015.
 *
 * Creates a sortable ListView based on the android music app (TouchInterceptor class). Allows sorting the items using
 * a grabber control and using left and right swipes to perform additional actions (e.g. removing items from the list).
 */
public class SortableListView extends ListView
{
	public static final int FLING = 0;
	public static final int SLIDE_RIGHT = 1;
	public static final int SLIDE_LEFT = 2;

	private ImageView mDragView;
	private Bitmap mDragBitmap;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private GestureDetector mGestureDetector;

	private int mDraggedItemPosition;      // which item is being dragged
	private int mDraggedItemInitialPosition; // where was the dragged item originally
	private int mDragPointOffset;    // at what offset inside the item did the user grab it
	private int mCoordinateOffset;  // the difference between screen coordinates and coordinates in this view
	private int mUpperBound;
	private int mLowerBound;
	private int mHeight;

	private DragListener mDragListener;
	private DropListener mDropListener;
	private RemoveListener mRemoveListener;

	private final int mTouchSlop;
	private int mRemoveMode = -1;
	private int mItemHeightNormal = -1;
	private int mItemHeightExpanded = -1;
	private int mGrabberId = -1;
	private int mDragBackgroundColor = 0x00000000;

	public SortableListView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public SortableListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		if (attrs != null)
		{
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SortableListView, 0, 0);

			mItemHeightNormal = a.getDimensionPixelSize(R.styleable.SortableListView_normal_height, 0);
			mItemHeightExpanded = a.getDimensionPixelSize(R.styleable.SortableListView_expanded_height, mItemHeightNormal);
			mGrabberId = a.getResourceId(R.styleable.SortableListView_grabber, -1);
			mDragBackgroundColor = a.getColor(R.styleable.SortableListView_drag_background, 0x00000000);
			mRemoveMode = a.getInt(R.styleable.SortableListView_remove_mode, -1);

			a.recycle();
		}
	}

	@Override
	final public void addHeaderView(View v, Object data, boolean isSelectable)
	{
		throw new RuntimeException("Headers are not supported with TouchListView");
	}

	@Override
	final public void addHeaderView(View v)
	{
		throw new RuntimeException("Headers are not supported with TouchListView");
	}

	@Override
	final public void addFooterView(View v, Object data, boolean isSelectable)
	{
		if (mRemoveMode == SLIDE_LEFT || mRemoveMode == SLIDE_RIGHT)
		{
			throw new RuntimeException("Footers are not supported with TouchListView in conjunction with remove_mode");
		}
	}

	@Override
	final public void addFooterView(View v)
	{
		if (mRemoveMode == SLIDE_LEFT || mRemoveMode == SLIDE_RIGHT)
		{
			throw new RuntimeException("Footers are not supported with TouchListView in conjunction with remove_mode");
		}
	}

	@Override
	public boolean onInterceptTouchEvent(@NonNull MotionEvent ev)
	{
		// Setup fling-to-remove if necessary (will only run once)
		if (mRemoveListener != null && mGestureDetector == null)
		{
			if (mRemoveMode == FLING)
			{
				// Setup the fling gesture listener
				mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()
				{
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
					{
						if (mDragView != null)
						{
							if (velocityX > 1000)
							{
								// The fling is fast enough, check finger travel distance
								Rect r = new Rect();
								mDragView.getDrawingRect(r);
								if (e2.getX() > r.right * 2 / 3)
								{
									// Fast fling right with release near the right edge of the screen
									stopDragging();
									mRemoveListener.remove(mDraggedItemInitialPosition);
									unExpandViews(true);
								}
							}

							return true;
						}

						return false;
					}
				});
			}
		}

		// Check for item dragging if any listeners exist
		if (mDragListener != null || mDropListener != null)
		{
			if (ev.getAction() == MotionEvent.ACTION_DOWN)
			{
				// Get touch coordinates to determine which item was touched
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				int itemNum = pointToPosition(x, y);

				// Check if an item was touched
				if (itemNum != AdapterView.INVALID_POSITION)
				{
					// Get the item at the touched position
					View item = getChildAt(itemNum - getFirstVisiblePosition());

					// Check if the item can be dragged to change sorting order
					if (isDraggableRow(item))
					{
						// Get the offset between the touch position and the top of the item
						mDragPointOffset = y - item.getTop();

						// Get the offset between screen coordinates and coordinates in this view
						mCoordinateOffset = ((int) ev.getRawY()) - y;

						// Get the grabber view and it's bounds
						View grabber = item.findViewById(mGrabberId);
						Rect r = new Rect();
						r.left = grabber.getLeft();
						r.right = grabber.getRight();
						r.top = grabber.getTop();
						r.bottom = grabber.getBottom();

						// Check if the grabber was touched
						if ((r.left < x) && (x < r.right))
						{
							// Create a copy of the drawing cache so that it does not get recycled by the framework
							// when the list tries to clean up memory
							item.setDrawingCacheEnabled(true);
							Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
							item.setDrawingCacheEnabled(false);

							// Get bounds for the list
							Rect listBounds = new Rect();
							getGlobalVisibleRect(listBounds, null);

							// Start dragging the list item view
							startDragging(bitmap, listBounds.left, y);

							// Store the starting position of the item
							mDraggedItemPosition = itemNum;
							mDraggedItemInitialPosition = mDraggedItemPosition;

							// Get the bounds for the view
							mHeight = getHeight();
							mUpperBound = Math.min(y - mTouchSlop, mHeight / 3);
							mLowerBound = Math.max(y + mTouchSlop, mHeight * 2 / 3);

							return false;
						}

						// The grabber was not touched
						mDragView = null;
					}
				}
			}
		}

		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * Returns true if the given row is draggable (contains a grabber view).
	 */
	protected boolean isDraggableRow(View view)
	{
		return (view.findViewById(mGrabberId) != null);
	}

	/**
	 * Maps a point to a position in the list. Also considers invisible views.
	 */
	private int myPointToPosition(int x, int y)
	{
		Rect frame = new Rect();
		View child;

		// Iterate through the children (list items) to find which one is at the given point
		for (int i = getChildCount() - 1; i >= 0; i--)
		{
			child = getChildAt(i);
			child.getHitRect(frame);
			if (frame.contains(x, y))
			{
				// The child view was found, return it's position
				return getFirstVisiblePosition() + i;
			}
		}
		return INVALID_POSITION;
	}

	/**
	 * Returns the list item position at the given y coordinate.
	 */
	private int getItemForPosition(int y)
	{
		// Adjust the y value to the vertical center of the item
		int adjustedY = y - mDragPointOffset - (mItemHeightNormal / 2);

		// Get the position of the item at the given point
		int pos = myPointToPosition(0, adjustedY);

		if (pos >= 0)
		{
			if (pos <= mDraggedItemInitialPosition)
			{
				pos += 1;
			}
		}
		else if (adjustedY < 0)
		{
			pos = 0;
		}
		return pos;
	}

	/**
	 * Adjust the scroll bounds if the item is dragged off the screen.
	 */
	private void adjustScrollBounds(int y)
	{
		if (y >= mHeight / 3)
		{
			mUpperBound = mHeight / 3;
		}
		if (y <= mHeight * 2 / 3)
		{
			mLowerBound = mHeight * 2 / 3;
		}
	}

	/**
	 * Restore size and visibility for all list items.
	 * @param deletion Whether a row has been deleted.
	 */
	private void unExpandViews(boolean deletion)
	{
		for (int i = 0; ; i++)
		{
			View v = getChildAt(i);
			if (v == null)
			{
				if (deletion)
				{
					// HACK: Force update of mItemCount
					int position = getFirstVisiblePosition();
					int y = getChildAt(0).getTop();
					setAdapter(getAdapter());
					setSelectionFromTop(position, y);
					// End hack
				}

				// Force children to be recreated where needed
				layoutChildren();

				// Get reference to the updated item at given position
				v = getChildAt(i);
				if (v == null)
				{
					// No more views to process
					break;
				}
			}

			// Update layout params for the view
			if (isDraggableRow(v))
			{
				ViewGroup.LayoutParams params = v.getLayoutParams();
				params.height = mItemHeightNormal;
				v.setLayoutParams(params);
				v.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Adjust visibility and size to make it appear as though an item is being dragged around
	 * and other items are making room for it:<br>
	 * - If dropping the item would result in it still being in the same place, then make the dragged list item's
	 * size normal, but make the item invisible.<br>
	 * - Otherwise, if the dragged list item is still on screen, make it as small as possible and expand the item
	 * below the insert point.<br>
	 * - If the dragged item is not on screen, only expand the item below the current insert point.
	 */
	private void doExpansion()
	{
		// Item that needs to be expanded
		int childNum = mDraggedItemPosition - getFirstVisiblePosition();
		if (mDraggedItemPosition > mDraggedItemInitialPosition)
		{
			childNum++;
		}

		// View of the initial item being dragged
		View first = getChildAt(mDraggedItemInitialPosition - getFirstVisiblePosition());

		// Iterate through all list items
		for (int i = 0; ; i++)
		{
			View v = getChildAt(i);
			if (v == null)
			{
				// No more views to process
				break;
			}

			// Height of the initial item view in the list
			int height = mItemHeightNormal;

			// Visibility of the initial item view in the list
			int visibility = View.VISIBLE;

			if (v.equals(first))
			{
				// Processing the item that is being dragged
				if (mDraggedItemPosition == mDraggedItemInitialPosition)
				{
					// The item being dragged is in the initial position, hide the initial view to avoid duplication
					visibility = View.INVISIBLE;
				}
				else
				{
					// The item being dragged is somewhere else, set the initial view's height to 1
					height = 1;
				}
			}
			else if (i == childNum)
			{
				// Processing the view where the dragged item is currently located
				if (mDraggedItemPosition < getCount() - 1)
				{
					// Increase the view height
					height = mItemHeightExpanded;
				}
			}

			// Update layout params for the view
			if (isDraggableRow(v))
			{
				ViewGroup.LayoutParams params = v.getLayoutParams();
				params.height = height;
				v.setLayoutParams(params);
				v.setVisibility(visibility);
			}
		}

		// Request re-layout to avoid bogus hit box calculation in myPointToPosition
		layoutChildren();
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent ev)
	{
		if (mGestureDetector != null)
		{
			// Call the touch event on the gesture detector to detect flicks
			mGestureDetector.onTouchEvent(ev);
		}

		// Check if something is being dragged
		if ((mDragListener != null || mDropListener != null) && mDragView != null)
		{
			int action = ev.getAction();
			switch (action)
			{
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:

					// The touch has finished, stop item dragging
					Rect r = new Rect();
					mDragView.getDrawingRect(r);
					stopDragging();

					// Check if the item was slided to the right and if it needs to be removed
					if (mRemoveMode == SLIDE_RIGHT && ev.getX() > r.left + (r.width() * 3 / 4))
					{
						if (mRemoveListener != null)
						{
							mRemoveListener.remove(mDraggedItemInitialPosition);
						}
						unExpandViews(true);
					}

					// Check if the item was slided to the left and if it needs to be removed
					else if (mRemoveMode == SLIDE_LEFT && ev.getX() < r.left + (r.width() / 4))
					{
						if (mRemoveListener != null)
						{
							mRemoveListener.remove(mDraggedItemInitialPosition);
						}
						unExpandViews(true);
					}

					// Check if the item position has changed while dragging and if the list needs to be reordered
					else
					{
						if (mDropListener != null && mDraggedItemPosition >= 0 && mDraggedItemPosition < getCount())
						{
							if (mDraggedItemPosition > mDraggedItemInitialPosition)
							{
								mDraggedItemPosition++;
							}

							mDropListener.drop(mDraggedItemInitialPosition, mDraggedItemPosition);
						}
						unExpandViews(false);
					}

					break;

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:

					// The touch is ongoing, update the dragged view position on screen
					int x = (int) ev.getX();
					int y = (int) ev.getY();
					dragView(x, y);

					// Get the position of the item under the touch
					int itemNum = getItemForPosition(y);
					if (itemNum >= 0)
					{
						if (action == MotionEvent.ACTION_DOWN || itemNum != mDraggedItemPosition)
						{
							// Notify the listener that the drag has started or the dragged item position has changed
							if (mDragListener != null)
							{
								mDragListener.drag(mDraggedItemPosition, itemNum);
							}

							// Change the dragged item position
							mDraggedItemPosition = itemNum;
							doExpansion();
						}

						// Adjust the scroll bounds for the touch y position
						int speed = 0;
						adjustScrollBounds(y);

						// Set the scroll speed based on the scroll bounds
						if (y > mLowerBound)
						{
							// Scroll the list up a bit
							speed = y > (mHeight + mLowerBound) / 2 ? 16 : 4;
						}
						else if (y < mUpperBound)
						{
							// Scroll the list down a bit
							speed = y < mUpperBound / 2 ? -16 : -4;
						}

						// If the speed is not zero, the list needs to be scrolled
						if (speed != 0)
						{
							int ref = pointToPosition(0, mHeight / 2);
							if (ref == AdapterView.INVALID_POSITION)
							{
								// We hit a divider or an invisible view, adjust the reference position
								ref = pointToPosition(0, mHeight / 2 + getDividerHeight() + 64);
							}

							View v = getChildAt(ref - getFirstVisiblePosition());
							if (v != null)
							{
								// Scroll the list by selecting the view at the reference position
								int pos = v.getTop();
								setSelectionFromTop(ref, pos - speed);
							}
						}
					}

					break;
			}

			// Something has been dragged, return true to avoid long click from being triggered
			return true;
		}

		return super.onTouchEvent(ev);
	}

	/**
	 * Start dragging the list item view. Creates a temporary view that represents the item being dragged.
	 * @param bm Bitmap that stores the temporary view
	 * @param x  X position of the touch.
	 * @param y  Y position of the touch.
	 */
	private void startDragging(Bitmap bm, int x, int y)
	{
		// Make sure nothing is being dragged
		stopDragging();

		// Set the window params
		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.TOP | Gravity.START;
		mWindowParams.x = x;
		mWindowParams.y = y - mDragPointOffset + mCoordinateOffset;

		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		// Create the temporary view that will be dragged
		ImageView v = new ImageView(getContext());
		v.setBackgroundColor(mDragBackgroundColor);
		v.setImageBitmap(bm);
		mDragBitmap = bm;

		// Add the view to the window and cache a reference to it
		mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	/**
	 * Updates the position of the dragged item on screen.
	 * @param x X coordinate of the touch event.
	 * @param y Y coordinate of the touch event.
	 */
	private void dragView(int x, int y)
	{
		float alpha = 1.0f;
		int width = mDragView.getWidth();

		// Check if the view can be slided to the right
		if (mRemoveMode == SLIDE_RIGHT)
		{
			if (x > width / 2)
			{
				// Decrease the view alpha once the view has been slided past half the screen
				alpha = ((float) (width - x)) / (width / 2);
			}
			mWindowParams.alpha = alpha;
		}

		// Check if the view can be slided to the left
		else if (mRemoveMode == SLIDE_LEFT)
		{
			if (x < width / 2)
			{
				// Decrease the view alpha once the view has been slided past half the screen
				alpha = ((float) x) / (width / 2);
			}
			mWindowParams.alpha = alpha;
		}

		// Update the view position based on the touch coordinates
		mWindowParams.y = y - mDragPointOffset + mCoordinateOffset;
		mWindowManager.updateViewLayout(mDragView, mWindowParams);
	}

	/**
	 * Cleans up the view after dragging has finished.
	 */
	private void stopDragging()
	{
		if (mDragView != null)
		{
			// Remove the temporary view that was created for item dragging
			WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
		if (mDragBitmap != null)
		{
			mDragBitmap.recycle();
			mDragBitmap = null;
		}
	}

	/**
	 * Set the listener that will be notified every time an item is dragged and changes position in the layout.
	 * @param l Listener to notify.
	 */
	public void setDragListener(DragListener l)
	{
		mDragListener = l;
	}

	/**
	 * Set the listener that will be notified every time an item is dropped after being dragged.
	 * Use this to handle reordering of the list items.
	 * @param l Listener to notify.
	 */
	public void setDropListener(DropListener l)
	{
		mDropListener = l;
	}

	/**
	 * Set the listener that will be notified every time an item should be removed from the list.
	 * This happens if a remove mode is set on the list view (flick, slide right or slide left).
	 * @param l Listener to notify.
	 */
	public void setRemoveListener(RemoveListener l)
	{
		mRemoveListener = l;
	}

	public interface DragListener
	{
		void drag(int from, int to);
	}

	public interface DropListener
	{
		void drop(int from, int to);
	}

	public interface RemoveListener
	{
		void remove(int which);
	}
}
