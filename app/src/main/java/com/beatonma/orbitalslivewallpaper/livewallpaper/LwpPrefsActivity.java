package com.beatonma.orbitalslivewallpaper.livewallpaper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.beatonma.orbitalslivewallpaper.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressWarnings("deprecation")
public class LwpPrefsActivity extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	
	private static final String TAG = "Orbitals PrefsActivity";
	
	CheckBoxPreference batteryColors = null;
	PreferenceScreen batteryColorsScreen = null;
	MultiSelectListPreference objectColors = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            setTheme(R.style.HoloLegacy);
        }
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(
				LwpService.SHARED_PREFS_NAME);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.preferences);
            batteryColors = (CheckBoxPreference) findPreference("pref_color_batterylevel");
            batteryColorsScreen = (PreferenceScreen) findPreference("pref_color_batterylevel_screen");
            objectColors = (MultiSelectListPreference) findPreference("pref_color_objectcolors");

            if (batteryColors.isChecked()) {
                Log.d(TAG, "Battery colors checked.");
                batteryColorsScreen.setEnabled(true);
                objectColors.setEnabled(false);
            }
            else {
                Log.d(TAG, "Battery colors not checked.");
                batteryColorsScreen.setEnabled(false);
                objectColors.setEnabled(true);
            }
		} else {
			addPreferencesFromResource(R.xml.preferences_gb);
		}
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
        if (batteryColors != null) {
            if (batteryColors.isChecked()) {
                Log.d(TAG, "Battery colors checked.");
                batteryColorsScreen.setEnabled(true);
                objectColors.setEnabled(false);
            } else {
                Log.d(TAG, "Battery colors not checked.");
                batteryColorsScreen.setEnabled(false);
                objectColors.setEnabled(true);
            }
        }
	}
	
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
    	super.onPreferenceTreeClick(preferenceScreen, preference);
    	if (preference!=null)
	    	if (preference instanceof PreferenceScreen)
	        	if (((PreferenceScreen)preference).getDialog()!=null)
	        		((PreferenceScreen)preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
    	return false;
    }
}