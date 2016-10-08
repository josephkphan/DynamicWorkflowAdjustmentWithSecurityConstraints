/*
 * Created by: Joseph Phan
 * File Name: Access_Control.java
 * File Description: Algorithm to find a possible feasible solution to assign users to tasks
 * given multiple constraints
 */
package com.jp.solver.algorithm;

import com.jp.solver.dataStructures.TaskPair;
import com.jp.solver.dataStructures.UserPair;
import com.jp.solver.helper.Copy;

import java.util.*;


public class Solution {
    //Read from files
    public FileData fileData = new FileData();
    public boolean resilSuccess;

    //parameters to test resilience
    List<List<UserPair>> resil_authorized = new ArrayList<List<UserPair>>();
    Deque<Integer> tasks_to_change = new LinkedList<Integer>();
    Stack<Integer> task_history = new Stack<Integer>();
    Stack<Integer> task_num_history = new Stack<Integer>();
    int[] previous_solution;
    Stack<Integer> num_same = new Stack<Integer>();
    List<List<Integer>> resil_a = new ArrayList<List<Integer>>();


    //used in search algorithm
    Stack<int[]> solution = new Stack<int[]>();        //contains history of task assignments
    Stack<Integer> next_index = new Stack<Integer>();        //used to find last task(s) assigned
    Stack<List<List<UserPair>>> authorized = new Stack<List<List<UserPair>>>();    //history contains which users are available
    Stack<double[]> users_load = new Stack<double[]>(); // contains history of user's load


    /**********************Initializing************************/
    //Description: initializes variables needed to find solution
    public void initialize() {
        int[] empty_sol = new int[fileData.num_tasks];
        for (int i = 0; i < fileData.num_tasks; i++)
            empty_sol[i] = -1;
        solution.push(empty_sol);
        next_index.push(0);
        users_load.push(new double[fileData.num_users]);
        create_task_authorizations();
        authorized.push(create_task_authorizations());

        //DEBUGGING
        //print_2DArrayList(fileData.task_authorizations, "task Authorizations: ");
        //print_authorized(authorized.peek());
    }

    //Description: creates 2D array with row index = task# and each row element is
    //a user who is authorized to do the task
    public List<List<UserPair>> create_task_authorizations() {
        List<List<UserPair>> copy = new ArrayList<List<UserPair>>();
        List<UserPair> row;
        for (int i = 0; i < fileData.num_tasks; i++)
            copy.add(new ArrayList<UserPair>());
        List<Integer> peek;
        for (int i = 0; i < fileData.user_authorizations.size(); i++) {
            peek = fileData.user_authorizations.get(i);
            for (int j = 0; j < peek.size(); j++) {
                row = copy.get(peek.get(j));
                row.add(new UserPair(i, fileData.user_capability.get(i).get(j)));
                //System.out.println("adding user" + i + " to task " + peek.get(j));
            }
        }
        return copy;
    }

    //Description: Returns a copy of the given matrix with AC elements
    public List<List<UserPair>> copy_AC_Stack(List<List<UserPair>> other) {
        List<List<UserPair>> copy = new ArrayList<List<UserPair>>();
        List<UserPair> row;
        List<UserPair> peek;
        for (int i = 0; i < other.size(); i++) {
            peek = other.get(i);
            row = new ArrayList<UserPair>();
            for (int j = 0; j < peek.size(); j++) {
                row.add(new UserPair(peek.get(j).get_user(), peek.get(j).get_capability()));
            }
            copy.add(row);
        }
        return copy;
    }


