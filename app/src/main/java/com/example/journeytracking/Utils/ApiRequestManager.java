package com.example.journeytracking.Utils;

/**
 * Class to manage requests made using volley
 */
public class ApiRequestManager {

    //Get Request to recive snap to road locations
    public static void snapToRoad(String url, String requestTag, VolleySeverRequest.VolleyResponseCallback volleyResponseCallback){
        VolleySeverRequest severRequest = new VolleySeverRequest(volleyResponseCallback);
        severRequest.makeGetRequest(url,requestTag);
    }

    public static void cancelAll(String requestTag){
        VolleySeverRequest severRequest = new VolleySeverRequest();
        severRequest.cancelRequest(requestTag);
    }
}
