package net.parkwayschools.core;

import javax.swing.table.AbstractTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Stack;

import net.parkwayschools.core.atk.Jab;
import net.parkwayschools.gfx.RenderObj;
import net.parkwayschools.phys.PhysicsBody;
import net.parkwayschools.phys.Vector2;

import java.util.Vector;

/***
 * Represents a combatant in the fighting game
 */
public class Goose {
    enum GooseType {
        BasicGoose
    }

   

    GooseType _playerType;
    public FacingDirection _facing;
    public Attack[] attacks;
    public PhysicsBody body;
    public StateManager manager;
    public int health;

    public Animation currentLoopingAnim;
    Animation _interruptAnim;
    int _interuptFrames;

    public void addInterrupt(Animation a){
        if (_interuptFrames > 0) return;
        _interruptAnim = a;
        _interuptFrames = switch (a){
            case JUMP -> 4;
            case DOUBLE_JUMP -> 5;
            case CROUCH -> 6;
            case ATK_JAB -> 11;
            default -> 0;
        };
    }



    RenderObj getRO(){
        this.manager.run();
        Animation desired = currentLoopingAnim;
        if (_interuptFrames > 0) {
            desired = _interruptAnim;
            _interuptFrames--;
        }
        Vector2 crouchOffset = new Vector2(body.position.x,body.position.y-body.collider.size.y);
        if (!body.crouching) crouchOffset = body.position;
        return switch (desired) {
            case Animation.AIRIDLE -> new RenderObj(body.position,"jump","Air",false,0,true,_facing == FacingDirection.Left);
            case Animation.JUMP -> new RenderObj(
                    body.position,
                    "jump", "J", true, 4, true, _facing == FacingDirection.Left);
            case Animation.DOUBLE_JUMP -> new RenderObj(
                    body.position,
                    "jump", "DJ", true, 5, true, _facing == FacingDirection.Left);
            case Animation.IDLE -> new RenderObj(
                    body.position,
                    "Goose", "Idle", true, 25, true, _facing == FacingDirection.Left);
            case Animation.RUN -> new RenderObj(
                    body.position,
                    "Goose", "Running", true, 8, true, _facing == FacingDirection.Left);
            case Animation.BACKRUN -> new RenderObj(
                    body.position,
                    "Goose", "Walkback", true, 8, true, _facing == FacingDirection.Left);
            case Animation.CROUCH -> new RenderObj(
                    crouchOffset,
                    "Goose", "Crouch", true, 6, true, _facing == FacingDirection.Left);
            case Animation.CROUCHIDLE -> new RenderObj(
                    crouchOffset,
                    "Goose", "Crouch6", false, 0, true, _facing == FacingDirection.Left);
            case Animation.UNCROUCH -> new RenderObj(
                    crouchOffset,
                    "Goose", "Uncrouch", true, 6, true, _facing == FacingDirection.Left);

            case Animation.ATK_JAB -> new RenderObj(
                    body.position,
                    "Jab", "Jab", true, 11, true, _facing == FacingDirection.Left);

            default -> new RenderObj(body.position,"err","err",false,0);
        };
    }

    public Goose(GameMgr m){
        if (GameMgr.geese.size() == 0)
            this.attacks = new Attack[]{new Jab(KeyEvent.VK_SLASH)};
        else
            this.attacks = new Attack[]{new Jab(KeyEvent.VK_R)};
        health = 100;
        this._playerType = GooseType.BasicGoose;
        this._facing = FacingDirection.Right;
        this.body = new PhysicsBody(0.,0.,32.,32.,1.,new Vector2(0.15, 0.1),0.04,"");
        this.manager = new StateManager(this);

        //AT THE END OF THE CONSTRUCTOR. DON'T LEAK THIS BEFORE YOU HAVE TO (we leak because easy lol)
        m.registerGoose(this);
    }

    public Attack[] getAttacks() { return attacks; }

    public StateManager getManager() { return manager; }

//    public void runManager() {
//        while (manager.isRunning()) {
//            manager.run();
//        }
//    }
//    public void interruptState(State s) {
//        manager.interrupt();
//        manager.addState(s);
//    }
}
