package com.jp.solver;

public class TaskPair {		
	int task1;
	int task2;
	
	public TaskPair(int t1, int t2){
		task1 = t1;
		task2 = t2;
	}
	public int get_task1(){	return task1;	}
	public int get_task2(){	return task2;	}

	  public String toString() {
	        return Integer.toString(get_task1());
	    }
	  
	 public void print(){
		 System.out.print("(" + task1 + "," + task2  + ")");
	 }
	 
}
