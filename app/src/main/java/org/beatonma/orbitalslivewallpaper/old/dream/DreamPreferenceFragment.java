package org.beatonma.orbitalslivewallpaper.old.dream;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import org.beatonma.orbitalslivewallpaper.old.LauncherNav;
import com.beatonma.orbitalslivewallpaper.R;
import org.beatonma.orbitalslivewallpaper.old.Utils;

/**
 * Created by Michael on 19/01/2015.
 */

public class DreamPreferenceFragment extends PreferenceFragment {
    private final static String TAG = "DreamPreferenceFragment";
    private final static String SECTION = "SECTION";
    private final static int FILE_PICKED = 65;
    private final static int SECTION_MAIN = 0;
    private final static int SECTION_APPEARANCE = 1;
    private final static int SECTION_VISUAL_AIDS = 2;
    private final static int SECTION_SYSTEMS = 3;
    private final static int SECTION_PHYSICS = 4;
    private final static int SECTION_TIME = 5;
    private final static int SECTION_NOTIFY = 6;
    private final static int SECTION_PERFORMANCE = 7;
    private final static int SECTION_EXPERIMENTAL = 8;
    private String[] SECTIONS = {};

    Utils utils;
    Context context;

    int section = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        getActivity().setTheme(R.style.SettingsTheme);
        getPreferenceManager().setSharedPreferencesName(DreamView.SHARED_PREFS_NAME);
        utils = new Utils(getActivity());
        SECTIONS = getActivity().getResources().getStringArray(R.array.dream_sections);

        if (getArguments() != null) {
            section = getArguments().getInt(SECTION, 0);
            ((LauncherNav) getActivity()).setSection(section);
        } else {
            Log.d(TAG, "No arguments available.");
        }

        try {
            LauncherNav activity = (LauncherNav) getActivity();
            activity.updateToolbarText();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't update toolbar text: " + e.toString());
        }

