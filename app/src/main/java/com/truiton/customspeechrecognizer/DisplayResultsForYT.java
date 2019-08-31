package com.truiton.customspeechrecognizer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DisplayResultsForYT extends AppCompatActivity {
    private final String BASE_YT_URL="https://youtu.be/7wtfhZwyrcc?t=";
    String VIDEO_ID="7wtfhZwyrcc";
    String TIME_CONSTANT="?t=";
    String TIME_VALUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);
        Intent i = new Intent(Intent.ACTION_VIEW);
        String url=BASE_YT_URL+"50";
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
