package com.lkunic.libs.apptoolbox.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Copyright (c) Luka Kunic 2015 / "BaseDialog.java"
 * Created by lkunic on 08/04/2015.
 *
 * Generic dialog base. Provides dialog logic and abstracts access to the dialog result listener.
 * The generic parameter T is used to specify the type of the dialog result.
 */
public abstract class BaseDialog<T> extends DialogFragment
{
	// Tag that is assigned to the dialog when adding it with a fragment manager
	private static final String DIALOG_TAG = "dialog";
	private static final String ARG_TITLE = "title";

	// Dialog title
	private String mTitle;

	// Dialog result that is returned when a complete action is triggered
	private T mResult;

	// Listener object used to notify about the dialog results
	private OnDialogResultListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(ARG_TITLE))
		{
			mTitle = savedInstanceState.getString(ARG_TITLE);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle)
	{
		super.onSaveInstanceState(bundle);

		bundle.putString(ARG_TITLE, mTitle);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Set the dialog title
		getDialog().setTitle(mTitle);

		// Setup the dialog layout and content
		View view = inflater.inflate(getViewResource(), container, false);
		setupContent(view);

		return view;
	}

	/**
	 * Display the dialog using the given FragmentManager.
	 * @param fm FragmentManager object to use for displaying the dialog.
	 */
	public void display(FragmentManager fm)
	{
		// Remove any existing dialog
		Fragment prev = fm.findFragmentByTag(DIALOG_TAG);
		if (prev != null)
		{
			fm.beginTransaction().remove(prev).commit();
		}

		show(fm, DIALOG_TAG);
	}

	// region Public access methods

	/**
	 * Set the listener object used to notify about the dialog results.
	 * @param listener OnDialogResultListener instance.
	 */
	public void setDialogResultListener(OnDialogResultListener listener)
	{
		mListener = listener;
	}

	/**
	 * Set the dialog title.
	 * @param title Title for the dialog.
	 */
	public void setDialogTitle(String title)
	{
		mTitle = title;
	}

	/**
	 * Returns the dialog result, or null if the result wasn't set.
	 */
	public T getDialogResult()
	{
		return mResult;
	}

	// endregion

	// region Listener notification methods

	/**
	 * Notify the calling activity that the dialog has been cancelled.
	 */
	protected void cancelDialog()
	{
		if (mListener != null)
		{
			// Notify the listener
			mListener.onDialogCancelled(this);
		}
	}

	/**
	 * Notify the calling activity that the result was positive.
	 * @param result Result of the dialog.
	 */
	protected void notifyResultPositive(T result)
	{
		mResult = result;

		if (mListener != null)
		{
			// Notify the listener
			mListener.onDialogResultPositive(this);
		}
	}

	/**
	 * Notify the calling activity that the result was negative.
	 */
	protected void notifyResultNegative()
	{
		if (mListener != null)
		{
			// Notify the listener
			mListener.onDialogResultNegative(this);
		}
	}

	// endregion

	// region Abstract methods

	/**
	 * Implement this method to provide a layout resource to the fragment.
	 * @return Layout resource id.
	 */
	protected abstract int getViewResource();

	/**
	 * Setup the content of the dialog views.
	 * @param view Parent view for the dialog fragment layout.
	 */
	protected abstract void setupContent(View view);

	// endregion
}
