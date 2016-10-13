package com.jp.solver.algorithm;

import com.jp.solver.helper.Print;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileData {
	
	public List<List<Integer>> user_authorizations = new ArrayList<List<Integer>>(); 		
	//Contains which tasks each user has access too. Row N = User N, 
	//items in a row represent the tasks User N has access too

	public List<List<Integer>> user_capability = new ArrayList<List<Integer>>(); 			
	//Contains the capability # for each task the user has access too Row N = User N,
	//elements is the capability, column # corresponding to the user_authorization task

	public List<List<Integer>> binding_constraints= new ArrayList<List<Integer>>(); 		
	//contains binding constraints. Every task has at least itself and the other tasks it's binded too
	//row N = task N

	public List<List<Integer>> separation_constraints = new ArrayList<List<Integer>>(); 	
	//contains binding constraints. Every task has at least itself and the other tasks it's separated from
	//row N = task N

	public List<Integer> avg_orders = new ArrayList<Integer>(); 							
	//contains the average number of orders per task. index = task#

	//derived from files
	public int num_users;										//contains total number of users
	public int num_tasks;										//contains total number of tasks

	public FileData(){
			
	}
	/***************************************************************************************/
	/*************************************Reading Files*************************************/
	/***************************************************************************************/
	 private void read_binding_constraints_file(String fileName){
		BufferedReader br = 
				null;
		int arraySize;
		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				List<Integer> row = new ArrayList<Integer>();
				if(!( sCurrentLine.equals(null) || sCurrentLine.equals("") )){
					String[] dataSet = sCurrentLine.split(" ");
					arraySize=dataSet.length;
					for(int i=0; i<arraySize; i++)
						row.add(Integer.parseInt(dataSet[i]));
				}
			   	binding_constraints.add(row);   
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}	
	}
	
	private void read_separation_constraints_file(String fileName){
		BufferedReader br = null;
		int arraySize;
		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				List<Integer> row = new ArrayList<Integer>();
				if(!( sCurrentLine.equals(null) || sCurrentLine.equals("") )){
					String[] dataSet = sCurrentLine.split(" ");
					arraySize=dataSet.length;
					for(int i=0; i<arraySize; i++)
						row.add(Integer.parseInt(dataSet[i]));
				}
			   	separation_constraints.add(row);   
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void read_user_capability_file(String fileName){
		BufferedReader br = null;
		int arraySize;
		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				List<Integer> row = new ArrayList<Integer>();
				if(!( sCurrentLine.equals(null) || sCurrentLine.equals("") )){
					String[] dataSet = sCurrentLine.split(" ");
					arraySize=dataSet.length;
					for(int i=0; i<arraySize; i++)
						row.add(Integer.parseInt(dataSet[i]));
				}
			   	user_capability.add(row);   
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void read_average_orders_file(String fileName){
		BufferedReader br = null;
		int arraySize;
		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] dataSet = sCurrentLine.split(" ");
				arraySize=dataSet.length;
				List<Integer> row = new ArrayList<Integer>();
				for(int i=0; i<arraySize; i++)
					avg_orders.add(Integer.parseInt(dataSet[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		num_tasks = avg_orders.size();
	}
	
	private void read_authorization_file(String fileName){
		BufferedReader br = null;
		int arraySize;	
		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader(fileName));

			while ((sCurrentLine = br.readLine()) != null) {
				List<Integer> row = new ArrayList<Integer>();
				if(!( sCurrentLine.equals(null) || sCurrentLine.equals("") )){
					String[] dataSet= sCurrentLine.split(" ");
					arraySize=dataSet.length;
					for(int i=0; i<arraySize; i++)
						row.add(Integer.parseInt(dataSet[i]));
					}
				user_authorizations.add(row);   
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		num_users = user_authorizations.size();		
	}
	
	public void read_files(String path, String avg, String ua, String uc, String bc, String sc ){
		System.out.println("Reading From: " + path);
		read_authorization_file(path + ua + ".txt");
		read_binding_constraints_file(path + bc + ".txt");
		read_separation_constraints_file(path + sc + ".txt");
		read_user_capability_file(path + uc +".txt");
		read_average_orders_file(path + avg +".txt");
		//printAll

	}

	private void printAll(){
		Print.ArrayList2D(user_authorizations, "User Authorizations: ");
		Print.ArrayList2D(user_capability,"User Capabilities: ");
		Print.ArrayList2D(binding_constraints,"Binding Constraints: ");
		Print.ArrayList2D(separation_constraints, "Separation Constraints: ");
		Print.ArrayList(avg_orders, "Average Orders: ");
		System.out.println("Number of Users: " + num_users);
		System.out.println("Number of Tasks: "+ num_tasks);

	}
	
	
}
