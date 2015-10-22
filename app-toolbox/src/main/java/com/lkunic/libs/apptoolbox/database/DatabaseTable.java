/**
 * Copyright (c) Luka Kunic 2015 / "DatabaseTable.java"
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

import android.database.sqlite.SQLiteDatabase;

/**
 * Template class for creating a database table model.
 */
public abstract class DatabaseTable
{
	/**
	 * Creates a new table using the provided database instance.
	 * @param db Database instance to use when creating the table
	 */
	public void createTable(SQLiteDatabase db)
	{
		db.execSQL(getSqlCreateStatement());
	}

	/**
	 * Drops the table using the provided database instance.
	 * @param db Database instance to use when destroying the table
	 */
	public void dropTable(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + getTableName());
	}

	/**
	 * Returns the name of this table
	 */
	protected abstract String getTableName();

	/**
	 * Returns the create statement in SQL for this table.
	 */
	protected abstract String getSqlCreateStatement();
}
