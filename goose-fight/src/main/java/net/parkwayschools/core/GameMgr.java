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
import java.util.HashSet;

public class GameMgr implements KeyListener {
    static final boolean ENABLE_DBG = false;
    static Log log = new Log("core/gamemgr");
    ArrayList<Goose> _geese;
    ArrayList<Collider> _fieldColliders;
    ArrayList<PhysicsBody> _bodies;

    GfxMgr _gfx;
    Thread _gfxThread;
    Thread _physThread;

    JFrame dbgInspector;
    JTextArea dbgPane;

    HashSet<Integer> currentKeys = new HashSet<>();

    void registerGoose(Goose g){
        log.inf("Registering a new goose with the United Geese of the Pond");
        g.body.position = new Vector2(30,130);
        _bodies.add(g.body);
        _geese.add(g);
        g.body.collisionObjects = _fieldColliders;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private PhysicsBody p1(){
        return _geese.get(0).body;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && p1().jumps > 0) {
            p1().velocity = new Vector2(p1().velocity.x, -10);
            p1().jumps--;
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP && p1().walljumps > 0 && p1().walled) {
            p1().velocity = new Vector2(p1().velocity.x, -10);
            p1().walljumps--;
        }

        if (!currentKeys.contains(e.getKeyCode())) currentKeys.add(e.getKeyCode());

    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKeys.remove(Integer.valueOf(e.getKeyCode()));
    }


    public void addGoose(){
        Goose greg = new Goose(this);
    }

    private static Collider createPlatform(double x, double y, double width, double height) {
        return new Collider(x, y, width, height);
    }
    void initField(){
        Collider platform = createPlatform(0, 160, 500, 10);
        Collider wLeft = createPlatform(0, 0, 10, 160);
        Collider wRight = createPlatform(310, 0, 10, 160);
        this._fieldColliders.add(platform);
        this._fieldColliders.add(wLeft);
        this._fieldColliders.add(wRight);
    }

    public GameMgr(){
        if (ENABLE_DBG) {
            dbgInspector = new JFrame("Dbg");
            dbgInspector.setVisible(true);
            dbgInspector.setSize(600, 300);
            dbgPane = new JTextArea();
            dbgInspector.add(dbgPane);
        }

        _geese = new ArrayList<>();
        _bodies = new ArrayList<>();
        _fieldColliders = new ArrayList<>();
        initField();

        log.inf("Starting GFX thread");
        _gfx = new GfxMgr();
        _gfx.addInputHandler(this);
        _gfxThread = new Thread(_gfx);
        _gfxThread.start();

        log.inf("Starting Compute thread");
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
        ArrayList<RenderObj> rq = new ArrayList<>();
        rq.add(new RenderObj(new Vector2(0,0),"maps","playplace",false,0));
        //players!
        for (Goose g : _geese){
            rq.add(new RenderObj(g.body.position,"Goose-idle","rIdle",true,4));
        }
        _gfx.submitRenderQueue(rq);
    }

    void tick(){
        if (ENABLE_DBG) {
            dbgPane.selectAll();
            dbgPane.replaceSelection("");
        }
        for (Goose g : _geese){
            if (ENABLE_DBG) dbgPane.append(g.body.toString()+"\n");
        }
        for (PhysicsBody b : _bodies){
            if (b.collisionObjects.size() == 0){
                throw new IllegalStateException();
            }
            b.update();
        }
        if (currentKeys.contains(KeyEvent.VK_LEFT) && !p1().walled) p1().velocity = new Vector2(-8, p1().velocity.y);
        if (currentKeys.contains(KeyEvent.VK_RIGHT) && !p1().walled) p1().velocity = new Vector2(8, p1().velocity.y);

        updateRender();
    }
}
