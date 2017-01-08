package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final String TAG = "CrimeActivity";

    public static Intent getIntent(Context context, UUID crimeId){
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        Log.d(TAG, "Crime ID: " + crimeId.toString());
        return intent;

    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int offScreenPageLimit = (int) getIntent().getSerializableExtra("offScreenPageLimit");
        return CrimeFragment.newInstance(crimeId, offScreenPageLimit);
    }
}
