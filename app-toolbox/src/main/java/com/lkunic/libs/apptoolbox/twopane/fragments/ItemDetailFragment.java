package com.lkunic.libs.apptoolbox.twopane.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Copyright (c) Luka Kunic 2015 / "ItemDetailFragment.java"
 * Created by lkunic on 08/04/2015.
 *
 * Base class for a detail fragment in the TwoPane master-detail flow.
 */
public abstract class ItemDetailFragment extends Fragment
{
	// The fragment argument representing the item id that this fragment displays
	public static final String ARG_ITEM_ID = "item_id";

	// Id of the item that was selected in the list
	private long mItemId;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(ARG_ITEM_ID))
		{
			// Load the item id from the saved instance
			mItemId = savedInstanceState.getLong(ARG_ITEM_ID);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the item id so that it can be loaded when the fragment is recreated
		outState.putLong(ARG_ITEM_ID, mItemId);
	}

	/**
	 * Refresh the fragment content. Assumes a new item id was set.
	 */
	public void refreshContent()
	{
		View view = getView();

		if (view != null)
		{
			setupContent(view);
		}
	}

	/**
	 * Returns the item id that was passed in by the list activity.
	 */
	protected long getItemId()
	{
		return mItemId;
	}

	/**
	 * Set the id of the item that will be displayed in this fragment.
	 */
	public void setItemId(long id)
	{
		mItemId = id;
	}

	// region Abstract methods

	/**
	 * Use to get the item data from the database.
	 */
	protected abstract void getItemData();

	/**
	 * Setup the fragment content.
	 */
	protected abstract void setupContent(View view);

	// endregion
}
