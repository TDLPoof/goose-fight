package net.parkwayschools.core;

import net.parkwayschools.phys.PhysicsBody;

/***
 * Represents a combatant in the fighting game
 */
public class Goose {
    enum GooseType {
        BasicGoose
    }
    enum FacingDirection {
        Left,
        Right,
        Up,
        Down
    }
    GooseType _playerType;
    FacingDirection _facing;
    public PhysicsBody body;

    public Goose(GameMgr m){
        this._playerType = GooseType.BasicGoose;
        this._facing = FacingDirection.Right;
        this.body = new PhysicsBody(0,0,32,32,1,0,0.1);
        //AT THE END OF THE CONSTRUCTOR. DON'T LEAK THIS BEFORE YOU HAVE TO (we leak because easy lol)
        m.registerGoose(this);
    }
}
