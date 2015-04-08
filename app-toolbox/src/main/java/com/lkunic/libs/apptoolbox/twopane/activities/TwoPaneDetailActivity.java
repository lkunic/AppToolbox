package com.lkunic.libs.apptoolbox.twopane.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.lkunic.libs.apptoolbox.R;
import com.lkunic.libs.apptoolbox.twopane.fragments.ItemDetailFragment;

/**
 * Copyright (c) Luka Kunic 2015 / "TwoPaneDetailActivity.java"
 * Created by lkunic on 08/04/2015.
 *
 * Host activity for the detail fragment in the TwoPane master-detail flow. This activity will only be used on
 * small-screen devices. When implementing this activity, make sure the TwoPaneMasterActivity implementation is
 * specified as parent in the application manifest in order for the 'Up' button to be enabled.
 */
public abstract class TwoPaneDetailActivity extends ActionBarActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twopane_detail);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
