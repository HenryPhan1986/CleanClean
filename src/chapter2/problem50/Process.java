package chapter2.problem50;
import java.util.Random;

public class Process {



    private int id;
    private int timeToRun;
    private int numCollisions; // this is n
    private int totalCount;
    private Random r; 
    private boolean complete;

    public Process(int id) {
        this.id = id;
        timeToRun = 0; 
        numCollisions = 0;
        totalCount = 0;
        complete = false;
        r = new Random();
    }

    public boolean run()
    {
    	if (isComplete())
    		return false;
    	else if (timeToRun == 0)
    	{
    		totalCount++;
    		complete = true;
    		return true;
    	}
    	else
    	{
    		totalCount++;
    		timeToRun--;
    		return false;
    	}
    }
    
    public boolean isComplete() {
        return complete;
    }


    public void collision() {
    	complete = false;
    	numCollisions++;
    	calculateTimeToRun();
    }

    public void calculateTimeToRun() {
        int k = (int) (Math.pow(2, numCollisions));
        timeToRun = r.nextInt(k);
    }

    
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getTimeToRun() {return timeToRun;}
    public void setTimeToRun(int timeToRun) {this.timeToRun = timeToRun;}
    public int getNumCollisions() {return numCollisions;}
    public void setNumCollisions(int numCollisions) {this.numCollisions = numCollisions;}
    public int getTotalCount() {return totalCount;}
    public void setTotalCount(int totalCount) {this.totalCount = totalCount;}
    
}
