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

    public void patch(PGraphics input) {
        this.pg = input;
    }

    public void render () {
        this.parent.image(pg,0,0, pg.width,pg.height);
    }

}
