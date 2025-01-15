package net.parkwayschools.core;

import net.parkwayschools.gfx.Effect;
import net.parkwayschools.gfx.GfxMgr;
import net.parkwayschools.gfx.HMZone;
import net.parkwayschools.gfx.RenderObj;
import net.parkwayschools.net.NetMgr;
import net.parkwayschools.phys.Collider;
import net.parkwayschools.phys.PhysicsBody;
import net.parkwayschools.phys.Vector2;
import net.parkwayschools.snd.SndMgr;
import net.parkwayschools.util.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

public class GameMgr implements KeyListener {
    static final boolean ENABLE_DBG = true;
    static Log log = new Log("core/gamemgr");

    enum GameState {
        TITLE_SCREEN,
        GAMEPLAY,
        RESULTS_SCREEN;

        boolean physOn() {
            return this == GAMEPLAY;
        }

        boolean scnRenderOn() {
            return this != TITLE_SCREEN;
        }
    }

    public static ArrayList<Goose> geese;
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
    int[] fieldHeightmap;

    record point(int x, int y) {
    }

    point nextToVisit(boolean[][] arr) {
        for (int x = 0; x < arr.length; x++) {
            for (int y = 0; y < arr[x].length; y++) {
                if (!arr[x][y]) {
                    return new point(x, y);
                }
            }
        }
        return new point(-1, -1);
    }

    int[][] visitMarked;

    ArrayList<Collider> getRectsFromImg(BufferedImage b) {


        ArrayList<RenderObj> r = new ArrayList<>();
        r.add(new RenderObj(Vector2.zero, "$Text", "colmap build", false, 0));
        _gfx.submitRenderQueue(r);
        ArrayList<Collider> res = new ArrayList<>();

        boolean[][] visited = new boolean[b.getWidth()][b.getHeight()];
        point start = new point(0, 0);
        while (start.x != -1) {
            //floodfill-style algorithm. we go in x then in y
            int rectColor = b.getRGB(start.x, start.y);

            int x = start.x;
            while (x < b.getWidth() && b.getRGB(x, start.y) == rectColor) {
                ;
                visited[x][start.y] = true;
                x++;
            }
            x--; //back up one
            int y = start.y;
            while (y < b.getHeight() && b.getRGB(x, y) == rectColor) {
                boolean keepGoing = true;
                for (int ix = start.x; ix < x; ix++) {
                    if (b.getRGB(ix, y) != rectColor) {
                        keepGoing = false;
                        break;
                    }
                    visited[ix][y] = true;
                }
                // log.dbg("Extending in y");
                if (keepGoing) y++;
                else break;
            }
            y--; //back up one
            if (rectColor == b.getRGB(0, 0) && x - start.x > 0 && y - start.y > 0)
                res.add(new Collider(start.x, start.y, x - start.x, y - start.y));
            start = nextToVisit(visited);
        }
        for (Collider c : res) {
            log.inf(c.toString());
        }
        log.inf(String.format("Built %d colision rects", res.size()));
        return res;
    }

    ArrayList<HMZone> _zones = new ArrayList<>();
    void buildHeightmap() {
        log.inf("Building heightmap");
        int runningLevel = -1;
        int startX = 0;
        for (int i = 0; i < _gfx.bufferX; i++) {
            int level = _gfx.bufferY;
            for (Collider c : _fieldColliders) {
                if (c.position.x <= i && i <= c.position.x + c.size.x) {
                    if (c.position.y < level) level = (int) c.position.y;
                }
            }
            fieldHeightmap[i] = level;
            if (level != runningLevel){
                _zones.add(new HMZone(startX,i-1,runningLevel));
                runningLevel = level;
                startX = i;
            }
        }
        _gfx._zones = _zones;
        _gfx.submitHeightmap(fieldHeightmap);
    }

    int shadowPrecession = -2;
    int shadowPostcession = -2;

    public class wtfIsHappening extends JPanel {
        public static Collider HELPMEPLEASE = new Collider(0,0,0,0,"");
        int[] _dbgHx = new int[320];

        public wtfIsHappening() {
            for (int i = 0; i < 320; i++) _dbgHx[i] = i;
        }

