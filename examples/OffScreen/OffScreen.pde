import cbrutius.mappa.*;

Light l;
Quote q;
OffScreen off;

void setup() {
    size(600, 400, P3D);
    off = new OffScreen(this);
    l = new Light(null);
    q = new Quote(null,"C:/Users/Arthur/Desktop/test.txt");
    off.patch(l,q);
    l.setShowing();
    q.setShowing();
    q.status();
    l.status();
}

void draw() {
    background(0);
    l.setColor(255,255,255, 255);
    l.backlight();
    q.run();
    off.render();
}