    /***********************Search Algorithm******************************/
    //Description: Search method process to find a feasible solution
    public void search_method() {
        System.out.println("---------------------------------------\n---------------------------------------");
        List<List<UserPair>> cur_authorized, new_authorized;
        double[] cur_users_load = Copy.array(users_load.peek());
        int cur_index;
        int[] cur_sol, new_sol;
        while (!next_index.empty()) {
            cur_authorized = authorized.peek();
            cur_index = next_index.peek();
            cur_sol = solution.peek();
            new_sol = Copy.array(cur_sol);
            //print_authorized(cur_authorized);

            while (cur_authorized.get(cur_index).size() != 0) {
                UserPair temp = cur_authorized.get(cur_index).get(0);

                if (propogate(cur_index, temp, cur_authorized, cur_users_load, new_sol)) {
                    if (check_separation_constraint(cur_index, temp, cur_authorized, new_sol)) {

                        users_load.push(cur_users_load);
                        authorized.push(copy_AC_Stack(cur_authorized));
                        solution.push(new_sol);
                        int counter = 0;
                        while (new_sol[cur_index] != -1) {
                            cur_index = (cur_index + 1) % fileData.num_tasks;    //index will land on next task that needs to be filled
                            if (counter++ > fileData.num_tasks) {
                                cur_index = fileData.num_tasks + 1;
                                break;
                            }
                        }
                        next_index.push(cur_index);
                        if (check_completed(cur_index))
                            break;
                        //found a solution
                        //print_array(new_sol,"current assignments:");
                        //print_darray(cur_users_load,"current users load:");
                        //print_authorized(cur_authorized);
                        //System.out.println("---------------------------------------");
                    }
                }
                if (check_completed(cur_index))
                    break;//found a solution
            }
            //System.out.println("Current Index" + cur_index );
            if (check_completed(cur_index))
                break;//found a solution

            if (authorized.empty()) {
                break;    //no feasible solution
            } else
                pop_stacks();

        }
        //Prints final results
        if (solution.isEmpty())
            System.out.println("FAILED: NO SOLUTION");
        else {
            //Print.solution(solution);
        }
    }

    //Description: Checks if algorithm is finished
    public boolean check_completed(int cur_index) {
        if (cur_index < fileData.num_tasks)
            return false;
        return true;
    }

    //Description: Pops multiple stacks
    public void pop_stacks() {
        solution.pop();
        next_index.pop();
        users_load.pop();
        authorized.pop();
    }

    /************************Check Constraints**********************/
    //Description: Given a user, this will propogate out basd on binding constraints. Will return true if user successfully propogate
    public boolean propogate(int task, UserPair user, List<List<UserPair>> cur_authorized, double[] cur_users_load, int[] new_sol) {
        List<UserPair> check;
        boolean success = true;
        double temp = cur_users_load[user.get_user()];

        List<Integer> row = fileData.binding_constraints.get(task);
        for (int i = 0; i < row.size(); i++) {
            check = cur_authorized.get(row.get(i));
            for (int j = 0; j < check.size(); j++) {        //propogates out. finds all user capable and checks all of them and their load
                if (user.get_user() == check.get(j).get_user()) {
                    //System.out.println("Checking User" + check.get(j).get_user() +" capability " + check.get(j).get_capability());
                    //System.out.println("Task " + row.get(i)+" with Orders" + fileData.avg_orders.get(row.get(i)));
                    new_sol[row.get(i)] = check.get(j).get_user();

                    cur_users_load[user.get_user()] += (double) fileData.avg_orders.get(row.get(i)) / (double) check.get(j).get_capability();
                    //System.out.println("Total Load for User"+user.get_user() + " is "+cur_users_load[user.get_user()]);
                    cur_authorized.get(row.get(i)).remove(j);
                    break;
                }
                if (j == check.size() - 1)
                    success = false;
            }
        }

        //print_authorized(cur_authorized);
        if (!success || cur_users_load[user.get_user()] >= 1) {        //propogate failed. must reset solution back to -1
            cur_users_load[user.get_user()] = temp;                //resets the cur_user load
            for (int i = 0; i < row.size(); i++) {
                new_sol[row.get(i)] = -1;
            }
            return false;
        }
        //System.out.println("Propogate success - Task " + task + " with User " + user.get_user());
        return success;
    }

    //Description: Checks if user given contradicts separation constant. if it matches then returns false
    public boolean check_separation_constraint(int task, UserPair user, List<List<UserPair>> cur_authorized, int[] new_sol) {
        List<Integer> constraint = fileData.separation_constraints.get(task);
        for (int i = 0; i < constraint.size(); i++)
            if (task != constraint.get(i) && user.get_user() == new_sol[constraint.get(i)])
                return false;
        return true;
    }

    /********************************Used to Check Resilience ***********************************/

