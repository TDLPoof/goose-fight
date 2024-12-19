package net.parkwayschools;

import net.parkwayschools.core.GameMgr;
import net.parkwayschools.gfx.GfxMgr;
import net.parkwayschools.util.Log;

public class Main {
    static Log log = new Log("main");

    public static void main(String[] args) {
        log.inf("Starting GameMgr");
        GameMgr mg = new GameMgr();
        mg.addGoose();

        while (true){
            //hold the main thread open
        }
    }
}