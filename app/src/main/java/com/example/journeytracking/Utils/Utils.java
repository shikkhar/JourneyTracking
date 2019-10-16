package com.example.journeytracking.Utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Simple Utility Methods for our App
 * Functions to return date, time from strings
 * Functions to return Display Height/Width
 */
public class Utils {

    public static String getDateTime(){
        Date dateTime = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return formatter.format(dateTime);
    }

    public static String getTime(String dateTimeString) throws ParseException {
        Date date = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").parse(dateTimeString);
        return new SimpleDateFormat("H:mm a").format(date);
    }

    public static String getDate(String dateTimeString) throws ParseException{
        Date date = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").parse(dateTimeString);
        return new SimpleDateFormat("dd-M-yyyy").format(date);
    }
    public static String getTimeFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public static String getTimeDifference(String startDateTimeString, String endDateTimeString) throws ParseException{
        Date startDateTime = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").parse(startDateTimeString);
        Date endDateTime= new SimpleDateFormat("dd-M-yyyy hh:mm:ss").parse(endDateTimeString);
        long diff = endDateTime.getTime() - startDateTime.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return (diffHours) + "h " + (diffMinutes) + "m " + (diffSeconds) +"s";
    }

    public static int getDisplayHeight(Context appContext){
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getDisplayWidth(Context appContext){
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


}
