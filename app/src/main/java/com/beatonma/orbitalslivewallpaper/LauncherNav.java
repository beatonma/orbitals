package com.beatonma.orbitalslivewallpaper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beatonma.orbitalslivewallpaper.dream.DreamPreferenceFragment;
import com.beatonma.orbitalslivewallpaper.dream.DreamView;
import com.beatonma.orbitalslivewallpaper.livewallpaper.LwpPreferenceFragment;

/**
 * Created by Michael on 12/11/2014.
 */
public class LauncherNav extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String TAG = "LauncherNav";
    private static final String LWP_SECTION = "lwp_section";
    private static final String DREAM_SECTION = "dream_section";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    Context context;
    Toolbar toolbar;

    int nav = 0;
    int section = 0;

    Utils utils;

    // Hold information for FAB animation
//    FabContainer fabContainer;

    //boolean firstRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        // Use basic version of app if device is running something older than Android 4.2
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.d(TAG, "Device is running Android version " + Build.VERSION.CODENAME + ". Launching basic activity.");
            Intent i = new Intent(LauncherNav.this, com.beatonma.orbitalslivewallpaper.livewallpaper.LwpPrefsActivity.class);
            startActivity(i);
            finish();
        }
        else {
            context = getApplicationContext();
            utils = new Utils(context);

            /*firstRun = getSharedPreference("current","first_run",true);
            if (firstRun) {

            }*/

            setContentView(utils.getLayout());

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

            // Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    findViewById(R.id.drawer_layout));

            // Load correct section if loaded from an intent - used for getting to dream settings from system settings
            if (getIntent() != null) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    nav = extras.getInt("nav", 0);
                    getNavDrawerFragment().selectItem(nav);
                    onNavigationDrawerItemSelected(nav);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateToolbarText();
                        }
                    }, 400);
                }
            }

            updateFab(nav);

            //setRunning(true);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Remember nav sectino (lwp or dream)
        outState.putInt("nav",nav);

        // Remember current section
        outState.putInt(nav == 0 ? LWP_SECTION : DREAM_SECTION, section);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        setTheme(R.style.AppTheme);

        if (utils == null) {
            utils = new Utils(this);
        }

        nav = savedInstanceState.getInt("nav", 0);
        Log.d(TAG, "Restoring nav=" + nav);

        //onNavigationDrawerItemSelected(nav);

        // Restore section
        if (nav == 0) {
            int section = savedInstanceState.getInt(LWP_SECTION, 0);
            if (utils.isWide()) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.narrow_container, WideHeaderListFragment.newInstance(nav, section - 1), "LwpPreferenceFragment")
                        .commit();
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.wide_container, LwpPreferenceFragment.newInstance(section == 0 ? 1 : section), "LwpPreferenceFragment")
                        .commit();
            }
            else {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, LwpPreferenceFragment.newInstance(section), "LwpPreferenceFragment")
                        .commit();
            }
        }
        else if (nav == 1) {
            int section = savedInstanceState.getInt(DREAM_SECTION, 0);
            if (utils.isWide()) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.narrow_container, WideHeaderListFragment.newInstance(nav, section - 1), "DreamPreferenceFragment")
                        .commit();
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.wide_container, DreamPreferenceFragment.newInstance(section == 0 ? 1 : section), "DreamPreferenceFragment")
                        .commit();
            }
            else {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, DreamPreferenceFragment.newInstance(section), "DreamPreferenceFragment")
                        .commit();
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateToolbarText();
            }
        }, 400);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        ImageUtils imageUtils;
