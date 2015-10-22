/**
 * Copyright (c) Luka Kunic 2015 / "TwoPaneDetailActivity.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 08/04/2015.
 */
package com.lkunic.libs.apptoolbox.twopane.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lkunic.libs.apptoolbox.R;
import com.lkunic.libs.apptoolbox.twopane.fragments.ItemDetailFragment;

/**
 * Host activity for the detail fragment in the TwoPane master-detail flow. This activity will only be used on
 * small-screen devices. When implementing this activity, make sure the TwoPaneMasterActivity implementation is
 * specified as parent in the application manifest in order for the 'Up' button to be enabled.
 */
public abstract class TwoPaneDetailActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twopane_detail);

		// The savedInstanceState is non-null when there is fragment state saved from previous configurations
		// of this activity (e.g. when rotating the screen from portrait to landscape). In this case,
		// the fragment will automatically be re-added to its container so we don't need to manually add it.
		if (savedInstanceState == null)
		{
			setData(getIntent().getLongExtra(ItemDetailFragment.ARG_ITEM_ID, -1));
		}
	}

	protected void setData(long id)
	{
		// Create the detail fragment and add it to the activity using a fragment transaction.
		ItemDetailFragment fragment = getItemDetailFragment();

		fragment.setItemId(id);

		// Display the fragment using the FragmentManager
		getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
	}

	// region Abstract methods

	/**
	 * A factory method for creating an instance of the item detail fragment. Can be overridden to provide
	 * a custom implementation of the fragment.
	 * @return A new instance of the item detail fragment.
	 */
	protected abstract ItemDetailFragment getItemDetailFragment();

	// endregion
}
