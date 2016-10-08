package com.jp.solver;
public class Copy {
	/***********************Copy Functions****************************/
	//Description: returns a copy of the given integer array
	public static int[] array(int[] array){
		int[] copy = new int[array.length];
		for(int i=0; i<array.length; i++)
			copy[i] = array[i];
		return copy;
	}
	
	//Description: returns a copy of the given double array
	public static double[] array(double[] array){
		double[] copy = new double[array.length];
		for(int i=0; i<array.length; i++)
			copy[i] = array[i];
		return copy;
	}
	
}
