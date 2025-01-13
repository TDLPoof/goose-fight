package net.parkwayschools;


import net.parkwayschools.core.GameMgr;
import net.parkwayschools.gfx.GfxMgr;
import net.parkwayschools.util.Log;
import javax.swing.*;

import java.awt.*;

public class Main {
    static Log log = new Log("main");

    public static void main(String[] args) {
if(false){
        /*log.inf("Starting GFX thread");
        _gfxThread = new Thread(_gfx);
        _gfxThread.start();*/
        JFrame myFrame = new JFrame();
        myFrame.setTitle("Goose");
        myFrame.setSize(800, 600);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        Goose g1 = new Goose();
//        AttackManager g1m = new AttackManager(g1);
//        g1m.start();
//
//
//        myFrame.addKeyListener(g1m);
//        myFrame.setVisible(true);

        //for (;;) { g1m.run(); }
} else{
        log.inf("Starting GameMgr");
        GameMgr mg = new GameMgr();
        mg.addGoose();
        mg.addGoose();
}

    }
}