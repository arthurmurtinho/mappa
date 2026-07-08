import cbrutius.mappa.Video;
import cbrutius.mappa.*;
import deadpixel.keystone.*;
import processing.video.*;
import KinectPV2.*;
import oscP5.*;
import netP5.*;
import spout.*;

OffScreen off;
Manager m;
Camera c;

void setup() {
    size(600, 400, P3D);
    m = new Manager(this);
    off = new OffScreen(this,m.manage());
    c = new Camera(this, Camera.CameraMode.NETWORK_STREAM);
    off.patch(c.output());
}

void draw() {
    background(0);
    c.run();
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