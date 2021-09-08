package org.beatonma.orbitalslivewallpaper.old.dream;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.dreams.DreamService;
import android.view.View;

@TargetApi(19)
public class DayDreamService extends DreamService {
    DreamView dream = null;
    SharedPreferences prefs = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        setInteractive(true);

        dream = new DreamView(getBaseContext(), null);
        dream.dayDreamService = this;

        prefs = getSharedPreferences(DreamView.SHARED_PREFS_NAME, 0);
        prefs.registerOnSharedPreferenceChangeListener(dream);
        dream.loadPreferences(prefs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dream.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        setContentView(dream);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        // TODO check if settings app is running and animate if so
        dream.stop();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        prefs.unregisterOnSharedPreferenceChangeListener(dream);
    }
}
