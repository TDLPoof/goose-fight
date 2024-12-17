package net.parkwayschools.core;

import net.parkwayschools.phys.Vector2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Stack;

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

    PhysicsBody hitbox;
    GooseType _playerType;
    Vector2 _position;
    FacingDirection _facing;
    StateManager _stateManager;

    State test = new State("3.4", a, "goofy");


}
