package com.truiton.customspeechrecognizer;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class DisplayResultsForVideoO extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results_for_o);

        VideoView videoView =(VideoView)findViewById(R.id.videoView);

        //Creating MediaController
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file
        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/1.mp4");
        Log.d("URIII",uri.toString());

        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);

        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        videoView.seekTo(12000);
    }
}
