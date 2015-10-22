/**
 * Copyright (c) Luka Kunic 2015 / "DbUtil.java"
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

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Utility class for executing database operations.
 */

public class DbUtil
{
	/**
	 * Attempts to insert the given IQueryable object into the database.
	 * @param resolver  Content resolver to use when accessing the database.
	 * @param queryable The IQueryable object to insert into the databse.
	 * @return The inserted item id if successful, -1 otherwise.
	 */
	public static long insert(ContentResolver resolver, IQueryable queryable)
	{
		// Insert the item into the database and get the result Uri
		Uri resultUri = resolver.insert(queryable.getCollectionUri(), queryable.getContentValues());

		if (resultUri != null)
		{
			return Long.parseLong(resultUri.getLastPathSegment());
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Attempts to update the given IQueryable object in the database. Uses the item id for selection.
	 * @param resolver  Content resolver to use when accessing the database.
	 * @param queryable The IQueryable object to update.
	 * @return Number of updated rows.
	 */
	public static int update(ContentResolver resolver, IQueryable queryable)
	{
		return resolver.update(queryable.getItemUri(), queryable.getContentValues(), null, null);
	}

	/**
	 * Attempts to delete the given IQueryable object from the database. Uses the item id for selection.
	 * @param resolver  Content resolver to use when accessing the database.
	 * @param queryable The IQueryable object to delete.
	 * @return Number of deleted rows.
	 */
	public static int delete(ContentResolver resolver, IQueryable queryable)
	{
		return resolver.delete(queryable.getItemUri(), null, null);
	}
}
