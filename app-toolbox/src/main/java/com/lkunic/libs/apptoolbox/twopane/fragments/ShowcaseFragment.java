package com.lkunic.libs.apptoolbox.twopane.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lkunic.libs.apptoolbox.R;
import com.lkunic.libs.apptoolbox.views.PagerTabControl;

import java.util.List;

/**
 * Copyright (c) Luka Kunic 2015 / "ShowcaseFragment.java"
 * Created by lkunic on 08/04/2015.
 *
 * Extension of the {@link ItemDetailFragment} that displays a header image with a title overlay and a ViewPager
 * with tab controls.
 */
public abstract class ShowcaseFragment extends ItemDetailFragment
{
	private ViewHolder viewHolder;

	// region Private support methods
	/**
	 * Listens for clicks of the pager buttons.
	 */
	private View.OnClickListener showcaseButtonClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			viewHolder.viewPager.setCurrentItem((int) v.getTag(), true);
		}
	};
	/**
	 * Listens for page changes in the view pager.
	 */
	private ViewPager.OnPageChangeListener showcasePageChangeListener = new ViewPager.OnPageChangeListener()
	{

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{
		}

		@Override
		public void onPageSelected(int position)
		{
			selectPageButton(position);
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{
		}
	};

	// endregion

	// region Listeners

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_showcase, container, false);

		setupContent(view);

		return view;
	}

	/**
	 * Sets up the fragment and it's views.
	 * @param view The layout view.
	 */
	@Override
	protected void setupContent(View view)
	{
		// Make sure item data is available before setting up content.
		getItemData();

		// Populate the view holder if needed
		if (viewHolder == null)
		{
			viewHolder = new ViewHolder();

			viewHolder.showcaseItemImage = (ImageView) view.findViewById(R.id.item_image);
			viewHolder.showcaseItemTitle = (TextView) view.findViewById(R.id.item_title);
			viewHolder.viewPager = (ViewPager) view.findViewById(R.id.pager);
			viewHolder.pagerButtonBar = (LinearLayout) view.findViewById(R.id.pager_button_bar);
		}

		// Set the item image
		viewHolder.showcaseItemImage.setImageBitmap(getShowcaseImage());

		// Set the item title
		viewHolder.showcaseItemTitle.setText(getShowcaseItemTitle());

		// Set up the view pager
		ShowcaseInfoFragment[] infoFragments = getInfoFragments();
		if (viewHolder.viewPager.getAdapter() == null)
		{
			// The ViewPager doesn't have an adapter yet, create it and populate with info fragments
			viewHolder.viewPager.setAdapter(new ShowcasePagerAdapter(getChildFragmentManager(), infoFragments));
			viewHolder.viewPager.setOnPageChangeListener(showcasePageChangeListener);
		}
		else
		{
			// The ViewPager already has an adapter, populate it with new info fragments
			ShowcasePagerAdapter adapter = (ShowcasePagerAdapter) viewHolder.viewPager.getAdapter();
			adapter.setPagerItems(infoFragments);
			adapter.notifyDataSetChanged();
		}

		// Build the button bar for controlling the view pager
		PagerTabControl pagerTabControl;
		LinearLayout showcaseButtonContainer;
		LinearLayout.LayoutParams showcaseButtonParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

		// Remove all buttons that could have been populated by previous item
		viewHolder.pagerButtonBar.removeAllViews();

		for (int i = 0, n = infoFragments.length; i < n; i++)
		{
			// Create a new showcase button and populate it with data from corresponding fragment
			pagerTabControl = new PagerTabControl(getActivity(), infoFragments[i].getPrimaryText(),
					infoFragments[i].getSecondaryText());

			// Setup the showcase view so that it can be used as a tab button
			showcaseButtonContainer = (LinearLayout) pagerTabControl.findViewById(R.id.container);
			showcaseButtonContainer.setTag(i);
			showcaseButtonContainer.setOnClickListener(showcaseButtonClickListener);
			showcaseButtonContainer.setBackgroundResource(
					i % 2 == 0 ? R.drawable.pager_tab_control_background_1
							   : R.drawable.pager_tab_control_background_2);

			// Add the button to the button bar
			pagerTabControl.setLayoutParams(showcaseButtonParams);
			viewHolder.pagerButtonBar.addView(pagerTabControl);
		}

		viewHolder.viewPager.setCurrentItem(0, true);
		selectPageButton(0);
	}

	// endregion

	// region Abstract methods

	/**
	 * Selects the current page of the view pager.
	 * @param pageNumber Index of the page to select.
	 */
	private void selectPageButton(int pageNumber)
	{
		for (int i = 0, n = viewHolder.pagerButtonBar.getChildCount(); i < n; i++)
		{
			if (i == pageNumber)
			{
				viewHolder.pagerButtonBar.getChildAt(i).setSelected(true);
			}
			else
			{
				viewHolder.pagerButtonBar.getChildAt(i).setSelected(false);
			}
		}
	}

	/**
	 * Provides a list of fragments to be displayed in the view pager.
	 */
	protected abstract ShowcaseInfoFragment[] getInfoFragments();

	/**
	 * Provides the bitmap image to be displayed for the selected item.
	 */
	protected abstract Bitmap getShowcaseImage();

	// endregion

	// region View holder

	/**
	 * Provides the title for the selected item.
	 */
	protected abstract String getShowcaseItemTitle();

	private static class ViewHolder
	{
		public ImageView showcaseItemImage;
		public TextView showcaseItemTitle;
		public ViewPager viewPager;
		public LinearLayout pagerButtonBar;
	}

	// endregion

	// region Showcase pager adapter

	private static class ShowcasePagerAdapter extends FragmentPagerAdapter
	{
		private FragmentManager mFragmentManager;
		private ShowcaseInfoFragment[] mFragments;

		public ShowcasePagerAdapter(FragmentManager fm, ShowcaseInfoFragment[] fragments)
		{
			super(fm);

			mFragmentManager = fm;
			setPagerItems(fragments);
		}

		public void setPagerItems(ShowcaseInfoFragment[] fragments)
		{
			List<Fragment> activeFragments = mFragmentManager.getFragments();

			// The fragment manager stores fragment instances, which means it doesn't update the data in the fragment
			// If there are any active ShowcaseInfoFragments, iterate through them and remove them
			if (activeFragments != null && activeFragments.size() != 0)
			{
				for (Fragment activeFragment : activeFragments)
				{
					if (activeFragment instanceof ShowcaseInfoFragment)
					{
						mFragmentManager.beginTransaction().remove(activeFragment).commit();
					}
				}
			}

			mFragments = fragments;
		}

		@Override
		public Fragment getItem(int position)
		{
			return mFragments[position];
		}

		@Override
		public int getCount()
		{
			return mFragments.length;
		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}
	}

	// endregion
}
