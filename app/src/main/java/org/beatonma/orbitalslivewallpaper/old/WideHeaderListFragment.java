package org.beatonma.orbitalslivewallpaper.old;

import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beatonma.orbitalslivewallpaper.R;

import org.beatonma.orbitalslivewallpaper.old.dream.DreamPreferenceFragment;
import org.beatonma.orbitalslivewallpaper.old.livewallpaper.LwpPreferenceFragment;

/**
 * Created by Michael on 24/01/2015.
 */
public class WideHeaderListFragment extends ListFragment {
    private final static String TAG = "WideHeaderListFragment";
    ListView listView;

    int nav;
    int section;

    private String[] SECTIONS = {};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nav = getArguments().getInt("nav", 0);
            section = getArguments().getInt("section", 1);
            if (section < 0) {
                section = 0;
            }
        }
        else {
            //Log.d(TAG, "No arguments available.");
        }

        switch (nav) {
            case 0:
                SECTIONS = getActivity().getResources().getStringArray(R.array.lwp_sections);
                break;
            case 1:
                SECTIONS = getActivity().getResources().getStringArray(R.array.dream_sections);
                break;
            default:
                SECTIONS = getActivity().getResources().getStringArray(R.array.lwp_sections);
        }

        String [] temp = new String[SECTIONS.length - 1];
        for (int i = 0; i < SECTIONS.length - 1; i++) {
            temp[i] = SECTIONS[i+1];
        }
        SECTIONS = temp;
    }

    public static WideHeaderListFragment newInstance(int nav, int section) {
        WideHeaderListFragment fragment = new WideHeaderListFragment();
        Bundle args = new Bundle();
        args.putInt("nav", nav);
        args.putInt("section", section);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.wide_header_list_fragment, container, false);
        listView = (ListView) rootView.findViewById(android.R.id.list);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String [] headers = {"Test 1", "Test 2"};

        setListAdapter(new ArrayAdapter<String>(
                getActivity(),
                R.layout.navigation_drawer_list_item,
                android.R.id.text1,
                SECTIONS
        ));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != section) {
                    LauncherNav activity = (LauncherNav) getActivity();
                    View v = activity.findViewById(R.id.wide_container);
                    activity.removeToolbarTail();
                    animateFragment(v, nav, position);
                    section = position;
                }
            }
        });

        listView.setItemChecked(section, true);
    }

    public void animateFragment(final View v, final int nav, final int position) {
        // Move left (under list, using elevation) if lollipop, otherwise move right off the screen
        int xDest = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? -v.getWidth() : v.getWidth();

        v.animate()
                .translationX(xDest)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(100);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction()
                        .replace(R.id.wide_container, nav == 0 ? LwpPreferenceFragment.newInstance(position + 1) : DreamPreferenceFragment.newInstance(position + 1), nav == 0 ? "LwpPreferenceFragment" : "DreamPreferenceFragment")
                        .commit();

                v.animate()
                        .translationX(0)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(100);
            }
        }, 300);
    }
}
