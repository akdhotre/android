package com.bignerdranch.android.criminalintent;

/**
 * Created by adhotre on 7/11/16.
 */
public class CrimeDbSchema {
    public static class CrimeTable{
        public static final String NAME = "crimes";

        public static class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String RESOLVED = "resolved";
            public static final String SUSPECT = "suspect";
            public static final String SUSPECT_ID = "suspect_id";
        }
    }
}
