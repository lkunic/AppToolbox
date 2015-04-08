package com.lkunic.libs.apptoolbox.twopane;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Copyright (c) Luka Kunic 2015 / "ItemListCursorAdapter.java"
 * Created by lkunic on 08/04/2015.
 *
 * Base for creating an adapter that populates a list with cursor data received from a data set.
 */
public abstract class ItemListCursorAdapter extends CursorAdapter
{
	private LayoutInflater mLayoutInflater;

	public ItemListCursorAdapter(Context context, Cursor c)
	{
		super(context, c, 0);

		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		// Inflate the view
		View view = mLayoutInflater.inflate(getListItemLayoutResourceId(), parent, false);

		// Setup the view holder object
		setupViewHolder(view);

		return view;
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor)
	{
		setupContent(convertView, cursor);
	}

	// region Abstract methods

	/**
	 * Implementation of this method should return a valid resource id for a grid item layout.
	 */
	protected abstract int getListItemLayoutResourceId();

	/**
	 * Implement this to set up a view holder object used for storing references to views in the layout.
	 * Create a static ViewHolder structure with references to all views that will be populated in the
	 * bindView() method. Store the object as a tag in the given parent view.
	 * @param view Parent view of the grid item layout.
	 */
	protected abstract void setupViewHolder(View view);

	/**
	 * Implement this to set up content of the view. The view is already inflated and should contain a
	 * ViewHolder object (if it has been added as a tag to the view in setupViewHolder() method).
	 * @param view   ConvertView that is used to display data for a single grid item.
	 * @param cursor Cursor containing data for the grid item.
	 */
	protected abstract void setupContent(View view, Cursor cursor);

	// endregion
}