        @Override
        protected void paintComponent(Graphics g1) {
            super.paintComponent(g1);
            Graphics2D g = (Graphics2D) g1;
            g.scale(2, 2);
            g.clearRect(0, 0, getWidth(), getHeight());
            //if (_gfx != null) g.drawImage(_gfx.getSprite("maps","playplace.col"),0,0,null);
            g.setColor(Color.BLUE);
            for (Collider c : _fieldColliders)
                g.drawRect((int) c.position.x, (int) c.position.y, (int) c.size.x, (int) c.size.y);
            g.setColor(Color.GREEN);
            for (PhysicsBody b : _bodies) {
                Collider c = b.collider;
                g.drawRect((int) c.position.x, (int) c.position.y, (int) c.size.x, (int) c.size.y);
                g.drawLine((int) c.position.x, (int) c.position.y+(int)c.size.y/2,(int) c.position.x+(int)(c.size.x/2)-30, (int) (int) c.position.y+(int)c.size.y/2);
                g.drawLine((int) c.position.x, (int) c.position.y+(int)c.size.y/2,(int) c.position.x+(int)(c.size.x/2)+30,(int) c.position.y+(int)c.size.y/2);
            }
            g.setColor(Color.RED);
            g.drawRect((int) HELPMEPLEASE.position.x, (int) HELPMEPLEASE.position.y, (int) HELPMEPLEASE.size.x, (int) HELPMEPLEASE.size.y);
            g.setFont(new Font("Arial", Font.PLAIN, 5));
            g.setColor(Color.BLACK);

            Point m = this.getMousePosition();
            if (m == null) m = new Point(0,0);
            g.setColor(Color.YELLOW);
            g.drawOval(m.x,m.y,5,5);
           // if (fieldHeightmap != null) g.drawPolyline(_dbgHx, fieldHeightmap, 320);
            if (_zones != null){
                /*
                for (int i = 8; i<320; i+=4) {
                    double sl = i;
                    Line2D ray = new Line2D.Float(i-8, m.y, i, 170);
                    for (HMZone z : _zones) {
                        Rectangle zR = new Rectangle(z.sx,z.height,z.ex-z.sx,10);
                        boolean works = false;
                        if (ray.intersects(zR) && z.height < 140) {
                            g.setColor(Color.BLUE);
                            works = true;
                        }
                        else
                            g.setColor(Color.ORANGE);
                        g.drawLine(i-8, m.y, i, 170);
                        if (works) break;
                    }
                }
                */


            }

            repaint();
        }
    }

    JCheckBox dbgR_BG = new JCheckBox("Render BG");
    JCheckBox dbgR_Player = new JCheckBox("Render Players");
    JCheckBox dbgR_FX = new JCheckBox("Render FX");
    JCheckBox dbgR_HUD = new JCheckBox("Render HUD");
    JCheckBox dbgR_GShd = new JCheckBox("Render Global Shadow");
    JCheckBox[] renderPasses = new JCheckBox[]{dbgR_BG, dbgR_FX, dbgR_HUD, dbgR_Player, dbgR_GShd};

