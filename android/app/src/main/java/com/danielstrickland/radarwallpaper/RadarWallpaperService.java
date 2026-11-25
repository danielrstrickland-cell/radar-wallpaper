package com.danielstrickland.radarwallpaper;

import android.app.Presentation;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** Renders the radar web page straight onto the wallpaper surface via a private virtual display. */
public class RadarWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() { return new RadarEngine(); }

    class RadarEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
        private VirtualDisplay display;
        private Presentation presentation;
        private WebView web;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            Prefs.get(RadarWallpaperService.this).registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            super.onSurfaceChanged(holder, format, w, h);
            int dpi = getResources().getDisplayMetrics().densityDpi;
            if (display == null) {
                DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
                display = dm.createVirtualDisplay("RadarWallpaper", w, h, dpi, holder.getSurface(), 0);
                presentation = new Presentation(RadarWallpaperService.this, display.getDisplay());
                web = new WebView(presentation.getContext());
                web.setBackgroundColor(Color.rgb(7, 9, 13));
                WebSettings s = web.getSettings();
                s.setJavaScriptEnabled(true);
                s.setDomStorageEnabled(true);
                s.setMediaPlaybackRequiresUserGesture(false);
                s.setCacheMode(WebSettings.LOAD_DEFAULT);
                web.setWebViewClient(new WebViewClient());
                presentation.setContentView(web);
                presentation.show();
                web.loadUrl(Prefs.url(RadarWallpaperService.this));
            } else {
                display.setSurface(holder.getSurface());
                display.resize(w, h, dpi);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (web == null) return;
            if (visible) { web.onResume(); web.resumeTimers(); }
            else { web.onPause(); web.pauseTimers(); }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            teardown();
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onDestroy() {
            Prefs.get(RadarWallpaperService.this).unregisterOnSharedPreferenceChangeListener(this);
            teardown();
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences p, String key) {
            if (web != null) web.loadUrl(Prefs.url(RadarWallpaperService.this));
        }

        private void teardown() {
            if (web != null) { web.destroy(); web = null; }
            if (presentation != null) { presentation.dismiss(); presentation = null; }
            if (display != null) { display.release(); display = null; }
        }
    }
}
