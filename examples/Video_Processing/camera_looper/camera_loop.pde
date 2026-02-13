import cbrutius.mappa.Video;
import cbrutius.mappa.*;
import deadpixel.keystone.*;
import processing.video.*;

OffScreen off;
Manager m;
Camera c;
boolean loop = false;

void setup() {
    size(600, 400, P3D);
    m = new Manager(this);
    off = new OffScreen(this,m.manage());
    c = new Camera(this);
    off.patch(c.output());
}

void draw() {
    background(0);
    c.looper(1, loop, map(mouseX, 0, width, 100, 500), true);
    off.render();
}
void mouseClicked() {
    loop = !loop;
}
void captureEvent(Capture cap) {
  cap.read();
}

void keyPressed() {
  m.keyControls();
}