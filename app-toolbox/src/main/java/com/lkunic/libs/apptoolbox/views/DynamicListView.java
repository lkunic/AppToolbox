/**
 * Copyright (c) Luka Kunic 2015 / "DynamicListView.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 22/10/2015.
 */
package com.lkunic.libs.apptoolbox.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lkunic.libs.apptoolbox.adapters.StableArrayAdapter;

import java.util.List;

/**
 * An extension to the regular ListView that allows drag-drop sorting of list items and swipe-to-delete functionality.
 *
 * <h4><b>Drag-drop functionality:</b></h4>
 * If no cell is selected, all touch events are passed down to the base ListView and are handled normally. If one of
 * the items is long-pressed, the contents of the selected item view are captured as a bitmap object and the original
 * item is hidden. A hover cell is then created and added as an overlaying bitmap drawable above the ListView. The
 * hover cell is positioned to the correct location on the screen based on touch events. The layout uses the position
 * of the hover cell to determine whether two cells should be swapped. Once the hover cell is translated enough to
 * indicate an item swap, a data set change occurs and is accompanied by an animation that visually swaps the two items.
 * When the touch interaction ends, the hover cell animates into the new position in the list view. When the hover cell
 * is at the upper or lower bound of the list view, scrolling occurs in order to reveal additional content.
 */
public class DynamicListView extends ListView
{
	// region Variables and Constants

	// Constants
	private final int SMOOTH_SCROLL_AMOUNT_AT_EDGE = 60;
	private final int ITEM_SWAP_OVERLAP_SIZE = 30;
	private final int LEFT_CELL_HOVER_GRABBER_WIDTH = 120;
	private final int BITMAP_SHADOW_SIZE = 10;
	private final int INVALID_ID = -1;
	private final int ANIMATION_DURATION = 150;

	// Touch
	private int mDownX = -1;
	private int mDownY = -1;
	private int mLastEventY = -1;
	private int mActivePointerId = INVALID_ID;

	private int mTotalOffset = 0;

	// Item id's
	private long mAboveItemId = INVALID_ID;
	private long mHoverItemId = INVALID_ID;
	private long mBelowItemId = INVALID_ID;

	// Hover cell
	private boolean mCellIsHovering = false;
	private boolean mCellIsScrolling = false;
	private boolean mIsWaitingForScrollToFinish = false;
	private BitmapDrawable mHoverCell;
	private Rect mHoverCellOriginalBounds;
	private Rect mHoverCellCurrentBounds;

	// Scrolling
	private int mSmoothScrollAmountAtEdge = 0;
	private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

	private List mListItems;

	// endregion

	public DynamicListView(Context context)
	{
		super(context);
		init(context);
	}