    //Description: Applies additional parameters to test resilience
    public void resilience_initialize(int[] u, TaskPair[] b, TaskPair[] s) {
        resilSuccess = false;
        int[] cur_sol = Copy.array(solution.peek());    //TODO: don't you have to remove from all stack history?
        Set<Integer> index_to_clear = new HashSet<Integer>();
        double[] cur_user_load = Copy.array(users_load.peek());
        previous_solution = Copy.array(solution.peek());
        while (!authorized.empty()) {
            authorized.pop();
            users_load.pop();
            solution.pop();
        }

        for (int i = 0; i < u.length; i++) {        //clear out any task with the indicated users
            fileData.user_authorizations.get(u[i]).clear();
            for (int j = 0; j < fileData.num_tasks; j++) {
                if (u[i] == cur_sol[j])
                    index_to_clear.add(j);
            }
        }

        resilience_add_binding_constraints(cur_user_load, cur_sol, b, index_to_clear);
        resilience_add_separation_constraints(cur_user_load, cur_sol, s, index_to_clear);

        resil_authorized = create_task_authorizations();
        create_resil_a();
        Iterator it = index_to_clear.iterator();
        while (it.hasNext()) {
            // Get element
            Object element = it.next();
            cur_sol[(Integer) element] = -1;
            //System.out.print(element + " ");
            tasks_to_change.addLast((Integer) element);
        }


        authorized.push(resil_authorized);
        users_load.push(cur_user_load);
        solution.push(cur_sol);
        num_same.push(0);
        //Print.solution(solution);
        //search_method();					//performs search algorithm using existing history
        //Print.authorized(resil_authorized);

    }

    public void create_resil_a() {
        for (List<Integer> row : fileData.user_authorizations) {
            List<Integer> copyRow = new ArrayList<Integer>();
            for (int i : row) {
                copyRow.add(i);
            }
            resil_a.add(copyRow);
        }
    }

    public void resilience_add_binding_constraints(double[] user_load, int[] cur_sol, TaskPair[] b, Set<Integer> L) {
        int t1, t2;
        double load;
        List<Integer> t1_row, t2_row, row_to_add, row;
        Set<Integer> temp = new HashSet<Integer>();
        for (int i = 0; i < b.length; i++) {
            t1 = b[i].get_task1();
            t2 = b[i].get_task2();

            t1_row = fileData.binding_constraints.get(t1);
            t2_row = fileData.binding_constraints.get(t2);
            for (int j = 0; j < t1_row.size(); j++) {
                row_to_add = fileData.binding_constraints.get(t1_row.get(j));
                temp.addAll(row_to_add);
                temp.addAll(t2_row);
                row_to_add.clear();
                row_to_add.addAll(temp);
                temp.clear();

            }
            for (int j = 0; j < t2_row.size(); j++) {
                row_to_add = fileData.binding_constraints.get(t2_row.get(j));
                //row_to_add.add(t1);
                temp.addAll(row_to_add);
                temp.addAll(t1_row);
                row_to_add.clear();
                row_to_add.addAll(temp);
                temp.clear();
            }
            row = fileData.user_authorizations.get(cur_sol[t1]);
            for (int j = 0; j < row.size(); j++) {    //add back in user load
                if (row.get(j) == t1) {
                    load = fileData.avg_orders.get(t1) / fileData.user_capability.get(cur_sol[t1]).get(j);
                    user_load[cur_sol[t1]] -= load;
                    //fixes load
                    //authorized.peek().get(t1).add(new AC_data(cur_sol[t1], fileData.user_capability.get(cur_sol[t1]).get(j)));
                    //fixes authorizations
                    break;
                }

            }
            row_to_add = fileData.binding_constraints.get(t1);    //removes user from current solution
            L.addAll(row_to_add);
        }
    }

