package net.parkwayschools.core;

import net.parkwayschools.gfx.GfxMgr;
import net.parkwayschools.phys.Collider;
import net.parkwayschools.util.Log;

import java.util.ArrayList;

public class GameMgr {
    static Log log = new Log("core/gamemgr");
    GfxMgr _gfxman;
    ArrayList<Goose> _geese;
    ArrayList<Collider> _colliders;

    static GfxMgr _gfx = new GfxMgr();
    static Thread _gfxThread;

    public GameMgr(){
        log.inf("Starting GFX thread");
        _gfxThread = new Thread(_gfx);
        _gfxThread.start();
    }

    void runGame(){

    }
}
