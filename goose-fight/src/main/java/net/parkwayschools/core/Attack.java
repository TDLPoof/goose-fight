package net.parkwayschools.core;

public class Attack extends State {
    /* inherits long framesActive, Goose focus from State */
    protected int activeKey;

    public Attack(long f, int k) {
        super(f);
        activeKey = k;
        hasFiredAttack = false;
    }

    public int getActiveKey() {
        System.out.println("getting active key" + this.activeKey);
        return this.activeKey; }

    protected void innerDoAttack(){

    }

    @Override
    public void run() {
     //       System.out.println("Triggered by Key " + activeKey + " for " + framesActive + " ms");
            if (!hasFiredAttack){
                innerDoAttack();
                hasFiredAttack = true;
            }
    }
}
