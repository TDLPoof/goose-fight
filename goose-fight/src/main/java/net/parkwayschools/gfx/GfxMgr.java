package net.parkwayschools.gfx;

import net.parkwayschools.core.GameMgr;
import net.parkwayschools.phys.Vector2;
import net.parkwayschools.util.Log;

import javax.swing.*;
import java.awt.*;
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
    public final int bufferX = 320, bufferY = 180;
    public final int groundLine = 128;
    public final int shadowLine = 160;
    final int animFrameRate = 12;
    final int systemFrameRate = 30;
    final int aFramesPerSFrame = (int)(systemFrameRate/animFrameRate);
    final double shadowSmearFactor = 0.5;
    static Log log = new Log("core/gfxmgr");
    JFrame _frame;
    RenderPanel _rp;
    ArrayList<RenderObj> _currentRQ;
    public HashMap<String,Spritesheet> _sheets;
    BufferedImage _framebuffer;
    HudRenderer _hudR;
    int[] _heights;
    public ArrayList<HMZone> _zones;
    int[] _dbgHx;

    public GfxMgr(GameMgr m){
        _frame = new JFrame("Cool Goose Fighting Game:tm:");

        _sheets = new HashMap<>();
        _currentRQ = new ArrayList<>();
        _currentRQ.add(new RenderObj(new Vector2(0,0),"title","goosefight",false,0));
        // rq.add(new RenderObj(new Vector2(110,155),"$Text","Loading game...",false,0));
        _currentRQ.add(new RenderObj(new Vector2(80,140),"$Text","Loading Game...",false,0));
        _framebuffer =  new BufferedImage(bufferX,bufferY,BufferedImage.TYPE_INT_ARGB);
        _hudR = new HudRenderer(m);
        initSpritesheets();
        _heights = new int[bufferX];
        _dbgHx = new int[bufferX];
        for (int i = 0; i<bufferX; i++) _dbgHx[i] = i;
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
    public void submitHeightmap(int[] map){
        _heights = map;
    }
    public BufferedImage getSprite(String sheet, String id){
        return _sheets.get(sheet).spr(id);
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

    int[] bobOffsets = new int[]{-6,-6,-6,-5,-5,-5,-4,-4,-4,-3,-3,-3,-2,-2,-2,-1,-1,-1,0,0,0,-1,-1,-1,-2,-2,-2,-3,-3,-3,-4,-4,-4,-5,-5,-5};

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
            if (frameCount > systemFrameRate*5) frameCount = 0;
            //=== Handle the internal buffer
            Graphics2D g = (Graphics2D)_framebuffer.getGraphics();
            g.clearRect(0,0,bufferX,bufferY);
            for (RenderObj o : _currentRQ){
                if (o.sheetID().equals("$Global")){
                    if (o.spriteID().equals("shadow2")){
                        for (HMZone z : _zones){
                            g.setColor(Color.RED);
                            GradientPaint shadowGrad = new GradientPaint(0,0,new Color(100,0,100,10),0,100,new Color(100,0,100,20));
                            if (o.renderShadow()) g.fillRect(z.sx(),z.height(),z.ex()-z.sx(),2);
                            g.setPaint(shadowGrad);
                            if (z.height() < 140)
                                g.fillPolygon(
                                        new int[]{z.sx(),z.ex(), (int) (z.ex()+o.pos().x), (int) (z.sx()-o.pos().y)},
                                        new int[]{z.height(),z.height(),170,170},
                                        4
                                );
                        }
                        continue;
                    }
                    if (o.spriteID().equals("shadow")){
                        GradientPaint shadowGrad = new GradientPaint(0,0,new Color(0,0,0,10),0,100,new Color(0,0,0,20));
                        if (true) {
                            g.setPaint(shadowGrad);
                            for (int i = 13; i < bufferX-13; i++) {
                                if (_heights[i] < groundLine) g.fillRect(i, _heights[i], 1, shadowLine - _heights[i]);
                            }
                        }
                        continue;
                    }
                }
                if (o.sheetID().equals("$Text")){
                    if (o.renderShadow() || o.flipHorizontal()) log.err("Effects not supported for text!");
                    g.drawString(o.spriteID(),(int)o.pos().x,(int)o.pos().y);
                    continue;
                }
                if (o.spriteID().equals("$HUD")){
                    if (o.renderShadow() || o.flipHorizontal()) log.err("Effects not supported for HudRenderer!");
                    _hudR.drawHUD(g);
                    continue;
                }

                y+=20;
                Spritesheet sp = _sheets.get(o.sheetID());
                BufferedImage spr = null;
                if (sp == null){
                    log.err("Spritesheet "+o.sheetID()+" doesn't exist. Replacing with nil sprite");
                    spr = _sheets.get("err").spr("err");
                    sp = _sheets.get("err");
                } else {

                    if (o.isAnim()) {
                        int animFrame = frameCount / aFramesPerSFrame;
                        int frame = (animFrame % o.animFrames()) + 1;
                        spr = sp.spr(o.spriteID() + frame);
                        if (spr == null)
                            spr = _sheets.get("err").spr("err");
                    } else {
                        spr = sp.spr(o.spriteID());
                        if (spr == null) spr = _sheets.get("err").spr("err");
                    }
                }
                switch (o.ma()){
                    case FLASH_WHITE -> {
                        ImageFilter filter = new GrayFilter(true, 100-(int)((frameCount/aFramesPerSFrame)%10));
                        ImageProducer producer = new FilteredImageSource(spr.getSource(), filter);
                        BufferedImage br = toBufferedImage(Toolkit.getDefaultToolkit().createImage(producer));
                        spr = br;
                    }
                    case NONE -> {}
                }


                if (o.renderShadow()){
                    int distanceFromFloor =groundLine- (int)o.pos().y;
                    distanceFromFloor -= 1; //stupid offset nonsense
                    //darken
                    ImageFilter filter = new GrayFilter(false, 100-(int)(distanceFromFloor*3.7));
                    ImageProducer producer = new FilteredImageSource(spr.getSource(), filter);
                    BufferedImage shPre = toBufferedImage(Toolkit.getDefaultToolkit().createImage(producer));
                    //soften
                    BufferedImage sh = new BufferedImage(spr.getWidth(),spr.getHeight(),BufferedImage.TYPE_INT_ARGB);
                    float[] matrix = {
                            0.111f, 0.111f,
                            0.111f, 0.111f,
                    };
                    BufferedImageOp blurOp = new ConvolveOp( new Kernel(2, 2, matrix) );
                    blurOp.filter(shPre, sh);
                    //squish
                    AffineTransform at = new AffineTransform();
                    if (!o.shadowRespectsGroundplane()) distanceFromFloor = 10;
                    double atSX = (1/shadowSmearFactor)*(0.5 - ((distanceFromFloor/10)*0.05));
                    double atSY = shadowSmearFactor*(0.5 - ((distanceFromFloor/10)*0.02));
                    if (atSX == 0) atSX = 0.01;
                    if (atSY == 0) atSY = 0.01;
                    at.scale( atSX,atSY);
                    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage fina = new BufferedImage((int)(spr.getWidth()*(1/shadowSmearFactor)*0.5),(int)(spr.getHeight()*shadowSmearFactor*0.5),BufferedImage.TYPE_INT_ARGB);
                    scaleOp.filter(sh,fina);

                    //distance from floor *increases* as the shadow should get smaller. We want to decrease the X component by half the delta in the change
                    int widthDistance = spr.getWidth() - (int)(fina.getWidth()*atSX);
                    widthDistance/=2;
                    widthDistance*=(o.flipHorizontal() ? -1 : 1);

                    int renderX = widthDistance+(int)o.pos().x-4+( o.flipHorizontal() ? sp.meta.xOff() : 0);
                    if (o.shadowRespectsGroundplane()) {
                        for (int x = renderX; x < renderX + fina.getWidth(); x++) {
                            x = Math.max(x, 0);
                            x = Math.min(x, 320);

                            if (_heights[x] > o.pos().y)
                                g.drawImage(fina.getSubimage(o.flipHorizontal() ? fina.getWidth() - (x - renderX) - 1 : x - renderX, 0, 1, fina.getHeight()), x, _heights[x] - 9, null);
                        }
                    }
                    else {
                        g.drawImage(fina,0,0,null);
                    }
                }
                g.drawImage(spr, (int) o.pos().x+(o.flipHorizontal() ? spr.getWidth() : 0)+ ( o.flipHorizontal() ? sp.meta.xOff() : 0)+(o.ma() == MagicAnim.X_BOB ? ((frameCount/aFramesPerSFrame)%12)-6 : 0), (int) o.pos().y+(o.ma() == MagicAnim.Y_BOB ? bobOffsets[(frameCount/aFramesPerSFrame)% bobOffsets.length] : 0), spr.getWidth()*(o.flipHorizontal() ? -1 : 1),spr.getHeight(), null);
            }

            g.setColor(Color.RED);

          //  g.drawPolyline(_dbgHx,_heights,bufferX);
            //== End Debug

            //=== Handoffpul
            _rp.internalBuffer = _framebuffer;
            _rp.repaint();

            try {
                Thread.sleep(1000/systemFrameRate); //lock at 30FPS rendering
            } catch (Exception e){

            }
           //hold open!
        }
    }
}
