package accel.app.com.myapplication;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private TextView getsteps;
    private SharedPreference sharedPrefrence;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sb;
    private String stepsDatabasePath = null;
    private File stepsAppDatabaseFolder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefrence = new SharedPreference();
        getsteps = (TextView)findViewById(R.id.getsteps);
        createDirectories();
        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        sb = dataBaseHelper.getWritableDatabase();
        initialiseListeners();
        startService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  Log.d("Destroy Called","");
    }

    private void createDirectories() {

        try{

            stepsDatabasePath = Environment.getExternalStorageDirectory()+ File.separator+
                    Constants.rootFolderName+File.separator+Constants.databaseFolder;
            stepsAppDatabaseFolder = new File(stepsDatabasePath);


            if((!stepsAppDatabaseFolder.exists())&&(!stepsAppDatabaseFolder.isDirectory())){
                stepsAppDatabaseFolder.mkdirs();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initialiseListeners() {

        getsteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<BeanSampleList> beamSampleList = sharedPrefrence.loadFavorites(MainActivity.this);
                Log.d("Array Lis",""+beamSampleList);
                Intent showSteps = new Intent(MainActivity.this,ShowSteps.class);
                startActivity(showSteps);

            }
        });
    }

    public void startService() {
        startService(new Intent(getBaseContext(), SensorService.class));
    }
}
