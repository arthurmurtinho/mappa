package cbrutius.mappa;
import processing.core.*;
import java.util.ArrayList;
import static processing.core.PConstants.P3D;

public class Picture extends Generator {
    PApplet p;
    PGraphics parent;
    ArrayList<PImage> images;
    boolean isShowing = false;
    int index = 0;
    boolean fade = false;
    int alpha = 255;

    public Picture(PApplet p, ArrayList<PImage> images, boolean vertical) {
         this.p = p;
         this.parent = this.p.createGraphics(this.p.width, this.p.height, P3D);
         this.images = images;
         for (PImage img : images) {
             if (vertical) {
                 img.resize(this.parent.height, this.parent.width); //we only inverse the width and height parameters
             } else {
                 img.resize(this.parent.width, this.parent.height);
             }
         }
     }

     public Picture(PApplet p, PImage image, boolean vertical) {
         this.p = p;
         this.parent = this.p.createGraphics(this.p.width, this.p.height, P3D);
         this.images = new ArrayList<>();
         this.images.add(image);
         for (PImage img : images) {
             if (vertical) {
                 img.resize(this.parent.height, this.parent.width); //we only inverse the width and height parameters
             } else {
                 img.resize(this.parent.width, this.parent.height);
             }
         }
     }

     public void show() {
         this.parent.beginDraw();
         this.parent.colorMode(PConstants.HSB);
         this.parent.background(0);
         if (this.isShowing) {
             try {
                 this.parent.tint(this.alpha);
                 this.parent.image(this.images.get(this.index), 0, 0, parent.width, parent.height);
             } catch (IndexOutOfBoundsException e) {
                 index = 0;
             }
             if (this.fade) {
                 this.alpha--;
             } else {
                 this.alpha = 255;
             }
             if (this.alpha <= 0) this.alpha = 0;
             this.parent.endDraw();
         }
    }



    public void setFade() {
        this.fade = !this.fade;
    }

    public void nextImage() {
        this.index++;
    }

    public void prevImage() {
        this.index--;
    }

    public void status() {
        PApplet.println("visible: " + this.isShowing);
        PApplet.println("current: " + this.index);
    }
}