	public DynamicListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public DynamicListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * Initializes the list view.
	 * @param context Application context
	 */
	private void init(Context context)
	{
		// Setup the listeners
		setOnItemLongClickListener(mItemLongClickListener);
		setOnScrollListener(mScrollListener);

		// Setup values to allow smooth scrolling
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mSmoothScrollAmountAtEdge = (int)(SMOOTH_SCROLL_AMOUNT_AT_EDGE / metrics.density);
	}

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);

		// If the hover cell is not null, this will draw it over the ListView items whenever the ListView is redrawn
		if (mHoverCell != null)
		{
			mHoverCell.draw(canvas);
		}
	}

	public void setListItems(List listItems)
	{
		mListItems = listItems;
	}

	// region Touch events

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				mDownX = (int)event.getX();
				mDownY = (int)event.getY();
				mActivePointerId = event.getPointerId(0);
				break;

			case MotionEvent.ACTION_MOVE:
				if (mActivePointerId == INVALID_ID)
				{
					break;
				}

				int pointerIndex = event.findPointerIndex(mActivePointerId);

				// Get the delta amount for the Y coordinate since the touch started
				mLastEventY = (int) event.getY(pointerIndex);
				int deltaY = mLastEventY - mDownY;

				if (mCellIsHovering)
				{
					// Move the hover cell on the Y axis to follow the touch location
					mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left,
							mHoverCellOriginalBounds.top + deltaY + mTotalOffset);

					// Set the new hover cell bounds and invalidate in order to redraw it
					mHoverCell.setBounds(mHoverCellCurrentBounds);
					invalidate();

					// Reorder list items if necessary
					handleCellSwap();

					// Handle scrolling
					mCellIsScrolling = handleCellScroll(mHoverCellCurrentBounds);

					return false;
				}

				break;

			case MotionEvent.ACTION_UP:
				touchEventsEnded();
				break;

			case MotionEvent.ACTION_CANCEL:
				touchEventsCancelled();
				break;

			case MotionEvent.ACTION_POINTER_UP:
				// Handles the case when a multitouch event occurred, but the original pointer that was handling the
				// hover cell is released - the dragging event finishes and the items are properly positioned
				pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
						MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				int pointerId = event.getPointerId(pointerIndex);
				if (pointerId == mActivePointerId)
				{
					touchEventsEnded();
				}
				break;

			default:
				break;
		}
		
		return super.onTouchEvent(event);
	}

	/**
	 * Resets all hover-related fields to the default values and animates the hover cell to the correct location.
	 */
	private void touchEventsEnded()
	{
		final View hoverView = getViewForId(mHoverItemId);

		if (hoverView != null && (mCellIsHovering || mIsWaitingForScrollToFinish))
		{
			// Stop cell hovering and scrolling
			mCellIsHovering = false;
			mIsWaitingForScrollToFinish = false;
			mCellIsScrolling = false;
			mActivePointerId = INVALID_ID;

			// If the auto-scroller has not yet completed with scrolling, we have to wait for it to finish in order
			// to determine the final location for the hover cell animation.
			if (mScrollState != OnScrollListener.SCROLL_STATE_IDLE)
			{
				mIsWaitingForScrollToFinish = true;
				return;
			}

			// Animate the hover cell falling in place
			mHoverCellCurrentBounds.offsetTo(mHoverCellOriginalBounds.left, hoverView.getTop());

			ObjectAnimator hoverViewAnimator =
					ObjectAnimator.ofObject(mHoverCell, "bounds", sBoundEvaluator, mHoverCellCurrentBounds);
			hoverViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
			{
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator)
				{
					invalidate();
				}
			});
			hoverViewAnimator.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					setEnabled(false);
				}

				@Override
				public void onAnimationEnd(Animator animation)
				{
					// Reset all hover-related variables
					mAboveItemId = INVALID_ID;
					mHoverItemId = INVALID_ID;
					mBelowItemId = INVALID_ID;
					hoverView.setVisibility(View.VISIBLE);
					mHoverCell = null;
					setEnabled(true);
					invalidate();
				}
			});

			hoverViewAnimator.start();
		}
		else
		{
			touchEventsCancelled();
		}
	}

	/**
	 * Resets all hover related variables to the default state.
	 */
	private void touchEventsCancelled()
	{
		View hoverView = getViewForId(mHoverItemId);
		if (hoverView != null && mCellIsHovering)
		{
			mAboveItemId = INVALID_ID;
			mHoverItemId = INVALID_ID;
			mBelowItemId = INVALID_ID;
			hoverView.setVisibility(View.VISIBLE);
			mHoverCell = null;
			invalidate();
		}

		mCellIsHovering = false;
		mCellIsScrolling = false;
		mActivePointerId = INVALID_ID;
	}

	/**
	 * The evaluator used for animating the hover cell to the correct location after the drag-drop event has ended.
	 */
	private static final TypeEvaluator<Rect> sBoundEvaluator = new TypeEvaluator<Rect>()
	{
		@Override
		public Rect evaluate(float delta, Rect start, Rect end)
		{
			return new Rect(
					interpolate(start.left, end.left, delta),
					interpolate(start.top, end.top, delta),
					interpolate(start.right, end.right, delta),
					interpolate(start.bottom, end.bottom, delta));
		}

		private int interpolate(int start, int end, float delta)
		{
			return (int)(start + delta * (end - start));
		}
	};

	// endregion

	// region Cell hover

	/**
	 * Listener that handles item long-click events. Used to initiate drag-drop functionality for selected item.
	 * When an item is selected, creates and sets up the hover cell.
	 */
	private AdapterView.OnItemLongClickListener mItemLongClickListener =
			new AdapterView.OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
			beginCellHover();
			return true;
		}
	};

	/**
	 * Initiates the drag-drop event by creating the hover cell and setting the list to a hover state.
	 */
	private void beginCellHover()
	{
		mTotalOffset = 0;

		// Get the position of the item at the initial touch location
		int pos = pointToPosition(mDownX, mDownY);
		int itemNum = pos - getFirstVisiblePosition();

		// Create the hover bitmap view and hide the original list item
		View selectedView = getChildAt(itemNum);
		mHoverItemId = getAdapter().getItemId(pos);
		mHoverCell = getAndAddHoverCell(selectedView);
		selectedView.setVisibility(View.INVISIBLE);

		mCellIsHovering = true;

		// Get references to the views above and below the selected item
		updateNeighbourViewsForId(mHoverItemId);
	}

	/**
	 * Creates a bitmap drawable that will be drawn as the hover cell during the drag-drop operation. The hover cell
	 * bitmap is drawn on top of the list every time the <code>invalidate()</code> method is called.
	 * @param v The selected item that will be used to create the bitmap.
	 * @return The BitmapDrawable created from the given view.
	 */
	private BitmapDrawable getAndAddHoverCell(View v)
	{
		// Create the bitmap drawable
		Bitmap bitmap = getBitmapWithShadow(v);
		BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

		// Set the bounds for the bitmap (used for moving the bitmap on the screen to follow the touch position
		int left = v.getLeft();
		int top = v.getTop();
		mHoverCellOriginalBounds = new Rect(left, top, left + v.getWidth(), top + v.getHeight());
		mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);

		drawable.setBounds(mHoverCellCurrentBounds);

		return drawable;
	}

	/**
	 * Creates a bitmap from the given view and adds a shadow on top/bottom to make it seem as if it floats above
	 * the other items in the list view.
	 * @param v The view to use to create the bitmap.
	 * @return The bitmap image extracted from the view.
	 */
	private Bitmap getBitmapWithShadow(View v)
	{
		// Create a canvas and use it to draw the given view onto the bitmap
		Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		// Fill the entire bitmap background with white (to avoid transparency issues)
		// TODO: enable color picking as a layout attribute
		canvas.drawARGB(255, 255, 255, 255);

		// Draw the view onto the bitmap
		v.draw(canvas);

		// TODO: Find the best way to draw the drop shadow

		return bitmap;
	}

	// endregion

	// region Neighbour views

	/**
	 * Stores references to the views directly above and below the selected item corresponding to the hover cell.
	 * If the selected item is at the top or bottom of the list, the above or below view will be invalid.
	 * @param id Id of the currently selected item.
	 */
	private void updateNeighbourViewsForId(long id)
	{
		int position = getPositionForId(id);

		StableArrayAdapter adapter = ((StableArrayAdapter) getAdapter());
		mAboveItemId = adapter.getItemId(position - 1);
		mBelowItemId = adapter.getItemId(position + 1);
	}

	/**
	 * Finds the view in the currently visible part of the list that is displaying the item with the given id.
	 * @param id Id of the item.
	 * @return View containing the item with the given id.
	 */
	private View getViewForId(long id)
	{
		// Get the list position of the first list item currently on screen
		int firstVisiblePosition = getFirstVisiblePosition();
		StableArrayAdapter adapter = ((StableArrayAdapter) getAdapter());
		View v;

		// Iterate through the views on the screen to find the one containing the item with the given id
		for (int i = 0, n = getChildCount(); i < n; i++)
		{
			v = getChildAt(i);

			if (adapter.getItemId(firstVisiblePosition + i) == id)
			{
				return v;
			}
		}

		return null;
	}

	/**
	 * Finds the view containing the item with the given id and returns its position if the view is valid.
	 * @param id The id the view.
	 * @return Position of the view containing the item with the given id.
	 */
	private int getPositionForId(long id)
	{
		View v = getViewForId(id);
		return v != null ? getPositionForView(v) : -1;
	}

	// endregion

	// region Item reordering

	/**
	 * If the hover cell has been moved far enough to trigger an item switch, the data set gets changed and the layout
	 * is invalidated. Using a ViewTreeObserver and a corresponding OnPreDrawListener, we can offset the cell being
	 * swapped to where it previously was and then animate it to its new position.
	 */
	private void handleCellSwap()
	{
		final int deltaY = mLastEventY - mDownY;
		int deltaYTotal = mHoverCellOriginalBounds.top + mTotalOffset + deltaY;

		View aboveView = getViewForId(mAboveItemId);
		View hoverView = getViewForId(mHoverItemId);
		View belowView = getViewForId(mBelowItemId);

		// Check if the hover view is either below the bottom neighbour or above the top neighbour
		boolean isAbove = (aboveView != null) && ((deltaYTotal - ITEM_SWAP_OVERLAP_SIZE) < aboveView.getTop());
		boolean isBelow = (belowView != null) && ((deltaYTotal + ITEM_SWAP_OVERLAP_SIZE) > belowView.getTop());

		if (hoverView != null && (isBelow || isAbove))
		{
			final long switchItemId = isBelow ? mBelowItemId : mAboveItemId;
			View switchView = isBelow ? belowView : aboveView;
			final int originalItemPosition = getPositionForView(hoverView);

			// Swap the items in the array adapter
			swapListItems(originalItemPosition, getPositionForView(switchView));
			((StableArrayAdapter)getAdapter()).notifyDataSetChanged();

			mDownY = mLastEventY;

			final int switchViewStartTop = switchView.getTop();

			// The items in the list have been swapped, so the hover cell now corresponds to the switch view
			hoverView.setVisibility(View.INVISIBLE);
			switchView.setVisibility(View.VISIBLE);

			// Get the new neighbours after the swap
			updateNeighbourViewsForId(mHoverItemId);

			// Use the PreDrawListener of the ViewTreeObserver to animate the cell swap
			final ViewTreeObserver observer = getViewTreeObserver();
			observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
			{
				@Override
				public boolean onPreDraw()
				{
					// Remove the listener immediately because we want this only to be called once
					observer.removeOnPreDrawListener(this);

					// The views were swapped so we need to get a new reference to the switch view
					View switchView = getViewForId(switchItemId);
					if (switchView != null)
					{
						mTotalOffset += deltaY;

						// Offset the switch view back to its original position before the swap
						int switchViewNewTop = switchView.getTop();
						int delta = switchViewStartTop - switchViewNewTop;

						switchView.setTranslationY(delta);

						ObjectAnimator animator = ObjectAnimator.ofFloat(switchView, View.TRANSLATION_Y, 0);
						animator.setDuration(ANIMATION_DURATION);
						animator.start();

						return true;
					}

					return false;
				}
			});
		}

	}

	/**
	 * Swaps the list items at the given positions.
	 * @param posFirst Position of the first item to be swapped.
	 * @param posSecond Position of the second item to be swapped.
	 */
	private void swapListItems(int posFirst, int posSecond)
	{
		Object temp = mListItems.get(posFirst);
		mListItems.set(posFirst, mListItems.get(posSecond));
		mListItems.set(posSecond, temp);
	}

	// endregion

	// region List scrolling

	/**
	 * The scroll listener in charge of handling cell swapping when the cell is at the top or the bottom of the visible
	 * view. If the hover cell is at the border, the list view will begin scrolling. The list view will continuously
	 * check if new cells became visible and determines whether they are potential candidates for a cell swap.
	 */
	private OnScrollListener mScrollListener = new OnScrollListener()
	{
		private int mPreviousFirstVisibleitem = -1;
		private int mPreviousVisibleItemCount = -1;
		private int mCurrentFirstVisibleItem;
		private int mCurrentVisibleItemCount;
		private int mCurrentScrollState;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			mCurrentScrollState = scrollState;
			mScrollState = scrollState;
			isScrollCompleted();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			mCurrentFirstVisibleItem = firstVisibleItem;
			mCurrentVisibleItemCount = visibleItemCount;

			mPreviousFirstVisibleitem =
					(mPreviousFirstVisibleitem == -1) ? mCurrentFirstVisibleItem : mPreviousFirstVisibleitem;
			mPreviousVisibleItemCount =
					(mPreviousVisibleItemCount == -1) ? mCurrentVisibleItemCount : mPreviousVisibleItemCount;

			checkAndHandleFirstVisibleCellChange();
			checkAndHandleLastVisibleCellChange();

			mPreviousFirstVisibleitem = mCurrentFirstVisibleItem;
			mPreviousVisibleItemCount = mCurrentVisibleItemCount;
		}

		/**
		 * (1) If the ListView is in a scrolling state invoked by the hover cell being outside of the bounds, this
		 * makes sure that the scrolling event continues. (2) If the hover cell has already been released, this triggers
		 * the touchEventsEnded() method that animates the hover cell moving to the correct position.
		 */
		private void isScrollCompleted()
		{
			if (mCurrentVisibleItemCount > 0 && mCurrentScrollState == SCROLL_STATE_IDLE)
			{
				if (mCellIsHovering && mCellIsScrolling)
				{
					mCellIsScrolling = handleCellScroll(mHoverCellCurrentBounds);
				}
				else if (mIsWaitingForScrollToFinish)
				{
					touchEventsEnded();
				}
			}
		}

		/**
		 * If the ListView scrolled up enough to reveal a new list item at the top, updates the neighbour values and
		 * swaps the views if necessary.
		 */
		private void checkAndHandleFirstVisibleCellChange()
		{
			if (mCurrentFirstVisibleItem != mPreviousFirstVisibleitem)
			{
				if (mCellIsHovering && mHoverItemId != INVALID_ID)
				{
					updateNeighbourViewsForId(mHoverItemId);
					handleCellSwap();
				}
			}
		}

		/**
		 * If the ListView scrolled down enough to reveal a new list item at the bottom, updates the neighbour values
		 * and swaps the views if necessary.
		 */
		private void checkAndHandleLastVisibleCellChange()
		{
			int currentLastVisibleItem = mCurrentFirstVisibleItem + mCurrentVisibleItemCount;
			int previousLastVisibleItem = mPreviousFirstVisibleitem + mPreviousVisibleItemCount;
			if (currentLastVisibleItem != previousLastVisibleItem)
			{
				if (mCellIsHovering && mHoverItemId != INVALID_ID)
				{
					updateNeighbourViewsForId(mHoverItemId);
					handleCellSwap();
				}
			}
		}
	};

	/**
	 * Handle list scrolling by determining whether the hover cell is above or below the bounds of the list view.
	 * @param r Bounds of the hover view
	 * @return True if scrolling occurred, false otherwise.
	 */
	private boolean handleCellScroll(Rect r)
	{
		int offset = computeVerticalScrollOffset();
		int extent = computeVerticalScrollExtent();
		int range = computeVerticalScrollRange();
		int height = getHeight();

		int hoverViewTop = r.top;
		int hoverHeight = r.height();

		// If the hover view is at the top of the view and we haven't reached the list top yet
		if (hoverViewTop <= 0 && offset > 0)
		{
			// Scroll up
			smoothScrollBy(-mSmoothScrollAmountAtEdge, 0);
			return true;
		}

		// If the hover view is at the bottom of the view and we haven't reached the list bottom yet
		if (hoverViewTop + hoverHeight >= height && (offset + extent) < range)
		{
			// Scroll down
			smoothScrollBy(mSmoothScrollAmountAtEdge, 0);
			return true;
		}

		return false;
	}

	// endregion
}