    public GameMgr() {
        if (ENABLE_DBG) {
            dbgInspector = new JFrame("Render Passes");
            dbgInspector.setVisible(true);
            dbgInspector.setLayout(new FlowLayout());
            dbgInspector.setSize(600, 300);
            for (JCheckBox c : renderPasses) {
                c.setSelected(true);
                dbgInspector.add(c);
            }
            JButton allSpr = new JButton("ALL SPRITE");
            allSpr.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(String sheet : _gfx._sheets.keySet()){
                        JFrame f = new JFrame(sheet);
                        f.setSize(400,400);
                        f.setLayout(new FlowLayout());
                        String[][] n = _gfx._sheets.get(sheet).meta.nameMap();
                        for (String[] s : n){
                            for (String sn : s){
                                JPanel p = new JPanel();
                                p.add(new JLabel(sn));
                                p.add(new JLabel(new ImageIcon(_gfx.getSprite(sheet,sn))));
                                f.add(p);
                            }
                        }
                        f.setVisible(true);
                        f.pack();
                    }
                }
            });
            dbgInspector.add(allSpr);
            dbgInspector.pack();
        }
        if (ENABLE_DBG) {
            JFrame pInspector = new JFrame("Physics View");
            pInspector.setVisible(true);
            pInspector.setSize(320 * 2, 180 * 2);
            pInspector.add(new wtfIsHappening());
        }


        geese = new ArrayList<>();
        _bodies = new ArrayList<>();
        _effects = new ArrayList<>();
        _fieldColliders = new ArrayList<>();


        log.inf("Starting GFX thread");
        _gfx = new GfxMgr(this);
        _gfx.addInputHandler(this);

        fieldHeightmap = new int[_gfx.bufferX];

        _gfxThread = new Thread(_gfx);
        _gfxThread.start();
        _net = new NetMgr(true);
        _snd = new SndMgr();
        initField();
        buildHeightmap();

        //_snd.setBGM("bgm.fight");

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

    synchronized void registerGoose(Goose g) {
        log.inf("Registering a new goose with the United Geese of the Pond");
        g.body.position = new Vector2(30, 120);
        g._facing = (geese.size() % 2 == 0) ? FacingDirection.Right : FacingDirection.Left;
//        g.body.setGroundedListener(new Runnable() {
//            @Override
//            public void run() {
//                log.inf("Escaping the ground!");
//                _effects.add(new Effect((int)g.body.position.x,(int)g.body.position.y,"particle","A",6, Effect.EffectType.STATIONARY));
//            }
//        });
        _bodies.add(g.body);
        //  _fieldColliders.add(g.body.collider);
        _gfx.addInputHandler(g.manager);
        geese.add(g);
        g.body.collisionObjects = _fieldColliders;

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private PhysicsBody p1() {
        return geese.get(0).body;
    }

    private PhysicsBody p2() {
        return geese.get(1).body;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) _snd.setBGM("bgm.lobby");
        if (e.getKeyCode() == KeyEvent.VK_P) geese.get(0).addInterrupt(Animation.ATK_JAB);
        if (e.getKeyCode() == KeyEvent.VK_UP && p1().jumps > 0) {
            p1().velocity = new Vector2(p1().velocity.x, -10);
            p1().jumps--;
            if (p1().jumps == 1) geese.get(0).addInterrupt(Animation.JUMP);
            if (p1().jumps == 0) geese.get(0).addInterrupt(Animation.DOUBLE_JUMP);
        } else if (e.getKeyCode() == KeyEvent.VK_UP && p1().walljumps > 0 && p1().walled) {
            p1().velocity = new Vector2(p1().velocity.x, -10);
            p1().walljumps--;
        }
        if (e.getKeyCode() == KeyEvent.VK_W && p2().jumps > 0) {
            p2().velocity = new Vector2(p2().velocity.x, -10);
            p2().jumps--;
            if (p2().jumps == 1) geese.get(1).addInterrupt(Animation.JUMP);
            if (p2().jumps == 0) geese.get(1).addInterrupt(Animation.DOUBLE_JUMP);
        } else if (e.getKeyCode() == KeyEvent.VK_W && p2().walljumps > 0 && p2().walled) {
            p2().velocity = new Vector2(p2().velocity.x, -10);
            p2().walljumps--;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN && !p1().crouching) {
            log.inf("Crouch");
            p1().crouch();
            geese.get(0).addInterrupt(Animation.CROUCH);
        }
        if (e.getKeyCode() == KeyEvent.VK_S && !p2().crouching) {
            p2().crouch();
            geese.get(1).addInterrupt(Animation.CROUCH);
        }

        if (!currentKeys.contains(e.getKeyCode())) currentKeys.add(e.getKeyCode());

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            p1().uncrouch();
            geese.get(0).addInterrupt(Animation.UNCROUCH);
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            p2().uncrouch();
            geese.get(1).addInterrupt(Animation.UNCROUCH);
        }
        currentKeys.remove(Integer.valueOf(e.getKeyCode()));
    }


    public void addGoose() {
        Goose greg = new Goose(this);
    }

    private static Collider createPlatform(double x, double y, double width, double height) {
        return new Collider(x, y, width, height);
    }

    void initField() {
        this._fieldColliders = getRectsFromImg(_gfx.getSprite("maps", "playplace.col"));

//        Collider platform = createPlatform(0, 160, 500, 40);
//        Collider wLeft = createPlatform(0, 0, 10, 160);
//        Collider wRight = createPlatform(310, 0, 10, 160);
//        //Collider testPlatform = createPlatform(40,120,120,20);
//        //this._fieldColliders.add(testPlatform);
//        this._fieldColliders.add(platform);
//        this._fieldColliders.add(wLeft);
//        this._fieldColliders.add(wRight);
    }

    synchronized void updateRender() {
        //   log.inf("UPDR");
        ArrayList<RenderObj> rq = new ArrayList<>();
        if (!ENABLE_DBG || dbgR_BG.isSelected()) rq.add(new RenderObj(Vector2.zero, "maps", "playplace", false, 0));
        //players!
        if (!ENABLE_DBG || dbgR_Player.isSelected()) for (Goose g : geese) {
            g.currentLoopingAnim = (g.body.grounded) ? Animation.IDLE : Animation.AIRIDLE;
            if (g.body.crouching) g.currentLoopingAnim = Animation.CROUCHIDLE;
            else {

                if (currentKeys.contains(KeyEvent.VK_LEFT) && geese.indexOf(g) % 2 == 0 || currentKeys.contains(KeyEvent.VK_A) && geese.indexOf(g) % 2 == 1) {

                    if (g._facing == FacingDirection.Right) g.currentLoopingAnim = Animation.BACKRUN;
                    else g.currentLoopingAnim = Animation.RUN;
                }
                if (currentKeys.contains(KeyEvent.VK_RIGHT) && geese.indexOf(g) % 2 == 0 || currentKeys.contains(KeyEvent.VK_D) && geese.indexOf(g) % 2 == 1) {
                    if (g._facing == FacingDirection.Left) g.currentLoopingAnim = Animation.BACKRUN;
                    else g.currentLoopingAnim = Animation.RUN;
                }
            }

            if (!g.body.grounded) {
                //     rq.add(new RenderObj(g.body.position,"$Text","AIR",false,0,false,false));
            }
            //rq.add(new RenderObj(
            //        g.body.position,
            //        "Jab","Jab",true,11,true,g._facing == FacingDirection.Left));

            rq.add(g.getRO());
        }
        if (!ENABLE_DBG || dbgR_FX.isSelected()) for (Effect e : _effects) {
            rq.add(e.ro());
            e.tick();
        }
        rq.add(new RenderObj(Vector2.zero,"maps","playplace.pfm",false,0,false,false));
        _effects.removeIf(Effect::over);
        if (!ENABLE_DBG || dbgR_GShd.isSelected()) rq.add(new RenderObj(new Vector2(shadowPostcession,shadowPrecession), "$Global", "shadow2", false, 0));
        //     rq.add(new RenderObj(Vector2.zero,"maps","playplace.pfm",false,0,true,false,false));

        if (!ENABLE_DBG || dbgR_HUD.isSelected()) {
            rq.add(new RenderObj(new Vector2(0, 0), "ui", "$HUD", false, 0, false, false));
            rq.add(new RenderObj(new Vector2(0, 0), "ui", "bar_left", false, 0, false, false));
            rq.add(new RenderObj(new Vector2(0, 0), "ui_chars", "bar_head_goose", false, 0, false, false));
            rq.add(new RenderObj(new Vector2(320 - 145, 0), "ui", "bar_right", false, 0, false, false));
            rq.add(new RenderObj(new Vector2(320 - 18, 0), "ui_chars", "bar_head_goose", false, 0, false, true));
        }
        //  rq.add(new RenderObj(new Vector2(40,40),"Jab","Jab",true,11,false,false));
        //  rq.add(new RenderObj(new Vector2(80,40),"Jab","Jab",true,11,false,true));
        //  rq.add(new RenderObj(new Vector2(40,120),"test-platform","test-platform",false,0,false,false));


        _gfx.submitRenderQueue(rq);
    }

    synchronized void tick() {
        // log.inf("TICK");
//        if (ENABLE_DBG) {
//            dbgPane.selectAll();
//            dbgPane.replaceSelection("");
//        }
        for (PhysicsBody b : _bodies) {
            if (b.collisionObjects.size() == 0) {
                throw new IllegalStateException();
            }
            b.update();
        }
        for (Goose g : geese) {
            //  if (ENABLE_DBG) dbgPane.append(g.body.toString()+"\n");
            if (g.body.groundedLastFrame != g.body.grounded && g.body.grounded)
                _effects.add(new Effect((int) g.body.position.x, (int) g.body.position.y + 30, "particle", "A", 3, Effect.EffectType.STATIONARY));
        }
        if (currentKeys.contains(KeyEvent.VK_LEFT) && !p1().walled && !p1().crouching) {
            geese.get(0)._facing = FacingDirection.Left;
            p1().velocity = new Vector2(-6, p1().velocity.y);
        }
        if (currentKeys.contains(KeyEvent.VK_RIGHT) && !p1().walled && !p1().crouching) {
            geese.get(0)._facing = FacingDirection.Right;
            p1().velocity = new Vector2(6, p1().velocity.y);
        }

        if (currentKeys.contains(KeyEvent.VK_A) && !p2().walled && !p2().crouching) {
            geese.get(1)._facing = FacingDirection.Left;
            p2().velocity = new Vector2(-6, p2().velocity.y);
        }
        if (currentKeys.contains(KeyEvent.VK_D) && !p2().walled && !p2().crouching) {
            geese.get(1)._facing = FacingDirection.Right;
            p2().velocity = new Vector2(6, p2().velocity.y);
        }


        updateRender();
    }
}
