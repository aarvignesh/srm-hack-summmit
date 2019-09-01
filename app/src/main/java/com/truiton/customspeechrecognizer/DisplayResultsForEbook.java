package com.truiton.customspeechrecognizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import static com.truiton.customspeechrecognizer.VoiceRecognitionActivity.PREFS_VC;
import static com.truiton.customspeechrecognizer.VoiceRecognitionActivity.VCD;

public class DisplayResultsForEbook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results_for_ebook);
        WebView mWebView =  findViewById(R.id.webview);
        SharedPreferences sp = getSharedPreferences(PREFS_VC , Context.MODE_PRIVATE);
        String  searchText = sp.getString(VCD,"-1");
//        String searchText = getIntent().getStringExtra("SEARCH_TEXT");

        mWebView.loadUrl("file:///android_asset/story.html");
        Toast.makeText(this, searchText, Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebView.findAllAsync(searchText);
        }
// now it will not fail here

    }
}
