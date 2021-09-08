package org.beatonma.orbitalslivewallpaper.old.dream;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.beatonma.orbitalslivewallpaper.R;

/**
 * Created by Michael on 19/01/2015.
 */
public class DreamPrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DreamPrefsFragment", "Loading dream preferences.");
        addPreferencesFromResource(R.xml.dream_preferences_test);
    }
}
