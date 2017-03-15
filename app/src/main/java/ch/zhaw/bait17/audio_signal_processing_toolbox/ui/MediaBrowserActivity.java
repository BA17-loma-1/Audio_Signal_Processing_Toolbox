package ch.zhaw.bait17.audio_signal_processing_toolbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.zhaw.bait17.audio_signal_processing_toolbox.R;

/**
 * @author georgrem, stockan1
 */

public class MediaBrowserActivity extends AppCompatActivity {

    public static final String SAMPLE = "Sample media files";
    public static final String DEVICE = "Media files from device";
    public final static String KEY_MEDIUMTYPE = "ch.zhaw.bait17.audio_signal_processing_toolbox.MEDIUMTYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_browser);

        String[] menuItems = {SAMPLE, DEVICE};
        ListView listView = (ListView) findViewById(R.id.mediumType_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, menuItems);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String mediumType = (String) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MediaBrowserActivity.this, MediaListActivity.class);
                intent.putExtra(KEY_MEDIUMTYPE, mediumType);
                startActivity(intent);
            }
        });
    }

}