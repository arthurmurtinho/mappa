package cbrutius.mappa;
import processing.core.PApplet;
import processing.core.PGraphics;
import static processing.core.PConstants.P3D;
/*
this is the main building block of the classes.
it has the basic methods that all image generator have,
basically setting visibility and routing.
 */

public class Generator {
    boolean isShowing = false;
    PGraphics parent;
    PApplet p;


    public Generator(PApplet p){
        this.p = p;
        this.parent = p.createGraphics(this.p.width, this.p.height, P3D);
    }

    public void setShowing(){
        this.isShowing = !this.isShowing;
    }

    public PGraphics output() {
        return this.parent;
    }
}
