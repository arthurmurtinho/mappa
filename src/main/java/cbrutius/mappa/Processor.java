package cbrutius.mappa;

import processing.core.PGraphics;

/*
this is a middle ground between the offscreen and the generator.
it has input and output, basically receives some graphics,
does stuff to them, and sends them away with the output.
 */

public class Processor {
    PGraphics parent;

    public void Processor(PGraphics parent) {
        this.parent = parent;
    }

    public void Processor() {
        this.parent = null;
    }

    public void patch(PGraphics input) {
        this.parent = input;
    }

    public PGraphics output() {
        return this.parent;
    }

}
