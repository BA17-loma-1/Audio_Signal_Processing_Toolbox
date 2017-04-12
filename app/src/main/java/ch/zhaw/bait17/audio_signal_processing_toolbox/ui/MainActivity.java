package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FilterUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.AudioView;

/**
 * @author georgrem, stockan1
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MediaListFragment.OnTrackSelectedListener,
        FilterFragment.OnItemSelectedListener {

    private static final String TAG_AUDIO_PLAYER_FRAGMENT = "AUDIO_PLAYER";
    private static final String TAG_FILTER_FRAGMENT = "FILTER";
    private static final String TAG_MEDIA_LIST_FRAGMENT = "MEDIA_LIST";
    private static final String TAG_VISUALISATION_CONFIGURATION_FRAGMENT = "VISUALISATION_CONFIGURATION";
    private static final String TAG_VISUALISATION_FRAGMENT = "VISUALISATION";

    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private List<Filter> filters;


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

        initFilters();
        initFragments();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
        setTrackList();
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
            apf.playPauseTrack();
        }
    }

    // Layout: onClick event
    public void onClickPreviousTrack(View view) {
        AudioPlayerFragment apf = getAudioPlayerFragment();
        if (apf != null) {
            apf.playPreviousTrack();
        }
    }

    // Layout: onClick event
    public void onClickNextTrack(View view) {
        AudioPlayerFragment apf = getAudioPlayerFragment();
        if (apf != null) {
            apf.playNextTrack();
        }
    }

    // when the fragment event fires
    @Override
    public void onFilterItemSelected(Filter[] filters) {
        AudioPlayerFragment apf = getAudioPlayerFragment();
        if (apf != null) {
            apf.setFilters(filters);
        }
    }

    private void initFragments() {
        Fragment visualisationConfigurationFragment = new VisualisationConfigurationFragment();
        AudioView[] activeViews = ((VisualisationConfigurationFragment) visualisationConfigurationFragment).getActiveViews();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, FilterFragment.newInstance(filters),
                TAG_FILTER_FRAGMENT);
        ft.replace(R.id.content_frame, visualisationConfigurationFragment,
                TAG_VISUALISATION_CONFIGURATION_FRAGMENT);
        ft.replace(R.id.content_frame, VisualisationFragment.newInstance(activeViews),
                TAG_VISUALISATION_FRAGMENT);
        ft.replace(R.id.content_frame, new MediaListFragment(), TAG_MEDIA_LIST_FRAGMENT);
        ft.replace(R.id.audio_player_fragment, new AudioPlayerFragment());
        ft.addToBackStack(null);
        ft.commit();
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

    private Fragment getFragmentByTag(String tag_fragmentName) {
        return getFragmentManager().findFragmentByTag(tag_fragmentName);
    }

    /*
        Handle navigation view item clicks here.
        Swap fragments in the main content view.
    */
    private boolean selectMenuItem(@NonNull MenuItem item) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // Store containerViewId and associated Fragment
        Fragment fragment = null;
        String title = "";
        String tag_fragmentName = "";

        switch (item.getItemId()) {
            case R.id.nav_media_list:
                fragment = getFragmentByTag(TAG_MEDIA_LIST_FRAGMENT);
                title = "My Music";
                tag_fragmentName = TAG_MEDIA_LIST_FRAGMENT;
                break;
            case R.id.nav_visualisation:
                Fragment vcf = getFragmentByTag(TAG_VISUALISATION_CONFIGURATION_FRAGMENT);
                AudioView[] activeViews = ((VisualisationConfigurationFragment) vcf).getActiveViews();
                fragment = getFragmentByTag(TAG_VISUALISATION_FRAGMENT);
                ((VisualisationFragment) fragment).setViews(activeViews);
                title = "Visualisation";
                tag_fragmentName = TAG_VISUALISATION_FRAGMENT;
                break;
            case R.id.nav_visualisation_configuration:
                fragment = getFragmentByTag(TAG_VISUALISATION_CONFIGURATION_FRAGMENT);
                title = "Visualisation Configuration";
                tag_fragmentName = TAG_VISUALISATION_CONFIGURATION_FRAGMENT;
                break;
            case R.id.nav_filter:
                fragment = getFragmentByTag(TAG_FILTER_FRAGMENT);
                title = "Filter";
                tag_fragmentName = TAG_FILTER_FRAGMENT;
                break;
            case R.id.nav_about:
                title = "About the app";
                break;
            default:
                break;
        }

        if (fragment != null) {
            ft.replace(R.id.content_frame, fragment, tag_fragmentName);
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
            ((AudioPlayerFragment) fragment).setTrack(trackPos);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void initFilters() {
        filters = new ArrayList<>();
        filters.add(null);
        filters.add(getLowPassFilter());
        filters.add(getHighPassFilter());
        filters.add(getBandPassFilter());
        filters.add(getBandStopFilter());
    }

    private void setTrackList() {
        Fragment mlf = getFragmentByTag(TAG_MEDIA_LIST_FRAGMENT);
        List<Track> tracks = ((MediaListFragment) mlf).getTracks();
        AudioPlayerFragment apf = getAudioPlayerFragment();
        apf.setTracks(tracks);
    }

}
