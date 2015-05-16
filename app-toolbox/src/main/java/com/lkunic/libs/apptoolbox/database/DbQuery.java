package com.lkunic.libs.apptoolbox.database;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * Copyright (c) Luka Kunic 2015 / "DbQuery.java"
 * Created by lkunic on 22/04/2015.
 *
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
