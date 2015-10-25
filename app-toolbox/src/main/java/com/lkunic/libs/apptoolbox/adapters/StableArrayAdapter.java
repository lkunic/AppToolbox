/**
 * Copyright (c) Luka Kunic 2015 / "StableArrayAdapter.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 22/10/2015.
 */
package com.lkunic.libs.apptoolbox.adapters;

import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This implementation of the ArrayAdapter makes sure that the id for each item in the adapter stays the same
 * throughout the lifecycle of the adapter.
 */
public class StableArrayAdapter<T> extends ArrayAdapter<T>
{
	// Dictionary that contains our custom id's for each item in the given list
	private HashMap<T, Integer> mIdMap = new HashMap<>();

	private List<T> mItems;

	public StableArrayAdapter(Context context, int resId, T[] objects)
	{
		this(context, resId, Arrays.asList(objects));
	}

	public StableArrayAdapter(Context context, int resId, List<T> objects)
	{
		super(context, resId, objects);

		mItems = objects;
		updateIdMap();
	}

	@Override
	public long getItemId(int position)
	{
		if (position < 0 || position >= mIdMap.size() || position >= getCount())
		{
			return -1;
		}

		// Returns our stored id instead of the actual id in the list
		return mIdMap.get(getItem(position));
	}

	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
		updateIdMap();
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	private void updateIdMap()
	{
		if (mItems.size() != mIdMap.size())
		{
			T item;

			for (int i = 0, n = mItems.size(); i< n; i++)
			{
				item = mItems.get(i);

				if (!mIdMap.containsKey(item))
				{
					mIdMap.put(item, i);
				}
			}
		}
	}
}
