package com.example.journeytracking.MainModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.journeytracking.R;
import com.example.journeytracking.TrackingModule.TrackingActivity;

import java.lang.ref.WeakReference;

/**
 * Splash Screen for our app
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide the action bar
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        //posts a runnable after 1.5 seconds to launch out tracking activity
        Handler handler = new Handler();
        handler.postDelayed(new MyRunnable(this), 1500);
    }

    /*Static inner class to post runnable
    * holds a weak reference to our activity so that tit can be GC'ed in case the user kills the app*/
    private static class MyRunnable implements Runnable {

        private WeakReference<MainActivity> mInstance;

         MyRunnable(MainActivity instance) {
            this.mInstance = new WeakReference<>(instance);
        }

        @Override
        public void run() {
            MainActivity activity = mInstance.get();
            if (activity != null) {
                activity.startActivity(new Intent(activity, TrackingActivity.class));
                activity.finish();
            }
        }
    }
}
