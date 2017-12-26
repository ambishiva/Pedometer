package accel.app.com.myapplication;

/**
 * Created by nirav kalola on 10/30/2014.
 */
public class BeanSampleList {

    private int steps;
    private String startTime;
    private String endTime;

    public BeanSampleList(int steps, String startTime, String endTime) {
        super();
        this.steps = steps;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



    public BeanSampleList() {
        super();
    }



}
