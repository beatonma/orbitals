package com.beatonma.orbitalslivewallpaper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class NumberDialogPreference extends DialogPreference {

    NumberPicker numberPicker = null;
    SeekBar slider = null;
    TextView text = null;

    String nums[] = {"0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8",
            "0.9", "1.0", "2.0", "3.0", "4.0", "5.0", "6.0", "7.0", "8.0",
            "9.0", "10.0"};

    public NumberDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setDialogLayoutResource(R.layout.number_dialog_preference);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindDialogView(View view) {

//		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
//
//			int standard = 30;
//			numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
//
//			if (getKey().equals("pref_performance_framerate")) {
//				numberPicker.setMaxValue(60);
//				numberPicker.setMinValue(1);
//			} else if (getKey().equals("pref_physics_gravity")) {
//				numberPicker.setDisplayedValues(nums);
//				numberPicker.setMinValue(1);
//				numberPicker.setMaxValue(19);
//				standard = 10;
//			} else if (getKey().equals("pref_color_alpha")) {
//				numberPicker.setMaxValue(255);
//				numberPicker.setMinValue(0);
//				standard = 255;
//			} else if (getKey().equals("pref_lockscreen_alpha")) {
//				numberPicker.setMaxValue(255);
//				numberPicker.setMinValue(0);
//				standard = 127;
//			} else if ((getKey().equals("pref_render_wireframewidth")) || (getKey().equals("pref_lockscreen_wireframewidth"))) {
//				numberPicker.setMaxValue(20);
//				numberPicker.setMinValue(1);
//				standard = 2;
//			} else {
//				numberPicker.setMaxValue(100);
//				numberPicker.setMinValue(1);
//			}
//
//			numberPicker.setWrapSelectorWheel(true);
//
//			SharedPreferences settings = getPreferenceManager()
//					.getSharedPreferences();
//
//			numberPicker.setValue(settings.getInt(getKey(), standard));
//		}
//
//		else {
//			int standard = 25;
//			slider = view.findViewById(R.id.seekBar);
//			if (getKey().equals("pref_performance_framerate")) {
//				slider.setMax(60);
//			} else if (getKey().equals("pref_physics_gravity")) {
//				slider.setMax(18);
//				standard = 9;
//			} else if ((getKey().equals("pref_color_alpha"))
//					|| (getKey().equals("pref_lockscreen_alpha"))) {
//				slider.setMax(255);
//				standard = 127;
//			} else if ((getKey().equals("pref_render_wireframewidth")) || (getKey().equals("pref_lockscreen_wireframewidth"))) {
//				slider.setMax(20);
//				standard = 2;
//			} else {
//				slider.setMax(100);
//			}
//			slider.setOnSeekBarChangeListener(mSeekBarChangeListener);
//
//			text = (TextView) view.findViewById(R.id.textValue);
//
//			SharedPreferences settings = getPreferenceManager()
//					.getSharedPreferences();
//			slider.setProgress(settings.getInt(getKey(), standard));
//			if (getKey().equals("pref_physics_gravity")) {
//				text.setText(nums[slider.getProgress()]);
//			} else {
//				text.setText(Integer.toString(slider.getProgress()));
//			}
//		}

        super.onBindDialogView(view);
    }

    private final OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (getKey().equals("pref_physics_gravity")) {
                text.setText(nums[slider.getProgress()]);
            } else {
                text.setText(Integer.toString(progress));
            }
        }
    };

    @SuppressLint("NewApi")
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                updatePreference(numberPicker.getValue());
            } else {
                updatePreference(slider.getProgress());
            }
        }
    }

    private void updatePreference(int newValue) {

        SharedPreferences.Editor editor = getEditor();
        editor.putInt(getKey(), newValue);
        editor.commit();
    }
}
