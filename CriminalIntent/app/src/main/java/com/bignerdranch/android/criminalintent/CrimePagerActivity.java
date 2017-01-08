package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity  implements CrimeFragment.Callbacks{

    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final String TAG = "CrimePagerActivity";
    private static final int OFFSCREEN_PAGE_LIMIT = 1;

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        mViewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
        mCrimes = CrimeLab.get(this).getCrimes();
        Log.d(TAG, "onCreate ...");

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId(), OFFSCREEN_PAGE_LIMIT);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        for(int i=0; i<mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent getIntent(Context context, UUID crimeId){
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        Log.d(TAG, "Crime ID: " + crimeId.toString());
        return intent;

    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}