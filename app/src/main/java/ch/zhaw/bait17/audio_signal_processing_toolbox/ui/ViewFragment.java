package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom.ViewAdapter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.AudioView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrogramView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.ViewName;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.VisualisationType;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.WaveformView;

/**
 * @author georgrem, stockan1
 */

public class ViewFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static final int NUMBER_OF_AUDIO_VIEWS = 2;

    private static Map<ViewName, AudioView> views = new HashMap<>();
    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private RadioButton radioButtonView1PreAudioEffect;
    private RadioButton radioButtonView2PreAudioEffect;
    private RadioButton radioButtonView1PreAndPostAudioEffect;
    private RadioButton radioButtonView2PreAndPostAudioEffect;
    private Spinner spinner1;
    private Spinner spinner2;
    private List<AudioView> activeViews;
    private VisualisationType currentVisualisationTypeView1 = VisualisationType.PRE_FX;
    private VisualisationType currentVisualisationTypeView2 = VisualisationType.PRE_FX;

    static {
        views.put(ViewName.NO_VIEW, null);
        views.put(ViewName.WAVEFORM, new WaveformView(ApplicationContext.getAppContext()));
        views.put(ViewName.SPECTROGRAM, new SpectrogramView(ApplicationContext.getAppContext()));
        //views.put(ViewName.SPECTRUM, new LineSpectrumView(ApplicationContext.getAppContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_config_view, container, false);

        Set<ViewName> keys = views.keySet();
        ViewName[] viewNames = keys.toArray(new ViewName[views.size()]);
        Arrays.sort(viewNames);
        ViewAdapter viewAdapter = new ViewAdapter(viewNames);
        activeViews = new ArrayList<>(NUMBER_OF_AUDIO_VIEWS);

        spinner1 = (Spinner) rootView.findViewById(R.id.spinner_view1);
        spinner1.setAdapter(viewAdapter);
        spinner1.setSelection(2);
        spinner1.setOnItemSelectedListener(this);

        spinner2 = (Spinner) rootView.findViewById(R.id.spinner_view2);
        spinner2.setAdapter(viewAdapter);
        spinner2.setSelection(0);
        spinner2.setOnItemSelectedListener(this);

        radioGroup1 = (RadioGroup) rootView.findViewById(R.id.radioGroup_view1);
        radioGroup1.setOnCheckedChangeListener(this);
        radioGroup2 = (RadioGroup) rootView.findViewById(R.id.radioGroup_view2);
        radioGroup2.setOnCheckedChangeListener(this);

        radioButtonView1PreAudioEffect = (RadioButton) rootView.findViewById(
                R.id.radioButton_pre_filter_view1);
        radioButtonView1PreAndPostAudioEffect = (RadioButton) rootView.findViewById(
                R.id.radioButton_pre_and_post_view1);

        radioButtonView2PreAudioEffect = (RadioButton) rootView.findViewById(
                R.id.radioButton_pre_filter_view2);
        radioButtonView2PreAndPostAudioEffect = (RadioButton) rootView.findViewById(
                R.id.radioButton_pre_and_post_view2);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * To dynamically add the same view to a wrapper layout we have to use the inflate of this views
     * because the inflate makes the view unique in the ViewGroup then the view object self is not unique
     * From <a href="http://androblip.huiges.nl/2010/05/14/add-a-view-to-a-wrapper-multiple-times-with-inflate/">androblip.huiges.nl</a>
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activeViews.clear();

        Object selected = spinner1.getSelectedItem();
        if (selected instanceof ViewName) {
            AudioView audioView = views.get(selected);
            if (audioView != null) {
                audioView = audioView.getInflatedView();
                audioView.setVisualisationType(currentVisualisationTypeView1);
                activeViews.add(audioView);
            }
        }

        selected = spinner2.getSelectedItem();
        if (selected instanceof ViewName) {
            AudioView audioView = views.get(selected);
            if (audioView != null) {
                audioView = audioView.getInflatedView();
                audioView.setVisualisationType(currentVisualisationTypeView2);
                activeViews.add(audioView);
            }
        }

        handleRadioButtonsVisibility();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public List<AudioView> getActiveViews() {
        return activeViews;
    }

    private void hideRadioButton(@NonNull RadioButton radioButton) {
        radioButton.setVisibility(View.GONE);
    }

    private void showRadioButton(@NonNull RadioButton radioButton) {
        radioButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        VisualisationType visualisationType;
        switch (checkedId) {
            case R.id.radioButton_pre_filter_view1:
                visualisationType = VisualisationType.PRE_FX;
                break;
            case R.id.radioButton_post_filter_view1:
                visualisationType = VisualisationType.POST_FX;
                break;
            case R.id.radioButton_pre_and_post_view1:
                visualisationType = VisualisationType.BOTH;
                break;
            case R.id.radioButton_pre_filter_view2:
                visualisationType = VisualisationType.PRE_FX;
                break;
            case R.id.radioButton_post_filter_view2:
                visualisationType = VisualisationType.POST_FX;
                break;
            case R.id.radioButton_pre_and_post_view2:
                visualisationType = VisualisationType.BOTH;
                break;
            default:
                visualisationType = null;
        }

        if (visualisationType != null) {
            if (group == radioGroup1) {
                currentVisualisationTypeView1 = visualisationType;
                if (activeViews.size() > 0) {
                    activeViews.get(0).setVisualisationType(visualisationType);
                }
            } else if (group == radioGroup2) {
                currentVisualisationTypeView2 = visualisationType;
                if (activeViews.size() > 1) {
                    activeViews.get(1).setVisualisationType(visualisationType);
                }
            }
        }

        handleRadioButtonsVisibility();
    }

    private void handleRadioButtonsVisibility() {
        Object selected = spinner1.getSelectedItem();
        if (selected instanceof ViewName) {
            if (selected.equals(ViewName.SPECTROGRAM)) {
                hideRadioButton(radioButtonView1PreAndPostAudioEffect);
                if (radioGroup1.getCheckedRadioButtonId() == R.id.radioButton_pre_and_post_view1) {
                    radioButtonView1PreAudioEffect.performClick();
                }
            } else {
                showRadioButton(radioButtonView1PreAndPostAudioEffect);
            }
        }

        selected = spinner2.getSelectedItem();
        if (selected instanceof ViewName) {
            if (selected.equals(ViewName.SPECTROGRAM)) {
                hideRadioButton(radioButtonView2PreAndPostAudioEffect);
                if (radioGroup2.getCheckedRadioButtonId() == R.id.radioButton_pre_and_post_view2) {
                    radioButtonView2PreAudioEffect.performClick();
                }
            } else {
                showRadioButton(radioButtonView2PreAndPostAudioEffect);
            }
        }
    }

    public void preDefineActiveViews() {
        if (activeViews == null) activeViews = new ArrayList<>(NUMBER_OF_AUDIO_VIEWS);

        AudioView waveformView = new WaveformView(ApplicationContext.getAppContext());
        waveformView.getInflatedView();
        waveformView.setVisualisationType(VisualisationType.BOTH);

        AudioView spectrogramView = new SpectrogramView(ApplicationContext.getAppContext());
        spectrogramView.getInflatedView();
        spectrogramView.setVisualisationType(VisualisationType.POST_FX);

        activeViews.add(waveformView);
        activeViews.add(spectrogramView);
    }
}
