package cbrutius.mappa;
import javax.swing.*;
import processing.core.PApplet;

public class SecondScreen extends PApplet {
    JWindow window = new JWindow();
    JFrame frame = new JFrame("Second Screen");
    public SecondScreen() {
    }

    public void settings() {
        this.size(800, 600, P2D);
        this.surface.setTitle("Control");
    }

    public void draw() {
        this.background(0);
    }
}
