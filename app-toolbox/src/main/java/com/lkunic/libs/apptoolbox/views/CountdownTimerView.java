/**
 * Copyright (c) Luka Kunic 2015 / "CountdownTimerView.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 16/05/2015.
 */
package com.lkunic.libs.apptoolbox.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.lkunic.libs.apptoolbox.R;

/**
 * Text view that acts as a countdown timer. Provides api for starting, pausing and stopping the timer.
 * Also allows setting different text/background colors for when the timer is active/paused/stopped.
 */
public class CountdownTimerView extends FontTextView
{
	private int mInitialSeconds;
	private boolean mDisplaySecondsLabel;

	private int mSeconds;
	private OnCountdownCompletedListener mListener;
	private CountdownThread mCountdownThread;

	public CountdownTimerView(Context context)
	{
		super(context);
	}

	public CountdownTimerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		// Get the initial countdown value from the attributes
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CountdownTimerView);
		mInitialSeconds = styledAttrs.getInt(R.styleable.CountdownTimerView_initial_seconds, 0);
		mDisplaySecondsLabel = styledAttrs.getBoolean(R.styleable.CountdownTimerView_display_seconds_label, false);
		styledAttrs.recycle();

		updateTime(mInitialSeconds);
	}

	/**
	 * Set the seconds for this timer.
	 * @param seconds Number of seconds to start countdown from.
	 */
	public void setInitialSeconds(int seconds)
	{
		mInitialSeconds = seconds;
		updateTime(mInitialSeconds);
	}

	/**
	 * Whether to display a label for seconds after the number value (e.g. '15s' instead of '15').
	 * @param value Set to true if the seconds label should be displayed.
	 */
	public void setDisplaySecondsLabel(boolean value)
	{
		mDisplaySecondsLabel = value;
	}

	/**
	 * Set the listener that will be notified when countdown is finished.
	 * @param listener Listener object.
	 */
	public void setOnCountdownCompletedListener(OnCountdownCompletedListener listener)
	{
		mListener = listener;
	}

	/**
	 * Starts the countdown from the initial seconds value. The seconds value should be set to a value != 0.
	 */
	public void startCountdown()
	{
		if (mSeconds == 0)
		{
			completeCountdown();
		}

		mCountdownThread = (CountdownThread) new CountdownThread().execute(mSeconds);
	}

	/**
	 * Terminates the countdown if it is running.
	 */
	public void stopCountdown()
	{
		if (mCountdownThread != null && mCountdownThread.getStatus() == AsyncTask.Status.RUNNING)
		{
			mCountdownThread.cancel(true);
			completeCountdown();
		}
	}

	/**
	 * Pauses the countdown so that it may be resumed from the current time.
	 */
	public void pauseCountdown()
	{
		if (mCountdownThread != null && mCountdownThread.getStatus() == AsyncTask.Status.RUNNING)
		{
			mCountdownThread.cancel(true);
		}
	}

	/**
	 * Resets the timer back to the initial value. This will cancel the timer if it is running.
	 */
	public void resetTimer()
	{
		if (mCountdownThread != null && mCountdownThread.getStatus() == AsyncTask.Status.RUNNING)
		{
			mCountdownThread.cancel(true);
		}

		updateTime(mInitialSeconds);
	}

	/**
	 * Updates the current timer value and updates the text view to display current number of seconds on the timer.
	 * @param seconds Seconds to display.
	 */
	private void updateTime(int seconds)
	{
		mSeconds = seconds;
		setText(mDisplaySecondsLabel ? String.format("%ds", seconds) : String.valueOf(seconds));
	}

	/**
	 * Completes the countdown by displaying zero on the timer and notifies the listener about the completion.
	 */
	private void completeCountdown()
	{
		updateTime(0);

		if (mListener != null)
		{
			mListener.onCountdownCompleted();
		}
	}

	// region Countdown thread

	private class CountdownThread extends AsyncTask<Integer, Integer, Void>
	{
		@Override
		protected Void doInBackground(Integer... params)
		{
			for (int i = params[0]; i > 0; i--)
			{
				if (isCancelled())
				{
					break;
				}

				publishProgress(i);
				SystemClock.sleep(1000);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			updateTime(values[0]);
		}

		@Override
		protected void onPostExecute(Void result)
		{
			completeCountdown();
		}
	}

	// endregion

	// region Countdown event listener

	public interface OnCountdownCompletedListener
	{
		void onCountdownCompleted();
	}

	// endregion
}
