import cbrutius.mappa.Video;
import cbrutius.mappa.*;
import deadpixel.keystone.*;
import processing.video.*;

Video v;
Movie mov;
OffScreen off;
Manager m;

void setup() {
    size(600, 400, P3D);
    m = new Manager(this);
    off = new OffScreen(this,m.manage());
    mov = new Movie(this, "test.mp4");
    v = new Video(this, mov);
    off.patch(v.output());
    v.setShowing();
}

void draw() {
    background(0);
    v.show();
    off.render();
}
void mouseClicked() {
    //v.setFade();
    v.setPlaying();
}
void movieEvent(Movie m) {
  m.read();
}

void keyPressed() {
  m.keyControls();
}