package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FilterType;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FilterUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;

/**
 * @author georgrem, stockan1
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MediaListFragment.OnTrackSelectedListener {

    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Map<FilterType, Filter> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new MediaListFragment());
        ft.replace(R.id.audio_player_fragment, new AudioPlayerFragment());
        ft.commit();

        initFilters();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return selectMenuItem(item);
    }

    // Layout: onClick event
    public void onClickPlayPauseTrack(View view) {
        AudioPlayerFragment apf = getAudioPlayerFragment();
        if (apf != null) {
            apf.playTrack();
        }
    }

    // Layout: onClick event
    public void onClickPreviousTrack(View view) {
        /*
        AudioPlayerFragment apf = getAudioPlayerFragment();
        if (apf != null) {
            apf.playPreviousTrack();
        }
        */
    }

    // Layout: onClick event
    public void onClickNextTrack(View view) {
        /*
        AudioPlayerFragment apf = getAudioPlayerFragment();
        if (apf != null) {
            apf.playNextTrack();
        }
        */
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        Filter filter = null;
        switch (view.getId()) {
            case R.id.radio_low_pass_filter:
                if (checked) {
                    filter = filters.get(FilterType.LOWPASS);
                }
                break;
            case R.id.radio_high_pass_filter:
                if (checked) {
                    filter = filters.get(FilterType.HIGHPASS);
                }
                break;
            case R.id.radio_band_pass_filter:
                if (checked) {
                    filter = filters.get(FilterType.BANDPASS);
                }
                break;
            case R.id.radio_band_stop_filter:
                if (checked) {
                    filter = filters.get(FilterType.BANDSTOP);
                }
                break;
            case R.id.radio_no_filter:
                if (checked) {
                    filter = null;
                }
                break;
            default:
        }
        AudioPlayerFragment apf = getAudioPlayerFragment();
        if (apf != null) {
            apf.setFilter(filter);
        }
    }

    @Nullable
    private AudioPlayerFragment getAudioPlayerFragment() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.audio_player_fragment);
        if (fragment instanceof AudioPlayerFragment) {
            return (AudioPlayerFragment) fragment;
        } else {
            return null;
        }
    }

    /*
        Handle navigation view item clicks here.
        Swap fragments in the main content view.
    */
    private boolean selectMenuItem(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Store containerViewId and associated Fragment
        Fragment fragment = null;
        String title = "";

        switch (id) {
            case R.id.nav_media_list:
                fragment = new MediaListFragment();
                title = "My Music";
                break;
            case R.id.nav_visualisation:
                fragment = new VisualisationFragment();
                title = "Visualisation";
                break;
            case R.id.nav_visualisation_configuration:
                fragment = new VisualisationConfigurationFragment();
                title = "Visualisation Configuration";
                break;
            case R.id.nav_filter:
                fragment = FilterFragment.newInstance(filters.values().toArray(new Filter[filters.size()]));
                title = "Filter";
                break;
            case R.id.nav_about:
                title = "About the app";
                break;
            default:
                break;
        }


        if (fragment != null) {
            ft.replace(R.id.content_frame, fragment);
        }
        ft.addToBackStack(null);
        ft.commit();

        setTitle(title);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Nullable
    private Filter getLowPassFilter() {
        return FilterUtil.getFilter(getResources().openRawResource(R.raw.b_fir_lowpass));
    }

    @Nullable
    private Filter getHighPassFilter() {
        return FilterUtil.getFilter(getResources().openRawResource(R.raw.b_fir_highpass));
    }

    @Nullable
    private Filter getBandPassFilter() {
        return FilterUtil.getFilter(getResources().openRawResource(R.raw.b_fir_bandpass));
    }

    @Nullable
    private Filter getBandStopFilter() {
        return FilterUtil.getFilter(getResources().openRawResource(R.raw.b_fir_bandstop));
    }

    @Override
    public void onTrackSelected(List<Track> tracks, int trackPos) {
        // Send track to audio player fragment
        Fragment fragment = getFragmentManager().findFragmentById(R.id.audio_player_fragment);
        if (fragment instanceof AudioPlayerFragment) {
            ((AudioPlayerFragment) fragment).setTrack(tracks.get(trackPos));
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    private void initFilters() {
        filters = new HashMap<>();
        filters.put(FilterType.LOWPASS, getLowPassFilter());
        filters.put(FilterType.HIGHPASS, getHighPassFilter());
        filters.put(FilterType.BANDPASS, getBandPassFilter());
        filters.put(FilterType.BANDSTOP, getBandStopFilter());
    }

}
