package accel.app.com.myapplication;

/**
 * Created by DELL on 10-02-2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;


public class DataBaseHelper extends SQLiteOpenHelper {


    public static final String SAMPLE_DB_NAME = Environment.getExternalStorageDirectory()+ File.separator+ Constants.rootFolderName
            + File.separator+ Constants.databaseFolder+ File.separator+"pedometerapp";
    public static  final String SAMPLE_TABLE_NAME = "pedometer";
    public static final String CONTACTS_TABLE_NAME = "contacts";

    public static final String STEP_MINUTES = "stepminutes";

    private Context context;

    public static final String DAIRY_COLUMN_TIMESTAMP = "TIME_STAMPID";
    public static final String DAIRY_COLUMN_STEPS = "STEPS";

    public static final String START_TIME = "START_TIME";
    public static final String END_TIME = "END_TIME";









    public DataBaseHelper(Context context)
    {
        super(context, SAMPLE_DB_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        try{
            File path = context.getDatabasePath(SAMPLE_DB_NAME);
            db = SQLiteDatabase.openOrCreateDatabase(path, null);
        }

        catch(SQLiteException e){
            Log.e("Error", "" + e.getMessage());
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub


        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + SAMPLE_TABLE_NAME + "( " +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        "TIME_STAMPID TEXT, " +
                        "STEPS TEXT)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + STEP_MINUTES + "( " +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        "START_TIME TEXT, " +
                        "END_TIME TEXT, " +
                        "STEPS TEXT)"
        );



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS time");
        onCreate(db);
    }

    public boolean insertSteps(StepsData dairyAppData)
    {

        try{

           // String timeStamp, String farmId, String userId, int status, String data, String type, int index, String date)
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DAIRY_COLUMN_TIMESTAMP, dairyAppData.getDate());
            contentValues.put(DAIRY_COLUMN_STEPS, dairyAppData.getSteps());

            db.insert(SAMPLE_TABLE_NAME, null, contentValues);

        }catch (Exception e){

            e.printStackTrace();
        }
        return true;
    }

    public boolean checkRecordExist(String dateTime)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        boolean isPresent = false;

        try{

            String query = "select * from pedometer where TIME_STAMPID ="+"'"+dateTime+"'";
            Cursor cursor =  db.rawQuery(query, null);
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false){



                isPresent = true;
                cursor.moveToNext();
            }

        }catch (Exception e){

            isPresent = false;
            e.printStackTrace();

        }

        return isPresent;

    }

    public boolean checkRecordExistInThatMinute(String dateTime)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        boolean isPresent = false;

        try{

            String query = "select * from pedometer where TIME_STAMPID ="+"'"+dateTime+"'";
            Cursor cursor =  db.rawQuery(query, null);
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false){

                isPresent = true;
                cursor.moveToNext();
            }

        }catch (Exception e){

            isPresent = false;
            e.printStackTrace();

        }

        return isPresent;

    }


    public boolean insertTimeData(String startTime, String endTime, String steps){
        try{

            // String timeStamp, String farmId, String userId, int status, String data, String type, int index, String date)
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(START_TIME ,startTime);
            contentValues.put(END_TIME, endTime);
            contentValues.put(DAIRY_COLUMN_STEPS, steps);

            db.insert(STEP_MINUTES, null, contentValues);

        }catch (Exception e){

            e.printStackTrace();
        }
        return true;
    }

    public ArrayList<StepsData> getStepsData(){
        StepsData stepsData = null;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<StepsData> stepsDataArrayList = new ArrayList<>();

        try{

            String query = "select * from "+SAMPLE_TABLE_NAME;
            Cursor cursor =  db.rawQuery(query, null);
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false){



                String time = cursor.getString(cursor.getColumnIndex("TIME_STAMPID")) ;
                String steps = cursor.getString(cursor.getColumnIndex(DAIRY_COLUMN_STEPS));
                stepsData = new StepsData(time,steps);
                stepsDataArrayList.add(stepsData);
                cursor.moveToNext();
            }

        }catch (Exception e){

            e.printStackTrace();

        }


        return stepsDataArrayList;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public Integer deleteContact (String timeStamp)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int status = -1;
        try{
            status = db.delete("dairyData",
                    "TIME_STAMPID = ? ",
                    new String[] { timeStamp });
        }catch (Exception e){
            e.printStackTrace();
        }

        return status;
    }

    public Integer deleteAllStepsEntries ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int status = -1;
        try{

            db.execSQL("delete from "+ STEP_MINUTES);
        }catch (Exception e){
            e.printStackTrace();
        }

        return status;
    }


    public int getSteps(String dateTime)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        boolean isPresent = false;
        int steps = 0;
        try{

            String query = "select * from pedometer where TIME_STAMPID ="+"'"+dateTime+"'";
            Cursor cursor =  db.rawQuery(query, null);
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false){


                steps = Integer.parseInt(cursor.getString(cursor.getColumnIndex("STEPS")));
                isPresent = true;
                cursor.moveToNext();
            }

        }catch (Exception e){

            isPresent = false;
            e.printStackTrace();

        }

        return steps;

    }


    public int updateSteps(StepsData stepsData)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        boolean isPresent = false;
        int steps = 0;

        ContentValues cv = new ContentValues();
        cv.put("STEPS",stepsData.getSteps());
        try{

            db.update(SAMPLE_TABLE_NAME, cv, "TIME_STAMPID = ?", new String[]{stepsData.getDate()});

        }catch (Exception e){

            isPresent = false;
            e.printStackTrace();

        }

        return steps;

    }

    public String getPreviousRowTimeStamp(String timeStamp) {

        String time_stampid = "";
        try{

            SQLiteDatabase db = this.getReadableDatabase();
            boolean isPresent = false;
            int steps = 0;

            try{


                String query = "select * from pedometer where TIME_STAMPID <"+"'"+timeStamp+"'" + "ORDER BY TIME_STAMPID DESC LIMIT 1";
                Cursor cursor =  db.rawQuery(query, null);
                cursor.moveToFirst();

                while(cursor.isAfterLast() == false){


                    time_stampid = cursor.getString(cursor.getColumnIndex("TIME_STAMPID"));
                    isPresent = true;
                    cursor.moveToNext();
                }

            }catch (Exception e){

                isPresent = false;
                e.printStackTrace();

            }

        }catch (Exception e){

        }
        return time_stampid;

    }


//    public HashMap<String,String> getAllCotacts()
//    {
//        HashMap<String,String> timeHashMap = new HashMap<>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from time", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            String hours = res.getString(res.getColumnIndex("HOUR"));
//            String minutes = res.getString(res.getColumnIndex("MINUTE"));
//            String alarm_title = res.getString(res.getColumnIndex("ALARM_TITLE"));
//            String day = res.getString(res.getColumnIndex("WEEK_DAY"));
//            String am_pm = res.getString(res.getColumnIndex("ALARM_AM_PM"));
//            int id = Integer.parseInt(res.getString(res.getColumnIndex("ID")));
//            String parent_id = res.getString(res.getColumnIndex("PARENT_ID"));
//
//
//            Time time = new Time(day,hours,minutes,alarm_title,id,parent_id);
//            timeHashMap.put(parent_id,hours+" : " + minutes + " " + am_pm+"-"+alarm_title);
//            res.moveToNext();
//        }
//        return timeHashMap;
//    }
}
