package net.parkwayschools.gfx;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class RenderPanel extends JPanel {

    ArrayList<RenderObj> _currentRQ;
    public BufferedImage internalBuffer;

    public RenderPanel(){
        _currentRQ = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (internalBuffer == null) return;
        g.drawImage(
        internalBuffer.getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_REPLICATE),
                0,0,null
        );
    }
}
