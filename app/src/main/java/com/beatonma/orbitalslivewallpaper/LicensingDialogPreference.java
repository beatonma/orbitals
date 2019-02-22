package com.beatonma.orbitalslivewallpaper;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class LicensingDialogPreference extends DialogPreference {

	public LicensingDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.licensing_dialog);
		setNegativeButtonText("");
	}

	@Override
	public void onBindDialogView(View view) {
		super.onBindDialogView(view);
	}
}