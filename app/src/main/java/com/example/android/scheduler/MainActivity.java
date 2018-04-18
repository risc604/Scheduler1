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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.Arrays;


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

        if (Build.VERSION.SDK_INT > 22)
            marshmallowPermission();

        logFileCreated();
        Log.w(TAG, "onCreate(), ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult()...");

        int grantTotal = 0;
        Log.w(TAG, "permissions: " + Arrays.toString(permissions) +
                ", grantResults: " + Arrays.toString(grantResults));

        for (int i = 0; i < grantResults.length; i++) {
            grantTotal += grantResults[i];
        }
        Log.w(TAG, "grantTotal: " + grantTotal);

        if (grantTotal < 0) {
            marshmallowPermission();
        } else {
            switch (requestCode) {
                case 1:
                    if ((grantResults.length > 0) &&
                            (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        //initAction();
                    } else {
                        Log.d(TAG, "onRequestPermissionsResult(), Permission denied.");
                        Toast.makeText(getApplicationContext(), "Permission denied",
                        //Toast.makeText(getApplicationContext(), getString(R.string.tas_permission_denied),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    private boolean marshmallowPermission()
    {
        Log.i(TAG, "marshmallowPermission() ...");

        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //int blePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        int sysPermissionState = PackageManager.PERMISSION_GRANTED;
        //Log.d(TAG,  "blePermission: " + blePermission +
        //        ", storagePermission: " + storagePermission +
        //        ", sysPermissionState: " + sysPermissionState);

        if (storagePermission != sysPermissionState)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH_ADMIN))
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{   Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },  1);
                ////Toast.makeText(this, "Please give App those permission To Run ...", Toast.LENGTH_LONG).show();
                Log.d(TAG, " Error !! PERMISSION_DENIED ");
                return false;
            }
            else
                return true;
        }
        else
        {
            Log.d(TAG, " PERMISSION_GRANTED ");
            return true;
        }
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
            //final String cmds01 = "logcat *:v | grep \"(" + mPID + ")\" -f ";

            boolean state = false;
            File f = new File(logFilePath);
                if (f.exists() && !f.isDirectory()) {
                    if (!f.delete()) {
                        Log.w(TAG, "FAIL !! file delete NOT ok.");
                    }
                    else
                    {
                        state = f.createNewFile();
                    }
                }

            java.lang.Process process = Runtime.getRuntime().exec(cmds01 + logFilePath);
            Log.w(TAG, "logFileCreated(), process: " + process.toString() +
                    ", path: " + logFilePath + ", f.exists: " + state);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


}
