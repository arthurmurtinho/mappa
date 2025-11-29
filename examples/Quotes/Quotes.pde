import cbrutius.mappa.*;
import deadpixel.keystone.*;


Quote q;
OffScreen off;
Manager m;

void setup() {
  size(600, 400, P3D);
  m = new Manager(this);
  off = new OffScreen(this, m.manage());
  q = new Quote(this,"test.txt");
  off.patch(q.output());
  q.setShowing();
  q.setSize(100);
  q.setTextColor(255, 255, 255);
}


void draw() {
  background(0);
  q.run();
  off.render();
}

void mouseClicked() {
  q.setShowing();
}

void keyPressed() {
  m.keyControls();
}