package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.delay.Flanger;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Bitcrusher;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.SoftClipper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.TubeDistortion;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.distortion.Waveshaper;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FIRCombFilter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.Filter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.filter.FilterUtil;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.RingModulation;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.modulation.Tremolo;
import ch.zhaw.bait17.audio_signal_processing_toolbox.player.Track;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.Constants;
import ch.zhaw.bait17.audio_signal_processing_toolbox.util.MediaListType;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.AudioView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrogramView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrumView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.VisualisationType;

/**
 * The application's entry point.
 *
 * @author georgrem, stockan1
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MediaListFragment.OnTrackSelectedListener,
        AudioEffectFragment.OnItemSelectedListener {

    private static final String TAG_AUDIO_PLAYER_FRAGMENT = "AUDIO_PLAYER";
    private static final String TAG_FILTER_FRAGMENT = "FILTER";
    private static final String TAG_MEDIA_LIST_FRAGMENT = "MEDIA_LIST";
    private static final String TAG_FX_PARAMS_FRAGMENT = "FX_PARAMS";
    private static final String TAG_SETTINGS_FRAGMENT = "SETTINGS";
    private static final String TAG_VISUALISATION_CONFIGURATION_FRAGMENT = "VISUALISATION_CONFIGURATION";
    private static final String TAG_VISUALISATION_FRAGMENT = "VISUALISATION";
    private static final String TAG_APROPOS_FRAGMENT = "APROPOS";
    private static final int NUMBER_OF_AUDIO_VIEWS = 2;

    private AudioPlayerFragment audioPlayerFragment;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<AudioEffect> audioEffects;
    private Toast onBackPressedToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initAudioEffects();
        initFragments();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
        initAudioPlayerFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getFragmentByTag(TAG_MEDIA_LIST_FRAGMENT);
            if (fragment.isVisible()) {
                if (onBackPressedToast != null && onBackPressedToast.getView().getWindowToken() != null) {
                    System.exit(0);
                } else {
                    onBackPressedToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
                    onBackPressedToast.show();
                }
            } else {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, TAG_MEDIA_LIST_FRAGMENT);
                ft.addToBackStack(TAG_MEDIA_LIST_FRAGMENT);
                ft.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        View actionView = menu.findItem(R.id.action_fx_switch).getActionView();
        if (actionView != null) {
            final SwitchCompat fxSwitch =
                    (SwitchCompat) actionView.findViewById(R.id.audio_effects_on_off_switch);
            fxSwitch.setChecked(true);
            fxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (audioPlayerFragment != null) {
                        audioPlayerFragment.setAudioEffectsChainOverride(!isChecked);
                        if (isChecked) {
                            Toast.makeText(getApplication(), "Audio effects on",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication(), "Audio effects off",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return selectMenuItem(item);
    }

    /**
     * Sends the {@code Track} id to the {@code AudioPlayerFragment}
     */
    @Override
    public void onTrackSelected(int trackPos) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.setTrack(trackPos);
        }
    }

    /**
     * Sets the title in the AppBar.
     *
     * @param title the title to be set
     */
    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    // When the fragment event fires
    @Override
    public void onFilterItemSelected(List<AudioEffect> audioEffects) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.setAudioEffects(audioEffects);
        }
    }

    // Layout: onClick event
    public void onClickPlayPauseTrack(View view) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.playPauseTrack();
        }
    }

    // Layout: onClick event
    public void onClickPreviousTrack(View view) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.playPreviousTrack();
        }
    }

    // Layout: onClick event
    public void onClickNextTrack(View view) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.playNextTrack();
        }
    }

    /**
     * Sets the gain.
     *
     * @param gain linear gain
     */
    public void setGain(float gain) {
        if (audioPlayerFragment != null) {
            audioPlayerFragment.setGain(gain);
        }
    }

    /**
     * Returns a list of all available audio effects and filter within the application.
     *
     * @return list of audio effects and filters
     */
    public List<AudioEffect> getAudioEffects() {
        return audioEffects;
    }

    @Nullable
    public AudioEffect getAudioEffectFromType(Class<? extends AudioEffect> clazz) {
        for (AudioEffect fx : audioEffects) {
            if (fx != null && fx.getClass().isAssignableFrom(clazz)) {
                return fx;
            }
        }
        return null;
    }

    /**
     * Loads all fragments into the {@code FrameLayout} placeholders.
     */
    private void initFragments() {
        audioPlayerFragment = new AudioPlayerFragment();

        // set default view on startup
        List<AudioView> activeViews = new ArrayList<>(NUMBER_OF_AUDIO_VIEWS);
        AudioView spectrogramView = new SpectrogramView(ApplicationContext.getAppContext());
        spectrogramView.getInflatedView();
        spectrogramView.setVisualisationType(VisualisationType.POST_FX);
        activeViews.add(spectrogramView);

        // TODO: remove this part later
        AudioView lineSpectrumView = new SpectrumView(ApplicationContext.getAppContext());
        lineSpectrumView.setVisualisationType(VisualisationType.BOTH);
        activeViews.add(lineSpectrumView);

        MediaListFragment mediaListFragment = new MediaListFragment();
        mediaListFragment.setMediaListType(MediaListType.MY_MUSIC);

        ViewFragment viewFragment = new ViewFragment();
        viewFragment.setActiveViews(activeViews);

        VisualisationFragment visualisationFragment = new VisualisationFragment();
        visualisationFragment.setViews(activeViews);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, AudioEffectFragment.newInstance(audioEffects),
                TAG_FILTER_FRAGMENT);
        ft.replace(R.id.content_frame, viewFragment,
                TAG_VISUALISATION_CONFIGURATION_FRAGMENT);
        ft.replace(R.id.content_frame, visualisationFragment,
                TAG_VISUALISATION_FRAGMENT);
        ft.replace(R.id.content_frame, new AproposFragment(), TAG_APROPOS_FRAGMENT);
        ft.replace(R.id.content_frame, new FXParamsFragment(), TAG_FX_PARAMS_FRAGMENT);
        ft.replace(R.id.content_frame, new SettingsFragment(), TAG_SETTINGS_FRAGMENT);
        ft.replace(R.id.content_frame, mediaListFragment, TAG_MEDIA_LIST_FRAGMENT);
        ft.replace(R.id.audio_player_fragment, audioPlayerFragment, TAG_AUDIO_PLAYER_FRAGMENT);
        ft.addToBackStack(null);
        ft.commit();
    }

    private Fragment getFragmentByTag(String tagFragmentName) {
        return getFragmentManager().findFragmentByTag(tagFragmentName);
    }

    /*
        Handle navigation view item clicks here.
        Swap fragments in the main content view.
        Stores containerViewId and associated Fragment
    */
    private boolean selectMenuItem(@NonNull MenuItem item) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        String tagFragmentName = "";
        MediaListType mediaListType;

        switch (item.getItemId()) {
            case R.id.nav_media_list:
                fragment = getFragmentByTag(TAG_MEDIA_LIST_FRAGMENT);
                mediaListType = ((MediaListFragment) fragment).getMediaListType();
                if (mediaListType != MediaListType.MY_MUSIC) {
                    ((MediaListFragment) fragment).setMediaListType(MediaListType.MY_MUSIC);
                    ((MediaListFragment) fragment).reloadList();
                    initAudioPlayerFragment();
                }
                title = getString(R.string.drawer_menu_item_music);
                tagFragmentName = TAG_MEDIA_LIST_FRAGMENT;
                break;
            case R.id.nav_stream_list:
                fragment = getFragmentByTag(TAG_MEDIA_LIST_FRAGMENT);
                mediaListType = ((MediaListFragment) fragment).getMediaListType();
                if (mediaListType != MediaListType.STREAM) {
                    ((MediaListFragment) fragment).setMediaListType(MediaListType.STREAM);
                    ((MediaListFragment) fragment).reloadList();
                    initAudioPlayerFragment();
                }
                title = getString(R.string.drawer_menu_item_music_streaming);
                tagFragmentName = TAG_MEDIA_LIST_FRAGMENT;
                break;
            case R.id.nav_visualisation:
                fragment = getFragmentByTag(TAG_VISUALISATION_FRAGMENT);
                title = getString(R.string.drawer_menu_item_visualisation);
                tagFragmentName = TAG_VISUALISATION_FRAGMENT;
                break;
            case R.id.nav_view:
                fragment = getFragmentByTag(TAG_VISUALISATION_CONFIGURATION_FRAGMENT);
                title = getString(R.string.drawer_menu_item_view_configuration);
                tagFragmentName = TAG_VISUALISATION_CONFIGURATION_FRAGMENT;
                break;
            case R.id.nav_filter:
                fragment = getFragmentByTag(TAG_FILTER_FRAGMENT);
                title = getString(R.string.drawer_menu_item_audio_effects_filter);
                tagFragmentName = TAG_FILTER_FRAGMENT;
                break;
            case R.id.nav_fx_params:
                fragment = getFragmentByTag(TAG_FX_PARAMS_FRAGMENT);
                title = getString(R.string.drawer_menu_item_fx_params);
                tagFragmentName = TAG_FX_PARAMS_FRAGMENT;
                break;
            case R.id.nav_settings:
                fragment = getFragmentByTag(TAG_SETTINGS_FRAGMENT);
                title = getString(R.string.drawer_menu_item_settings);
                tagFragmentName = TAG_SETTINGS_FRAGMENT;
                break;
            case R.id.nav_about:
                fragment = getFragmentByTag(TAG_APROPOS_FRAGMENT);
                title = getString(R.string.drawer_menu_item_about_app);
                tagFragmentName = TAG_APROPOS_FRAGMENT;
                break;
            default:
                break;
        }

        if (fragment != null && !fragment.isVisible()) {
            ft.replace(R.id.content_frame, fragment, tagFragmentName);
            ft.addToBackStack(tagFragmentName);
        }
        ft.commit();

        setTitle(title);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Finds all filter spec files in the raw resources folder and returns a list of
     * {@code Filter} objects.
     *
     * @return a list of {@code Filter}s
     */
    private List<Filter> getAllFilters() {
        List<Filter> filters = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            int rawId = getResources().getIdentifier(field.getName(), "raw",
                    ApplicationContext.getAppContext().getPackageName());
            if (rawId != 0) {
                TypedValue value = new TypedValue();
                getResources().getValue(rawId, value, true);
                String[] s = value.string.toString().split("/");
                String filename = s[s.length - 1];
                if (filename.startsWith("b_fir")) {
                    filters.add(FilterUtil.getFilter(getResources().openRawResource(rawId)));
                }
            }
        }
        return filters;
    }

    private void initAudioEffects() {
        audioEffects = new ArrayList<>();
        audioEffects.add(null);          // No filter
        audioEffects.addAll(getAllFilters());
        audioEffects.add(new FIRCombFilter(Constants.FIR_COMB_FILTER_DEFAULT_DELAY));
        audioEffects.add(new Bitcrusher(Constants.BITCRUSHER_DEFAULT_NORM_FREQUENCY,
                Constants.BITCRUSHER_DEFAULT_BITS));
        audioEffects.add(new Waveshaper(Constants.WAVESHAPER_DEFAULT_THRESHOLD));
        audioEffects.add(new SoftClipper(Constants.SOFT_CLIPPER_DEFAULT_CLIPPING_FACTOR));
        audioEffects.add(new TubeDistortion());
        audioEffects.add(new RingModulation(Constants.RING_MODULATOR_DEFAULT_FREQUENCY));
        audioEffects.add(new Tremolo(Constants.TREMOLO_DEFAULT_MOD_FREQUENCY,
                Constants.TREMOLO_DEFAULT_AMPLITUDE));
        audioEffects.add(new Flanger(Constants.FLANGER_DEFAULT_RATE,
                Constants.FLANGER_DEFAULT_AMPLITUDE, Constants.FLANGER_DEFAULT_DELAY));
    }

    private void initAudioPlayerFragment() {
        Fragment mlf = getFragmentByTag(TAG_MEDIA_LIST_FRAGMENT);
        List<Track> tracks = ((MediaListFragment) mlf).getTracks();
        RecyclerView recyclerView = ((MediaListFragment) mlf).getRecyclerView();
        MediaListType mediaListType = ((MediaListFragment) mlf).getMediaListType();
        if (audioPlayerFragment != null) {
            audioPlayerFragment.setRecyclerView(recyclerView);
            audioPlayerFragment.setTracks(tracks);
            audioPlayerFragment.setMediaListType(mediaListType);
        }
    }
}
