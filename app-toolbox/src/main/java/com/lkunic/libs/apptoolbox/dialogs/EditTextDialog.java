/**
 * Copyright (c) Luka Kunic 2015 / "EditTextDialog.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 08/04/2015.
 */
package com.lkunic.libs.apptoolbox.dialogs;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lkunic.libs.apptoolbox.R;

/**
 * Dialog implementation featuring a simple EditText with done/cancel actions. Allows setting a custom input method
 * for the EditText. Returns a string result.
 */
public class EditTextDialog extends BaseDialog<String>
{
	// Argument constants
	private static final String ARG_HINT = "hint";
	private static final String ARG_VALUE = "value";
	private static final String ARG_INPUT_TYPE = "input_type";

	// Private variables
	private String mHint;
	private String mValue = "";
	private int mInputType;

	public EditTextDialog()
	{
		// Required empty constructor
	}

	/**
	 * Factory method for creating a new dialog instance.
	 * @param value     Initial value that will be displayed in the dialog.
	 * @param hint      Hint text for the EditText view.
	 * @param inputType Input type id for the dialog EditText view.
	 * @return A new EditTextDialog instance.
	 */
	public static EditTextDialog newInstance(String value, String hint, int inputType)
	{
		EditTextDialog dialog = new EditTextDialog();

		Bundle args = new Bundle();
		args.putString(ARG_HINT, hint);
		args.putString(ARG_VALUE, value);
		args.putInt(ARG_INPUT_TYPE, inputType);

		dialog.setArguments(args);

		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Get the argument bundle from the saved instance state or the fragment arguments
		Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();

		if (args != null)
		{
			// Fetch the values from the bundle
			mHint = args.getString(ARG_HINT, "");
			mValue = args.getString(ARG_VALUE, "");
			mInputType = args.getInt(ARG_INPUT_TYPE, InputType.TYPE_CLASS_TEXT);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle)
	{
		super.onSaveInstanceState(bundle);

		// Save the values to the bundle
		bundle.putString(ARG_HINT, mHint);
		bundle.putString(ARG_VALUE, mValue);
		bundle.putInt(ARG_INPUT_TYPE, mInputType);
	}

	// region Abstract method implementations

	@Override
	protected int getViewResource()
	{
		return R.layout.dialog_edit_text;
	}

	@Override
	protected void setupContent(View view)
	{
		// Result edit text
		final EditText etResult = (EditText) view.findViewById(R.id.et_result);
		etResult.append(mValue);
		etResult.setHint(mHint);
		etResult.setInputType(mInputType);

		// Cancel button
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cancelDialog();
			}
		});

		// Done button
		Button btnDone = (Button) view.findViewById(R.id.btn_done);
		btnDone.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				notifyResultPositive(etResult.getText().toString());
			}
		});
	}

	// endregion
}
