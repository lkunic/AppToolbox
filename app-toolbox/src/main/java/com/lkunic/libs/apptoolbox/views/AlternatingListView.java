/**
 * Copyright (c) Luka Kunic 2015 / "AlternatingListView.java"
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lkunic.libs.apptoolbox.R;

/**
 * Simple list view implementation that alternates the background of the list items. Ideal for listing simple string values.
 * Allows setting a string header or a custom header view.
 */
public class AlternatingListView extends ListView
{
	public AlternatingListView(Context context)
	{
		super(context);
	}

	public AlternatingListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	// region Public content setters

	/**
	 * Set the data being displayed in the list.
	 * To add secondary data to the list item, append an additional string with '|' character as the separator.
	 * @param data Array of strings representing the data.
	 */
	public void setData(String[] data)
	{
		ShortListAdapter adapter = (ShortListAdapter) getAdapter();
		if (adapter == null)
		{
			setAdapter(new ShortListAdapter(getContext(), data));
		}
		else
		{
			adapter.clear();
			adapter.addAll(data);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Adds a simple title header to the list using the given string as the title.
	 * @param headerTitle Title to be displayed in the header.
	 */
	public void setHeader(String headerTitle)
	{
		View headerView = inflate(getContext(), R.layout.view_shortlist_header, null);
		TextView txtHeader = (TextView) headerView.findViewById(R.id.title);
		txtHeader.setText(headerTitle);

		addHeaderView(headerView, null, false);
	}

	/**
	 * Use to add a custom header view to the list.
	 * @param headerView The header view to be added.
	 */
	public void setHeader(View headerView)
	{
		addHeaderView(headerView, null, false);
	}

	// endregion

	// region List adapter

	private static class ShortListAdapter extends ArrayAdapter<String>
	{
		public ShortListAdapter(Context context, String[] data)
		{
			super(context, R.layout.view_shortlist_item, data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				// The convertView is not yet inflated, inflate it and populate the view holder
				convertView = inflate(getContext(), R.layout.view_shortlist_item, null);

				ViewHolder viewHolder = new ViewHolder();
				viewHolder.text = (TextView) convertView.findViewById(R.id.text);
				viewHolder.secondaryText = (TextView) convertView.findViewById(R.id.text_secondary);

				// Add the view holder to the view
				convertView.setTag(viewHolder);
			}

			String[] textElements = getItem(position).split("\\|");

			// Set the item text
			TextView text = ((ViewHolder) convertView.getTag()).text;

			text.setText(textElements[0]);
			text.setBackgroundResource(position % 2 == 0 ? R.color.list_background_1
														 : R.color.list_background_2);

			if (textElements.length > 1)
			{
				((ViewHolder) convertView.getTag()).secondaryText.setText(textElements[1]);
			}

			return convertView;
		}

		/**
		 * Helper structure that contains references to the inner views of the view layout.
		 */
		private static class ViewHolder
		{
			private TextView text;
			private TextView secondaryText;
		}
	}

	// endregion
}