//        if (nav == 0) {
//            imageUtils = new ImageUtils(context, "lwp");
//        }
//        else {
//            imageUtils = new ImageUtils(context, "dream");
//        }
//        //ImageUtils imageUtils = new ImageUtils(context);
//        Uri uri = null;
//        if (resultData != null) {
//            uri = resultData.getData();
//        }
//        if (uri == null) {
//            uri = Uri.parse(imageUtils.cachedImage);
//        }
//
//        if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
//            Log.d(TAG, "Cropped image received. Processing in background...");
//            if (uri != null) {
//                imageUtils.processBackgroundImage(uri);
//            }
//            else {
//                Log.e(TAG, "Received cropped image uri is null.");
//            }
//        }
//        else {
//            super.onActivityResult(requestCode, resultCode, resultData);
//        }
//    }

    /**
     * Navigation drawer stuff
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Remember current position
        nav = position;

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        if (utils.isWide()) {
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.narrow_container, WideHeaderListFragment.newInstance(nav, 0), nav == 0 ? "LwpPreferenceFragment" : "DreamPreferenceFragment")
                    .commit();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.wide_container, nav == 0 ? LwpPreferenceFragment.newInstance(1) : DreamPreferenceFragment.newInstance(1), nav == 0 ? "LwpPreferenceFragment" : "DreamPreferenceFragment")
                    .commit();
        }
        else {
            View v = findViewById(R.id.container);

            if (v != null) {
                animateFragment(v);
            }
            else {
                Log.d(TAG, "View is null");
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, position == 0 ? LwpPreferenceFragment.newInstance(0) : DreamPreferenceFragment.newInstance(0), nav == 0 ? "LwpPreferenceFragment" : "DreamPreferenceFragment")
                        .commit();
            }
        }

        updateFab(position);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        }
        else {
            if (nav == 0) {
                if (utils.isWide()) {
                    super.onBackPressed();
                }
                else {
                    try {
                        int section = ((LwpPreferenceFragment) fragmentManager.findFragmentByTag("LwpPreferenceFragment")).getSection();
                        Log.d(TAG, "Current section: " + section);
                        if (section != 0) {
                            // User is in a subsection
                            fragmentManager.beginTransaction().replace(R.id.container, LwpPreferenceFragment.newInstance(0), "LwpPreferenceFragment").commit();
                        } else {
                            super.onBackPressed();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting preference fragment: " + e.toString());
                        super.onBackPressed();
                    }
                }
            }
            else if (nav == 1) {
                if (utils.isWide()) {
                    super.onBackPressed();
                }
                else {
                    try {
                        int section = ((DreamPreferenceFragment) fragmentManager.findFragmentByTag("DreamPreferenceFragment")).getSection();
                        Log.d(TAG, "Current section: " + section);
                        if (section != 0) {
                            // User is in a subsection
                            fragmentManager.beginTransaction().replace(R.id.container, DreamPreferenceFragment.newInstance(0), "DreamPreferenceFragment").commit();
                        } else {
                            super.onBackPressed();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting preference fragment: " + e.toString());
                        super.onBackPressed();
                    }
                }
            }
            else {
                super.onBackPressed();
            }
        }
    }

    private void updateFab(int position) {
//        try {
//            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//            fabContainer = new FabContainer();
//            switch (position) {
//                case 0:
//                    fab.hide();
//                    break;
//                case 1:
//                    fab.setVisibility(View.VISIBLE);
//                    fab.show();
//                    fab.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//                                if (fabContainer.hasMoved) {
//                                    if (utils.isLollipop()) {
//                                        removeDreamOverlay();
//                                    }
//                                }
//                                else {
//                                    moveFabToCenter(v);
//                                    Intent i = new Intent(Intent.ACTION_MAIN);
//                                    i.setClassName("com.android.systemui", "com.android.systemui.Somnambulator");
//                                    startActivity(i);
//                                }
//                            } catch (Exception e) {
//                                Log.e(TAG, "Couldn't start daydream: " + e.toString());
//                                Toast.makeText(context, "Couldn't start daydream - please try starting it from your system display menu.", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                    break;
//                default:
//                    break;
//            }
//        }
//        catch (Exception e) {
//            Log.e(TAG, "Error updating toolbar: " + e.toString());
//        }
    }

    public void updateToolbarText(final String root, final String newText) {
        final TextView textTitle = (TextView) toolbar.findViewById(R.id.textTitle);

        if (root.length() == 0 && newText.length() == 0) {
            // Something's gone wrong. Don't update toolbar text
            return;
        }

        if (textTitle.getText().toString().equals(root + " " + newText)) {
            // Don't change anything if it doesn't need to be changed.
            return;
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ValueAnimator offsetAnimator = ValueAnimator.ofInt(0, newText.length());
                offsetAnimator
                        .setDuration(30 * newText.length())
                        .setInterpolator(new AccelerateDecelerateInterpolator());

                offsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int offset = (int) animation.getAnimatedValue();
                        textTitle.setText(root + " " + newText.substring(0, offset));
                    }
                });

                offsetAnimator.start();
            } else {
                textTitle.setText(getString(R.string.app_name_root) + " " + newText);
            }
        }
    }

    public void removeToolbarTail() {
        final TextView textTitle = (TextView) toolbar.findViewById(R.id.textTitle);
        String text = textTitle.getText().toString();
        final String root = text.substring(0, text.indexOf(" "));
        final String oldText = text.replace(root, "");

        ValueAnimator oldOffsetAnimator = ValueAnimator.ofInt(0, oldText.length());
        oldOffsetAnimator
                .setDuration(30 * oldText.length())
                .setInterpolator(new AccelerateDecelerateInterpolator());

        oldOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (int) animation.getAnimatedValue();
                textTitle.setText(root + "" + oldText.substring(0, oldText.length() - offset));
            }
        });

        oldOffsetAnimator.start();
    }

    public void updateToolbarText() {
        FragmentManager fm = getFragmentManager();
        int section = 0;
        String title = "";

        try {
            if (nav == 0) {
                LwpPreferenceFragment fragment;
                if (utils.isWide()) {
                    fragment = (LwpPreferenceFragment) fm.findFragmentById(R.id.wide_container);
                } else {
                    fragment = (LwpPreferenceFragment) fm.findFragmentById(R.id.container);
                }
                section = fragment.getSection();
                title = getResources().getStringArray(R.array.lwp_sections)[section];
            } else {
                DreamPreferenceFragment fragment;
                if (utils.isWide()) {
                    fragment = (DreamPreferenceFragment) fm.findFragmentById(R.id.wide_container);
                } else {
                    fragment = (DreamPreferenceFragment) fm.findFragmentById(R.id.container);
                }
                section = fragment.getSection();
                title = getResources().getStringArray(R.array.dream_sections)[section];
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Error updating toolbar text: " + e.toString());
        }

        updateToolbarText(getToolbarTextRoot(section), title);
    }

    public String getToolbarTextRoot(int section) {
        String root;
        if (utils.isWide()) {
            if (nav == 0) {
                root = "Wallpaper";
            }
            else if (nav == 1) {
                root = "Dream";
            }
            else {
                root = getString(R.string.app_name_root);
            }
        }
        else {
            if (section != 0) {
                if (nav == 0) {
                    root = "Wallpaper";
                }
                else if (nav == 1) {
                    root = "Dream";
                }
                else {
                    root = getString(R.string.app_name_root);
                }
            }
            else {
                root = getString(R.string.app_name_root);
            }
        }

        return root;
    }

    /*Animations*/
    /*public void moveFabToCenter(final View v) {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int xDest = dm.widthPixels - v.getLeft() - (dm.widthPixels / 2) - (v.getWidth() / 2);
        int yDest = dm.heightPixels - v.getTop() - (dm.heightPixels / 2) - (v.getHeight() / 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Only >= Lollipop can use Z translation, PathInterpolator and circular reveals
            v.animate()
                    .translationX(xDest)
                    .translationY(yDest)
                    .translationZBy(10f)
                    .scaleXBy(2f)
                    .scaleYBy(2f)
                    .setInterpolator(new PathInterpolator(0.4f, 0f, 0.2f, 1f))
                    .setDuration(400);

            // Delay overlay reveal until fab has reached center
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    revealDreamOverlay();
                }
            }, 400);
        }
        else {
            v.animate()
                    .translationX(xDest)
                    .translationY(yDest)
                    .scaleXBy(2f)
                    .scaleYBy(2f)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(400);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveFabBack(v);
                }
            }, 1000);
        }
        fabContainer.hasMoved = true;
    }*/

    // Curved FAB animation.
    public void moveFabToCenter(final View v) {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        final int xDest = Math.abs(dm.widthPixels - v.getLeft() - (dm.widthPixels / 2) - (v.getWidth() / 2));
        final int yDest = Math.abs(dm.heightPixels - v.getTop() - (dm.heightPixels / 2) - (v.getHeight() / 2));

        int xDuration = xDest / 2;
        int yDuration = yDest / 2;

        final int maxDuration = Math.max(xDuration, yDuration);
        Log.d(TAG, "Max duration=" + maxDuration);

        if (maxDuration > 300) {
            if (maxDuration == xDuration) {
                yDuration = (int) ((float) yDuration / (float) xDuration * 300f);
                xDuration = 300;
            }
            else {
                xDuration = (int) ((float) xDuration / (float) yDuration * 300f);
                yDuration = 300;
            }

            Log.d(TAG, "Scaled xDuration=" + xDuration + "; yDuration=" + yDuration);
        }

        ValueAnimator offsetAnimatorX = ValueAnimator.ofInt(0, xDest);
        offsetAnimatorX
                .setDuration(xDuration > yDuration ? yDuration : xDuration)
                .setInterpolator(new AccelerateDecelerateInterpolator());

        offsetAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationX(-(animation.getAnimatedFraction() * xDest));
                v.setScaleX(1 + (2f * animation.getAnimatedFraction()));
                v.setScaleY(1 + (2f * animation.getAnimatedFraction()));
            }
        });

        ValueAnimator offsetAnimatorY = ValueAnimator.ofInt(0, yDest);
        offsetAnimatorY
                .setDuration(xDuration > yDuration ? xDuration : yDuration)
                .setInterpolator(new AccelerateDecelerateInterpolator());

        offsetAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setTranslationY(-(animation.getAnimatedFraction() * yDest));
            }
        });

        offsetAnimatorX.start();
        offsetAnimatorY.start();

        // Circular transition if Lollipop, no transition otherwise.
        if (utils.isLollipop()) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Lollipop: using circuler reveal for daydream transition.");
                    revealDreamOverlay();
                }
            }, maxDuration);
        }
        else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveFabBack(v);
                }
            }, 1000);
        }

