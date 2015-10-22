/**
 * Copyright (c) Luka Kunic 2015 / "ItemListFragment.java"
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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lkunic.libs.apptoolbox.R;

/**
 * Base class for implementing a list fragment for the TwoPane master-detail flow.
 * Activities containing this fragment must implement the {@link ItemListFragment.OnItemSelectedListener}
 */
public abstract class ItemListFragment extends Fragment
{
	/**
	 * A dummy implementation of the {@link OnItemSelectedListener} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static OnItemSelectedListener sDummyListener = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(long id)
		{
			Log.e("ItemListFragment", "Dummy callback method called. Something is wrong with the implementation");
		}
	};

	// The fragment's current callback object, which is notified of list item clicks
	private OnItemSelectedListener mListener = sDummyListener;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// Activities containing this fragment must implement its callback
		if (!(activity instanceof OnItemSelectedListener))
		{
			throw new IllegalStateException(String.format("%s must implement fragment's callbacks.", activity
					.getClass().getName()));
		}

		mListener = (OnItemSelectedListener) activity;
	}

	// region Abstract methods

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_item_list, container, false);

		// Setup the item list
		ListView list = (ListView) view.findViewById(R.id.item_list);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				mListener.onItemSelected(id);
			}
		});

		// Call to the abstract method that is used by a derived class to add content to the list
		setupListContent(list);

		return view;
	}

	// endregion

	// region Listener implementation

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Reset the active callback interface to the dummy implementation
		mListener = sDummyListener;
	}

	/**
	 * Implement to set up list adapter and content. You can extend the ItemListCursorAdapter and use
	 * it for populating your list using a cursor.
	 */
	protected abstract void setupListContent(View list);

	/**
	 * Returns the callback object.
	 */
	protected OnItemSelectedListener getCallbacks()
	{
		return mListener;
	}

	/**
	 * A callback interface that all activities containing this fragment must implement. This mechanism allows
	 * activities to be notified of item selections.
	 */
	public interface OnItemSelectedListener
	{
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemSelected(long id);
	}

	// endregion
}
