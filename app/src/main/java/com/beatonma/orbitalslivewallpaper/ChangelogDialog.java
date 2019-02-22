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

import com.afollestad.materialdialogs.MaterialDialog;

public class ChangelogDialog extends DialogFragment {
    Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();

        //LayoutInflater inflater = getActivity().getLayoutInflater();

        /*final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        builder.setTitle("Change Log")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setView(inflater.inflate(R.layout.dialog_changelog,null));

        AlertDialog d = builder.create();
        d.show();*/

        boolean wrapInScrollView = true;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title("Change log")
                .customView(R.layout.dialog_changelog, wrapInScrollView)
                .btnSelector(R.drawable.dialog_button_selector)
                .positiveText("OK");

        MaterialDialog d = builder.build();
        d.show();

        TextView aboutBodyTextView = (TextView) d.findViewById(R.id.changelog_body_textview);
        aboutBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        /*Button buttonDebug = (Button) d.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonDebug.setTextColor(getResources().getColor(R.color.Primary));
        Button buttonOK = (Button) d.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonOK.setTextColor(getResources().getColor(R.color.PrimaryDark));*/

        return d;
    }
}