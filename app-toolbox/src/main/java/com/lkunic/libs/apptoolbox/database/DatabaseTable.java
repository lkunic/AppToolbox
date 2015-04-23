package com.lkunic.libs.apptoolbox.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Copyright (c) Luka Kunic 2015 / "DatabaseTable.java"
 * Created by lkunic on 08/04/2015.
 *
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
