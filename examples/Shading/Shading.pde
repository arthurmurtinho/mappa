import cbrutius.mappa.*;
import deadpixel.keystone.*;

Picture p;
OffScreen off, off2;
Manager m;
Shade s;
int index = 0;

void setup() {
  size(600, 400, P3D);
  m = new Manager(this);
  off = new OffScreen(this, m.manage());
  off2 = new OffScreen(this, m.manage());
  s = new Shade(this);
  PImage img = loadImage("default.png");
  p = new Picture(this, img, false);
  //s.patch(p.output());
  off2.patch(p.output());
  off.patch(s.output());
  p.status();
}

void draw() {
  background(0);
  p.show();
  s.process(p.output());
  if (index == 0) s.blur(map(off.getSMouse().x, 0, width, 0, 10), (int) map(off.getSMouse().y, 0, height, 0, 10), 1);
  if (index == 1) s.hue(map(off.getSMouse().x, 0, width, 0, 2));
  if (index == 2) s.pixelate(map(off.getSMouse().x, 0, width, 1, width*0.1), map(off.getSMouse().y, 0, height, 0, height*0.1));
  if (index == 3) s.channels(map(off.getSMouse().x, 0, width, 0, 255), 0, 0, 0, 0, 0);
  if (index == 4) s.threshold(map(off.getSMouse().x, 0, width, 0, 1));
  if (index == 5) s.neon(0.5, 3);
  if (index == 6) s.edges();
  if (index == 7) s.wrap(map(off.getSMouse().x, 0, width, 0, 2), map(off.getSMouse().x, 0, width, 0.5, 10));
  if (index == 8) s.deform(millis()/1000, map(off.getSMouse().x, 0, width, 0, 1), map(off.getSMouse().y, 0, height, 0, 1));
  //if (index == 9) s.pixelRolls(,,, );
  if (index == 10) s.patches(map(off.getSMouse().x, 0, width, 0, 1), map(off.getSMouse().y, 0, height, 0, 1));
  if (index == 11) s.modColor(map(off.getSMouse().x, 0, width, 0, 1), 1, map(off.getSMouse().y, 0, height, 0, 1));
  if (index == 12) s.halftone(map(off.getSMouse().x, 0, width, 2, 255));
  if (index == 13) s.halftoneCMYK(map(off.getSMouse().x, 0, width, 0, 1), map(off.getSMouse().y, 0, height, 0, 100));
  if (index == 14) s.inversion();
  //if (index == 15) s.bilateralFilter(,);
  if (index > 15) index = 0;
  off.render();
  off2.render();
}

void keyPressed() {
  m.keyControls();
  switch (key) {
  case '=':
    index++;
    break;
  case '-':
    index--;
    break;
  }
}
