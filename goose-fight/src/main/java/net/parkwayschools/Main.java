package net.parkwayschools;

import net.parkwayschools.core.AttackManager;
import net.parkwayschools.core.Goose;
import net.parkwayschools.gfx.GfxManager;
import net.parkwayschools.util.Log;
import javax.swing.*;

import java.awt.*;

public class Main {
    static Log log = new Log("main");
    static GfxManager _gfx = new GfxManager();
    static Thread _gfxThread;

    public static void main(String[] args) {
        /*log.inf("Starting GFX thread");
        _gfxThread = new Thread(_gfx);
        _gfxThread.start();*/
        JFrame myFrame = new JFrame();
        myFrame.setTitle("Goose");
        myFrame.setSize(800, 600);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Goose g1 = new Goose();
        AttackManager g1m = new AttackManager(g1);
        g1m.start();


        myFrame.addKeyListener(g1m);
        myFrame.setVisible(true);

        //for (;;) { g1m.run(); }






    }
}