package cbrutius.mappa;

import deadpixel.keystone.CornerPinSurface;
import deadpixel.keystone.Keystone;
import processing.core.*;
import static processing.core.PConstants.P3D;

public class OffScreen  {
PGraphics pg;
PApplet parent;
CornerPinSurface surface;
boolean isShowing = true;

    public OffScreen(PApplet parent, Keystone ks, int w, int h) {
        this.parent = parent;
        this.pg = parent.createGraphics(w,h,P3D);
        this.surface = ks.createCornerPinSurface(w,h,80);
    }

    public OffScreen(PApplet parent, Keystone ks) { //default if no size arguments are passed
        this.parent = parent;
        this.pg = parent.createGraphics(parent.width,parent.height,P3D);
        this.surface = ks.createCornerPinSurface(this.parent.width, this.parent.height,80);
    }

    public void patch(PGraphics input) {
        this.pg = input;
    }

    public PVector getSMouse() {

        return surface.getTransformedMouse();
    }

    public boolean isHoovered() {
        return surface.isMouseOver();
    }

    public void setShowing() {
        this.isShowing = !this.isShowing;
    }

    public void render () {
        if (this.isShowing) {
            this.surface.render(pg);
        }
    }

    public int getWidth() {
        return this.pg.width;
    }

    public int getHeight() {
        return this.pg.height;
    }

}
