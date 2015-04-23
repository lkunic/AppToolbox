package com.lkunic.libs.apptoolbox.exceptions;

/**
 * Copyright (c) Luka Kunic 2015 / "DatabaseProviderException.java"
 * Created by lkunic on 23/04/2015.
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
