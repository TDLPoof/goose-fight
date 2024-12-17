package net.parkwayschools.core;

import java.util.EventListener;

//umar will create an incredible piece of code here shortly.
public class State extends Thread {
    public double framesActive;
    private String test;
    public String activeKey;

    public State(double f, String k) {
        framesActive = f;
        activeKey = k;
    }
    public State(double f, String k, String t) {
        framesActive = f;
        activeKey = k;
        test = t;
    }

    public void run() {
        try {
            this.sleep((int)(1000*framesActive));
            System.out.println(test);
        } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}