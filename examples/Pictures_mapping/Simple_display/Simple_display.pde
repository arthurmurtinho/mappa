import cbrutius.mappa.*;

Picture p;
OffScreen off;

void setup() {
    size(600, 400, P3D);
    off = new OffScreen(this);
    PImage img = loadImage("default.png");
    p = new Picture(this, img, false);
    off.patch(p.output());
    p.setShowing();
    p.status();
}

void draw() {
    background(0);
    p.show();
    off.render();
}
void mouseClicked() {
    p.setFade();
}