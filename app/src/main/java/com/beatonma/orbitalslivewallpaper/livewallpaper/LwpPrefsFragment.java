package com.beatonma.orbitalslivewallpaper.livewallpaper;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.beatonma.orbitalslivewallpaper.R;

/**
 * Created by Michael on 19/01/2015.
 */
public class LwpPrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
