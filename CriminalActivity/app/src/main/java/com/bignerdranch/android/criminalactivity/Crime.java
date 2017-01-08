package com.bignerdranch.android.criminalactivity;

import java.util.UUID;

/**
 * Created by adhotre on 5/8/16.
 */
public class Crime {
    private UUID mId;
    private String mTitle;


    public Crime() {
        mId = UUID.randomUUID();
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
}
