package com.truiton.customspeechrecognizer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class DisplayResultsForYT extends AppCompatActivity {
    private final String BASE_YT_URL="https://youtu.be/";
    String VIDEO_ID="7wtfhZwyrcc";
    String TIME_CONSTANT="?t=";
    String TIME_VALUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        Intent i = new Intent(Intent.ACTION_VIEW);
        VIDEO_ID = getIntent().getExtras().getString("VIDEO_URI");

        TIME_VALUE=String.valueOf(Math.round(Double.parseDouble(getIntent().getExtras().getString("START"))));



        String url=BASE_YT_URL+VIDEO_ID+TIME_CONSTANT+TIME_VALUE;
        Log.d("YT",url);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
