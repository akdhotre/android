package com.lotustech.apps.android.livedarshan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class FaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        FragmentManager fm = getSupportFragmentManager();
        Fragment faqFragment = fm.findFragmentById(R.id.fragment_faq);

        if(faqFragment == null){
            faqFragment = FaqFragment.newInstance();
            fm.beginTransaction().
                    add(R.id.activity_faq, faqFragment).
                    commit();
        }
    }
}