//        fabContainer.hasMoved = true;
    }

    public void moveFabBack(View v) {
        // Only animate elevation and use different interpolator on Lollipop or above
        Log.d(TAG, "moveFabBack isLollipop=" + utils.isLollipop());
        if (utils.isLollipop()) {
            v.animate()
//                    .translationX(-fabContainer.x)
//                    .translationY(-fabContainer.y)
                    .scaleXBy(-2f)
                    .scaleYBy(-2f)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(300);
        }
        else {
            v.animate()
//                    .translationX(-fabContainer.x)
//                    .translationY(-fabContainer.y)
                    .scaleXBy(-2f)
                    .scaleYBy(-2f)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(300);
        }
//        fabContainer.hasMoved = false;
    }

    public void revealDreamOverlay() {
        // previously invisible view
        final View overlay = findViewById(R.id.dream_overlay);
        overlay.setBackgroundColor(getSharedPreferences(DreamView.SHARED_PREFS_NAME, MODE_PRIVATE).getInt("pref_color_background", Color.BLACK));

        // get the center for the clipping circle
        int cx = (overlay.getLeft() + overlay.getRight()) / 2;
        int cy = (overlay.getTop() + overlay.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(overlay.getWidth(), overlay.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(overlay, cx, cy, 0, finalRadius);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeDreamOverlay();
                    }
                }, 400);
            }
        });

        // make the view visible and start the animation
        overlay.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void removeDreamOverlay() {
        final View overlay = findViewById(R.id.dream_overlay);

        // get the center for the clipping circle
        int cx = (overlay.getLeft() + overlay.getRight()) / 2;
        int cy = (overlay.getTop() + overlay.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = overlay.getWidth();

        // create the animation (the final radius is zero)
//        Animator anim = ViewAnimationUtils.createCircularReveal(overlay, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                overlay.setVisibility(View.GONE);
//                moveFabBack(findViewById(R.id.fab));
//            }
//        });

        // start the animation
//        anim.start();
    }

    // Slide fragment off screen, replace and slide back into position
    public void animateFragment(final View v) {
        int xDest = -v.getWidth();

        v.animate()
                .translationX(xDest)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(150);

        try {
            removeToolbarTail();
        }
        catch (Exception e) {

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, nav == 0 ? LwpPreferenceFragment.newInstance(0) : DreamPreferenceFragment.newInstance(0), nav == 0 ? "LwpPreferenceFragment" : "DreamPreferenceFragment")
                        .commit();

                v.animate()
                        .translationX(0)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(150);
            }
        }, 300);
    }

    private void initContainer() {
        View v = findViewById(R.id.container);
        if (v != null) {
            v.setTranslationX(-v.getWidth());
        v.animate()
                .translationX(0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(150)
                .start();
        }
    }

    public void setSection(int n) {
        this.section = n;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public NavigationDrawerFragment getNavDrawerFragment() {
        return mNavigationDrawerFragment;
    }

//    public FloatingActionButton getFab() {
//        return fabContainer.fab;
//    }
//
//    private class FabContainer {
//        FloatingActionButton fab;
//        int x;
//        int y;
//        boolean hasMoved = false;
//
//        public void FabContainer(FloatingActionButton fab) {
//            this.fab = fab;
//        }
//    }
}
