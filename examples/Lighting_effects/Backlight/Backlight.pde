import cbrutius.mappa.*;
import deadpixel.keystone.*;

Light l;
OffScreen off;
Manager m;
SecondScreen control;

void setup() {
    size(600, 400, P3D);

    m = new Manager(this);
    Manager.screenSetup(control);
    off = new OffScreen(this, m.manage());
    l = new Light(this);
    control = new SecondScreen();
    off.patch(l.output());
    l.status();
}


void draw() {
    background(0);
    if (off.isHoovered()) {
        l.setColor((int)map(off.getSMouse().x, 0, l.output().width, 0, 255),(int)map(off.getSMouse().y, 0, l.output().height, 255, 0),255, 255);
    }
    l.backlight();
    off.render();
}

void mouseClicked() {
  l.setShowing();
}

void keyPressed() {
  m.keyControls();
}
