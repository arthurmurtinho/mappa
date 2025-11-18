package cbrutius.mappa;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import static processing.core.PConstants.P3D;

/*
this is a middle ground between the offscreen and the generator.
it has input and output, basically receives some graphics,
does stuff to them, and sends them away with the output.
 */

public class Processor {
    PApplet p;
    PGraphics parent;

    public Processor(PApplet p) {
        this.p = p;
        this.parent = this.p.createGraphics(p.width, p.height, P3D);
    }
    /* patch() creates a hard link between objects, and must be used in setup();
    when objects are patch, they basically operate with the same graphics.
    This means that a Picture object which is patched to a Shader object will have
    it's PGraphics modified no matter the chain of patches. The 'clean' object is lost.
     */
    public void patch(PGraphics input) {
        this.parent = input;
    }
    /*
    process() is the soft equivalent of patch(). Instead of being a one-use method,
    it is invoked during draw, and continuously applies the effect on a PImage copy
    of the input Graphics. This means that the original PGraphics is never altered,
    which allows parallel processing of the same base generator.
     */
    public void process(PGraphics input) {
        PImage temp = input.get();
        parent.beginDraw();
        parent.image(temp, 0, 0);
        parent.endDraw();
    }

    public PGraphics output() {
        return this.parent;
    }
}
