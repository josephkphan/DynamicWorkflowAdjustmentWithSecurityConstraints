package com.jp.solver.dataStructures;

public class UserPair implements Comparable<UserPair> {
    int user;
    int capability;

    public UserPair(int u, int c) {
        user = u;
        capability = c;
    }

    public int get_user() {
        return user;
    }

    public int get_capability() {
        return capability;
    }

    @Override
    public int compareTo(UserPair other) {
        return -1 * Integer.valueOf(this.capability).compareTo(other.capability);
    }


}
