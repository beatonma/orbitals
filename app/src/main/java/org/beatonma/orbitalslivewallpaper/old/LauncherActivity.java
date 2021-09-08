package org.beatonma.orbitalslivewallpaper.old;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.beatonma.orbitalslivewallpaper.R;

import org.beatonma.orbitalslivewallpaper.old.dream.DreamPrefsActivity;
import org.beatonma.orbitalslivewallpaper.old.livewallpaper.LwpPrefsActivity;

public class LauncherActivity extends Activity {
    ListView list;
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.launcher);
        //setTheme(android.R.style.Theme_Holo);
        list = (ListView) findViewById(R.id.launcherListView);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent i;
                switch(position) {
                case 0:
                    // Wallpaper options
                    i = new Intent(LauncherActivity.this, LwpPrefsActivity.class);
                    startActivity(i);
                    break;
                case 1:
                    // Daydream options
                    i = new Intent(LauncherActivity.this, DreamPrefsActivity.class);
                    startActivity(i);
                    break;
                case 2:
                    try {
                        i = new Intent(Intent.ACTION_MAIN);
                        i.setClassName("com.android.systemui", "com.android.systemui.Somnambulator");
                        startActivity(i);
                    }
                    catch (Exception e) {
                        Toast.makeText(context, "Couldn't start daydream - please try starting it from your system display menu.", Toast.LENGTH_LONG).show();
                    }
                /*
                 * Option to remove activity from launcher
                 *
                 * case 2:
                    PackageManager p = getPackageManager();
                    p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    Toast.makeText(com.beatonma.orbitalslivewallpaper.LauncherActivity.this, "This application will be hidden from the launcher soon! (May require device restart!)", Toast.LENGTH_LONG).show();
                    break;*/
                default:
                    break;
                }
            }
        });
//        ActionBar bar = this.getActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
    }

    protected void onStart() {
        super.onStart();

    }
    
    protected void onRestart() {
        super.onRestart();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
