package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class VisualizationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizations);

        Intent intent = getIntent();  // get the intent
        String data = intent.getStringExtra(MainActivity.KEY_SONG); // retrieve the data
    }

}
