package accel.app.com.myapplication;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by shiva on 19/12/17.
 */

public class MyApplication extends Application {


    SensorService mService;
    boolean mBound = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent service = new Intent(this,SensorService.class);
       // startService(new Intent(this, SensorService.class));
        bindService(service, mConnection, Context.BIND_AUTO_CREATE);
        //bindService(service,)

    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SensorService.LocalBinder binder = (SensorService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.d("On Service Connected","On Service Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.d("On Service Disconnected","On Service Disconnected");
        }
    };

}
