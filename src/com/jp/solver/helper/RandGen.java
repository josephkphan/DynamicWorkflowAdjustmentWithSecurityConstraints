package com.jp.solver.helper;

import com.jp.solver.dataStructures.TaskPair;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandGen {
    public static int[] removeUsers(int numConstraints, int max) {
        int[] array = new int[numConstraints];
        Set<Integer> set = new HashSet<Integer>();
        Random random = new Random();
        while (set.size() < numConstraints) {
            set.add(random.nextInt(max));
        }
        int counter = 0;
        for (Integer i : set) {
            array[counter++] = i;
        }
        return array;
    }

    public static TaskPair[] taskPair(int numConstraints, int max) {
        TaskPair[] array = new TaskPair[numConstraints];
        Random random = new Random();
        Set<TaskPair> set = new HashSet<TaskPair>();
        int a, b;
        while (set.size() < numConstraints) {
            a = b = 0;
            while (a == b) {
                a = random.nextInt(max);
                b = random.nextInt(max);
            }
            set.add(new TaskPair(a, b));
        }
        int counter = 0;
        for (TaskPair i : set) {
            array[counter++] = i;
        }
        return array;
    }

}
