/**
 * Copyright (c) Luka Kunic 2015 / "FontTextView.java"
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software to deal in the software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, provided that the licence notice is included
 * in all copies or substantial portions of the software.
 *
 * Created by lkunic on 08/04/2015.
 */
package com.lkunic.libs.apptoolbox.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lkunic.libs.apptoolbox.R;

import java.util.HashMap;

/**
 * Text view that allows setting the font in XML or in code. The TTF file for the font must be in the assets directory.
 */
public class FontTextView extends TextView
{
	// Hash table containing typefaces that have already been loaded
	private static HashMap<String, Typeface> fontDictionary = new HashMap<>();

	public FontTextView(Context context)
	{
		super(context);
	}

	public FontTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		// Don't try to change fonts in the developer layout editor
		if (!isInEditMode())
		{
			// Read the attributes and change the font if needed
			setFontFromAttrs(context, attrs);
		}
	}

	/**
	 * Sets the font of the text view using the given font file name.
	 * @param fontName Name of the file in the assets directory that contains the font, with extension.
	 */
	public void setFont(String fontName)
	{
		if (!fontDictionary.containsKey(fontName))
		{
			// The font hasn't been loaded yet, fetch it and add it to the dictionary
			fetchFontFromAssets(fontName);
		}

		setTypeface(fontDictionary.get(fontName));
	}

	//region Private support methods

	/**
	 * Parses attributes of the view from the XML layout and sets the font if the font name is provided.
	 * @param context Application context.
	 * @param attrs   Attributes for this view set in the XML layout.
	 */
	private void setFontFromAttrs(Context context, AttributeSet attrs)
	{
		// Get the selected font from the xml attributes
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.FontTextView);
		String fontName = styledAttrs.getString(R.styleable.FontTextView_font);
		styledAttrs.recycle();

		if (fontName != null)
		{
			// Font name is provided, change the font of this text view
			setFont(fontName);
		}
	}

	/**
	 * Gets the font from the assets folder and adds it to the dictionary.
	 * @param fontName Name of the font to fetch.
	 */
	private void fetchFontFromAssets(String fontName)
	{
		Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), String.format("fonts/%s", fontName));

		if (typeface == null)
		{
			// File with the given name could not be found, throw exception since this must be fixed
			throw new IllegalArgumentException(
					String.format("Font file %s could not be found in the assets directory", fontName));
		}

		fontDictionary.put(fontName, typeface);
	}

	// endregion
}
