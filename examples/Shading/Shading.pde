import cbrutius.mappa.*;
import deadpixel.keystone.*;
Picture p;
OffScreen off, off2;
Manager m;
Shade s;

void setup() {
    size(600, 400, P3D);
    m = new Manager(this);
    off = new OffScreen(this, m.manage());
    off2 = new OffScreen(this,m.manage());
    s = new Shade(this);
    PImage img = loadImage("default.png");
    p = new Picture(this, img, false);
    //s.patch(p.output());
    off2.patch(p.output());
    off.patch(s.output());
    p.setShowing();
    p.status();
}

void draw() {
    background(0);
    p.show();
    s.process(p.output());
    s.blur(map(off.getSMouse().x, 0, width, 0, 10), (int) map(off.getSMouse().y, 0, height, 0, 10), 1);
    off.render();
    off2.render();
}

void keyPressed() {
    m.keyControls();
}