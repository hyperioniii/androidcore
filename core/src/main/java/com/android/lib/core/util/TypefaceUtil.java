
package com.android.lib.core.util;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class TypefaceUtil {
    /**
     * Using reflection to override default typeface NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT
     * TYPEFACE WHICH WILL BE OVERRIDDEN
     * 
     * @param context
     *            to work with assets
     * @param defaultFontName
     *            for example "monospace"
     * @param fontFile
     *            file name of the font from assets
     */
    public static void overrideCustomFont(Context context, String defaultFontName, String fontFile) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), fontFile);
            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontName);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            DebugLog.e("Can not set custom font " + fontFile + " instead of " + defaultFontName);
        }
    }
}
