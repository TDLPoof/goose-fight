package net.parkwayschools.core;

//umar will create an incredible piece of code here shortly.
public class Attack extends Thread {
    private long framesActive;
    private int activeKey;
    Goose focus;

    public Attack(long f, int k) {
        framesActive = f;
        activeKey = k;
    }

    public Attack(Attack a) {
        framesActive = a.framesActive;
        activeKey = a.activeKey;
    }

    public void setFocus(Goose g) { this.focus = g; }
    public long getFramesActive() { return this.framesActive; }
    public int getActiveKey() { return this.activeKey; }

    public void run() {
        System.out.println("Ran with active Key " + activeKey);
    }
}