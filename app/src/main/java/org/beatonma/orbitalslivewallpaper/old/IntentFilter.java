package org.beatonma.orbitalslivewallpaper.old;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by Michael on 06/01/2015.
 */
public class IntentFilter extends IntentService {
    private final static String TAG = "ORBITALS NOTIFIER";
    private final static String FILENAME = "active_notifications";

    public IntentFilter() {
        super("OrbitalsIntentFilter");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Intent Filter started.");
    }

    @Override
    protected void onHandleIntent(Intent i) {
        Bundle extras = i.getExtras();

        Log.d(TAG, "Intent received!");
        Log.d(TAG, "type:" + extras.getString("type"));
        Log.d(TAG, "package_name:" + extras.getString("package_name"));
        Log.d(TAG, "color:" + extras.getInt("color"));
        String type = extras.getString("type");
        String packageName= extras.getString("package_name");

        if (type.equals("new_notification")) {
            addToFile(extras.getString("package_name"), extras.getInt("color"));
        }
        else if (type.equals("remove_notification")) {
            removeFromFile(packageName);
        }
    }

    // Append a subscription to file
    private boolean addToFile(String mPackage, int mColor) {
        String filename = FILENAME;
        String newLine = mPackage + ";" + mColor;
        String input = "";

        try {
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isReader = new InputStreamReader(fis);
            BufferedReader bf = new BufferedReader(isReader);
            String line;
            while ((line = bf.readLine()) != null) {
                if (line.contains(mPackage)) {
                    Log.d(TAG, "New sub is already in this file.");
                }
                if (input.equals("")) {
                    input = line;
                }
                else {
                    input = input + "\n" + line;
                }
            }
            isReader.close();
        }
        catch (Exception e) {
            Log.e(TAG, "Error getting subs: " + e.toString());
        }

        if (input.equals("")) {
            input = newLine;
        }
        else {
            input = input + "\n" + newLine;
        }

        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(input.getBytes());
            fos.close();
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "Error writing to subs file: " + e.toString());
            return false;
        }
    }

    // Remove a subscription from file
    private void removeFromFile(String sub) {
        String filename = FILENAME;
        String input = "";

        try {
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isReader = new InputStreamReader(fis);
            BufferedReader bf = new BufferedReader(isReader);
            String line;
            while ((line = bf.readLine()) != null) {
                if (line.contains(sub)) {
                    Log.d(TAG, "Sub has been found and will be removed.");
                }
                else if (input.equals("")) {
                    input = line;
                }
                else {
                    input = input + "\n" + line;
                }
            }
            isReader.close();
        }
        catch (Exception e) {
            Log.e(TAG, "Error getting subs: " + e.toString());
        }

        try {
            FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(input.getBytes());
            fos.close();
        }
        catch (Exception e) {
            Log.e(TAG, "Error writing to subs file: " + e.toString());
        }
    }

}
