package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.zhaw.bait17.audio_signal_processing_toolbox.ApplicationContext;
import ch.zhaw.bait17.audio_signal_processing_toolbox.CustomSpinnerAdapter;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.AudioView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.LineSpectrumView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrogramView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrumView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.WaveformView;

/**
 * @author georgrem, stockan1
 */

public class VisualisationConfigurationFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = VisualisationConfigurationFragment.class.getSimpleName();

    private static Map<String, AudioView> views = new HashMap<>();

    private RadioGroup radioGroupTop;
    private RadioGroup radioGroupBottom;
    private Spinner spinnerTop;
    private Spinner spinnerBottom;
    private AudioView[] activeViews = {
            new SpectrogramView(ApplicationContext.getAppContext()),
            new SpectrogramView(ApplicationContext.getAppContext())};
    private int images[] = {R.drawable.line_spectrum, R.drawable.spectrogram,
            R.drawable.spectrum, R.drawable.waveform};

    static {
        views.put("Line Spectrum", new LineSpectrumView(ApplicationContext.getAppContext()));
        views.put("Spectrogram", new SpectrogramView(ApplicationContext.getAppContext()));
        views.put("Spectrum", new SpectrumView(ApplicationContext.getAppContext()));
        views.put("Waveform", new WaveformView(ApplicationContext.getAppContext()));
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.visualisation_configuration_view, container, false);

        Set<String> keys = views.keySet();
        String[] viewNames = keys.toArray(new String[keys.size()]);
        Arrays.sort(viewNames);
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(ApplicationContext.getAppContext(), images, viewNames);

        spinnerTop = (Spinner) view.findViewById(R.id.top_spinner);
        spinnerTop.setAdapter(customSpinnerAdapter);
        spinnerTop.setOnItemSelectedListener(this);

        spinnerBottom = (Spinner) view.findViewById(R.id.bottom_spinner);
        spinnerBottom.setAdapter(customSpinnerAdapter);
        spinnerBottom.setOnItemSelectedListener(this);

        radioGroupTop = (RadioGroup) view.findViewById(R.id.radioGroup_top);
        radioGroupTop.setOnCheckedChangeListener(this);

        radioGroupBottom = (RadioGroup) view.findViewById(R.id.radioGroup_bottom);
        radioGroupBottom.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public AudioView[] getActiveViews() {
        return activeViews;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AudioView audioView = views.get(parent.getItemAtPosition(position));
        // to dynamically add the same view to a wrapper layout we have to use the inflate of this views
        // because the inflate makes the view unique in the ViewGroup then the view object self is not unique
        //  From: {@link http://androblip.huiges.nl/2010/05/14/add-a-view-to-a-wrapper-multiple-times-with-inflate/}
        audioView = audioView.getInflatedView();
        switch (parent.getId()) {
            case R.id.top_spinner:
                boolean isPreFilterViewTop = radioGroupTop.getCheckedRadioButtonId() == R.id.radio_preFilter_top;
                audioView.setPreFilterView(isPreFilterViewTop);
                activeViews[0] = audioView;
                break;
            case R.id.bottom_spinner:
                boolean isPreFilterViewBottom = radioGroupBottom.getCheckedRadioButtonId() == R.id.radio_preFilter_bottom;
                audioView.setPreFilterView(isPreFilterViewBottom);
                activeViews[1] = audioView;
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.radio_preFilter_top:
                activeViews[0].setPreFilterView(true);
                break;
            case R.id.radio_postFilter_top:
                activeViews[0].setPreFilterView(false);
                break;
            case R.id.radio_preFilter_bottom:
                activeViews[1].setPreFilterView(true);
                break;
            case R.id.radio_postFilter_bottom:
                activeViews[1].setPreFilterView(false);
                break;
            default:
                break;
        }
    }

}
