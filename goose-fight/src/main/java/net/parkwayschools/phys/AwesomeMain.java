import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AwesomeMain {
    private static PhysicsBody createBox(double x, double y, double width, double height, double m, double d) {
        return new PhysicsBody(x, y, width, height, m, d);
    }

    private static Collider createPlatform(double x, double y, double width, double height) {
        return new Collider(x, y, width, height);
    }

    public static PhysicsBody box = createBox(100, 0, 40, 40, 1, 0);
    public static PhysicsBody box2 = createBox(300, 0, 40, 40, 1, 0);
    public static Collider platform = createPlatform(0, 340, 500, 40);
    public static Collider platform2 = createPlatform(480, 0, 40, 380);
    public static Collider platform2 = createPlatform(480, 0, 40, 380);

    public static class PhysicsThread extends Thread {

        public GraphPanel panel;

        @Override public void start() {
            panel = new GraphPanel();
        }

        @Override public void run() {
            box.update();
            box2.update();
            panel.repaint();
            try { Thread.sleep(1000 / 24); } catch (InterruptedException e) {}
        }
    }

    public static class GraphPanel extends JPanel {
        @Override
        public void paint(Graphics g) {
            g.setColor(Color.BLUE);
            g.fillRect((int)box.position.x, (int)box.position.y, (int)box.collider.size.x, (int)box.collider.size.y);
            g.setColor(Color.RED);
            Vector2 boxCenter = new Vector2(box.position.x + (box.collider.size.x / 2), box.position.y + (box.collider.size.y / 2));
            g.drawLine((int)boxCenter.x, (int)boxCenter.y, (int)(boxCenter.x + box.velocity.x), (int)(boxCenter.y + box.velocity.y));
            g.drawString(box.velocity.toString(), (int)boxCenter.x + 40, (int)boxCenter.y + 40);

            g.setColor(Color.GREEN);
            g.fillRect((int)box2.position.x, (int)box2.position.y, (int)box2.collider.size.x, (int)box2.collider.size.y);
            g.setColor(Color.MAGENTA);
            Vector2 box2Center = new Vector2(box2.position.x + (box2.collider.size.x / 2), box2.position.y + (box2.collider.size.y / 2));
            g.drawLine((int)box2Center.x, (int)box2Center.y, (int)(box2Center.x + box2.velocity.x), (int)(box2Center.y + box2.velocity.y));
            g.drawString(box2.velocity.toString(), (int)box2Center.x + 40, (int)box2Center.y + 40);

            g.setColor(Color.BLACK);
            g.fillRect((int)platform.position.x, (int)platform.position.y, (int)platform.size.x, (int)platform.size.y);
            g.fillRect((int)platform2.position.x, (int)platform2.position.y, (int)platform2.size.x, (int)platform2.size.y);
        }
    }

    private static void initialize() {
        box.restitution = 0.6;
        box2.restitution = 1.5;
        box.velocity = new Vector2(10, 0);
        box2.velocity = new Vector2(-7.07, -1);
        box.position = new Vector2(100, 0);
        box2.position = new Vector2(300, 0);
        if (!box.collisionObjects.contains(platform)) box.collisionObjects.add(platform);
        if (!box.collisionObjects.contains(platform2)) box.collisionObjects.add(platform2);
        if (!box.collisionObjects.contains(box2.collider)) box.collisionObjects.add(box2.collider);
        if (!box2.collisionObjects.contains(platform)) box2.collisionObjects.add(platform);
        if (!box2.collisionObjects.contains(platform2)) box2.collisionObjects.add(platform2);
        if (!box2.collisionObjects.contains(box.collider)) box2.collisionObjects.add(box.collider);
    }

    static boolean paused = true;
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
        frame.setSize(800, 600);

        frame.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    paused = !paused;
                }

                if (e.getKeyCode() == KeyEvent.VK_S)
                {
                    thread.run();
                }

                if (e.getKeyCode() == KeyEvent.VK_R) initialize();


                if (e.getKeyCode() == KeyEvent.VK_LEFT) box.addForce(new Vector2(-1, 0));
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) box.addForce(new Vector2(1, 0));
                if (e.getKeyCode() == KeyEvent.VK_UP) box.addForce(new Vector2(0, -1));
                if (e.getKeyCode() == KeyEvent.VK_DOWN) box.addForce(new Vector2(0, 1));
            }
        });

        for (;true;)
        {
            if (!paused) thread.run();
            frame.repaint();
        }
    }
}