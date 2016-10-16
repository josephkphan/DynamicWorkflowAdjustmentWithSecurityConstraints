package com.jp.solver.main;

import com.jp.solver.helper.Print;
import com.jp.solver.helper.RandGen;
import com.jp.solver.algorithm.Solution;
import com.jp.solver.dataStructures.TaskPair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Solution AC = new Solution();

        String outputFile = "output.txt";

        int numUsers,numTasks,bc,sc,ua,uc;
        numUsers = 50;
        numTasks = 450;
        bc = 10;
        sc = 10;
        ua = 10;
        uc = 10;
        //*****************SOLVING**********************//
        String sizeFolder = numUsers+ "x" +numTasks;
        String bcFile = "java_bc_" + bc;
        String scFile = "java_sc_" + sc;
        String uaFile = "java_ua_" + ua;
        String ucFile = "java_uc_" + uc;
        String avgFile = "java_avg_10";
        String path = "/home/jphan/IdeaProjects/DynamicWorkflowAdjustmentWithSecurityConstraints/resources/ratio/" + sizeFolder + "/";

        //RESIL
//        double tmpu = Integer.parseInt(args[3]) * .01 * AC.fileData.num_tasks;
//        double tmpb = Integer.parseInt(args[4]) * .01 * AC.fileData.num_tasks;
//        double tmps = Integer.parseInt(args[5]) * .01 * AC.fileData.num_tasks;
//        double tmpc = Integer.parseInt(args[6]) * .01 * AC.fileData.num_tasks;
        //todo put that back later
        double tmpu = 0;
        double tmpb = 0;
        double tmps = 0;
        double tmpc = 0;


        AC.fileData.read_files(path, avgFile, uaFile, ucFile, bcFile, scFile);
        AC.initialize();
        long startTime = System.currentTimeMillis();
        AC.search_method();
        long endTime = System.currentTimeMillis();

        /*System.out.println("-------------------------");
        System.out.println("path = " + path);
        System.out.println("sizeFolder = " + sizeFolder);
        System.out.println("avgFile = " + avgFile);
        System.out.println("uaFile = " + uaFile);
        System.out.println("ucFile = " + ucFile);
        System.out.println("bcFile = " + bcFile);
        System.out.println("scFile = " + scFile);
        */
        double executionTime = (endTime - startTime);

        System.out.println("Total execution time: " + executionTime + "ms");

        //System.out.println("-------------------------------------------------");
        //System.out.println("-------------------------------------------------");


        //*****************RESILIENCE**********************//

        double resilExecutionTime = 0;

        int num_u = (int) tmpu;
        int num_b = (int) tmpb;
        int num_s = (int) tmps;
        int maxTaskChange = (int) tmpc;
        /*System.out.println("\nUser Added: " + num_u
                + "\nBoD Added: " + num_b
                + "\nSoD Added: " + num_s
                + "\nMax Task Change: " + maxTaskChange);
        */

        boolean resil = false;
        if (resil) {
            int[] u = RandGen.removeUsers(num_u, AC.fileData.num_tasks);
            TaskPair[] b = RandGen.taskPair(num_b, AC.fileData.num_tasks);
            TaskPair[] s = RandGen.taskPair(num_s, AC.fileData.num_tasks);
            Print.resilience_constraints(u, b, s);

            AC.resilience_initialize(u, b, s);
            long startTime2 = System.currentTimeMillis();
            AC.resilience_search_method(maxTaskChange);
            long endTime2 = System.currentTimeMillis();
            resilExecutionTime = endTime2 - endTime;
            System.out.println("Total execution time: " + resilExecutionTime + "ms");
        }
        System.out.println("-------------------------");

        /***********************WRITE SOLUTION TO FILE******************************/
        if (!resil) {
            try {
                String data = "\n---------------------------"
                        + "\nTesting DataSet: " + sizeFolder
                        + "\nBoDPercent: " + bcFile
                        + "\nSoDPercent: " + scFile
                        + "\nUAPercent: " + uaFile
                        + "\nUCPercent: " + ucFile
                        + "\nTotal execution time: " + executionTime + "ms"
                        + "\n---------------------------";


                File file = new File(outputFile);

                //if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                //true = append file
                FileWriter fileWritter = new FileWriter(file.getName(), true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write(data);
                bufferWritter.close();

                System.out.println("Done");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            /***********************WRITE RESILIENCE SOLUTION TO FILE******************************/
            try {
                String result = "";
                if (AC.resilSuccess)
                    result += "SUCCESS";
                else
                    result += "FAILED";
                String data = "\n---------------------------"
                        + "\nTesting DataSet: " + sizeFolder
                        + "\nBoDPercent: " + bcFile
                        + "\nSoDPercent: " + scFile
                        + "\nUAPercent: " + uaFile
                        + "\nUCPercent: " + ucFile
                        + "\n---RESILENCE CHECK---"
                        + "\nUser Added: " + num_u
                        + "\nBoD Added: " + num_b
                        + "\nSoD Added: " + num_s
                        + "\nMax Task Change: " + maxTaskChange
                        + "\nTotal execution time: " + resilExecutionTime + "ms"
                        + "\n" + result
                        + "\n---------------------------";


                File file = new File(outputFile);

                //if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                //true = append file
                FileWriter fileWritter = new FileWriter(file.getName(), true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write(data);
                bufferWritter.close();

                System.out.println("Done");

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
