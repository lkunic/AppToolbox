/**
 * Copyright (c) Luka Kunic 2015 / "DbContentProvider.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 23/04/2015.
 */
package com.lkunic.libs.apptoolbox.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.lkunic.libs.apptoolbox.R;
import com.lkunic.libs.apptoolbox.exceptions.DatabaseProviderException;

/**
 * Provides common functionality for creating an application content provider.
 */
public abstract class DbContentProvider extends ContentProvider
{
	// Used to store the database item types (representing the database tables)
	private IQueryable[] mDatabaseItemTypes;

	// The helper object used for executing database operations
	private DatabaseHelper dbHelper;

	// The helper object used for determining the given uri type
	private static UriMatcher sUriMatcher = new UriMatcher((UriMatcher.NO_MATCH));

	@Override
	public boolean onCreate()
	{
		mDatabaseItemTypes = getDatabaseItemTypes();

		if (mDatabaseItemTypes == null || mDatabaseItemTypes.length == 0)
		{
			throw new DatabaseProviderException("Could not create the content provider. No item types set.");
		}

		for (int i = 0, n = mDatabaseItemTypes.length; i < n; i++)
		{
			sUriMatcher.addURI(getAuthority(), mDatabaseItemTypes[i].getUriPath(), i * 10);
			sUriMatcher.addURI(getAuthority(), mDatabaseItemTypes[i].getUriPath() + "/#", i * 10 + 1);
		}

		// Create the database helper object
		dbHelper = getDatabaseHelper();

		return true;
	}

	/**
	 * Creates a new DatabaseHelper object that is used for executing database operations.
	 */
	private DatabaseHelper getDatabaseHelper()
	{
		DatabaseTable[] tables = new DatabaseTable[mDatabaseItemTypes.length];

		for (int i = 0, n = mDatabaseItemTypes.length; i < n; i++)
		{
			tables[i] = mDatabaseItemTypes[i].getDatabaseTable();
		}

		return new DatabaseHelper(getContext(), getDatabaseName(), getDatabaseVersion(), tables);
	}

	@Override
	public String getType(@NonNull Uri uri)
	{
		int uriType = sUriMatcher.match(uri);

		if (uriType % 10 == 0)
		{
			return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
					getContext().getResources().getString(R.string.app_name);
		}
		else if (uriType % 10 == 1)
		{
			return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
					getContext().getResources().getString(R.string.app_name);
		}
		else
		{
			throw new DatabaseProviderException("Invalid uri: " + uri.toString());
		}
	}

	// region Database access methods

	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		// Get the uri type
		int uriType = sUriMatcher.match(uri);
		if (uriType == -1)
		{
			// The given uri can't be matched
			throw new DatabaseProviderException("Invalid uri: " + uri.toString());
		}
		
		int uriIndex = uriType / 10;
		
		// Set the table based on the uri type
		builder.setTables(mDatabaseItemTypes[uriIndex].getDatabaseTable().getTableName());
		
		if (uriType % 10 == 1)
		{
			// Set the item id in the selection clause
			builder.appendWhere("_id = " + uri.getLastPathSegment());
		}
		
		// Query the database with the built query
		Cursor cursor = builder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values)
	{
		if (values == null)
		{
			// No values to insert
			throw new DatabaseProviderException("Insertion values can not be null");
		}
		
		// Get the uri type
		int uriType = sUriMatcher.match(uri);
		if (uriType == -1)
		{
			// The given uri can't be matched
			throw new DatabaseProviderException("Invalid uri: " + uri.toString());
		}
		
		int uriIndex = uriType / 10;
		
		// Set the table based on the uri type
		String tableName = mDatabaseItemTypes[uriIndex].getDatabaseTable().getTableName();
		
		try
		{
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			// Try to insert the values into the table
			long newId = db.insertOrThrow(tableName, null, values);

			// The values were successfully added, build the result Uri
			Uri resultUri = ContentUris.withAppendedId(uri, newId);

			// Notify the content resolver about the change (automatically updates active cursors)
			getContext().getContentResolver().notifyChange(uri, null);

			db.close();
			return resultUri;
		}
		catch (SQLException e)
		{
			// There was a problem while inserting the values
			throw new DatabaseProviderException("Values not inserted. See inner exception for details.", e);
		}
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		if (values == null)
		{
			throw new DatabaseProviderException("Update values can not be null");
		}

		// Get the uri type
		int uriType = sUriMatcher.match(uri);
		if (uriType == -1)
		{
			// The given uri can't be matched
			throw new DatabaseProviderException("Invalid uri: " + uri.toString());
		}

		int uriIndex = uriType / 10;

		// Set the table based on the uri type
		String tableName = mDatabaseItemTypes[uriIndex].getDatabaseTable().getTableName();

		if (uriType % 10 == 1)
		{
			// Set the item id in the selection clause
			selection = "_id = " + uri.getLastPathSegment();
		}

		try
		{
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			// Try to update the selected values in the database
			int affectedRows = db.update(tableName, values, selection, selectionArgs);

			if (affectedRows != 0)
			{
				// Notify the content resolver about the change (automatically updates active cursors)
				getContext().getContentResolver().notifyChange(uri, null);
			}

			db.close();
			return affectedRows;
		}
		catch (Exception e)
		{
			// There was a problem while updating the values
			throw new DatabaseProviderException("Values not updated. See inner exception for details", e);
		}
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs)
	{
		// Get the uri type
		int uriType = sUriMatcher.match(uri);
		if (uriType == -1)
		{
			// The given uri can't be matched
			throw new DatabaseProviderException("Invalid uri: " + uri.toString());
		}

		int uriIndex = uriType / 10;

		// Set the table based on the uri type
		String tableName = mDatabaseItemTypes[uriIndex].getDatabaseTable().getTableName();

		if (uriType % 10 == 1)
		{
			// Set the item id in the selection clause
			selection = "_id = " + uri.getLastPathSegment();
		}

		try
		{
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			// Try to delete the selected entries from the database
			int affectedRows = db.delete(tableName, selection, null);

			if (affectedRows != 0)
			{
				// Notify the content resolver about the change (automatically updates active cursors)
				getContext().getContentResolver().notifyChange(uri, null);
			}

			db.close();
			return affectedRows;
		}
		catch (Exception e)
		{
			// There was a problem while deleting the entries
			throw new DatabaseProviderException("Entries not deleted. See inner exception for details", e);
		}
	}

	// endregion

	// region Abstract methods

	/**
	 * Implement to provide a list of data types that can be added to the database (database tables).
	 */
	protected abstract IQueryable[] getDatabaseItemTypes();

	/**
	 * The unique authority string for this content provider.
	 */
	public abstract String getAuthority();

	/**
	 * Name of the database.
	 */
	public abstract String getDatabaseName();

	/**
	 * Version number of the database.
	 */
	public abstract int getDatabaseVersion();

	// endregion
}
