package cbrutius.mappa;
import processing.core.PGraphics;

/*
this is the main building block of the classes.
it has the basic methods that all image generator have,
basically setting visibility and routing.
 */

public class Generator {
    boolean isShowing = false;
    PGraphics parent;

    public Generator(PGraphics parent){
        this.parent = parent;
    }

    public Generator(){
        this.parent = null;
    }

    public void setShowing(){
        this.isShowing = !this.isShowing;
    }

    public PGraphics output() {
        return this.parent;
    }
}
