public class State implements Runnable {
    protected long framesActive; // make sure any processes you do here do not exceed this value! 
    protected Goose focus;
    public volatile boolean running = true;

    public State(State s) {
        framesActive = s.getFramesActive();
    }

    public State(long f) {
        this.framesActive = f;
    }

    public void setFocus(Goose g) { this.focus = g; }
    public long getFramesActive() { return this.framesActive; }
    
    public void stop() { 
        running = false; 
        System.out.println("State stopped.");
    }

    @Override public String toString() {return "state, active for " + framesActive + " on " + focus; }

    @Override public void run() {
        running = true; 
        while (running) {
            System.out.println("State occuring for " + this.framesActive + " state");
            try { Thread.sleep(this.getFramesActive()); }
            catch(InterruptedException e) {}
            running = false;
        }
    }
}
