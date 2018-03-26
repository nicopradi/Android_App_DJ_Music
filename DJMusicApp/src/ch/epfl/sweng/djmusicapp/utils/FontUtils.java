package ch.epfl.sweng.djmusicapp.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Utility class to manage fonts. Avoids loading font at every call by using
 * static calls. Use: simply call the static method with the font's name.
 */
public class FontUtils {

    private static Typeface mRobotoLight;
    private static Typeface mRobotoRegular;

    public static Typeface robotoLight(Context context) {

        if (mRobotoLight == null) {
            mRobotoLight = Typeface.createFromAsset(context.getAssets(),
                    "Roboto-Light.ttf");
        }

        return mRobotoLight;
    }

    public static Typeface robotoRegular(Context context) {

        if (mRobotoRegular == null) {
            mRobotoRegular = Typeface.createFromAsset(context.getAssets(),
                    "Roboto-Regular.ttf");
        }

        return mRobotoRegular;
    }

}