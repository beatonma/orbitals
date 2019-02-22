package com.beatonma.orbitalslivewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 20/01/2015.
 */
public class MultiSelectListPreference extends Preference {
    private final static String TAG = "MultiSelectListPreference";
    String [] entries;
    String [] entryValues;
    Integer[] preselection;

    Context context;

    public MultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        // Get xml attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ListPreference,
                0, 0);

        Resources resources = context.getResources();

        try {
            int idEntries = a.getResourceId(R.styleable.ListPreference_entries, 0);
            entries = resources.getStringArray(idEntries);

            int idEntryValues = a.getResourceId(R.styleable.ListPreference_entryValues, 0);
            entryValues = resources.getStringArray(idEntryValues);
        }
        finally {
            a.recycle();
        }
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    public void onClick() {
        String title = (String) getTitle();
        String [] selected = getPreference(getKey());

        ArrayList<Integer> chosen = new ArrayList<Integer>();
        List v = Arrays.asList(entryValues);
        for (String s : selected) {
            if (v.contains(s)) {
                chosen.add(v.indexOf(s));
            }
        }

        // Default to select first value
        if (chosen.isEmpty()) {
            chosen.add(0);
        }

        preselection = chosen.toArray(new Integer[chosen.size()]);

        new MaterialDialog.Builder(context)
                .title(title)
                .items(entries)
                .itemsCallbackMultiChoice(preselection, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        Set<String> results = new HashSet<String>();
                        for (Integer i : which) {
                            results.add(String.valueOf(i));
                        }
                        Log.d(TAG, "Selected:" + results.toString());
                        updatePreference(results);
                        return true;
                    }
                })
                .btnSelector(R.drawable.dialog_button_selector)
                .positiveText("OK")
                .show();
    }

    private void updatePreference(Set<String> s) {
        SharedPreferences.Editor editor = getEditor();
        editor.putStringSet(getKey(), s);
        editor.commit();
    }

    private String[] getPreference(String key) {
        SharedPreferences sp = getPreferenceManager().getSharedPreferences();
        String[] result = {""};
        if (sp != null) {
            result = sp.getStringSet(key, new HashSet<String>()).toArray(new String[]{});
        }
        else {
            Log.e(TAG, "Shared preferences = null");
        }
        return result;
    }
}
