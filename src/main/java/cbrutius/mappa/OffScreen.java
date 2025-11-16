package cbrutius.mappa;
import processing.core.*;
import static processing.core.PConstants.P3D;

public class OffScreen  {
PGraphics pg;
PApplet parent;


    public OffScreen(PApplet parent, int w, int h) {
        this.parent = parent;
        this.pg = parent.createGraphics(w,h,P3D);
    }

    public OffScreen(PApplet parent) { //default if no size arguments are passed
        this.parent = parent;
        this.pg = parent.createGraphics(parent.width,parent.height,P3D);
    }

    public void patch(Light l, Quote q) {
        this.pg = l.parent;
        this.pg = q.parent;
    }

    public void render () {
        parent.image(pg,0,0);
    }

}
