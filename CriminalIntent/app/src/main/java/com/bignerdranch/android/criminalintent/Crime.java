package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by adhotre on 5/8/16.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mSuspectId;


    public Crime(){
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public String toString(){
        return String.format("{mId: %s, mTitle: %s, mDate: %s, mSolved: %s, mSuspect: %s, mSuspectId: %s}", mId, mTitle, mDate, mSolved, mSuspect, mSuspectId);
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getSuspectId() {
        return mSuspectId;
    }

    public void setSuspectId(String suspectId) {
        mSuspectId = suspectId;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}
