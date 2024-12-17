package net.parkwayschools.gfx;

import javax.swing.*;
import java.awt.*;

public class RenderPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("placeholder render panel yippee!",50,50);
    }
}
