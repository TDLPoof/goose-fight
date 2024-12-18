package net.parkwayschools.core;

import net.parkwayschools.gfx.GfxMgr;
import net.parkwayschools.gfx.RenderObj;
import net.parkwayschools.phys.Collider;
import net.parkwayschools.phys.PhysicsBody;
import net.parkwayschools.phys.Vector2;
import net.parkwayschools.util.Log;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GameMgr implements KeyListener {
    static Log log = new Log("core/gamemgr");
    ArrayList<Goose> _geese;
    ArrayList<Collider> _fieldColliders;
    ArrayList<PhysicsBody> _bodies;

    GfxMgr _gfx;
    Thread _gfxThread;
    Thread _physThread;

    JFrame dbgInspector;
    JTextArea dbgPane;

    void registerGoose(Goose g){
        log.inf("Registering a new goose with the United Geese of the Pond");
        g.body.position = new Vector2(0,60);
        _bodies.add(g.body);
        _geese.add(g);
        g.body.collisionObjects = _fieldColliders;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            _geese.get(0).body.addForce(new Vector2(1,0));
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            _geese.get(0).body.addForce(new Vector2(-1,0));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class PhysMgr implements Runnable {
        ArrayList<PhysicsBody> _toUpdate;
        public PhysMgr(ArrayList<PhysicsBody> pt){
            _toUpdate = pt;
        }

        @Override
        public void run() {
           // while (true) {
                for (PhysicsBody b : _toUpdate) {
                    if (b.collisionObjects.size() == 0){
                        throw new IllegalStateException();
                    }
                  //  b.addForce(new Vector2(0, -0.5));
                    b.update();
                }
//                try {
//                    Thread.sleep(1000 / 30);
//                } catch (InterruptedException e) {
//                }
          //  }
        }
    }

    public void addGoose(){
        Goose greg = new Goose(this);
    }

    private static Collider createPlatform(double x, double y, double width, double height) {
        return new Collider(x, y, width, height);
    }
    void initField(){
        Collider platform = createPlatform(0, 130, 500, 20);
      //  Collider platform2 = createPlatform(480, 0, 40, 380);
        this._fieldColliders.add(platform);
       // this._fieldColliders.add(platform2);
    }

    public GameMgr(){
        dbgInspector = new JFrame("Dbg");
        dbgInspector.setVisible(true);
        dbgInspector.setSize(600,300);
        dbgPane = new JTextArea();
        dbgInspector.add(dbgPane);
      //  dbgInspector.setLayout(new BoxLayout(dbgInspector,BoxLayout.Y_AXIS));
        _bodies = new ArrayList<>();
        _fieldColliders = new ArrayList<>();
        initField();
        _geese = new ArrayList<>();
        log.inf("Starting Phys thread");
        PhysMgr mgr = new PhysMgr(_bodies);
        _gfx = new GfxMgr(mgr);
//        _physThread = new Thread(new PhysMgr(_bodies));
//        _physThread.start();
        log.inf("Starting GFX thread");
        _gfxThread = new Thread(_gfx);
        _gfxThread.start();
        _gfx.addInputHandler(this);
        log.inf("Starting Sync thread");
        Thread hermes = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    tick();
                    try {
                        Thread.sleep(1000 / 24);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        hermes.start();
    }

    void updateRender(){
        //log.inf("updateRender");
        ArrayList<RenderObj> rq = new ArrayList<>();
        rq.add(new RenderObj(new Vector2(0,0),"maps","playplace",false,0));
        //players!
        for (Goose g : _geese){
            rq.add(new RenderObj(g.body.position,"Goose-idle","rIdle",true,4));
        }
        //log.inf("Submitting RQ");
        _gfx.submitRenderQueue(rq);
    }

    void tick(){
        dbgPane.selectAll();
        dbgPane.replaceSelection("");
        for (Goose g : _geese){
            dbgPane.append(g.body.toString()+"\n");
        }
        updateRender();
    }
}
