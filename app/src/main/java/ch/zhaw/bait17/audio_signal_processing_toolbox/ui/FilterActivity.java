package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.app.Activity;
import android.os.Bundle;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;

/**
 * @author  georgrem, stockan1
 */

public class FilterActivity extends Activity {

    private static final String TAG = VisualisationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
