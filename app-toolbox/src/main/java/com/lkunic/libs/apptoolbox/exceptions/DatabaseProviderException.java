/**
 * Copyright (c) Luka Kunic 2015 / "DatabaseProviderException.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 23/04/2015.
 */
package com.lkunic.libs.apptoolbox.exceptions;

/**
 * Custom database exception.
 */
public class DatabaseProviderException extends RuntimeException
{
	public DatabaseProviderException(String detailMessage)
	{
		// Base implementation good enough
		super(detailMessage);
	}

	public DatabaseProviderException(String detailMessage, Throwable throwable)
	{
		// Base implementation good enough
		super(detailMessage, throwable);
	}
}
