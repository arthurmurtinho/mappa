import cbrutius.mappa.*;

Picture p;
OffScreen off;

void setup() {
    size(600, 400, P3D);
    off = new OffScreen(this);
    ArrayList<PImage> imgs = new ArrayList<PImage>();
    for (int i = 1; i < 10; i++) {
        PImage img = loadImage("hands/" + i + ".png");
        imgs.add(img);
    }
    p = new Picture(this, imgs, false);
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
    p.nextImage();
}