/**
 * Copyright (c) Luka Kunic 2015 / "TagBarView.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 08/04/2015.
 */
package com.lkunic.libs.apptoolbox.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Layout for displaying item tags. The tags are displayed on a single line if there is enough room, but extend on
 * multiple lines if needed. To populate the tag bar, provide a simple array of strings. All tags can be clicked and
 * trigger the {@link OnTagSelectedListener#onTagSelected(String)} method.
 */
public class TagBarView extends ViewGroup
{
	// Tag selection listener
	private OnTagSelectedListener mListener;

	// Required for calculating height when extending to multiple lines
	private int mTotalLineHeight;

	public TagBarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setTags(String[] tags, String font, int backgroundColor, int textColor)
	{
		TagView tagView;

		LayoutParams layoutParams = new LayoutParams(8, 8);

		for (final String tag : tags)
		{
			tagView = new TagView(getContext());
			tagView.setText(tag);
			tagView.setTextColor(textColor);
			tagView.setFont(font);
			tagView.setBackgroundColor(backgroundColor);

			tagView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mListener != null)
					{
						mListener.onTagSelected(tag);
					}
				}
			});

			addView(tagView, layoutParams);
		}
	}

	public void setOnTagSelectedListener(OnTagSelectedListener listener)
	{
		mListener = listener;
	}

	// region Layout setup

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED)
		{
			throw new IllegalArgumentException("TagBarView - widthMeasureSpec has invalid mode of UNSPECIFIED");
		}

		int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
		int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

		int count = getChildCount();
		int lineHeight = 0;

		int xPosition = getPaddingLeft();
		int yPosition = getPaddingTop();

		int childHeightMeasureSpec;
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
		{
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
		}
		else
		{
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}

		for (int i = 0; i < count; i++)
		{
			View child = getChildAt(i);
			if (child.getVisibility() != GONE)
			{
				LayoutParams lp = (LayoutParams) child.getLayoutParams();

				child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);

				int childWidth = child.getMeasuredWidth();
				lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + lp.verticalSpacing + getPaddingTop());

				if (xPosition + childWidth > width)
				{
					xPosition = getPaddingLeft();
					yPosition += lineHeight;
				}

				xPosition += childWidth + lp.horizontalSpacing;
			}
		}

		mTotalLineHeight = lineHeight;

		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED)
		{
			height = yPosition + lineHeight;
		}
		else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
		{
			if (yPosition + lineHeight < height)
			{
				height = yPosition + lineHeight;
			}
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p)
	{
		return p instanceof LayoutParams;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int count = getChildCount();
		int width = r - l;
		int xPosition = getPaddingLeft();
		int yPosition = getPaddingTop();

		for (int i = 0; i < count; i++)
		{
			final View child = getChildAt(i);

			if (child.getVisibility() != GONE)
			{
				int childw = child.getMeasuredWidth();
				int childh = child.getMeasuredHeight();
				LayoutParams lp = (LayoutParams) child.getLayoutParams();

				if (xPosition + childw > width)
				{
					xPosition = getPaddingLeft();
					yPosition += mTotalLineHeight;
				}
				child.layout(xPosition, yPosition, xPosition + childw, yPosition + childh);
				xPosition += childw + lp.horizontalSpacing;
			}
		}
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams()
	{
		return new LayoutParams(1, 1);
	}

	/**
	 * Listener interface for notifying the parent activity when one of the tags was selected.
	 */
	public interface OnTagSelectedListener
	{
		void onTagSelected(String tag);
	}

	// endregion

	// region Tag selection listener

	public static class LayoutParams extends ViewGroup.LayoutParams
	{
		public final int horizontalSpacing;
		public final int verticalSpacing;

		public LayoutParams(int width, int height)
		{
			super(0, 0);
			horizontalSpacing = width;
			verticalSpacing = height;
		}
	}

	// endregion
}
