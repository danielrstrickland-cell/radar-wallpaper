package com.danielstrickland.radarwallpaper;

import android.content.Context;
import android.content.SharedPreferences;

final class Prefs {
    static final String DEFAULT_URL = "https://danielrstrickland-cell.github.io/radar-wallpaper/";
    static SharedPreferences get(Context c) { return c.getSharedPreferences("radar", Context.MODE_PRIVATE); }
    static String url(Context c) { return get(c).getString("url", DEFAULT_URL); }
    static void setUrl(Context c, String u) { get(c).edit().putString("url", u.isEmpty() ? DEFAULT_URL : u).apply(); }
}
