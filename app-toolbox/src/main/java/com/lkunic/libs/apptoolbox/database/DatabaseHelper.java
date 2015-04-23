package com.lkunic.libs.apptoolbox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright (c) Luka Kunic 2015 / "DatabaseHelper.java"
 * Created by lkunic on 08/04/2015.
 *
 * Helper class used for managing the database lifecycle.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	// List of tables to be created in the database
	private DatabaseTable[] mTables;

	public DatabaseHelper(Context context, String name, int version, DatabaseTable[] tables)
	{
		super(context, name, null, version);

		if (tables == null)
		{
			throw new IllegalArgumentException(String.format("%s - Database table array can not be null", getClass()
					.getName()));
		}

		mTables = tables;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		for (DatabaseTable table : mTables)
		{
			if (table != null)
			{
				table.createTable(db);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		for (DatabaseTable table : mTables)
		{
			if (table != null)
			{
				table.dropTable(db);
			}
		}

		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);

		// Enable foreign key support
		db.execSQL("PRAGMA foreign_keys=ON;");
	}
}
