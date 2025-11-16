import cbrutius.mappa.*;

Light l;
PGraphics offscreen;
void setup() {
    size(600, 400, P3D);
    offscreen = createGraphics(600,400, P3D);
    l = new Light(offscreen);
    l.setShowing();
    l.status();
}

void draw() {
    background(0);
    l.setColor(255,255,255, 255);
    l.backlight();
    image(offscreen, 0,0);
}