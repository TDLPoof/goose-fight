import  java.awt.Graphics;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.*;
import java.awt.*;
import javax.imageio.*;
import java.util.HashSet;

public class AwesomeMain {
    private static PhysicsBody createBox(double x, double y, double width, double height, double m, Vector2 d, double r, String n) {
        return new PhysicsBody(x, y, width, height, m, d, r, n);
    }

    private static Collider createPlatform(double x, double y, double width, double height, String n) {
        return new Collider(x, y, width, height, n);
    }

    public static PhysicsBody box = createBox(100, 0, 32, 32, 1, new Vector2(0.1, 0.05), 0.1, "cBox");
    public static PhysicsBody box2 = createBox(300, 0, 32, 32, 1, Vector2.zero, 0.1, "tBox");
    public static Collider platform = createPlatform(0, 740, 1920, 40, "hPlatform");
    public static Collider platform2 = createPlatform(0, 0, 40, 1000, "lPlatform");
    public static Collider platform3 = createPlatform(1400, 0, 40, 1000, "rPlatform");

    public static class PhysicsThread extends Thread {

        public GraphPanel panel;

        @Override public void start() {
            panel = new GraphPanel();
        }

        @Override public void run() {
            if (currentKeys.contains(KeyEvent.VK_LEFT) && !box.walled) box.velocity = new Vector2(-8, box.velocity.y);
            if (currentKeys.contains(KeyEvent.VK_RIGHT) && !box.walled) box.velocity = new Vector2(8, box.velocity.y);
            box.update();
            box2.update();
            panel.repaint();
            try { Thread.sleep(1000 / 30); } catch (InterruptedException e) {}
        }
    }

    public static class GraphPanel extends JPanel {
        @Override
        public void paint(Graphics g) {

            /*try { Image background = ImageIO.read(new File("playplace_map.png"));
            g.drawImage(background, 0, 0, null); } catch (Exception ex) {}*/

            g.setColor(Color.BLUE);
            g.fillRect((int)box.position.x, (int)box.position.y, (int)box.collider.size.x, (int)box.collider.size.y);
            g.setColor(Color.RED);
            Vector2 boxCenter = new Vector2(box.position.x + (box.collider.size.x / 2), box.position.y + (box.collider.size.y / 2));
            g.drawLine((int)boxCenter.x, (int)boxCenter.y, (int)(boxCenter.x + box.velocity.x), (int)(boxCenter.y + box.velocity.y));

            g.drawString("Grounded: " + box.grounded, (int)boxCenter.x + 40, (int)boxCenter.y - 20);
            g.drawString("Position: " + box.position.toString(2), (int)boxCenter.x + 40, (int)boxCenter.y - 40);
            g.drawString("Velocity: " + box.velocity.toString(2), (int)boxCenter.x + 40, (int)boxCenter.y - 60);

            g.setColor(Color.GREEN);
            g.fillRect((int)box2.position.x, (int)box2.position.y, (int)box2.collider.size.x, (int)box2.collider.size.y);
            g.setColor(Color.MAGENTA);
            Vector2 box2Center = new Vector2(box2.position.x + (box2.collider.size.x / 2), box2.position.y + (box2.collider.size.y / 2));
            g.drawLine((int)box2Center.x, (int)box2Center.y, (int)(box2Center.x + box2.velocity.x), (int)(box2Center.y + box2.velocity.y));
            g.drawString(box2.velocity.toString(2), (int)box2Center.x + 40, (int)box2Center.y + 40);

            g.setColor(Color.BLACK);
            g.fillRect((int)platform.position.x, (int)platform.position.y, (int)platform.size.x, (int)platform.size.y);
            g.fillRect((int)platform2.position.x, (int)platform2.position.y, (int)platform2.size.x, (int)platform2.size.y);
            g.fillRect((int)platform3.position.x, (int)platform3.position.y, (int)platform3.size.x, (int)platform3.size.y);
        }
    }

    private static void initialize() {
        box.velocity = new Vector2(10, 0);
        box2.velocity = new Vector2(-7.07, -1);
        if (!box.collisionObjects.contains(platform)) box.collisionObjects.add(platform);
        if (!box.collisionObjects.contains(platform2)) box.collisionObjects.add(platform2);
        if (!box.collisionObjects.contains(platform3)) box.collisionObjects.add(platform3);
        if (!box.collisionObjects.contains(box2.collider)) box.collisionObjects.add(box2.collider);
        if (!box2.collisionObjects.contains(platform)) box2.collisionObjects.add(platform);
        if (!box2.collisionObjects.contains(platform2)) box2.collisionObjects.add(platform2);
        if (!box2.collisionObjects.contains(platform3)) box2.collisionObjects.add(platform3);
        if (!box2.collisionObjects.contains(box.collider)) box2.collisionObjects.add(box.collider);
    }

    static boolean paused = true;
    static HashSet<Integer> currentKeys = new HashSet<>();
    public static void main(String[] args) {
        JFrame frame = new JFrame("awesome");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PhysicsThread thread = new PhysicsThread();
        thread.start();
        GraphPanel panel = thread.panel;
        frame.add(panel);

        initialize();

        frame.pack();
        frame.setVisible(true);
        frame.setSize(1920, 1080);

        frame.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_UP && box.jumps > 0) {
                    box.velocity = new Vector2(box.velocity.x, -10);
                    box.jumps--;
                }
                else if (e.getKeyCode() == KeyEvent.VK_UP && box.walljumps > 0 && box.walled) {
                    box.velocity = new Vector2(box.velocity.x, -10);
                    box.walljumps--;
                }

                if (!currentKeys.contains(e.getKeyCode())) currentKeys.add(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    paused = !paused;
                }

                if (e.getKeyCode() == KeyEvent.VK_S)
                {
                    thread.run();
                }

                if (e.getKeyCode() == KeyEvent.VK_R) initialize();

                //if (e.getKeyCode() == KeyEvent.VK_DOWN) box.addForce(new Vector2(0, 1));
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                currentKeys.remove(Integer.valueOf(e.getKeyCode()));
            }
        });

        for (;true;)
        {
            if (!paused) thread.run();
            frame.repaint();
        }
    }
}