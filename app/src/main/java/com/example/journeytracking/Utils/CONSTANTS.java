package com.example.journeytracking.Utils;

public class CONSTANTS {

    public static class Notification {
        public static final String CHANNEL_ID = "channel_id";
    }

    public static class QuerySelector {
        public static final String INSERT_RIDE = "1";
        public static final String UPDATE_RIDE  = "2";
        public static final String INSERT_LOCATION_UPDATE = "3";
        public static final String FETCH_RIDE_LIST = "4";
        public static final String FETCH_RIDE_LOCATIONS_LIST = "5";
    }

    public static class VolleyRequestTags {
        public static final String SNAP_TO_ROAD = "1";
    }

    public static int locationSettingRequestCount = 0;
}
