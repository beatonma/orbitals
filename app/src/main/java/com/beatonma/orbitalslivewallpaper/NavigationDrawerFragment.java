package com.beatonma.orbitalslivewallpaper;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    private static final String TAG = "OrbitalsNavDrawer";

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    LauncherNav context;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout mRelativeLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    //TextView textLicensing;
    TextView textChangeLog;
    TextView textCommunity;

    public NavigationDrawerFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) mRelativeLayout.findViewById(R.id.navigation_listview);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                R.layout.navigation_drawer_list_item,
                android.R.id.text1,
                getResources().getStringArray(R.array.navigation_array)
               ));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        return mRelativeLayout;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        /*try {
            drawerLayout.setStatusBarBackground(new ColorDrawable(Color.parseColor("#222222")));
        }
        catch (Exception e) {
            Log.e(TAG, "Error settings status bar color: " + e.toString());
        }*/

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerSlide(View v, float f) {
                if (v == null) {
                    super.onDrawerSlide(null, f);
                }else {
                    // Disable arrow animation
                    super.onDrawerSlide(v, 0f);
                }
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        textChangeLog = (TextView) mFragmentContainerView.findViewById(R.id.text_about_changelog);
        textCommunity = (TextView) mFragmentContainerView.findViewById(R.id.text_about_community);

        textChangeLog.setOnClickListener(new LinkListener());
        textCommunity.setOnClickListener(new LinkListener());

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    public void toggleToolbar(final boolean b) {
        if (b) {
            toggleActionBarIcon(ActionDrawableState.ARROW, mDrawerToggle, true);
        }
        else {
            toggleActionBarIcon(ActionDrawableState.BURGER, mDrawerToggle, true);
        }
    }

    private enum ActionDrawableState{
        BURGER, ARROW
    }
    private static void toggleActionBarIcon(ActionDrawableState state, final ActionBarDrawerToggle toggle, boolean animate){
        if(animate) {
            float start = state == ActionDrawableState.BURGER ? 0f : 1.0f;
            float end = Math.abs(start - 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ValueAnimator offsetAnimator = ValueAnimator.ofFloat(start, end);
                offsetAnimator.setDuration(300);
                offsetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                offsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float offset = (Float) animation.getAnimatedValue();
                        toggle.onDrawerSlide(null, offset);
                    }
                });
                offsetAnimator.start();
            }else{
                //do the same with nine-old-androids lib :)
            }
        } else {
            if (state == ActionDrawableState.BURGER) {
                toggle.onDrawerClosed(null);
            } else {
                toggle.onDrawerOpened(null);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (LauncherNav) activity;
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null) {// && !isDrawerOpen()) {
            inflater.inflate(R.menu.menu, menu);
            //showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.system_settings:
                if (context.nav == 0) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                        startActivity(Intent.createChooser(intent, "Select Wallpaper"));
                    }
                    catch (Exception e) {
                        Toast.makeText(context, "Couldn't open wallpaper system settings.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Couldn't open system lwp settings.");
                    }
                }
                else if (context.nav == 1) {
                    try {
                        Intent i = new Intent(Settings.ACTION_DREAM_SETTINGS);
                        startActivity(i);
                    }
                    catch (Exception e) {
                        Toast.makeText(context, "Couldn't open dream system settings.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Couldn't open system daydream settings.");
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {

    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public void closeDrawer() {
        try {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        catch (Exception e) {
            Log.d(TAG, "Couldn't close the navigation drawer: " + e.toString());
        }
    }

    public void openDrawer() {
        try {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
        catch (Exception e) {
            Log.d(TAG, "Couldn't open the navigation drawer: " + e.toString());
        }
    }

    public void lockDrawer() {
        try {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        catch (Exception e) {
            Log.d(TAG, "Couldn't lock the navigation drawer: " + e.toString());
        }
    }

    public void unlockDrawer() {
        try {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        catch (Exception e) {
            Log.d(TAG, "Couldn't unlock the navigation drawer: " + e.toString());
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public class LinkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.equals(textChangeLog)) {
                FragmentManager fm = context.getFragmentManager();
                ChangelogDialog changelogDialog = new ChangelogDialog();
                changelogDialog.show(fm,"Change Log Dialog");
            }
            else if (v.equals(textCommunity)) {
                Intent i = new Intent();
                i.setAction("android.intent.action.VIEW");
                i.setData(Uri.parse("https://plus.google.com/u/0/communities/113955537219140914914"));
                startActivity(i);
            }
        }
    }
}
