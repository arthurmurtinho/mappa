package cbrutius.mappa;
import processing.core.*;

public class Light extends Generator{
    float size;
    float h, s, b, alpha;
//    ArrayList<Bulb> bulbs;
    PVector mouseLight = new PVector(0, 0);
    boolean reset = true;
    boolean fade = false;
    float fadeSpeed = 20;
    int count = 0;
//    float writingColor = 255 - backgroundColor;
//
    public Light(PApplet p) {
        super(p);
//        this.p = p;
        this.h = 255;
        this.s = 255;
        this.b = 255;
        this.alpha = 255;
        this.size = 50;
    }

    public void setColor(int h, int s, int b, float alpha){
        this.h = h;
        this.s = s;
        this.b = b;
        this.alpha = alpha;
    }

    public void backlight(){
        if (isShowing){
            this.parent.beginDraw();
            this.parent.colorMode(PConstants.HSB);
            this.parent.background(this.h, this.s, this.b, this.alpha);
            this.parent.endDraw();
        }
    }

    public void stroboscopic(int rate) {
        if (isShowing){
            this.parent.beginDraw();
            this.count++;
            if (this.count % rate == 0) {
                parent.background(0);
            } else {
                parent.background(255);
            }
            if (this.count > 1200) {
                this.count = 0;
            }
            this.parent.endDraw();
        }
    }

    public void spotlight(PVector pos, int size){
        if (isShowing){
            this.parent.beginDraw();
            this.parent.colorMode(PConstants.HSB);
            this.parent.fill(this.h, this.s, this.b, this.alpha);
            this.parent.circle(pos.x, pos.y,size);
            this.parent.endDraw();
        }
    }

    public void status() {
        PApplet.println("visible: " + this.isShowing);
    }


}
