package com.example.journeytracking;

import android.app.Application;

import androidx.room.Room;

import com.example.journeytracking.Data.RoomDb.RideDatabase;

public class AppController extends Application {

    private  RideDatabase db;
    private static AppController mInstance;
    public  final String DB_NAME = "rideDatabase";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public synchronized RideDatabase getRideDatabaseInstance(){
        if(db == null){
            db = Room.databaseBuilder(mInstance.getApplicationContext(), RideDatabase.class, DB_NAME).build();
            return db;
        }

        return db;
    }
}
