/**
 * Copyright (c) Luka Kunic 2015 / "IQueryable.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 22/04/2015.
 */
package com.lkunic.libs.apptoolbox.database;

import android.content.ContentValues;
import android.net.Uri;

/**
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
