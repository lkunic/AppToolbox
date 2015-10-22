/**
 * Copyright (c) Luka Kunic 2015 / "ShowcaseInfoFragment.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 08/04/2015.
 */
package com.lkunic.libs.apptoolbox.twopane.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Base class for creating info fragments that are displayed in the ShowcaseFragment view pager.
 */
public abstract class ShowcaseInfoFragment extends Fragment
{
	// Argument keys for the button text
	private static final String ARG_PRIMARY_TEXT = "primaryText";
	private static final String ARG_SECONDARY_TEXT = "secondaryText";

	// Button text values
	private String mPrimaryText;
	private String mSecondaryText;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(ARG_SECONDARY_TEXT))
		{
			mPrimaryText = savedInstanceState.getString(ARG_PRIMARY_TEXT);
			mSecondaryText = savedInstanceState.getString(ARG_SECONDARY_TEXT);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putString(ARG_PRIMARY_TEXT, mPrimaryText);
		outState.putString(ARG_SECONDARY_TEXT, mSecondaryText);
	}

	/**
	 * Returns the primary text on the fragment selection button.
	 */
	public String getPrimaryText()
	{
		return mPrimaryText;
	}

	/**
	 * Sets the primary text on the fragment selection button.
	 */
	protected void setPrimaryText(String primaryText)
	{
		mPrimaryText = primaryText;
	}

	/**
	 * Returns the secondary text on the fragment selection button.
	 */
	public String getSecondaryText()
	{
		return mSecondaryText;
	}

	/**
	 * Sets the secondary text on the fragment selection button.
	 */
	protected void setSecondaryText(String secondaryText)
	{
		mSecondaryText = secondaryText;
	}

	/**
	 * Use to setup the fragment views.
	 * @param view Root view of the fragment layout.
	 */
	protected abstract void setupContent(View view);
}
