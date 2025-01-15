package net.parkwayschools.core.atk;

import net.parkwayschools.core.*;
import net.parkwayschools.phys.Collider;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Jab extends Attack {
    public Jab(int k) {
        super(11, k);
        this.attackAnim = Animation.ATK_JAB;
    }

    @Override
    protected void innerDoAttack() {
        int innocentGoose = GameMgr.geese.indexOf(this.focus);
        System.out.printf("--> %d | %d",innocentGoose,innocentGoose ==  1 ?  0 : 1);
        Goose tGoose = GameMgr.geese.get(innocentGoose);
        Goose enemy = GameMgr.geese.get(innocentGoose == 1 ? 0 : 1);
        if (tGoose == enemy) System.out.println("BAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD");
        Rectangle2D eRect = new Rectangle2D.Double(enemy.body.collider.position.x,enemy.body.collider.position.y,enemy.body.collider.size.x,enemy.body.collider.size.y);
        Line2D atkCastLine = new Line2D.Double(tGoose.body.position.x+tGoose.body.collider.size.x/2,tGoose.body.position.y+tGoose.body.collider.size.y/2,tGoose._facing == FacingDirection.Left ? tGoose.body.position.x+tGoose.body.collider.size.x/2+40 : tGoose.body.position.x+tGoose.body.collider.size.x/2-40,tGoose.body.position.y+tGoose.body.collider.size.y/2);
        if (atkCastLine.intersects(eRect)){
            enemy.health -= 5;
        }
//        GameMgr.wtfIsHappening.HELPMEPLEASE = atkCol;
        super.innerDoAttack();
    }
}
