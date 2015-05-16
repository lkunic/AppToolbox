package com.lkunic.libs.apptoolbox.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lkunic.libs.apptoolbox.R;

/**
 * Copyright (c) Luka Kunic 2015 / "ViewPagerTabControl.java"
 * Created by lkunic on 08/04/2015.
 *
 * Button that has primary and secondary text values.
 */
public class TextButton extends LinearLayout
{

	public TextButton(Context context)
	{
		super(context);

		inflateLayout();
	}

	public TextButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		inflateLayout();

		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TextButton);
		String secondaryText = styledAttrs.getString(R.styleable.TextButton_secondary_text);
		styledAttrs.recycle();

		if (secondaryText != null)
		{
			setSecondaryText(secondaryText);
		}
	}

	/**
	 * Creates a new PagerTabControl view with the given text values.
	 * @param context       Application context.
	 * @param primaryText   Primary text to be displayed, or null to hide the view.
	 * @param secondaryText Secondary text to be displayed, or null to hide the view.
	 */
	public TextButton(Context context, String primaryText, String secondaryText)
	{
		super(context);

		inflateLayout();

		setPrimaryText(primaryText);
		setSecondaryText(secondaryText);
	}

	/**
	 * Set the primary text of this view.
	 * @param text Text to set as primary.
	 */
	public void setPrimaryText(String text)
	{
		TextView txtPrimary = (TextView) findViewById(R.id.primary_text);
		if (text != null)
		{
			// Set the primary text
			txtPrimary.setText(text);
		}
		else
		{
			// Hide the primary text view since no content was provided
			txtPrimary.setVisibility(View.GONE);
		}
	}

	/**
	 * Set the secondary text of this view.
	 * @param text Text to set as secondary.
	 */
	public void setSecondaryText(String text)
	{
		TextView txtSecondary = (TextView) findViewById(R.id.secondary_text);
		if (text != null)
		{
			// Set the secondary text
			txtSecondary.setText(text);
		}
		else
		{
			// Hide the secondary text since no content was provided
			txtSecondary.setVisibility(View.GONE);
		}
	}

	/**
	 * Returns the string of the primary text.
	 */
	public String getPrimaryText()
	{
		TextView txtPrimary = (TextView) findViewById(R.id.primary_text);
		return txtPrimary.getText().toString();
	}

	private void inflateLayout()
	{
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_text_button, this, true);

		setClickable(true);
	}
}
