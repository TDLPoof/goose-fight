package net.parkwayschools.gfx;

import net.parkwayschools.core.GameMgr;

import java.awt.*;

public class HudRenderer {
    GameMgr _gm; //for the necessary core state, and goose access

    public HudRenderer(GameMgr mg){
        _gm = mg;
    }
    final Color RED_HP = new Color(190,50,61);
    final Color BLUE_HP = new Color(54,65,202);
    final Color BAR_BACKING = new Color(100,100,100);

    public void drawHUD(Graphics2D g){
        //red HP
        g.setColor(BAR_BACKING);
        g.fillRect(18,0,102,24);
        g.setColor(RED_HP);
        g.fillRect(18,0,_gm.geese.get(0).health,24);

        //blue HP
        g.setColor(BAR_BACKING);
        g.fillRect(320-18-102,0,102,24);
        g.setColor(BLUE_HP);
        g.fillRect(320-18-_gm.geese.get(1).health,0,_gm.geese.get(1).health,24);
    }
}
