package com.example.journeytracking.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String getDateTime(){
        Date dateTime = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return formatter.format(dateTime);
    }

    public static String getTime(String dateTimeString) throws ParseException {
        Date date = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").parse(dateTimeString);
        return new SimpleDateFormat("H:mm").format(date);
    }

    public static String getDate(String dateTimeString) throws ParseException{
        Date date = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").parse(dateTimeString);
        return new SimpleDateFormat("dd-M-yyyy").format(date);
    }
}
