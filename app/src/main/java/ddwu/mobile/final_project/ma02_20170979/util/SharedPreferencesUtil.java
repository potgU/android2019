package ddwu.mobile.final_project.ma02_20170979.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPreferencesUtil {
    public static void edit(Activity activity, String key, String value) {
        SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void edit(Activity activity, String key, int value) {
        SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String get(Activity activity, String key, String defaultValue) {
        return activity.getPreferences(Context.MODE_PRIVATE).getString(key, defaultValue);
    }

    public static int get(Activity activity, String preferenceName, int defaultValue) {
        return activity.getPreferences(Context.MODE_PRIVATE).getInt(preferenceName, defaultValue);
    }

    public static Set<String> get(Activity activity, String preferenceName, Set<String> defaultValue) {
        return activity.getPreferences(Context.MODE_PRIVATE).getStringSet(preferenceName, defaultValue);
    }

    public static void delete(Activity activity, String preferenceName) {
        activity.getPreferences(Context.MODE_PRIVATE).edit().remove(preferenceName).apply();
    }
}
