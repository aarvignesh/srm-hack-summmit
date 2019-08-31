package com.truiton.customspeechrecognizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class DisplayResultsForEbook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results_for_ebook);
        WebView mWebView =  findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/story.html");   // now it will not fail here

    }
}
