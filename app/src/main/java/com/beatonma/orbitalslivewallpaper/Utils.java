package com.beatonma.orbitalslivewallpaper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.beatonma.orbitalslivewallpaper.livewallpaper.LwpService;

import java.util.HashSet;

/**
 * Created by Michael on 23/01/2015.
 */
public class Utils {
    Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public int getLayout() {
        Configuration configuration = context.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        int layout;

        if (screenWidthDp >= 500) {
            layout = R.layout.launcher_drawer_activity_wide;
        } else {
            layout = R.layout.launcher_drawer_activity;
        }

        return layout;
    }

    public boolean isWide() {
        return (getLayout() == R.layout.launcher_drawer_activity_wide);
    }

    public boolean isLollipop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        } else {
            return false;
        }
    }

    public static float map(float value, float start1, float stop1, float start2, float stop2) {
        float range1 = stop1 - start1;
        float fraction = (value - start1) / range1;

        float range2 = stop2 - start2;

        float out = (fraction * range2) + start2;

        return out;
    }

    public static void updateWidgets(Context context, int color1, int color2, int color3) {
        Log.d("", "Broadcasting to widgets: " + context.getPackageName() + ";" + color1 + ";" + color2 + ";" + color3);

        Intent mIntent = new Intent();
        mIntent.setAction("com.beatonma.formclockwidget.EXTERNAL_LWP");

        Bundle mExtras = new Bundle();
        mExtras.putString("lwp_package", context.getPackageName());
        mExtras.putInt("lwp_color1", color1);
        mExtras.putInt("lwp_color2", color2);
        mExtras.putInt("lwp_color3", color3);

        mIntent.putExtras(mExtras);

        context.sendBroadcast(mIntent);
    }

    public static void updateWidgets(Context context) {
        SharedPreferences sp = context.getSharedPreferences(LwpService.SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        String[] allowedColors = sp.getStringSet("pref_color_objectcolors",
                new HashSet<String>()).toArray(new String[]{});

        int[] colors = getWidgetColors(allowedColors);
        updateWidgets(context, colors[0], colors[1], colors[2]);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int[] getWidgetColors(String[] allowedColors) {
        int[] output = new int[]{0, 0, 0};
        int colorFilter;

        for (int i = 0; i < 3; i++) {
            if (allowedColors.length >= 1) {
                colorFilter = Integer.valueOf(allowedColors[((int) Math
                        .floor(Math.random() * allowedColors.length))]);
            } else {
                colorFilter = 0;
            }

            output[i] = generateColor(colorFilter);
        }

        return output;
    }

    public static int generateColor(int colorFilter) {
        float[] hsv = new float[]{0, 0, 0};

        switch (colorFilter) {
            case 0: // any color
                break;
            case 1: // greyscale
                break;
            case 2: // reddish
                hsv[0] = ((hsv[0] / 360f * 20f) + 350f) % 360f;
                break;
            case 3: // orangish
                hsv[0] = (hsv[0] / 360f * 20f) + 15f;
                break;
            case 4: // yellowish
                hsv[0] = (hsv[0] / 360f * 15f) + 45f;
                break;
            case 5: // greenish
                hsv[0] = (hsv[0] / 360f * 80f) + 70f;
                break;
            case 6: // blueish
                hsv[0] = (hsv[0] / 360f * 50f) + 200;
                break;
            case 7: // purplish
                hsv[0] = (hsv[0] / 360f * 25f) + 260f;
                break;
            case 8: // pinkish
                hsv[0] = ((hsv[0] / 360f * 80f) + 290f) % 360f;
                break;
            default:
                break;
        }

        hsv[1] = (float) (Math.random() * 0.4) + 0.4f;
        hsv[2] = (float) (Math.random() * 0.4) + 0.4f;

        return Color.HSVToColor(hsv);
    }
}
