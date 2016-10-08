package com.jp.solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Solution AC = new Solution();

        //*****************SOLVING**********************//
        String Size = args[0];
        String BoDPercent = args[1];
        String SoDPercent = args[2];
        String TestSet = "/home/jphan/IdeaProjects/DynamicWorkflowAdjustmentWithSecu\n" +
                "rityConstraints/src/com/jp/resources"
                + Size + "/" + BoDPercent + "-" + SoDPercent + "/";
        AC.fileData.read_files(TestSet);
        AC.initialize();
        long startTime = System.currentTimeMillis();
        AC.search_method();
        long endTime = System.currentTimeMillis();

        System.out.println("-------------------------");
        System.out.println("Testing DataSet: " + Size);
        System.out.println("BoDPercent: " + BoDPercent);
        System.out.println("SoDPercent: " + SoDPercent);
        double executionTime = (endTime-startTime);

        System.out.println("Total execution time: " + executionTime + "ms");

        System.out.println("-------------------------------------------------");
        System.out.println("-------------------------------------------------");
        //*****************RESILIENCE**********************//
        double resilExecutionTime = 0;
        double tmpu = Integer.parseInt(args[3]) * .01 * AC.fileData.num_tasks;
        double tmpb = Integer.parseInt(args[4]) * .01 * AC.fileData.num_tasks;
        double tmps = Integer.parseInt(args[5]) * .01 * AC.fileData.num_tasks;
        double tmpc = Integer.parseInt(args[6]) * .01 * AC.fileData.num_tasks;
        int num_u = (int) tmpu;
        int num_b = (int) tmpb;
        int num_s = (int) tmps;
        int maxTaskChange = (int)tmpc;
        System.out.println( "\nUser Added: " + num_u
                + "\nBoD Added: " + num_b
                + "\nSoD Added: " + num_s
                + "\nMax Task Change: " + maxTaskChange);

        boolean resil = false;
        if(resil){
            int[] u = RandGen.removeUsers(num_u, AC.fileData.num_tasks);
            TaskPair[] b = RandGen.taskPair(num_b, AC.fileData.num_tasks);
            TaskPair[] s = RandGen.taskPair(num_s, AC.fileData.num_tasks);
            Print.resilience_constraints(u, b, s);

            AC.resilience_initialize(u,b,s);
            long startTime2 = System.currentTimeMillis();
            AC.resilience_search_method(maxTaskChange);
            long endTime2 = System.currentTimeMillis();
            resilExecutionTime = endTime2-endTime;
            System.out.println("Total execution time: " + resilExecutionTime + "ms");
        }
        System.out.println("-------------------------");

        /***********************WRITE SOLUTION TO FILE******************************/
        if(!resil){
            try{
                String data = "\n---------------------------"
                        + "\nTesting DataSet: " + Size
                        + "\nBoDPercent: " + BoDPercent
                        + "\nSoDPercent: " + SoDPercent
                        + "\nTotal execution time: " + executionTime + "ms"
                        +"\n---------------------------";


                File file =new File(args[7]);

                //if file doesnt exists, then create it
                if(!file.exists()){
                    file.createNewFile();
                }

                //true = append file
                FileWriter fileWritter = new FileWriter(file.getName(),true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write(data);
                bufferWritter.close();

                System.out.println("Done");

            }catch(IOException e){
                e.printStackTrace();
            }
        }else{

            /***********************WRITE RESILIENCE SOLUTION TO FILE******************************/
            try{
                String result = "";
                if(AC.resilSuccess)
                    result += "SUCCESS";
                else
                    result += "FAILED";
                String data = "\n---------------------------"
                        + "\nTesting DataSet: " + Size
                        + "\nBoDPercent: " + BoDPercent
                        + "\nSoDPercent: " + SoDPercent
                        + "\n---RESILENCE CHECK---"
                        + "\nUser Added: " + num_u
                        + "\nBoD Added: " + num_b
                        + "\nSoD Added: " + num_s
                        + "\nMax Task Change: " + maxTaskChange
                        + "\nTotal execution time: " + resilExecutionTime + "ms"
                        + "\n" + result
                        +"\n---------------------------";


                File file =new File(args[7]);

                //if file doesnt exists, then create it
                if(!file.exists()){
                    file.createNewFile();
                }

                //true = append file
                FileWriter fileWritter = new FileWriter(file.getName(),true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write(data);
                bufferWritter.close();

                System.out.println("Done");

            }catch(IOException e){
                e.printStackTrace();
            }


        }
    }
}
