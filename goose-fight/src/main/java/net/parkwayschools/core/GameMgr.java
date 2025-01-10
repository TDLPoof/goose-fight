package net.parkwayschools.core;

import net.parkwayschools.gfx.Effect;
import net.parkwayschools.gfx.GfxMgr;
import net.parkwayschools.gfx.RenderObj;
import net.parkwayschools.net.NetMgr;
import net.parkwayschools.phys.Collider;
import net.parkwayschools.phys.PhysicsBody;
import net.parkwayschools.phys.Vector2;
import net.parkwayschools.snd.SndMgr;
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

    enum GameState {
        TITLE_SCREEN,
        GAMEPLAY,
        RESULTS_SCREEN;

        boolean physOn(){return this == GAMEPLAY;}
        boolean scnRenderOn(){ return this != TITLE_SCREEN; }
    }

    ArrayList<Goose> _geese;
    ArrayList<Collider> _fieldColliders;
    ArrayList<PhysicsBody> _bodies;
    ArrayList<Effect> _effects;

    GfxMgr _gfx;
    Thread _gfxThread;
    NetMgr _net;
    SndMgr _snd;

    JFrame dbgInspector;
    JTextArea dbgPane;

    HashSet<Integer> currentKeys = new HashSet<>();

    synchronized void registerGoose(Goose g){
        log.inf("Registering a new goose with the United Geese of the Pond");
        g.body.position = new Vector2(30,120);
        g._facing = (_geese.size() % 2 == 0) ? FacingDirection.Right : FacingDirection.Left;
        g.body.setGroundedListener(new Runnable() {
            @Override
            public void run() {
                log.inf("Escaping the ground!");
                _effects.add(new Effect((int)g.body.position.x,(int)g.body.position.y,"particle","A",6, Effect.EffectType.STATIONARY));
            }
        });
        _bodies.add(g.body);
      //  _fieldColliders.add(g.body.collider);
        _geese.add(g);
        g.body.collisionObjects = _fieldColliders;

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private PhysicsBody p1(){
        return _geese.get(0).body;
    }
    private PhysicsBody p2(){
        return _geese.get(1).body;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) _snd.setBGM("bgm.lobby");
        if (e.getKeyCode() == KeyEvent.VK_UP && p1().jumps > 0) {
            p1().velocity = new Vector2(p1().velocity.x, -10);
            p1().jumps--;
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP && p1().walljumps > 0 && p1().walled) {
            p1().velocity = new Vector2(p1().velocity.x, -10);
            p1().walljumps--;
        }
        if (e.getKeyCode() == KeyEvent.VK_W && p2().jumps > 0) {
            p2().velocity = new Vector2(p2().velocity.x, -10);
            p2().jumps--;
        }
        else if (e.getKeyCode() == KeyEvent.VK_W && p2().walljumps > 0 && p2().walled) {
            p2().velocity = new Vector2(p2().velocity.x, -10);
            p2().walljumps--;
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
        Collider platform = createPlatform(0, 160, 500, 40);
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
        _effects = new ArrayList<>();
        _fieldColliders = new ArrayList<>();
        initField();

        log.inf("Starting GFX thread");
        _gfx = new GfxMgr(this);
        _gfx.addInputHandler(this);
        _gfxThread = new Thread(_gfx);
        _gfxThread.start();
        _net = new NetMgr(true);
        _snd = new SndMgr();
        _snd.setBGM("bgm.fight");

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

    synchronized void updateRender(){
     //   log.inf("UPDR");
        ArrayList<RenderObj> rq = new ArrayList<>();
        rq.add(new RenderObj(Vector2.zero,"maps","playplace",false,0));
        //players!
        for (Goose g : _geese){
            String sprite = "Idle";
            int frames = 25;

            if (currentKeys.contains(KeyEvent.VK_LEFT) && _geese.indexOf(g)%2==0 || currentKeys.contains(KeyEvent.VK_A) && _geese.indexOf(g)%2==1){
                frames = 8;
                sprite = (g._facing == FacingDirection.Right) ? "Walkback" : "Running";
            }
            if (currentKeys.contains(KeyEvent.VK_RIGHT) && _geese.indexOf(g)%2==0 || currentKeys.contains(KeyEvent.VK_D) && _geese.indexOf(g)%2==1){
                frames = 8;
                sprite = (g._facing == FacingDirection.Left) ? "Walkback" : "Running";
            }
            if (currentKeys.contains(KeyEvent.VK_DOWN)){
                frames = 6;
                sprite = "Crouch";
            }
            if (!g.body.grounded){
           //     rq.add(new RenderObj(g.body.position,"$Text","AIR",false,0,false,false));
            }
            //rq.add(new RenderObj(
            //        g.body.position,
            //        "Jab","Jab",true,11,true,g._facing == FacingDirection.Left));

            rq.add(new RenderObj(
                    g.body.position,
                    "Goose",sprite,true,frames,true,g._facing == FacingDirection.Left));
        }
        for (Effect e: _effects){
            rq.add(e.ro());
            e.tick();
        }
        _effects.removeIf(Effect::over);
        rq.add(new RenderObj(new Vector2(0,0),"ui","$HUD",false,0,false,false));
        rq.add(new RenderObj(new Vector2(0,0),"ui","bar_left",false,0,false,false));
        rq.add(new RenderObj(new Vector2(320-145,0),"ui","bar_right",false,0,false,false));
      //  rq.add(new RenderObj(new Vector2(40,40),"Jab","Jab",true,11,false,false));
      //  rq.add(new RenderObj(new Vector2(80,40),"Jab","Jab",true,11,false,true));


        _gfx.submitRenderQueue(rq);
    }

    synchronized void tick(){
       // log.inf("TICK");
        if (ENABLE_DBG) {
            dbgPane.selectAll();
            dbgPane.replaceSelection("");
        }
        for (PhysicsBody b : _bodies){
            if (b.collisionObjects.size() == 0){
                throw new IllegalStateException();
            }
            b.update();
        }
        for (Goose g : _geese){
            if (ENABLE_DBG) dbgPane.append(g.body.toString()+"\n");
            if (g.body.groundedLastFrame != g.body.grounded && g.body.grounded) _effects.add(new Effect((int)g.body.position.x,(int)g.body.position.y+30,"particle","A",3, Effect.EffectType.STATIONARY));
        }
        if (currentKeys.contains(KeyEvent.VK_LEFT) && !p1().walled){
         //   _geese.get(0)._facing = FacingDirection.Left;
            p1().velocity = new Vector2(-6, p1().velocity.y);
        }
        if (currentKeys.contains(KeyEvent.VK_RIGHT) && !p1().walled){
          //  _geese.get(0)._facing = FacingDirection.Right;
            p1().velocity = new Vector2(6, p1().velocity.y);
        }

        if (currentKeys.contains(KeyEvent.VK_A) && !p2().walled){
         //   _geese.get(1)._facing = FacingDirection.Left;
            p2().velocity = new Vector2(-6, p2().velocity.y);
        }
        if (currentKeys.contains(KeyEvent.VK_D) && !p2().walled){
          //  _geese.get(1)._facing = FacingDirection.Right;
            p2().velocity = new Vector2(6, p2().velocity.y);
        }



        updateRender();
    }
}
