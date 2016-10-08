package com.jp.solver;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Print {
	
	public static void ArrayList2D(List<List<Integer>> L, String s){
		System.out.println(s);
		for(int i=0; i < L.size(); i++){
			List row= L.get(i);
			for (int j=0; j<row.size();j++ )
				System.out.print(row.get(j)+" ");
			System.out.println();
		}
		System.out.println();
	}   
	public static void authorized(	List<List<UserPair>> src){
		System.out.println("Users:");
		for(int i=0; i < src.size(); i++){
			List<UserPair> row= src.get(i);
			System.out.print("Task " + i+ ": ");
			for (int j=0; j<row.size();j++ )
				System.out.print(row.get(j).get_user()+" ");
			System.out.println();
		}/*
		System.out.println("Users Capability");
		for(int i=0; i < src.size(); i++){
			List<AC_data> row= src.get(i);
			for (int j=0; j<row.size();j++ )
				System.out.print(row.get(j).get_capability()+" ");
			System.out.println();
		}
		System.out.println();*/
	} 
	
	public static void ArrayList(List<Integer> L, String s){
		System.out.println(s);
		for (int i=0; i<L.size(); i++)
			System.out.print(L.get(i) + " ");
		System.out.println("\n");
	}
	
	public static void solution(Stack<int[]> solution){
		int[] sol = solution.peek();
		System.out.println("Solution: ");
		for(int i=0; i<sol.length;i++){
			System.out.print("Task" + i +"  :  User " + sol[i] + "\n" );
		}
	}
	public static void array(int[] src, String s){
		System.out.print(s+ " : ");
		for(int i=0; i<src.length;i++)
			System.out.print(src[i] + " ");
		System.out.println("\n");
	}
	public static void array(double[] src, String s){
		System.out.print(s+ " : ");
		for(int i=0; i<src.length;i++)
			System.out.print(src[i] + " ");
		System.out.println("\n");
	}
	public static void Set(Set<Integer> Set, String s){
		System.out.println("\n"+s);
		Iterator it = Set.iterator();
		  while (it.hasNext()) {
		      // Get element
		      Object element = it.next();
		      System.out.print(element + " ");
		  }
	}
	public static void queue(Deque<Integer> tasks_to_change){
		System.out.println("Queue: ");
		Iterator it = tasks_to_change.iterator();
		  while (it.hasNext()) {
		      // Get element
		      Object element = it.next();
		      System.out.print(element + " ");
		  }
		System.out.println("\n");
	}
	public static void task_history(Stack<Integer>task_history){
		System.out.println("Task History: ");
		for(int i=0; i<task_history.size(); i++)
			System.out.print(task_history.get(i) + " ");
		System.out.println();
	}
	
	public static void resilience_constraints(int[] u, TaskPair[] b, TaskPair[] s){
		System.out.println("User Constraint: ");
		for(int i : u){
			System.out.print(i + ", ");
		}
		System.out.println();
		System.out.println();
		System.out.println("Binding Constraints: " );
		for (TaskPair taskpair : b){
			taskpair.print();
			System.out.print(", " );
		}
		System.out.println();
		System.out.println();
		System.out.println("Separation Constraints: " );
		for (TaskPair taskpair : s){
			taskpair.print();
			System.out.print(", " );
		}
		System.out.println();
	}

}
