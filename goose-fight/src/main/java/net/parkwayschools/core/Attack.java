public class Attack extends State {
    /* inherits long framesActive, Goose focus from State */
    protected int activeKey;

    public Attack(long f, int k) {
        super(f);
        activeKey = k;
    }

    public int getActiveKey() {
        System.out.println("getting active key" + this.activeKey);
        return this.activeKey; }

    @Override public void run() {
        while (running) {
            System.out.println("Triggered by Key " + activeKey + " for " + framesActive + " ms");
            try { Thread.sleep(this.getFramesActive()); }
            catch(InterruptedException e) {}
            running = false;
        }
        
    }
}
