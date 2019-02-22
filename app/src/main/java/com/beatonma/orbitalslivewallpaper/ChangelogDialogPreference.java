package com.beatonma.orbitalslivewallpaper;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class ChangelogDialogPreference extends DialogPreference {

	public ChangelogDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setDialogLayoutResource(R.layout.changelog);
		setNegativeButtonText("");
	}

	@Override
	public void onBindDialogView(View view) {
		super.onBindDialogView(view);
	}
}