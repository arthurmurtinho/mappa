import cbrutius.mappa.*;
import deadpixel.keystone.*;

Picture p;
OffScreen off;
Manager m;

void setup() {
    //size(600, 400, P3D);
    fullScreen(P3D, 2);
    m = new Manager(this);
    off = new OffScreen(this,m.manage());
    PImage img = loadImage("default.png");
    p = new Picture(this, img, false);
    off.patch(p.output());
    p.setShowing();
    p.status();
}

void draw() {
    background(0);
    p.show();
    off.render();
}
void mouseClicked() {
    p.setFade();
}

void keyPressed() {
    m.keyControls();
}