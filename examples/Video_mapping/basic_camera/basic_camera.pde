import cbrutius.mappa.Video;
import cbrutius.mappa.*;
import deadpixel.keystone.*;
import processing.video.*;

OffScreen off;
Manager m;
Camera c;

void setup() {
    size(600, 400, P3D);
    m = new Manager(this);
    off = new OffScreen(this,m.manage());
    c = new Camera(this);
    off.patch(c.output());
}

void draw() {
    background(0);
    c.basicCapture();
    off.render();
}
void mouseClicked() {
}
void captureEvent(Capture cap) {
  cap.read();
}

void keyPressed() {
  m.keyControls();
}