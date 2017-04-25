package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.dsp.AudioEffect;
import ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom.AudioEffectAdapter;

/**
 * @author georgrem, stockan1
 */

public class AudioEffectFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String BUNDLE_ARGUMENT_FILTERS =
            AudioEffectFragment.class.getSimpleName() + ".AUDIOEFFECT";

    private Spinner spinnerFirstFilter;
    private Spinner spinnerSecondFilter;
    private Spinner spinnerThirdFilter;
    private Spinner spinnerFourthFilter;
    private List<AudioEffect> audioEffects;
    private List<AudioEffect> activeEffects;

    // listener of the interface type
    private OnItemSelectedListener listener;

    // the event that the fragment will use to communicate
    public interface OnItemSelectedListener {
        void onFilterItemSelected(List<AudioEffect> audioEffect);
    }

    /*
        In certain cases, your fragment may want to accept certain arguments.
        A common pattern is to create a static newInstance method for creating a Fragment with arguments.
        This is because a Fragment must have only a constructor with no arguments.
        From: <a href="https://guides.codepath.com/android/Creating-and-Using-Fragments#communicating-with-fragments">codepath.com</a>
    */
    public static AudioEffectFragment newInstance(ArrayList<AudioEffect> audioEffects) {
        AudioEffectFragment fragment = new AudioEffectFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(BUNDLE_ARGUMENT_FILTERS, audioEffects);
        fragment.setArguments(arguments);
        return fragment;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        // This method avoid to call super.onAttach(context) if I'm not using api 23 or more
        if (Build.VERSION.SDK_INT >= 23) {
            super.onAttach(context);
            onAttachToContext(context);
        }
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(activity);
        }
    }

    /*
     * This method will be called from one of the two previous method
     * Store the listener (activity) that will have events fired once the fragment is attached
     */
    private void onAttachToContext(Context context) {
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement FilterFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        Bundle arguments = this.getArguments();
        if (arguments.getParcelableArrayList(BUNDLE_ARGUMENT_FILTERS) != null)
            audioEffects = arguments.getParcelableArrayList(BUNDLE_ARGUMENT_FILTERS);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_effect_view, container, false);

        AudioEffectAdapter audioEffectAdapter = new AudioEffectAdapter(audioEffects);

        spinnerFirstFilter = (Spinner) view.findViewById(R.id.spinner_first_fx);
        spinnerFirstFilter.setAdapter(audioEffectAdapter);
        spinnerFirstFilter.setOnItemSelectedListener(this);

        spinnerSecondFilter = (Spinner) view.findViewById(R.id.spinner_second_fx);
        spinnerSecondFilter.setAdapter(audioEffectAdapter);
        spinnerSecondFilter.setOnItemSelectedListener(this);

        spinnerThirdFilter = (Spinner) view.findViewById(R.id.spinner_third_fx);
        spinnerThirdFilter.setAdapter(audioEffectAdapter);
        spinnerThirdFilter.setOnItemSelectedListener(this);

        spinnerFourthFilter = (Spinner) view.findViewById(R.id.spinner_fourth_fx);
        spinnerFourthFilter.setAdapter(audioEffectAdapter);
        spinnerFourthFilter.setOnItemSelectedListener(this);

        spinnerFirstFilter.getSelectedItem();

        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activeEffects = new ArrayList<>();

        AudioEffect audioEffect = (AudioEffect) spinnerFirstFilter.getSelectedItem();
        if (audioEffect != null)
            activeEffects.add(audioEffect);

        audioEffect = (AudioEffect) spinnerSecondFilter.getSelectedItem();
        if (audioEffect != null)
            activeEffects.add(audioEffect);

        audioEffect = (AudioEffect) spinnerThirdFilter.getSelectedItem();
        if (audioEffect != null)
            activeEffects.add(audioEffect);

        audioEffect = (AudioEffect) spinnerFourthFilter.getSelectedItem();
        if (audioEffect != null)
            activeEffects.add(audioEffect);

        // fire the event
        if (listener != null)
            listener.onFilterItemSelected(activeEffects);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
