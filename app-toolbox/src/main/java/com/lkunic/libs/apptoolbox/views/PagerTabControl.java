package com.lkunic.libs.apptoolbox.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lkunic.libs.apptoolbox.R;

/**
 * Copyright (c) Luka Kunic 2015 / "ViewPagerTabControl.java"
 * Created by lkunic on 08/04/2015.
 *
 * View that can be used for choosing tabs in a ViewPager.
 */
public class PagerTabControl extends LinearLayout
{
	/**
	 * Creates a new PagerTabControl view with the given text values.
	 * @param context       Application context.
	 * @param primaryText   Primary text to be displayed, or null to hide the view.
	 * @param secondaryText Secondary text to be displayed, or null to hide the view.
	 */
	public PagerTabControl(Context context, String primaryText, String secondaryText)
	{
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_pager_tab_control, this, true);

		setupContent(primaryText, secondaryText);
	}

	// region Private support methods

	/**
	 * Use to setup the tab view content (text values).
	 * @param primaryText   Value for the primary text view.
	 * @param secondaryText Value for the secondary text view.
	 */
	private void setupContent(String primaryText, String secondaryText)
	{
		TextView txtPrimary = (TextView) findViewById(R.id.primary_text);
		if (primaryText != null)
		{
			// Set the primary text
			txtPrimary.setText(primaryText);
		}
		else
		{
			// Hide the primary text view since no content was provided
			txtPrimary.setVisibility(View.GONE);
		}

		TextView txtSecondary = (TextView) findViewById(R.id.secondary_text);
		if (secondaryText != null)
		{
			// Set the secondary text
			txtSecondary.setText(secondaryText);
		}
		else
		{
			// Hide the secondary text since no content was provided
			txtSecondary.setVisibility(View.GONE);
		}

		setClickable(true);
	}

	// endregion
}
