package com.beatonma.orbitalslivewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import net.simonvt.numberpicker.NumberPicker;

/**
 * Created by Michael on 21/01/2015.
 */
public class NiceNumberDialogPreference extends Preference {
    private final static String TAG = "NiceNumberDialogPreference";
    private final static String NUMS[] = { "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8",
            "0.9", "1.0", "2.0", "3.0", "4.0", "5.0", "6.0", "7.0", "8.0",
            "9.0", "10.0" };

    Context context;
    private int currentNumber = 0;

    public NiceNumberDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    public void onClick() {
        String title = (String) getTitle();
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(title)
                .customView(R.layout.nice_number_picker, wrapInScrollView)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //updatePreference(currentNumber);
                        int newValue = ((NumberPicker) dialog.findViewById(R.id.numberPicker)).getValue();
                        Log.d(TAG, "newValue=" + newValue);
                        updatePreference(newValue);
                    }
                })
                .btnSelector(R.drawable.dialog_button_selector)
                .positiveText("OK")
                .build();

        dialog.show();

        NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        initNumberPicker(np);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentNumber = newVal;
            }
        });
    }

    private void initNumberPicker(NumberPicker numberPicker) {
        int standard = 30;

        if (getKey().equals("pref_performance_framerate")) {
            numberPicker.setMaxValue(60);
            numberPicker.setMinValue(1);
        } else if (getKey().equals("pref_physics_gravity")) {
            numberPicker.setDisplayedValues(NUMS);
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(19);
            standard = 10;
        } else if (getKey().equals("pref_color_alpha")) {
            numberPicker.setMaxValue(255);
            numberPicker.setMinValue(0);
            standard = 255;
        } else if (getKey().equals("pref_lockscreen_alpha")) {
            numberPicker.setMaxValue(255);
            numberPicker.setMinValue(0);
            standard = 127;
        } else if ((getKey().equals("pref_render_wireframewidth")) || (getKey().equals("pref_lockscreen_wireframewidth"))) {
            numberPicker.setMaxValue(20);
            numberPicker.setMinValue(1);
            standard = 2;
        } else {
            numberPicker.setMaxValue(100);
            numberPicker.setMinValue(1);
        }

        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);

        numberPicker.setValue(getPreference(getKey(), standard));
    }

    private void updatePreference(int n) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(getKey(), n);
        editor.commit();
    }

    private int getPreference(String key, int n) {
        SharedPreferences sp = getPreferenceManager().getSharedPreferences();
        int result = 0;
        if (sp != null) {
            result = sp.getInt(key, n);
        }
        else {
            Log.e(TAG, "Shared preferences = null");
        }
        return result;
    }
}
