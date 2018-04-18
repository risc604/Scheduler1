package com.example.android.scheduler;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SampleSchedulingService extends IntentService {
    private static final String TAG1 = SampleSchedulingService.class.getSimpleName();
    public SampleSchedulingService() {
        super("SchedulingService");
        //logSFileCreated();
        Log.w(TAG1, "SampleSchedulingService(), ");
    }
    
    public static final String TAG = "Scheduling Demo";
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
    // The string the app searches for in the Google home page content. If the app finds 
    // the string, it indicates the presence of a doodle.  
    public static final String SEARCH_STRING = "doodle";
    // The Google home page URL from which the app fetches content.
    // You can find a list of other Google domains with possible doodles here:
    // http://en.wikipedia.org/wiki/List_of_Google_domains
    public static final String URL = "http://www.google.com";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w(TAG1, "onHandleIntent(), ");
        // BEGIN_INCLUDE(service_onhandle)
        // The URL from which to fetch content.
        String urlString = URL;
    
        String result ="";
        
        // Try to connect to the Google homepage and download content.
        try {
            result = loadFromNetwork(urlString);
        } catch (IOException e) {
            Log.i(TAG, getString(R.string.connection_error));
        }
    
        // If the app finds the string "doodle" in the Google home page content, it
        // indicates the presence of a doodle. Post a "Doodle Alert" notification.
        if (result.indexOf(SEARCH_STRING) != -1) {
            sendNotification(getString(R.string.doodle_found));
            Log.i(TAG, "Found doodle!!");
        } else {
            sendNotification(getString(R.string.no_doodle));
            Log.i(TAG, "No doodle found. :-(");
        }
        // Release the wake lock provided by the BroadcastReceiver.
        SampleAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
    
    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        Log.w(TAG1, " sendNotification(), msg: " + msg);
        mNotificationManager = (NotificationManager)
               this.getSystemService(Context.NOTIFICATION_SERVICE);
    
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(getString(R.string.doodle_alert))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg)
        .setVibrate(new long[]{0, 20000, 500, 20000});

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
 
//
// The methods below this line fetch content from the specified URL and return the
// content as a string.
//
    /** Given a URL string, initiate a fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        Log.w(TAG1, "loadFromNetwork(), urlString: " + urlString);
        InputStream stream = null;
        String str ="";
      
        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }      
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        Log.w(TAG1, "downloadUrl(), urlString: " + urlString);
    
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    /** 
     * Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from www.google.com.
     * @return String version of InputStream.
     * @throws IOException
     */
    private String readIt(InputStream stream) throws IOException {
        Log.w(TAG1, " readIt(), ");
      
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for(String line = reader.readLine(); line != null; line = reader.readLine()) 
            builder.append(line);
        reader.close();
        return builder.toString();
    }


    //private final static String mPID = String.valueOf(android.os.Process.myPid());
    final static String mPIDs = String.valueOf(android.os.Process.myPid());
    //final static String cmds01 = "logcat *:v *:w *:e *:d *:i | grep \"(" + mPID + ")\" -f ";
    final static String cmds011 = "logcat *:v | grep \"(" + mPIDs + ")\" -f ";

    public void logSFileCreated()
    {
        try
        {
            //final String logFilePath = "/storage/emulated/0/Download/"+"Log_mt24.txt";
            final String logFilePath =  Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Download/scheduler1s.txt";
            //final String cmds01 = "logcat *:v | grep \"(" + mPID + ")\" -f ";

            File f = new File(logFilePath);
            if (f.exists() && !f.isDirectory()) {
                if (!f.delete()) {
                    Log.w(TAG, "FAIL !! file delete NOT ok.");
                }
            }

            java.lang.Process process = Runtime.getRuntime().exec(cmds011 + logFilePath);
            Log.w(TAG, "logFileCreated(), process: " + process.toString() +
                    ", path: " + logFilePath + ", f.exists: " + f.exists());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


}
