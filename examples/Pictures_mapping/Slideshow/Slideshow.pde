import cbrutius.mappa.*;
import deadpixel.keystone.*;

Picture p;
OffScreen off;
Manager m;

void setup() {
    //size(600, 400, P3D);
    fullScreen(P3D, 2);
    m =  new Manager(this);
    off = new OffScreen(this, m.manage());
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

void keyPressed() {
    m.keyControls();
}