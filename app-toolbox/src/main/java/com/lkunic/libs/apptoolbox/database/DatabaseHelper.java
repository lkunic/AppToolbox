/**
 * Copyright (c) Luka Kunic 2015 / "DatabaseHelper.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 08/04/2015.
 */
package com.lkunic.libs.apptoolbox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
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
