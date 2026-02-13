package cbrutius.mappa;
import processing.core.*;
import java.util.ArrayList;
import static processing.core.PConstants.P3D;

public class Picture extends Generator {
    ArrayList<PImage> images;
    int index = 0;
    boolean fade = false;
    int alpha = 255;
    int fadeSpeed = 1;
    boolean forward = false;
    boolean backward = false;

    public Picture(PApplet p, ArrayList<PImage> images, boolean vertical) {
         super(p);
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
        super(p);
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
         if (this.forward) {
             this.index++;
             this.forward = false;
         }
         if (this.backward) {
             this.index--;
             this.backward = false;
         }
         if (this.isShowing) {
             try {
                 this.parent.tint(this.alpha);
                 this.parent.image(this.images.get(this.index), 0, 0, parent.width, parent.height);
             } catch (IndexOutOfBoundsException e) {
                 index = 0;
             }
             if (this.fade) {
                 this.alpha -= this.fadeSpeed;
             } else {
                 this.alpha = 255;
             }
             if (this.alpha <= 0) this.alpha = 0;
             this.parent.endDraw();
         }
    }

    public void slideShow() {
        this.parent.beginDraw();
        this.parent.colorMode(PConstants.HSB);
        this.parent.background(0);
        if (this.forward) {
            this.fade = true;
            if (this.alpha == 0) {
                this.index++;
                this.fade = false;
                this.forward = false;
            }
        }
        if (this.backward) {
            this.fade = true;
            if (this.alpha == 0) {
                this.index--;
                this.fade = false;
                this.backward = false;
            }
        }
        if (this.isShowing) {
            try {
                this.parent.tint(this.alpha);
                this.parent.image(this.images.get(this.index), 0, 0, parent.width, parent.height);
            } catch (IndexOutOfBoundsException e) {
                index = 0;
            }
            if (this.fade) {
                this.alpha -= this.fadeSpeed;
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
        this.forward = true;
    }

    public void prevImage() {
        this.backward = true;
    }

    public void status() {
        PApplet.println("visible: " + this.isShowing);
        PApplet.println("current: " + this.index);
    }
}
