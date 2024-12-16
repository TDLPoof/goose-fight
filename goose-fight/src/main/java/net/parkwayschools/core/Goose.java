package net.parkwayschools.core;

import net.parkwayschools.phys.Vector2;

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
    Vector2 _position;
    FacingDirection _facing;
}