    //TODO: ADD USER LOAD and Re add users back to cur Sol
    public void resilience_add_separation_constraints(double[] user_load, int[] cur_sol, TaskPair[] s, Set<Integer> L) {
        int t1, t2;
        double load;
        List<Integer> t1_row, t2_row, row_to_add, row;
        Set<Integer> temp = new HashSet<Integer>();
        for (int i = 0; i < s.length; i++) {
            t1 = s[i].get_task1();
            t2 = s[i].get_task2();

            t1_row = fileData.separation_constraints.get(t1);
            t2_row = fileData.separation_constraints.get(t2);
            for (int j = 0; j < t1_row.size(); j++) {
                row_to_add = fileData.separation_constraints.get(t1_row.get(j));
                temp.addAll(row_to_add);
                temp.addAll(t2_row);
                row_to_add.clear();
                row_to_add.addAll(temp);
                temp.clear();

            }
            for (int j = 0; j < t2_row.size(); j++) {
                row_to_add = fileData.separation_constraints.get(t2_row.get(j));
                //row_to_add.add(t1);
                temp.addAll(row_to_add);
                temp.addAll(t1_row);
                row_to_add.clear();
                row_to_add.addAll(temp);
                temp.clear();
            }
            row = fileData.user_authorizations.get(cur_sol[t1]);
            for (int j = 0; j < row.size(); j++) {    //add back in user load
                if (row.get(j) == t1) {
                    load = fileData.avg_orders.get(t1) / fileData.user_capability.get(cur_sol[t1]).get(j);
                    user_load[cur_sol[t1]] -= load;
                    //fixes load
                    //authorized.peek().get(t1).add(new AC_data(cur_sol[t1], fileData.user_capability.get(cur_sol[t1]).get(j)));
                    //fixes authorizations
                    break;
                }

            }
            row = fileData.user_authorizations.get(cur_sol[t1]);
            for (int j = 0; j < row.size(); j++) {    //add back in user load
                if (row.get(j) == t1) {
                    load = fileData.avg_orders.get(t1) / fileData.user_capability.get(cur_sol[t1]).get(j);
                    user_load[cur_sol[t1]] -= load;
                    //fixes load
                    //authorized.peek().get(t1).add(new AC_data(cur_sol[t1], fileData.user_capability.get(cur_sol[t1]).get(j)));
                    //fixes authorizations
                    break;
                }

            }
            row_to_add = fileData.separation_constraints.get(t1);    //sets solution back to -1
            L.addAll(row_to_add);
            row_to_add = fileData.separation_constraints.get(t2);
            L.addAll(row_to_add);
        }

    }

