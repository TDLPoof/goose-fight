package net.parkwayschools.core;

//import net.parkwayschools.phys.Vector2;

import javax.swing.table.AbstractTableModel;
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

    GooseType _playerType;
    //Vector2 _position;
    FacingDirection _facing;
    Attack[] attacks;

    public Attack[] getAttacks() {
        return attacks;
    }

    public Goose() {
        attacks = new Attack[]{new Attack(1000, KeyEvent.VK_K), new Attack(2000, KeyEvent.VK_1)};
    }

   /*
   * */



}
