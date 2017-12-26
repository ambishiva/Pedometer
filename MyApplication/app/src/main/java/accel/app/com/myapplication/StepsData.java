package accel.app.com.myapplication;

/**
 * Created by shiva on 14/9/17.
 */

public class StepsData {



    private String steps;
    private String date;

    public StepsData(String steps, String date) {
        this.steps = steps;
        this.date = date;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