        loadPreferenceFragment(section);
    }

    public static DreamPreferenceFragment newInstance(int section) {
        DreamPreferenceFragment fragment = new DreamPreferenceFragment();
        Bundle args = new Bundle();
        args.putInt(SECTION, section);
        fragment.setArguments(args);
        return fragment;
    }

    public void loadPreferenceFragment(int section) {
        switch (section) {
            case SECTION_MAIN:
                addPreferencesFromResource(R.xml.dream_pref_main);
                setUpMainPrefs();
                break;
            case SECTION_APPEARANCE:
                addPreferencesFromResource(R.xml.dream_pref_appearance);
                setUpAppearancePrefs();
                break;
            case SECTION_VISUAL_AIDS:
                addPreferencesFromResource(R.xml.dream_pref_visual_aids);
                break;
            case SECTION_SYSTEMS:
                addPreferencesFromResource(R.xml.dream_pref_systems);
                break;
            case SECTION_PHYSICS:
                addPreferencesFromResource(R.xml.dream_pref_physics);
                break;
            case SECTION_TIME:
                addPreferencesFromResource(R.xml.dream_pref_time);
                break;
            case SECTION_NOTIFY:
                addPreferencesFromResource(R.xml.dream_pref_notify);
                setUpNotifyPrefs();
                break;
            case SECTION_PERFORMANCE:
                addPreferencesFromResource(R.xml.dream_pref_performance);
                break;
            case SECTION_EXPERIMENTAL:
                addPreferencesFromResource(R.xml.dream_pref_experimental);
                //setUpExperimentalPrefs();
                break;
            default:
                addPreferencesFromResource(R.xml.dream_pref_main);
                break;
        }

        if (!utils.isWide()) {
            try {
                if (section == SECTION_MAIN) {
                    ((LauncherNav) getActivity()).getNavDrawerFragment().unlockDrawer();
                } else {
                    ((LauncherNav) getActivity()).getNavDrawerFragment().lockDrawer();
                }
            } catch (Exception e) {
                Log.e(TAG, "Navigation drawer is unavailable.");
            }

            try {
                if (section == 0) {
                    ((LauncherNav) getActivity()).getNavDrawerFragment().toggleToolbar(true);
                } else {
                    ((LauncherNav) getActivity()).getNavDrawerFragment().toggleToolbar(false);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating toolbar icon: " + e.toString());
            }
        } else {
            try {
                ((LauncherNav) getActivity()).getNavDrawerFragment().unlockDrawer();
            } catch (Exception e) {

            }
        }

        // Handle clicks on toolbar burger/arrow.
//        try {
//            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (utils.isWide()) {
//                        try {
//                            FragmentManager fragmentManager = getFragmentManager();
//                            int section = ((DreamPreferenceFragment) fragmentManager.findFragmentByTag("DreamPreferenceFragment")).getSection();
//
//                            Log.d(TAG, "Current section: " + section);
//                            ((LauncherNav) getActivity()).getNavDrawerFragment().openDrawer();
//                        }
//                        catch (Exception e) {
//                            Log.e(TAG, "Error getting preference fragment: " + e.toString());
//                        }
//                    }
//                    else {
//                        Log.d(TAG, "Back arrow clicked.");
//                        try {
//                            FragmentManager fragmentManager = getFragmentManager();
//                            int section = ((DreamPreferenceFragment) fragmentManager.findFragmentByTag("DreamPreferenceFragment")).getSection();
//
//                            Log.d(TAG, "Current section: " + section);
//                            if (section != 0) {
//                                // User is in a subsection
//                                fragmentManager.beginTransaction()
//                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                        .replace(R.id.container, DreamPreferenceFragment.newInstance(0), "DreamPreferenceFragment")
//                                        .commit();
//                            }
//                            else {
//                                // User is at top level
//                                ((LauncherNav) getActivity()).getNavDrawerFragment().openDrawer();
//                            }
//                        }
//                        catch (Exception e) {
//                            Log.e(TAG, "Error getting preference fragment: " + e.toString());
//                        }
//                    }
//                }
//            });
//        }
//        catch (Exception e) {
//            Log.e(TAG, "Error handling hamburger/arrow touch event: " + e.toString());
//        }
    }

    public void setUpMainPrefs() {
        final int container = utils.isWide() ? R.id.wide_container : R.id.container;
        Preference appearance = findPreference("dream_appearance");
        appearance.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(SECTION_APPEARANCE), "DreamPreferenceFragment")
                        .commit();
                return true;
            }
        });

        Preference visualAids = findPreference("dream_visual_aids");
        visualAids.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(SECTION_VISUAL_AIDS), "DreamPreferenceFragment")
                        .commit();
                return true;
            }
        });

        Preference systems = findPreference("dream_systems");
        systems.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(SECTION_SYSTEMS), "DreamPreferenceFragment")
                        .commit();
                return true;
            }
        });

        Preference physics = findPreference("dream_physics");
        physics.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(SECTION_PHYSICS), "DreamPreferenceFragment")
                        .commit();
                return true;
            }
        });

        Preference time = findPreference("dream_time");
        time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(SECTION_TIME), "DreamPreferenceFragment")
                        .commit();
                return true;
            }
        });

        Preference notify = findPreference("dream_notify");
        notify.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(SECTION_NOTIFY), "DreamPreferenceFragment")
                        .commit();
                return true;
            }
        });

        Preference performance = findPreference("dream_performance");
        performance.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(SECTION_PERFORMANCE), "DreamPreferenceFragment")
                        .commit();
                return true;
            }
        });

        Preference experimental = findPreference("dream_experimental");
        experimental.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(SECTION_EXPERIMENTAL), "DreamPreferenceFragment")
                        .commit();
                return true;
            }
        });

        // Remove notification preferences on < 4.3 because they will not work
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Android version is below 4.3: removing notification settings.");
            PreferenceScreen screen = getPreferenceScreen();
            screen.removePreference(notify);
        }
    }

    public void setUpNotifyPrefs() {
        Log.d(TAG, "Setting up notification preferences.");
        if (isPackageInstalled("com.beatonma.orbitalsnotificationextension", getActivity())) {
            Preference download = findPreference("notify_download");
            Log.d(TAG, "Package found.");
            try {
                download.setTitle(getString(R.string.open_notify_extension));
                download.setSummary(getString(R.string.open_notify_extension_summ));
                PackageManager pm = getActivity().getPackageManager();
                download.setIntent(pm.getLaunchIntentForPackage("com.beatonma.orbitalsnotificationextension"));
            } catch (Exception e) {
                Log.e(TAG, "Couldn't get launch intent.");
            }
        } else {
            Log.d(TAG, "Notify extension is not installed.");
            PreferenceScreen ps = getPreferenceScreen();
            ps.removePreference(findPreference("pref_notify_pulse"));
            ps.removePreference(findPreference("pref_notify_speed"));
        }
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Package " + packagename + " is not installed.");
            return false;
        }
    }

    public int getSection() {
        return section;
    }

    // Slide fragment off screen, replace and slide back into position
    private void animateFragment(final int container, final int newSection) {
        final View v = getActivity().findViewById(R.id.container);
        int xDest = -v.getWidth();

        v.animate()
                .translationX(xDest)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(150);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(container, DreamPreferenceFragment.newInstance(newSection), "DreamPreferenceFragment")
                        .commit();

                v.animate()
                        .translationX(0)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(150);
            }
        }, 300);
    }

    private void setUpAppearancePrefs() {
        final Preference pref = findPreference("pref_background_image_file");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    String[] mimetypes = {"image/jpeg", "image/png"};
                    i.setType("*/*");
                    i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    i = new Intent();
                    i.setType("image/jpeg|image/png");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                }
                try {
                    startActivityForResult(i, FILE_PICKED);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error: Can't find a file picker. Please install a file explorer.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error opening file picker: " + e.toString());
                }
                return false;
            }
        });
    }

    public void setUpExperimentalPrefs() {
        final CheckBoxPreference cb = (CheckBoxPreference) findPreference("pref_color_background_image");
        cb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (cb.isChecked()) {
                    Intent i;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        String[] mimetypes = {"image/jpeg", "image/png"};
                        i.setType("*/*");
                        i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                    } else {
                        //Toast.makeText(getActivity(), "Currently only supported on Kitkat and above.", Toast.LENGTH_SHORT).show();
                        i = new Intent();
                        //i.setType("image*//*");
                        i.setType("image/jpeg|image/png");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                    }
                    try {
                        startActivityForResult(i, FILE_PICKED);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error: Can't find a file picker. Please install a file explorer.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error opening file picker: " + e.toString());
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        Uri uri = null;
//        ImageUtils imageUtils = new ImageUtils(context, "dream");
//
//        if (requestCode == FILE_PICKED) {
//            if (resultData != null) {
//                uri = resultData.getData();
//                imageUtils.copyFileFromUri(uri);
//
//                File file = new File(context.getCacheDir() + File.separator + "dream_bg_image");
//                Uri cachedFile = Uri.fromFile(file);
//
//                new Crop(cachedFile).output(cachedFile).start(getActivity());
//            }
//        }
//        else if (requestCode == Crop.REQUEST_CROP && resultCode == getActivity().RESULT_OK) {
//            Log.d(TAG, "Cropped image received. Processing in background...");
//            uri = resultData.getData();
//            imageUtils.processBackgroundImage(uri);
//        }
    }

    private void setSharedPreference(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("preferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