    public void resilience_search_method(int limit) {
        int next_task, possible_user;
        int[] cur_sol;
        double[] cur_user_load;
        int user_cap;
        boolean success;
        List<Integer> change = new ArrayList<Integer>();
        List<List<UserPair>> cur_authorized;
        while (tasks_to_change.size() != 0) {
            if (authorized.isEmpty()) {
                System.out.println("FAILED: NO SOLUTION");
                break;
            }
            cur_authorized = authorized.peek();
            cur_sol = solution.peek();
            cur_user_load = users_load.peek();
            next_task = tasks_to_change.peek();
            ///System.out.println("------------------------\nFilling task: "+next_task);
            success = false;
            while (cur_authorized.get(next_task).size() != 0) {
//				System.out.println("tasks_to_change: " + tasks_to_change);
//				System.out.println("task_history: " + task_history);
                possible_user = cur_authorized.get(next_task).get(0).get_user();
                //System.out.println("Next User: " + possible_user);
                //print_authorized(cur_authorized);
                //Print.queue(tasks_to_change);
                //Print.task_history(task_history);
                //System.out.println("PossibleUser: " + possible_user);
                user_cap = cur_authorized.get(next_task).get(0).get_capability();
                cur_authorized.get(next_task).remove(0);    //removes
                change.clear();

                if (resil_check_user_load(next_task, possible_user, user_cap, cur_user_load)) {
                    if (resil_check_BoD(next_task, possible_user, change)) {    //!!!BoD must go before SoD
                        if (resil_check_SoD(next_task, possible_user, change)) {
                            //TODO dont need to care about task to change until it is actually over
                            if (change.size() + task_history.size() - num_same.peek() < limit) {
                                //if(change.size()+tasks_to_change.size()+task_history.size() - num_same.peek()<limit){ //success

                                //System.out.println("SUCCESS");
                                if (previous_solution[next_task] == possible_user)
                                    num_same.push(num_same.peek() + 1);
                                else
                                    num_same.push(num_same.peek());
                                cur_sol[next_task] = possible_user;
                                solution.push(Copy.array(cur_sol));
                                users_load.push(Copy.array(cur_user_load));
                                task_history.push(tasks_to_change.peek());
                                task_num_history.push(change.size());
                                cur_sol[next_task] = possible_user;
                                tasks_to_change.pop();
                                authorized.push(copy_AC_Stack(cur_authorized));
                                for (int i = 0; i < change.size(); i++) {
                                    tasks_to_change.addLast(change.get(i));
                                }
//								System.out.println("Set Task " + next_task + " to " + possible_user);
//								System.out.println("tasks_to_change: " + tasks_to_change);
//								System.out.println("task_history: " + task_history);
                                success = true;
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("Too much Load");
                }
            }
            if (task_history.empty()) {
                System.out.println("FAILED: NO SOLUTION!!");
                break;
            }
            if (tasks_to_change.size() == 0) {
                break;
            }
            if (!success) {
                resil_pop();
//				System.out.println("POP");
                //pop back
            }
        }
        if (tasks_to_change.size() == 0) {
            resilSuccess = true;
            System.out.println("SUCCESS!!!");
            //Print.solution(solution);
        }
    }

    public void resil_pop() {
        solution.pop();
        authorized.pop();
        users_load.pop();

        for (int i = 0; i < task_num_history.peek(); i++)
            tasks_to_change.pollLast();
        tasks_to_change.addFirst(task_history.peek());
        task_history.pop();
        task_num_history.pop();
        //need to possible remove some of the deque
    }

    public boolean resil_check_user_load(int task, int user, int capability, double[] cur_user_load) {
        double additional_load = fileData.avg_orders.get(task) / capability;
        if ((cur_user_load[user] + additional_load) <= 1) {
            cur_user_load[user] += additional_load;
            return true;
        }
        return false;
    }

    public boolean resil_check_BoD(int task, int user, List<Integer> change) {
//		System.out.println("-------------------------------------");
        int[] cur_sol = solution.peek();
        List<Integer> row = fileData.binding_constraints.get(task);

//		System.out.print("BinD Cons: "  );
//		for( int j : row)
//			System.out.print(j + " ");
//		System.out.println();

        for (int i = 0; i < row.size(); i++) {
//			System.out.println("checking task: " + row.get(i) + " with user " + user);
//			System.out.print("User " + user + ": ");
//			for(int j : fileData.user_authorizations.get(user))
//				System.out.print(j + " ");
//			System.out.println();

            if (!fileData.user_authorizations.get(user).contains(row.get(i))) {
//				System.out.println("Checkpoint");
                return false;
            }

            if (task_history.contains(row.get(i)) && user != solution.peek()[row.get(i)]) {
                return false;
            }

            if (cur_sol[row.get(i)] != user && row.get(i) != task &&    // dnot need to add current task back to queue
                    !tasks_to_change.contains(row.get(i)) && !task_history.contains(row.get(i))) {

//				System.out.println("BoD adding to change:" + row.get(i));
                change.add(row.get(i));

            }

        }
        return true;
    }

    public boolean resil_check_SoD(int task, int user, List<Integer> change) {
        int[] cur_sol = solution.peek();
        List<Integer> row = fileData.separation_constraints.get(task);
        Set<Integer> different = new HashSet<Integer>();
        different.add(cur_sol[task]);
        for (int i = 0; i < row.size(); i++) {
            if (row.get(i) != task) {
                if (different.contains(cur_sol[row.get(i)])) { //add to queue

                    if (!task_history.contains(row.get(i))) {
                        if (!tasks_to_change.contains(row.get(i))) {
                            //System.out.println("SoD adding to change:" + row.get(i));
                            change.add(row.get(i));
                        }
                    } else {
                        //System.out.println("SoD FAIL");
                        return false;
                    }
                } else {
                    different.add(cur_sol[row.get(i)]);
                }
            }

        }
        //change.remove(task);
        /*
		for(int i=0; i<task_history.size();i++)
			change.remove(task_history.get(i));
		Iterator it = tasks_to_change.iterator();
		for(int i=0; i<tasks_to_change.size();i++)
			change.remove(tasks_to_change.;
		*/
        return true;
    }


}
