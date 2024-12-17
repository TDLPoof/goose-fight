package net.parkwayschools;

import net.parkwayschools.gfx.GfxMgr;
import net.parkwayschools.util.Log;

public class Main {
    static Log log = new Log("main");
    static GfxMgr _gfx = new GfxMgr();
    static Thread _gfxThread;

    public static void main(String[] args) {
        log.inf("Starting GFX thread");
        _gfxThread = new Thread(_gfx);
        _gfxThread.start();

        while (true){
            //hold the main thread open
        }
    }
}