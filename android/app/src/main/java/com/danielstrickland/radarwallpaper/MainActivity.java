package com.danielstrickland.radarwallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(pad, pad, pad, pad);
        root.setBackgroundColor(Color.rgb(14, 17, 22));

        TextView title = new TextView(this);
        title.setText("Live Radar Wallpaper");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        root.addView(title);

        TextView hint = new TextView(this);
        hint.setText("Page to show (leave as is unless you moved it):");
        hint.setTextColor(Color.rgb(180, 178, 169));
        hint.setPadding(0, pad, 0, pad / 3);
        root.addView(hint);

        EditText url = new EditText(this);
        url.setText(Prefs.url(this));
        url.setTextColor(Color.WHITE);
        url.setSingleLine(true);
        root.addView(url, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button set = new Button(this);
        set.setText("Set as wallpaper");
        set.setOnClickListener(v -> {
            Prefs.setUrl(this, url.getText().toString().trim());
            try {
                Intent i = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(this, RadarWallpaperService.class));
                startActivity(i);
            } catch (Exception e) {
                startActivity(new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = pad;
        root.addView(set, lp);
        setContentView(root);
    }
}
