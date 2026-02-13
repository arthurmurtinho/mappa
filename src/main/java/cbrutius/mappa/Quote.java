package cbrutius.mappa;
import processing.core.*;
import processing.core.PConstants;
import static processing.core.PConstants.P3D;


public class Quote extends Generator {
    String current;
    String[] memory;
    int index = 0;
    int h , s, b = 0;
    int size = 200;
    PVector position;

    public Quote(PApplet p, String fileName) {
        super(p);
        this.parent = p.createGraphics(this.p.width, this.p.height, P3D);
        this.memory = p.loadStrings(fileName);
//        this.current = wrapText(this.memory[0], this.parent.width);
        this.current = this.memory[0];
        this.position = new PVector((float) this.parent.width/2, (float) this.parent.height/2);
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
        this.parent.background(0, this.offscreen_alpha);
        if (this.isShowing) {
            this.parent.colorMode(PConstants.HSB);
            this.parent.fill(this.h, this.s, this.b);
            this.parent.textAlign(PConstants.CENTER, PConstants.CENTER);
            this.parent.textSize(this.size);
            this.parent.text(this.current, 0, 0, this.parent.width, this.parent.height);
        }
        this.parent.endDraw();
    }

    private String wrapText(String txt, float maxWidth) { //unused
        String[] words = txt.split(" ");
        String result = "";
        String line = "";
        for (String w : words) {
            if (this.parent.textWidth(line + w) < maxWidth) {
                line += w + " ";
            } else {
                result += line + "\n";
                line = w + " ";
            }
        }
        result += line;
        return result;
    }

    public void nextQuote() {
        this.index++;
        if  (this.index >= this.memory.length) index = 0;
        this.current =  this.memory[this.index];
    }

    public void previousQuote() {
        this.index--;
        if (this.index < 0) this.index = this.memory.length - 1;
        this.current = this.memory[this.index];
    }

    public void status() {
        PApplet.println("visible: " + this.isShowing);
        PApplet.println("size: " + this.size);
        PApplet.println("position: " + this.position);
        PApplet.println("current: " + this.current);
    }

}
