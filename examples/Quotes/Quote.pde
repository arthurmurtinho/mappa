import cbrutius.mappa.*;

Quote q;
PGraphics p;

void setup() {
  size(600,400, P3D);
  p = createGraphics(600,400, P3D);
  q = new Quote(p, "C:/Users/Arthur/Desktop/test.txt");
  q.setShowing();
  q.setTextColor(255,255,255);
}


void draw() {
  //background(255);
  q.run();
}
void mouseClicked() {
  q.setShowing();
}

