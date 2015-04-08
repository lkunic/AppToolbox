package com.lkunic.libs.apptoolbox.twopane.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.lkunic.libs.apptoolbox.R;
import com.lkunic.libs.apptoolbox.twopane.fragments.ItemDetailFragment;
import com.lkunic.libs.apptoolbox.twopane.fragments.ItemListFragment;

/**
 * Copyright (c) Luka Kunic 2015 / "TwoPaneMasterActivity.java"
 * Created by lkunic on 08/04/2015.
 *
 * Abstract activity that provides functionality for creating a master-detail flow for browsing a set of items. If the
 * device is a tablet, the fragments are displayed as two side-by-side columns. If the device is a phone, the detail
 * fragment is opened in a separate activity.
 */
public abstract class TwoPaneMasterActivity extends ActionBarActivity implements ItemListFragment.OnItemSelectedListener
{
	// Whether or not the activity is in two-pane mode, i.e. running on a tablet device
	private boolean mTwoPane;

	// When the activity is first displayed, only the list fragment is shown. Only used in two-pane mode.
	private boolean mListOnly;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twopane_master);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set up the item list fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.item_list_container, getItemListFragment())
				.commit();

		// The detail container will be present only on big screen devices (values-large & values-sw600dp)
		if (findViewById(R.id.item_detail_container) != null)
		{
			// Detail container is present, enable two-pane mode
			mTwoPane = true;
			mListOnly = true;
		}
	}

	@Override
	public void onBackPressed()
	{
		if (!mTwoPane || mListOnly)
		{
			// Only the list is being displayed (small screen device or list-only mode for tablets), continue
			super.onBackPressed();
		}
		else
		{
			// Instead of going back immediately, first switch to list-only mode on large screen devices
			closeDetailPane();
		}
	}

	/**
	 * Callback method from {@link ItemListFragment.OnItemSelectedListener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(long id)
	{
		if (mTwoPane)
		{
			// Two-pane mode is active, the detail fragment gets added here
			ItemDetailFragment fragment = (ItemDetailFragment) getSupportFragmentManager()
					.findFragmentById(R.id.item_detail_container);

			if (fragment == null)
			{
				// Create and display the fragment
				fragment = getItemDetailFragment();
				fragment.setItemId(id);

				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.item_detail_container, fragment).commit();
			}
			else
			{
				fragment.setItemId(id);
				fragment.refreshContent();
			}

			// Change to list-detail mode
			mListOnly = false;

			findViewById(R.id.item_detail_container).setVisibility(View.VISIBLE);
		}
		else
		{
			// In single-pane mode, simply start the detail activity for the selected item ID
			Intent detailIntent = getDetailActivityIntent();
			detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);

			startActivity(detailIntent);
		}
	}

	// region Private support methods

	/**
	 * Closes the detail pane of the two-pane activity.
	 */
	private void closeDetailPane()
	{
		mListOnly = true;

		findViewById(R.id.item_detail_container).setVisibility(View.GONE);
	}

	// endregion

	// region Abstract methods

	/**
	 * A factory method for creating an instance of the item detail fragment. Can be overridden to provide
	 * a custom implementation of the fragment.
	 * @return A new instance of the item detail fragment.
	 */
	protected abstract ItemDetailFragment getItemDetailFragment();

	/**
	 * A factory method for creating an instance of the item list fragment. Can be overridden to provide
	 * a custom implementation of the fragment.
	 * @return A new instance of the item list fragment.
	 */
	protected abstract ItemListFragment getItemListFragment();

	/**
	 * A template factory method for creating an Intent for starting the item detail activity. Can be overridden to
	 * provide a custom implementation of the activity.
	 * @return A new intent for starting the item detail activity.
	 */
	protected Intent getDetailActivityIntent()
	{
		return new Intent(this, TwoPaneDetailActivity.class);
	}

	// endregion
}
