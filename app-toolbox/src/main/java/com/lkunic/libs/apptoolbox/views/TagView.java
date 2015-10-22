/**
 * Copyright (c) Luka Kunic 2015 / "TagView.java"
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
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.Gravity;

import com.lkunic.libs.apptoolbox.R;

/**
 * A simple TextView extension for displaying tags.
 */
public class TagView extends FontTextView
{
	public TagView(Context context)
	{
		super(context);

		setupView();
	}

	public TagView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		setupView();
	}

	/**
	 * Sets the TagView background color to the given color.
	 */
	public void setBackgroundColor(int color)
	{
		getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
	}

	// region Private support methods

	/**
	 * Sets up the appearance of the TagView (background, margins, padding).
	 */
	private void setupView()
	{
		// Set the background
		setBackgroundResource(R.drawable.tag_view_background);

		// Set the layout parameters
		int horizontalPadding = (int) getResources().getDimension(R.dimen.tag_view_side_padding);
		setPadding(horizontalPadding, 0, horizontalPadding, 0);
		setHeight((int) getResources().getDimension(R.dimen.tag_view_height));
		setGravity(Gravity.CENTER_VERTICAL);

		setTextSize(getResources().getDimension(R.dimen.tag_view_text_size));

		// The tag view should be clickable
		setClickable(true);
	}

	// endregion
}
