package com.beatonma.orbitalslivewallpaper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Michael on 20/01/2015.
 */
public class LicensingDialog extends DialogFragment {
    Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        builder.setTitle("Licensing")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setView(inflater.inflate(R.layout.dialog_licensing,null));

        AlertDialog d = builder.create();
        d.show();

        TextView aboutBodyTextView = (TextView) d.findViewById(R.id.licensing_body_textview);
        aboutBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        Button buttonDebug = (Button) d.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonDebug.setTextColor(getResources().getColor(R.color.Primary));
        Button buttonOK = (Button) d.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOK.setTextColor(getResources().getColor(R.color.PrimaryDark));

        return d;
    }
}
