package net.parkwayschools.gfx;

import net.parkwayschools.phys.Vector2;
import net.parkwayschools.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*
* Implements the graphics and render thread
* */
public class GfxMgr implements Runnable{
    final int bufferX = 320, bufferY = 160;
    final double shadowSmearFactor = 0.5;
    static Log log = new Log("core/gfxmgr");
    JFrame _frame;
    RenderPanel _rp;
    ArrayList<RenderObj> _currentRQ;
    HashMap<String,Spritesheet> _sheets;
    BufferedImage _framebuffer;
  //  Runnable subordinateThread;

    public GfxMgr(){
        _frame = new JFrame("Cool Goose Fighting Game:tm:");
       // subordinateThread = r;
        _sheets = new HashMap<>();
        _currentRQ = new ArrayList<>();
        _currentRQ.add(new RenderObj(Vector2.zero,"maps","playplace",false,0));
        _currentRQ.add(new RenderObj(new Vector2(20,20),"Goose-idle","rIdle",true,4));
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
        _currentRQ = ros;
    }

    int frameCount = 0;

    public void addInputHandler(KeyListener k){
        this._frame.addKeyListener(k);
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        BufferedImage res = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = res.createGraphics();
        g.drawImage(img, 0, 0, null);
        return res;
    }

    @Override
    public void run() {
        _rp = new RenderPanel();
        _frame.setSize(1920/2,1080/2);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.add(_rp);
        _frame.setVisible(true);
        while (true){
            int y = 10;
            frameCount++;
            if (frameCount > 30) frameCount = 0;
            //=== Handle the internal buffer
            Graphics2D g = (Graphics2D)_framebuffer.getGraphics();
            g.clearRect(0,0,bufferX,bufferY);
            for (RenderObj o : _currentRQ){
             //   _framebuffer.getGraphics().drawString(o.toString(),10,y);
                y+=20;
                Spritesheet sp = _sheets.get(o.sheetID());
                BufferedImage spr = null;
                if (o.isAnim()){
                   int frameDuration = 30/o.animFrames();
                   int frame = ((Math.min(29,frameCount)/frameDuration+1)%o.animFrames())+1;
                    assert sp != null;
                    spr = sp.spr(o.spriteID()+frame);
                } else {
                    assert sp != null;
                    spr = sp.spr(o.spriteID());
                }

                if (o.renderShadow()){

                    //darken
                    ImageFilter filter = new GrayFilter(false, 0);
                    ImageProducer producer = new FilteredImageSource(spr.getSource(), filter);
                    BufferedImage shPre = toBufferedImage(Toolkit.getDefaultToolkit().createImage(producer));
                    //soften
                    BufferedImage sh = new BufferedImage(spr.getWidth(),spr.getHeight(),BufferedImage.TYPE_INT_ARGB);
                    float[] matrix = {
                            0.111f, 0.111f, //0.111f,
                            0.111f, 0.111f, //0.111f,
              //              0.111f, 0.111f, 0.111f,
                    };
                    BufferedImageOp blurOp = new ConvolveOp( new Kernel(2, 2, matrix) );
                    blurOp.filter(shPre, sh);
                    //squish
                    AffineTransform at = new AffineTransform();
                    at.scale((1/shadowSmearFactor)*0.5,shadowSmearFactor*0.5);
                    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage fina = new BufferedImage((int)(spr.getWidth()*(1/shadowSmearFactor)*0.5),(int)(spr.getHeight()*shadowSmearFactor*0.5),BufferedImage.TYPE_INT_ARGB);
                    scaleOp.filter(sh,fina);
                    g.drawImage(fina,(int)o.pos().x-4,(int)o.pos().y+spr.getHeight()-12,null);
                    g.drawImage(spr, (int) o.pos().x, (int) o.pos().y, null);
                } else {
                    g.drawImage(spr, (int) o.pos().x, (int) o.pos().y, null);
                }
            }
            //=== Handoff
            _rp.internalBuffer = _framebuffer;
            _rp.repaint();
          //  subordinateThread.run();
            try {
                Thread.sleep(1000/30); //lock at 30FPS rendering, todo: adapt to how long the actual frame pass takes
            } catch (Exception e){

            }
           //hold open!
        }
    }
}
