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
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.LineSpectrumView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrogramView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.SpectrumView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.visualisation.WaveformView;

/**
 * @author georgrem, stockan1
 */

public class ViewFragment extends Fragment implements
        AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = ViewFragment.class.getSimpleName();

    private static Map<String, AudioView> views = new HashMap<>();

    private RadioGroup radioGroupTop;
    private RadioGroup radioGroupBottom;
    private Spinner spinnerTop;
    private Spinner spinnerBottom;
    private List<AudioView> activeViews;
    private boolean isPreFilterViewTop;
    private boolean isPreFilterViewBottom;

    static {
        views.put("No view", null);
        views.put("Line Spectrum", new LineSpectrumView(ApplicationContext.getAppContext()));
        views.put("Spectrogram", new SpectrogramView(ApplicationContext.getAppContext()));
        views.put("Spectrum", new SpectrumView(ApplicationContext.getAppContext()));
        views.put("Waveform", new WaveformView(ApplicationContext.getAppContext()));
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_view, container, false);

        Set<String> keys = views.keySet();
        String[] viewNames = keys.toArray(new String[keys.size()]);
        Arrays.sort(viewNames);
        ViewAdapter viewAdapter = new ViewAdapter(viewNames);

        spinnerTop = (Spinner) view.findViewById(R.id.spinner_first_view);
        spinnerTop.setAdapter(viewAdapter);
        spinnerTop.setSelection(2);
        spinnerTop.setOnItemSelectedListener(this);

        spinnerBottom = (Spinner) view.findViewById(R.id.spinner_second_view);
        spinnerBottom.setAdapter(viewAdapter);
        spinnerBottom.setSelection(1);
        spinnerBottom.setOnItemSelectedListener(this);

        radioGroupTop = (RadioGroup) view.findViewById(R.id.radioGroup_first_view);
        radioGroupTop.setOnCheckedChangeListener(this);

        radioGroupBottom = (RadioGroup) view.findViewById(R.id.radioGroup_second_view);
        radioGroupBottom.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<AudioView> getActiveViews() {
        return activeViews;
    }

    // to dynamically add the same view to a wrapper layout we have to use the inflate of this views
    // because the inflate makes the view unique in the ViewGroup then the view object self is not unique
    //  From: {@link http://androblip.huiges.nl/2010/05/14/add-a-view-to-a-wrapper-multiple-times-with-inflate/}
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activeViews = new ArrayList<>();

        AudioView audioView = views.get(spinnerTop.getSelectedItem());
        if (audioView != null) {
            audioView = audioView.getInflatedView();
            isPreFilterViewTop = radioGroupTop.getCheckedRadioButtonId() == R.id.radioButton_preFilter_first_view;
            audioView.setPreFilterView(isPreFilterViewTop);
            activeViews.add(audioView);
        }

        audioView = views.get(spinnerBottom.getSelectedItem());
        if (audioView != null) {
            audioView = audioView.getInflatedView();
            isPreFilterViewBottom = radioGroupBottom.getCheckedRadioButtonId() == R.id.radioButton_preFilter_second_view;
            audioView.setPreFilterView(isPreFilterViewBottom);
            activeViews.add(audioView);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.radioButton_preFilter_first_view:
                isPreFilterViewTop = true;
                break;
            case R.id.radioButton_postFilter_first_view:
                isPreFilterViewTop = false;
                break;
            case R.id.radioButton_preFilter_second_view:
                isPreFilterViewBottom = true;
                break;
            case R.id.radioButton_postFilter_second_view:
                isPreFilterViewBottom = false;
                break;
            default:
                break;
        }
    }

}
