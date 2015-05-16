package com.lkunic.libs.apptoolbox.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lkunic.libs.apptoolbox.R;

/**
 * Copyright (c) Luka Kunic 2015 / "DeleteConfirmationDialog.java"
 * Created by lkunic on 10/05/2015.
 */
public class DeleteConfirmationDialog extends BaseDialog<Void>
{
	private static final String ARG_ITEM_NAME = "item_name";

	/**
	 * Factory method for creating a new instance of the DeleteConfigurationDialog.
	 * @param itemName Name of the item to be deleted.
	 */
	public static DeleteConfirmationDialog newInstance(String itemName)
	{
		DeleteConfirmationDialog dialog = new DeleteConfirmationDialog();

		Bundle args = new Bundle();
		args.putString(ARG_ITEM_NAME, itemName);
		dialog.setArguments(args);

		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		String itemName = getArguments().getString(ARG_ITEM_NAME);

		setDialogTitle(String.format(getResources().getString(R.string.dialog_title_format_delete), itemName));
	}

	public DeleteConfirmationDialog()
	{
		// Required empty constructor
	}

	/**
	 * Implement this method to provide a layout resource to the fragment.
	 * @return Layout resource id.
	 */
	@Override
	protected int getViewResource()
	{
		return R.layout.dialog_delete_confirmation;
	}

	/**
	 * Setup the content of the dialog views.
	 * @param view Parent view for the dialog fragment layout.
	 */
	@Override
	protected void setupContent(View view)
	{
		Button deleteButton = (Button) view.findViewById(R.id.btn_delete);
		deleteButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				notifyResultNegative();
			}
		});

		Button cancelButton = (Button) view.findViewById(R.id.btn_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cancelDialog();
			}
		});
	}
}
