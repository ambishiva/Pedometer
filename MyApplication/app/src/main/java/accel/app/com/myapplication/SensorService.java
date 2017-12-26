/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package accel.app.com.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



public class SensorService extends Service implements SensorEventListener, StepListener {

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer_sensor;
    private int step_count = 0;
    private long time_stamp;
    private SharedPreferences preferences;
    private SharedPreference sharedPreference;
    private int prefKey = 0;
    private IntentFilter intentFilter;
    private Handler handler;
    private int iteration_count = 0;
    private String current_Date;
    private String new_Date;
    private ArrayList<StepsData> stepsDataArrayList;
    private DataBaseHelper dataBaseHelper;
    private static Timer timer;
    boolean flag = true;
    private SQLiteDatabase sb;

    private final IBinder mBinder = new LocalBinder();
    private String sensorType;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d("On create","On create");
        timer = new Timer();
        super.onCreate();
        dataBaseHelper = new DataBaseHelper(SensorService.this);
        sb = dataBaseHelper.getWritableDatabase();
        Log.d("service started","service started");
        current_Date = getCurrentSystemDate();

        intentFilter = new IntentFilter();

        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIME");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.TIME_TICK");

        getApplication().registerReceiver(dateChangedReceiver, intentFilter);

        preferences = PreferenceManager.getDefaultSharedPreferences(SensorService.this);
        sharedPreference = new SharedPreference();


        SharedPreferences.Editor editor = preferences.edit();
        iteration_count = preferences.getInt("keyCount", 0);

        editor.putInt("keyCount", iteration_count);
        editor.apply();

        if (iteration_count != 0) {
            prefKey = iteration_count;
        }

        initSensor();



    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        SensorService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SensorService.this;
        }
    }

    private String getCurrentSystemDate() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return df.format(c.getTime());

    }

    private void initSensor() {

        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorType = "Pedo";
           // isSensorPresent = true;
            Toast.makeText(SensorService.this,"Pedometer Sensor Present",Toast.LENGTH_SHORT).show();
            mStepDetectorSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            // startService();
            //mStepsDBHelper = new StepsDBHelper(this);
        }else {
            sensorType = "Accelo";
            // isSensorPresent = false;
            Toast.makeText(SensorService.this, "Accelerometer Sensor Present", Toast.LENGTH_SHORT).show();
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_GAME);

            simpleStepDetector = new StepDetector();
            simpleStepDetector.registerListener(this);
            //initSensor();
            // initHandler();
            //  }
        }
    }


    private void startService() {

        timer.scheduleAtFixedRate(new mainTask(), 0, 60000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
        }
    }


    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {



            time_stamp = System.currentTimeMillis();
            updateStepsData(time_stamp);
            try{

                flag = false;
                //new TimerTask().execute();
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Calendar calobjendTime = Calendar.getInstance();
                String endTime = df.format(calobjendTime.getTime());

                Date dateEntTime = df.parse(endTime);
                Calendar calObjStartTime = Calendar.getInstance();
                calObjStartTime.setTime(df.parse(endTime));
                calObjStartTime.add(Calendar.MINUTE,-1);
                String startTime = df.format(calObjStartTime.getTime());
                Log.d("StartTime"+startTime,"EndTime"+endTime);


                //Removal of duplicate entry
//                boolean exist = dataBaseHelper.checkRecordExist(currentDateTimeString);
//                if(exist){
//
//                    //updathe particular entry with exist
//
//                }else {binder
//
//                }
                SharedPreferences.Editor editor = preferences.edit();
                sharedPreference.addFavorite(SensorService.this,new BeanSampleList(step_count,startTime,endTime));
              //  dataBaseHelper.insertSteps(new StepsData(time_stamp, String.valueOf(lastStepValue)));
                editor.putFloat("Last_Step_Value",0);
                editor.apply();
                editor.commit();
                String endtime = DateFormat.getDateTimeInstance().format(new Date());
                Log.d("EndTime",""+endtime);
                flag = true;

            }catch (Exception e){

                e.printStackTrace();
            }


        }
    };



    private final BroadcastReceiver dateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals("android.intent.action.TIME_SET") ||
                    action.equals("android.intent.action.TIMEZONE_CHANGED") ||
                    action.equals("android.intent.action.TIME") ||
                    action.equalsIgnoreCase("android.intent.action.TIME_TICK")) {


                new_Date = getCurrentSystemDate();
                if(!current_Date.equalsIgnoreCase(new_Date)){
                    showToast("date changed");
                    current_Date = new_Date;
                    step_count = 0;
                    prefKey = 0;
                    iteration_count = 0;
                    PreferenceManager.getDefaultSharedPreferences(SensorService.this).edit().clear().apply();
                   // handler.removeCallbacksAndMessages(null);
                   // initHandler();

                }

            }
        }
    };



    private void showToast(String msg) {


    }

    @Override
    public void onDestroy() {
        getApplication().unregisterReceiver(dateChangedReceiver);
       // sensorManager.unregisterListener(this);
        super.onDestroy();
      //  Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
      //  sendBroadcast(broadcastIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);



        }else if(event.sensor.getType()==Sensor.TYPE_STEP_DETECTOR){
           int  countOnResume = (int) event.values[0];
            step_count++;
            setNotification();
            Log.d("Steps working","steps working");
            try{

                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm",Locale.ENGLISH);
                Calendar calobjendTime = Calendar.getInstance();
                String endTime = df.format(calobjendTime.getTime());

                Date dateEntTime = df.parse(endTime);
                Calendar calObjStartTime = Calendar.getInstance();
                calObjStartTime.setTime(df.parse(endTime));
                calObjStartTime.add(Calendar.MINUTE,-1);
                String startTime = df.format(calObjStartTime.getTime());
                Log.d("StartTime"+startTime,"EndTime"+endTime);
                sharedPreference.addFavorite(this,new BeanSampleList(1,startTime,endTime));

                //Check whether entry exist for that minute
                boolean status = dataBaseHelper.checkRecordExistInThatMinute(startTime);
                if(status){
                    //update entry in that minute.

                    //Check previous Entries.....
                    // checkPreviousEntries(startTime);

                    int previousSteps = dataBaseHelper.getSteps(startTime);
                    previousSteps = previousSteps+1;
                    dataBaseHelper.updateSteps(new StepsData(String.valueOf(previousSteps),startTime));
                }else{
                    //Do entry in that minute
                    //checkPreviousEntries(startTime);
                    // boolean newStatus = dataBaseHelper.checkRecordExistInThatMinute(startTime);
                    //if(newStatus){
                    //  int previousSteps = dataBaseHelper.getSteps(startTime);
                    // previousSteps = previousSteps+1;
                    //  dataBaseHelper.updateSteps(new StepsData(String.valueOf(previousSteps),startTime));
                    //}else{
                    dataBaseHelper.insertSteps(new StepsData("1",startTime));
                    //}

                }

            }catch (Exception e){

                e.printStackTrace();
            }

        }
    }

    private void updateStepsData(long time_stamp) {

        SharedPreferences.Editor editor = preferences.edit();
        if (step_count > 0) {

            //Toast.makeText(this, "steps " + step_count, Toast.LENGTH_LONG).show();
            prefKey = prefKey + 1;

            editor.putInt("key" + prefKey, step_count);
            editor.putLong("timeKey" + prefKey,time_stamp);
            editor.putInt("keyCount", prefKey);

            Log.d("Steps",""+step_count);

            try{

                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Calendar calobjendTime = Calendar.getInstance();
                String endTime = df.format(calobjendTime.getTime());

                Date dateEntTime = df.parse(endTime);
                Calendar calObjStartTime = Calendar.getInstance();
                calObjStartTime.setTime(df.parse(endTime));
                calObjStartTime.add(Calendar.MINUTE,-1);
                String startTime = df.format(calObjStartTime.getTime());
                Log.d("StartTime"+startTime,"EndTime"+endTime);
                sharedPreference.addFavorite(this,new BeanSampleList(step_count,startTime,endTime));

            }catch (Exception e){

                e.printStackTrace();
            }

            step_count = 0;
        }else{

            try{

                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Calendar calobjendTime = Calendar.getInstance();
                String endTime = df.format(calobjendTime.getTime());

                Date dateEntTime = df.parse(endTime);
                Calendar calObjStartTime = Calendar.getInstance();
                calObjStartTime.setTime(df.parse(endTime));
                calObjStartTime.add(Calendar.MINUTE,-1);
                String startTime = df.format(calObjStartTime.getTime());
                Log.d("StartTime"+startTime,"EndTime"+endTime);
                sharedPreference.addFavorite(this,new BeanSampleList(step_count,startTime,endTime));

            }catch (Exception e){
                e.printStackTrace();
            }

            //sharedPreference.addFavorite(this,new BeanSampleList(step_count,time_stamp,""));
        }
        editor.apply();
    }

    @Override
    public void step(long timeNs) {
        step_count++;
        setNotification();
        Log.d("Steps working","steps working");
        try{

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm",Locale.ENGLISH);
            Calendar calobjendTime = Calendar.getInstance();
            String endTime = df.format(calobjendTime.getTime());

            Date dateEntTime = df.parse(endTime);
            Calendar calObjStartTime = Calendar.getInstance();
            calObjStartTime.setTime(df.parse(endTime));
            calObjStartTime.add(Calendar.MINUTE,-1);
            String startTime = df.format(calObjStartTime.getTime());
            Log.d("StartTime"+startTime,"EndTime"+endTime);
            sharedPreference.addFavorite(this,new BeanSampleList(1,startTime,endTime));

            //Check whether entry exist for that minute
            boolean status = dataBaseHelper.checkRecordExistInThatMinute(startTime);
            if(status){
                //update entry in that minute.

                //Check previous Entries.....
               // checkPreviousEntries(startTime);

                int previousSteps = dataBaseHelper.getSteps(startTime);
                previousSteps = previousSteps+1;
                dataBaseHelper.updateSteps(new StepsData(String.valueOf(previousSteps),startTime));
            }else{
              //Do entry in that minute
                //checkPreviousEntries(startTime);
               // boolean newStatus = dataBaseHelper.checkRecordExistInThatMinute(startTime);
                //if(newStatus){
                  //  int previousSteps = dataBaseHelper.getSteps(startTime);
                   // previousSteps = previousSteps+1;
                  //  dataBaseHelper.updateSteps(new StepsData(String.valueOf(previousSteps),startTime));
                //}else{
                    dataBaseHelper.insertSteps(new StepsData("1",startTime));
                //}

            }

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    private void setNotification() {

        Random rn = new Random();
        int max = 10000000;
        int min = 1;
        int range = max - min + 1;
        int randomNum =  rn.nextInt(range) + min;
            PendingIntent contentIntent;
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, SensorService.class), 0);

            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setColor(this.getResources().getColor(R.color.colorAccent))
                            .setContentTitle("Pedometer" + " -Type-" + sensorType)
                            .setOngoing(true)
                            .setAutoCancel(false)
                            .setContentText("Healthia"+randomNum);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(randomNum, mBuilder.build());

            Notification notification = mBuilder.build();
            startForeground(randomNum, notification); //NOTIFICATION_ID is a random integer value which has to be unique in an app

    }

    private void checkPreviousEntries(String startTime) {

        ArrayList<StepsData> stepsData = dataBaseHelper.getStepsData();

        if(stepsData.size()==0){

        }else{
            String previoustimeStamp =    dataBaseHelper.getPreviousRowTimeStamp(startTime);
            Log.d("Time Stamp",""+previoustimeStamp);

            try {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date startDate = simpleDateFormat.parse(startTime);
                Date endDate = simpleDateFormat.parse(previoustimeStamp);
                // Date date1 = simpleDateFormat.parse("10/10/2013 11:30:10");
                // Date date2 = simpleDateFormat.parse("13/10/2013 20:35:55");

                printDifference(endDate, startDate);
            }catch (Exception e){

            }
        }

    }

    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        Date startNewDate = startDate;
        try{
            long different = endDate.getTime() - startDate.getTime();

            System.out.println("startDate : " + startDate);
            System.out.println("endDate : "+ endDate);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if(elapsedDays>0){
                long updatedElapsedMinutes = elapsedDays * 24 * 60;
               startNewDate =   addTimeWithZeroEntries(updatedElapsedMinutes,startNewDate,endDate);
            }

            if(elapsedHours>0){
                long updatedElapsedMinutes = elapsedHours *60;
                startNewDate =  addTimeWithZeroEntries(updatedElapsedMinutes,startNewDate,endDate);
            }

            if(elapsedMinutes>1&&elapsedMinutes>0){
                addTimeWithZeroEntries(elapsedMinutes,startNewDate,endDate);
            }

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Date addTimeWithZeroEntries(long flag,Date startDate,Date endDate) {
        Date startNewDate = startDate;
        try{
            SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);

            for(int i = 0;i<flag;i++){

                Calendar cal = Calendar.getInstance();
                cal.setTime(startNewDate);
                cal.add(Calendar.MINUTE, 1);
                String endTime = parser.format(cal.getTime());
                dataBaseHelper.insertSteps(new StepsData("0",endTime));
                startNewDate = parser.parse(endTime);
            }
        }catch (Exception e){

        }

        return startNewDate;

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("Tak Removed","Removed");
        super.onTaskRemoved(rootIntent);
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);


    }

}
