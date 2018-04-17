/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.scheduler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;


/**
 * This sample demonstrates how to schedule an alarm that causes a service to
 * be started. This is useful when you want to schedule alarms that initiate
 * long-running operations, such as retrieving a daily forecast.
 * This particular sample retrieves content from the Google home page once a day and  
 * checks it for the search string "doodle". If it finds this string, that indicates 
 * that the page contains a custom doodle instead of the standard Google logo.
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    SampleAlarmReceiver alarm = new SampleAlarmReceiver();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logFileCreated();
        Log.w(TAG, "onCreate(), ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.w(TAG, "onCreateOptionsMenu(), menu: " + menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Menu options to set and cancel the alarm.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG, "onOptionsItemSelected(), item: " + item);
        switch (item.getItemId()) {
            // When the user clicks START ALARM, set the alarm.
            case R.id.start_action:
                alarm.setAlarm(this);
                return true;
            // When the user clicks CANCEL ALARM, cancel the alarm. 
            case R.id.cancel_action:
                alarm.cancelAlarm(this);
                return true;
        }
        return false;
    }

    //private final static String mPID = String.valueOf(android.os.Process.myPid());
    final static String mPID = String.valueOf(android.os.Process.myPid());
    //final static String cmds01 = "logcat *:v *:w *:e *:d *:i | grep \"(" + mPID + ")\" -f ";
    final static String cmds01 = "logcat *:v | grep \"(" + mPID + ")\" -f ";

    public void logFileCreated()
    {
        try
        {
            //final String logFilePath = "/storage/emulated/0/Download/"+"Log_mt24.txt";
            final String logFilePath =  Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Download/scheduler1.txt";
            final String cmds00 = "logcat -d -f ";
            //final String cmds01 = "logcat *:e *:i | grep \"(" + mPID + ")\"";

            //String mPID = String.valueOf(android.os.Process.myPid());
            //String cmds01 = "logcat *:e *:i | grep \"(" + mPID + ")\"";

            File f = new File(logFilePath);
            if (f.exists() && !f.isDirectory())
            {
                if (!f.delete())
                {
                    Log.w(TAG, "FAIL !! file delete NOT ok.");
                }
            }
            f.createNewFile();

            java.lang.Process process = Runtime.getRuntime().exec(cmds01 + logFilePath);
            Log.w(TAG, "logFileCreated(), process: " + process.toString() +
                    ", path: " + logFilePath);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


}
