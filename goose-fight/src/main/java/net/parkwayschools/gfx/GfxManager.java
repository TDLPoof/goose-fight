package net.parkwayschools.gfx;

import javax.swing.*;
import java.util.ArrayList;

/*
* Implements the graphics and render thread
* */
public class GfxManager implements Runnable{

    JFrame _frame;
    ArrayList<RenderObj> _currentRQ;

    public GfxManager(){
        _currentRQ = new ArrayList<>();
    }

    void submitRenderQueue(ArrayList<RenderObj> ros){

    }

    @Override
    public void run() {
        _frame = new JFrame("Cool Goose Fighting Game:tm:");
        _frame.setSize(800,500);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setVisible(true);
        while (true){
            for (RenderObj ro : _currentRQ){

            }
        }
    }
}
