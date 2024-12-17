package net.parkwayschools.gfx;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
* Implements the graphics and render thread
* */
public class GfxManager implements Runnable{

    JFrame _frame;
    RenderPanel _rp;
    ArrayList<RenderObj> _currentRQ;
    HashMap<String,Spritesheet> _sheets;

    public GfxManager(){
        _currentRQ = new ArrayList<>();
    }

    void initSpritesheets(){

    }

    public void submitRenderQueue(ArrayList<RenderObj> ros){

    }

    @Override
    public void run() {
        _rp = new RenderPanel();
        _frame = new JFrame("Cool Goose Fighting Game:tm:");
        _frame.setSize(800,500);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.add(_rp);
        _frame.setVisible(true);
        while (true){
            for (RenderObj ro : _currentRQ){

            }
        }
    }
}
