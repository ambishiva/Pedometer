package accel.app.com.myapplication;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shiva on 23/8/17.
 */

public class ShowSteps extends Activity {



    private ListView listViewSteps;
    private ArrayList<StepsData> arrayListStepsData;
   // private DataBaseHelper dataBaseHelper;
    private CustomAdapter customAdapter;
    private SharedPreference sharedPrefrence;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sb;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefrence = new SharedPreference();
        ArrayList<BeanSampleList> beamSampleList = sharedPrefrence.loadFavorites(ShowSteps.this);
        setContentView(R.layout.list);
        listViewSteps = (ListView) findViewById(R.id.listViewSteps);
        dataBaseHelper = new DataBaseHelper(ShowSteps.this);
        sb = dataBaseHelper.getWritableDatabase();
        ArrayList<StepsData> arrayListStepData = dataBaseHelper.getStepsData();

        if (arrayListStepData != null && arrayListStepData.size() > 0) {
            customAdapter = new CustomAdapter(ShowSteps.this, arrayListStepData);
            listViewSteps.setAdapter(customAdapter);
        }
    }

        // arrayListStepsData = dataBaseHelper.getStepsData();

      //  if (beamSampleList != null && beamSampleList.size() > 0) {

            // customAdapter = new CustomAdapter(ShowSteps.this, beamSampleList);
            // listViewSteps.setAdapter(customAdapter);

//            dataBaseHelper.deleteAllStepsEntries();
//
//        try {
//
//
//            BeanSampleList stepsData = beamSampleList.get(0);
//            String startingTime = stepsData.getStartTime();
//
//            SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.ENGLISH);
//            Date startingTimeDate = parser.parse(startingTime);
//
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(startingTimeDate);
//            cal.add(Calendar.MINUTE, 1);
//            String endTime = parser.format(cal.getTime());
//            Date endTimeDate = parser.parse(endTime);
//
//            Date currentDate = new Date();
//
//
//            while (endTimeDate.before(currentDate)) {
//
//                int steps = 0;
//                for (int i = 0; i < beamSampleList.size(); i++) {
//
//                    String time = beamSampleList.get(i).getStartTime();
//                    Date date = parser.parse(time);
//
//                    if (date.equals(startingTimeDate)) {
//                        Log.d("Equals", "Equals");
//                        steps++;
//                    }
//
//                    //  if(date.equals(endTime))
//
//                    if (date.after(startingTimeDate) && date.before(endTimeDate)) {
//                        steps++;
//                    }
//                }
//
//                dataBaseHelper.insertTimeData(startingTime, endTime, String.valueOf(steps));
//                Calendar calstart = Calendar.getInstance();
//                calstart.setTime(endTimeDate);
//                calstart.add(Calendar.SECOND, 1);
//                startingTime = parser.format(calstart.getTime());
//                parser = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.ENGLISH);
//                startingTimeDate = parser.parse(startingTime);
//
//
//                Calendar calend = Calendar.getInstance();
//                calend.setTime(startingTimeDate);
//                calend.add(Calendar.MINUTE, 1);
//                endTime = parser.format(calend.getTime());
//                parser = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.ENGLISH);
//                endTimeDate = parser.parse(endTime);
//
//
//                Log.d("Steps", "" + steps);
//
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

            //First Entry in Database

        //}








}
