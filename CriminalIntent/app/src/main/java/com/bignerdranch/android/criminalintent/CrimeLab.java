package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by adhotre on 5/9/16.
 */
public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);

        }
        return  sCrimeLab;
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<Crime>();
        CrimeCursorWrapper crimeWrapperCursor = queryCrimes(null, null);
        try {
            crimeWrapperCursor.moveToFirst();
            while(!crimeWrapperCursor.isAfterLast()){
                crimes.add(crimeWrapperCursor.getCrime());
                crimeWrapperCursor.moveToNext();
            }

        }finally {
            crimeWrapperCursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id){
        Log.d(TAG, "Not found Crime ID: " + id);
        CrimeCursorWrapper cursor = queryCrimes(CrimeDbSchema.CrimeTable.Cols.UUID + "=?", new String[]{id.toString()});
        try {
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();

        }finally {
            cursor.close();
        }

    }

    public static int getPosition(List<Crime> mCrimes, UUID crimeId){
        for (int i=0; i<mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                Log.d(TAG, String.format("position %d set!", i));
                return i;
            }
        }
        Log.d(TAG, String.format("Invalid Crime ID passed!"));
        return -1;
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                        CrimeDbSchema.CrimeTable.Cols.UUID  + "=?", new String[]{uuidString});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(CrimeDbSchema.CrimeTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.RESOLVED, crime.isSolved());
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT_ID, crime.getSuspectId());
        return values;
    }

    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFilename());
    }
}
