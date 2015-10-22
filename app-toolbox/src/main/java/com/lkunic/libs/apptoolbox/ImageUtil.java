/**
 * Copyright (c) Luka Kunic 2015 / "ImageUtil.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 30/05/2015.
 */
package com.lkunic.libs.apptoolbox;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: class description
 */
public class ImageUtil
{
	public static File createImageFile(String dir, String filename) throws IOException
	{
		if (filename == null)
		{
			filename = String.format("IMG%s", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		}

		File pictureStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File storageDir = new File(pictureStorage, dir);

		if (storageDir.mkdir() || storageDir.isDirectory())
		{
			return File.createTempFile(filename, ".jpg", storageDir);
		}

		return null;
	}

	public static Byte[] imageToBytes(Bitmap image)
	{
		return null;
	}

	public static Bitmap imageFromBytes(Byte[] imageBytes)
	{
		return null;
	}
}
