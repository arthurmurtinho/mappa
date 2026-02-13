package cbrutius.mappa;

import processing.core.PApplet;

public class SecondScreen extends PApplet {

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
