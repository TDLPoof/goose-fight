package net.parkwayschools.gfx;

import net.parkwayschools.phys.Vector2;
import net.parkwayschools.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*
* Implements the graphics and render thread
* */
public class GfxMgr implements Runnable{
    final int bufferX = 320, bufferY = 160;
    static Log log = new Log("core/gfxmgr");
    JFrame _frame;
    RenderPanel _rp;
    ArrayList<RenderObj> _currentRQ;
    HashMap<String,Spritesheet> _sheets;
    BufferedImage _framebuffer;

    public GfxMgr(){
        _sheets = new HashMap<>();
        _currentRQ = new ArrayList<>();
        _currentRQ.add(new RenderObj(new Vector2(20,20),"sasha","2",true,3));
        _framebuffer =  new BufferedImage(bufferX,bufferY,BufferedImage.TYPE_INT_ARGB);
        initSpritesheets();
    }

    void initSpritesheets(){
        File idxf = new File("data/sprite/.idx");
        try {
            Scanner idx = new Scanner(idxf);
            while (idx.hasNext()){
                String id = idx.nextLine().replace("\n","");
                log.inf("Loading "+id);
                _sheets.put(id,new Spritesheet(id));
            }

        } catch (Exception e){
            log.err("Failed to init spritesheets");
            e.printStackTrace();
        }
    }

    public void submitRenderQueue(ArrayList<RenderObj> ros){

    }

    int frameCount = 0;

    @Override
    public void run() {
        _rp = new RenderPanel();
        _frame = new JFrame("Cool Goose Fighting Game:tm:");
        _frame.setSize(1920/2,1080/2);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.add(_rp);
        _frame.setVisible(true);
        int x = 0, y = 0;
        while (true){
            frameCount++;
            if (frameCount > 30) frameCount = 0;
            //=== Handle the internal buffer
            Graphics2D g = (Graphics2D)_framebuffer.getGraphics();
            g.clearRect(0,0,bufferX,bufferY);
            for (RenderObj o : _currentRQ){
                Spritesheet sp = _sheets.get(o.sheetID());
                if (o.isAnim()){
                   int frameDuration = 30/o.animFrames();
                   int frame = Math.min(29,frameCount)/frameDuration+1;
                   g.drawImage(sp.spr(o.spriteID()+frame), (int) o.pos().x, (int) o.pos().y, null);
                   g.drawString(String.format("%d/%d",frame,o.animFrames()),(int)o.pos().x,(int)o.pos().y);
                } else {
                    g.drawImage(sp.spr(o.spriteID()), (int) o.pos().x, (int) o.pos().y, null);
                }
            }
            //=== Handoff
            _rp.internalBuffer = _framebuffer;
            _rp.repaint();
            try {
                Thread.sleep(1000/30); //lock at 30FPS rendering, todo: adapt to how long the actual frame pass takes
            } catch (Exception e){

            }
           //hold open!
        }
    }
}
