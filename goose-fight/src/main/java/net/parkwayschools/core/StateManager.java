import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class StateManager implements Runnable, KeyListener {
    HashMap<Integer, Attack> allAttacks = new HashMap<>();
    Queue<State> activeStates = new LinkedList<>();
    boolean running = true;
    Goose focus;
    State currentState;

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

    public boolean isRunning() { return running; }

    /* Running! Put stuff in the line, then run them */
    public void run() {
        System.out.print("");
        if (!activeStates.isEmpty()) {
            currentState = activeStates.peek();
            currentState.run();
            activeStates.poll();
       }
    }

    public void stop() {
        currentState.stop();
        running = false;
        activeStates.clear();
        System.out.println("StateManager stopped");
    }

    public void interruptState(State s) {
        stop();
        activeStates.add(s);
        focus.runManager();
    }


}
