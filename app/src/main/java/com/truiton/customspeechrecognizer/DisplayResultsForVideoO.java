package com.truiton.customspeechrecognizer;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class DisplayResultsForVideoO extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results_for_o);

        String VIDEO_URI = getIntent().getStringExtra("VIDEO_URI");
        String MODE = getIntent().getStringExtra("MODE");
        String START = getIntent().getStringExtra("START");

        VideoView videoView =(VideoView)findViewById(R.id.videoView);

        //Creating MediaController
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file
        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/"+VIDEO_URI);
        Log.d("URIII",uri.toString());

        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        Double time=Double.parseDouble(START)*1000;
        int inttime = (int) Math.round(time);
        Toast.makeText(this, String.valueOf(time), Toast.LENGTH_SHORT).show();
        videoView.seekTo(inttime);
    }
}
