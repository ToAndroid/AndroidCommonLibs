//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PreferencesUtils {
    private static final String DEFAULT_SPNAME = "prefrence.sp";
    private static final String DEFAULT_STR_VALUE = "";
    private static final int DEFAULT_INT_VALUE = -1;

    public PreferencesUtils() {
    }

    public static boolean putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("prefrence.sp", 0);
        return sp.edit().putString(key, value).commit();
    }

    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("prefrence.sp", 0);
        return sp.edit().putInt(key, value).commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("prefrence.sp", 0);
        return sp.getString(key, "");
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("prefrence.sp", 0);
        return sp.getInt(key, -1);
    }

    public static boolean putString(Context context, String spName, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        return sp.edit().putString(key, value).commit();
    }

    public static boolean putInt(Context context, String spName, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        return sp.edit().putInt(key, value).commit();
    }

    public static String getString(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        return sp.getString(key, "");
    }

    public static int getInt(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        return sp.getInt(key, -1);
    }

    public static boolean putMap(Context context, String spName, Map<String, Object> map) {
        if(map != null && map.size() > 0) {
            SharedPreferences sp = context.getSharedPreferences(spName, 0);
            Editor edit = sp.edit();
            Iterator i$ = map.entrySet().iterator();

            while(i$.hasNext()) {
                Entry entry = (Entry)i$.next();
                if(entry.getValue() instanceof Integer) {
                    edit.putInt((String)entry.getKey(), ((Integer)entry.getValue()).intValue());
                } else {
                    edit.putString((String)entry.getKey(), (String)entry.getValue());
                }
            }

            return edit.commit();
        } else {
            return false;
        }
    }
}
