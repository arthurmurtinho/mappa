import cbrutius.mappa.*;

Light l;
OffScreen off;

void setup() {
    size(600, 400, P3D);
    off = new OffScreen(this);
    l = new Light();
    off.patch(l.output());
    l.setShowing();
    l.status();
}

void draw() {
    background(0);
    l.setColor((int)map(mouseX, 0, width, 0, 255),(int)map(mouseY, 0, height, 255, 0),255, 255);
    l.backlight();
    off.render();
}

void mouseClicked() {
  l.setShowing();
}