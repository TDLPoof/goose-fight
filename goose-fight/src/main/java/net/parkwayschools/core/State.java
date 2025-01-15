package net.parkwayschools.core;
public class State implements Runnable {
    protected long framesActive; // make sure any processes you do here do not exceed this value! 
    protected Goose focus;
    public volatile boolean running = true;
    public boolean hasFiredAttack;
    public Animation attackAnim;
    protected long ogFramesActive;

    public State(State s) {
        framesActive = s.getFramesActive();
    }

    public State(long f) {
        this.framesActive = f;
        this.ogFramesActive = framesActive;
    }
    public void reset(){
        this.framesActive = ogFramesActive;
        this.hasFiredAttack = false;
    }

    public void setFocus(Goose g) { this.focus = g; }
    public long getFramesActive() { return this.framesActive; }
    
    @Override public String toString() {return "state, active for " + framesActive + " on " + focus; }

    @Override public void run() {
        try {
            System.out.println("State occuring for " + this.framesActive + " state");
            Thread.sleep(this.getFramesActive()); 
        }
        catch(InterruptedException e) {}
    }
}
