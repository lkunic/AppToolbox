package com.lkunic.libs.apptoolbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Luka Kunic 2015 / "AssetLoader.java"
 * Created by lkunic on 08/04/2015.
 *
 * Helper class used for loading data from assets.
 */
public class AssetLoader
{
	private static final int THUMBNAIL_SIZE = 1024;

	/**
	 * Loads given image asset, scaling the image down if it is too big to improve performance.
	 * @param context Application context
	 * @param path    Path in the assets folder of the image to load
	 * @return Loaded image bitmap
	 */
	public static Bitmap loadImageFromAssets(Context context, String path)
	{
		try
		{
			// Open the input stream to the image in assets
			InputStream is = context.getAssets().open(path);

			// Load the image dimensions first so that big images can be scaled down (improves memory usage)
			BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
			onlyBoundsOptions.inJustDecodeBounds = true;
			onlyBoundsOptions.inDither = true;
			BitmapFactory.decodeStream(is, null, onlyBoundsOptions);
			is.close();

			if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			{
				// There was an error while decoding
				return null;
			}

			// Find the bigger dimension (width, height)
			int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ?
							   onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

			// Calculate the sampling ratio for images that are bigger than the thumbnail size
			double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;
			int sampleSize = Integer.highestOneBit((int) Math.floor(ratio));

			// Load the image sampled using the calculated ratio
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inSampleSize = sampleSize;
			bitmapOptions.inDither = true;
			is = context.getAssets().open(path);
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, bitmapOptions);
			is.close();

			return bitmap;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * Loads lines of text from the given path in the assets directory.
	 * @param context Application context.
	 * @param path    Path in the assets folder to the text file to load.
	 * @return String array representing lines of text in the file.
	 */
	public static String[] loadTextFromAssets(Context context, String path)
	{
		try
		{
			// Open the input stream to the text in assets
			InputStream inputStream = context.getAssets().open(path);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			List<String> lines = new ArrayList<>();

			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				lines.add(line);
			}

			inputStream.close();

			return lines.toArray(new String[lines.size()]);
		}
		catch (IOException e)
		{
			return null;
		}
	}
}
