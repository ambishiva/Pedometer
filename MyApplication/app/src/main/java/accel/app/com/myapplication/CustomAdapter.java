package accel.app.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shiva on 23/8/17.
 */

public class CustomAdapter extends BaseAdapter {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<StepsData> data;
    private static LayoutInflater inflater=null;


    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(Activity a, ArrayList<StepsData> d) {

        /********** Take passed values **********/
        activity = a;
        data=d;

        inflater = (LayoutInflater)activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);



    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView textTime;
        public TextView textSteps;


    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.textTime = (TextView) vi.findViewById(R.id.textTime);
            holder.textSteps = (TextView) vi.findViewById(R.id.textSteps);


            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
            } else{
            holder = (ViewHolder) vi.getTag();

             }

            StepsData stepsData =  data.get( position );

    /************  Set Model values in Holder elements ***********/

            holder.textTime.setText(stepsData.getDate());

            holder.textSteps.setText( String.valueOf(stepsData.getSteps()));

             return vi;
    }



    /********* Called when Item click in ListView ************/

}