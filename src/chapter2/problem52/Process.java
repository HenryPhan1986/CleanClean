package chapter2.problem52;

import java.util.Comparator;
import java.util.Random;

public class Process {

	protected int lambda;
    private boolean collision;
	private double totalTime;
    protected Random r; 
    
    public Process(int lambda) {
        this.lambda = lambda;
        collision = false;
        totalTime = 0;
        r = new Random();
    }

    public Process collision() {
    	calculateTimeToRun();
    	return this;
    }

    public double calculateTimeToRun() {
    	return totalTime += - (lambda * (Math.log(r.nextDouble())));
    }
    
    public static Comparator<Process> totalCountComparator() {
		return new Comparator<Process>() {
			@Override
			public int compare(Process procA, Process procB) {
				if (procB.getTotalCount() - procA.getTotalCount() < 0) return 1;
				if (procB.getTotalCount() - procA.getTotalCount() > 0) return -1;
				return 0;
			}
		};
	}
    
    public double getTotalCount() {return totalTime;}
    public void setTotalCount(int totalCount) {this.totalTime = totalCount;}

    public int getLambda() {
		return lambda;
	}

	public void setLambda(int lambda) {
		this.lambda = lambda;
	}

    public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}
}
