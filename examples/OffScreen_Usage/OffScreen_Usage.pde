import cbrutius.mappa.*;
import deadpixel.keystone.*;

Light l;
OffScreen off;
Manager m;
void setup() {
    size(600, 400, P3D);
    m = new Manager(this);
    off = new OffScreen(this, m.manage());
    l = new Light(this);
    off.patch(l.output());
    l.setShowing();
    l.status();
}

void draw() {
    background(0);
    l.setColor((int)map(off.getSMouse().x, 0, width, 0, 255),(int)map(off.getSMouse().y, 0, height, 255, 0),255, 255);
    l.backlight();
    off.render();
}

void keyPressed() {
    m.keyControls();
}