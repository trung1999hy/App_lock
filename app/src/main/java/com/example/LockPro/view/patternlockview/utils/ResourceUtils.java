
package com.example.LockPro.view.patternlockview.utils;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class ResourceUtils {

    private ResourceUtils() {
        throw new AssertionError("You can not instantiate this class. Use its static utility " +
                "methods instead");
    }

    /**
     * Get color from a resource id
     *
     * @param context  The context
     * @param colorRes The resource identifier of the color
     * @return The resolved color value
     */
    public static int getColor(@NonNull Context context, @ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    /**
     * Get string from a resource id
     *
     * @param context   The context
     * @param stringRes The resource identifier of the string
     * @return The string value
     */
    public static String getString(@NonNull Context context, @StringRes int stringRes) {
        return context.getString(stringRes);
    }

    /**
     * Get dimension in pixels from its resource id
     *
     * @param context  The context
     * @param dimenRes The resource identifier of the dimension
     * @return The dimension in pixels
     */
    public static float getDimensionInPx(@NonNull Context context, @DimenRes int dimenRes) {
        return context.getResources().getDimension(dimenRes);
    }
}
