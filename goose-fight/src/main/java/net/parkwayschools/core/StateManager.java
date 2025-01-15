import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class StateManager implements Runnable, KeyListener {
    HashMap<Integer, Attack> allAttacks = new HashMap<>();
    Queue<State> activeStates = new LinkedList<>();
    Goose focus;
    Thread currentState;

    public StateManager(Goose g) {
        focus = g;
        for (Attack a : g.getAttacks()) {
            System.out.println("adding attack");
            a.setFocus(focus);
            allAttacks.put((Integer)a.getActiveKey(), a);
        }

        System.out.println("All attacks for goose loaded");
    }

    
    /* Input Handling for Attacks */
    @Override public void keyTyped(KeyEvent e) { /* :) */ }

    @Override public void keyPressed(KeyEvent e) { /* :) */ }

    @Override public void keyReleased(KeyEvent e) {
        if (allAttacks.containsKey(e.getKeyCode()) && activeStates.isEmpty()) {
            activeStates.add(allAttacks.get(e.getKeyCode()));
            System.out.println(activeStates);
        }
    }

    /* Running! Put stuff in the line, then run them */
    public void run() {
        try {
            System.out.print("");
            if (!activeStates.isEmpty()) {
                currentState = new Thread(activeStates.peek());
                currentState.run(); 
                activeStates.poll();
           }
        }
        catch (InterruptedException e) {
            currentState.interrupt();
            activeStates.clear();
            System.out.println("State Manager stopped");
        }
    }


    public void addState(State s) {
        activeStates.add(s);
        focus.runManager();
    }


}
