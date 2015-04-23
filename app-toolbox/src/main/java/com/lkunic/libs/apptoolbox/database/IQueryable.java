package com.lkunic.libs.apptoolbox.database;

import android.content.ContentValues;
import android.net.Uri;

/**
 * Copyright (c) Luka Kunic 2015 / "IQueryable.java"
 * Created by lkunic on 22/04/2015.
 *
 * Interface that should be implemented by data types that can be queried in the database.
 */
public interface IQueryable
{
	/**
	 * Returns content values for this item instance.
	 */
	ContentValues getContentValues();

	/**
	 * Returns the collection level uri (only contains the main part of the uri for this item type).
	 */
	Uri getCollectionUri();

	/**
	 * Returns the item level uri (contains the item id appended to the end of the uri).
	 */
	Uri getItemUri();

	/**
	 * Returns a path for this item type that will be included in the uri.
	 */
	String getUriPath();

	/**
	 * Returns the database table object representing this IQueryable.
	 */
	DatabaseTable getDatabaseTable();
}
