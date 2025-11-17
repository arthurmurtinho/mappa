package cbrutius.mappa;
import processing.core.*;
import processing.core.PConstants;
import java.io.File;
import static processing.core.PApplet.loadStrings;
import static processing.core.PApplet.println;


public class Quote extends Generator {
    PGraphics parent;
    boolean isShowing = false;
    String current;
    String[] memory;
    int index = 0;
    int h , s, b = 0;
    int size = 200;
    PVector position;

    public Quote(PGraphics p, String fileName) {
        this.parent = p;
        this.memory = loadStrings(new File(fileName));
        this.current = this.memory[0];
        this.position = new PVector(parent.width/2, parent.height/2);
    }

    public Quote(String fileName) {
//        this.parent = p;
        this.memory = loadStrings(new File(fileName));
        this.current = this.memory[0];
        this.position = new PVector(parent.width/2, parent.height/2);
    }

    public void  setTextColor(int h, int s, int b) {
        this.h = h;
        this.s = s;
        this.b = b;
    }

    public void setSize(int s) {
        this.size = s;
    }

    public void run() {
        this.parent.beginDraw();
        this.parent.background(0);
        if (isShowing) {
            this.parent.colorMode(PConstants.HSB);
            this.parent.fill(this.h, this.s, this.b);
            this.parent.textAlign(PConstants.CENTER, PConstants.CENTER);
            this.parent.textSize(this.size);
            this.parent.text(this.current, this.position.x, this.position.y);
        }
        this.parent.endDraw();
    }


    public void status() {
        PApplet.println("visible: " + this.isShowing);
        PApplet.println("size: " + this.size);
        PApplet.println("position: " + this.position);
        PApplet.println("current: " + this.current);
    }

}
