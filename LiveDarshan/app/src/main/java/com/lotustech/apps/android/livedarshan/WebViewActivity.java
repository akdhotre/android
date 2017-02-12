package com.lotustech.apps.android.livedarshan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class WebViewActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private static final String TAG = "WebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String url = getIntent().getStringExtra("url");
        Log.d(TAG, "load url: " + url);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        FragmentManager fm = getSupportFragmentManager();
        Fragment webViewFragment = fm.findFragmentById(R.id.fragment_web_view);

        if(webViewFragment == null){
            webViewFragment = WebViewFragment.newInstance(url);
            fm.beginTransaction().
                    add(R.id.activity_web_view, webViewFragment).
                    commit();
        }

    }
}
