/**
 * Copyright (c) Luka Kunic 2015 / "DbQuery.java"
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
import android.database.Cursor;
import android.net.Uri;

/**
 * A query that can be executed against a database.
 */
public class DbQuery
{
	private String[] mProjection;
	private String mSelection;
	private String mOrder;

	private Uri mUri;
	private ContentResolver mResolver;

	public static DbQuery create(ContentResolver resolver, Uri uri)
	{
		DbQuery query = new DbQuery();

		query.mResolver = resolver;
		query.mUri = uri;
		
		return query;
	}

	public Cursor execute()
	{
		return mResolver.query(mUri, mProjection, mSelection, null, mOrder);
	}

	public DbQuery withColumns(String... columns)
	{
		this.mProjection = columns;
		return this;
	}

	public DbQuery select(String selection)
	{
		this.mSelection = selection;
		return this;
	}

	public DbQuery orderBy(String order)
	{
		this.mOrder = order;
		return this;
	}
}
