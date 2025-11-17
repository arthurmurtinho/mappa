import cbrutius.mappa.*;

Quote q;
OffScreen off;

void setup() {
  size(600, 400, P3D);
  off = new OffScreen(this);
  q = new Quote("test.txt");
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
