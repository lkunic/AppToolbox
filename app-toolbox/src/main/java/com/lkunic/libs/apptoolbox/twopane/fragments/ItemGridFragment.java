package com.lkunic.libs.apptoolbox.twopane.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lkunic.libs.apptoolbox.R;

/**
 * Copyright (c) Luka Kunic 2015 / "ItemGridFragment.java"
 * Created by lkunic on 08/04/2015.
 *
 * Extension of the {@link ItemListFragment} that displays the items in a grid.
 */
public abstract class ItemGridFragment extends ItemListFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_item_grid, container, false);

		// Setup the item grid
		GridView grid = (GridView) view.findViewById(R.id.item_grid);
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				ItemGridFragment.this.getCallbacks().onItemSelected(id);
			}
		});

		// Call to the abstract method that is used by a derived class to add content to the list
		setupListContent(grid);

		return view;
	}

	// region Abstract methods

	/**
	 * Implement to set up grid adapter and content. You can extend the ItemListCursorAdapter to use
	 * for populating your grid using a cursor.
	 */
	@Override
	protected abstract void setupListContent(View list);

	// endregion
}
