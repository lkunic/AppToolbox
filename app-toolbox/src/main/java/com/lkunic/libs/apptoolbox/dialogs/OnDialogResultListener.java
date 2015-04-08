package com.lkunic.libs.apptoolbox.dialogs;

/**
 * Copyright (c) Luka Kunic 2015 / "OnDialogResultListener.java"
 * Created by lkunic on 08/04/2015.
 *
 * Listener interface used for passing dialog results from the dialog to the calling activity.
 */
public interface OnDialogResultListener
{
	/**
	 * Dialog has been cancelled, no action should be performed except closing the dialog.
	 * @param dialog The dialog that triggered the event.
	 */
	void onDialogCancelled(BaseDialog<?> dialog);

	/**
	 * Dialog result has been positive and a result has been set (e.g. save or update action)
	 * @param dialog The dialog that triggered the event.
	 */
	void onDialogResultPositive(BaseDialog<?> dialog);

	/**
	 * Dialog result has been negative (e.g. delete action)
	 * @param dialog The dialog that triggered the event.
	 */
	void onDialogResultNegative(BaseDialog<?> dialog);
}
