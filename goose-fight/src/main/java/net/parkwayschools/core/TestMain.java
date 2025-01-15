import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.naming.ldap.ManageReferralControl;
import javax.swing.*;

public class TestMain {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setSize(new Dimension(800,800));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        
        Goose g1 = new Goose(new Attack[]{new Attack(1000, KeyEvent.VK_K), new Attack(2000, KeyEvent.VK_1)});
        window.addKeyListener(g1.getManager());
        g1.runManager();         
    }
}
