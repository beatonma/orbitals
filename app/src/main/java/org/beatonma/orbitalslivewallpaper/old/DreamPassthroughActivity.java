package org.beatonma.orbitalslivewallpaper.old;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Michael on 22/01/2015.
 */
public class DreamPassthroughActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Intent i = new Intent(DreamPassthroughActivity.this, LauncherNav.class);
        Bundle extras = new Bundle();
        extras.putInt("nav", 1);
        i.putExtras(extras);
        startActivity(i);
        finish();
    }
}
