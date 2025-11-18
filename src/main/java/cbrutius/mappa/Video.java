package cbrutius.mappa;
import processing.core.PApplet;
import processing.video.*;
import processing.video.Movie;
import static java.lang.Math.ceil;

public class Video extends Generator {
    Movie[] movies;
    boolean playing = false;
    float volume = 1f;
    float alpha = 255;
    boolean fade = false;
    int index = 0;
    float speed = 1;
    boolean forward = false;
    boolean back = false;
    float fadeSpeed = 20;

    public Video(PApplet p) {
        super(p);
        movies = new Movie[]{};
    }

    public Video(PApplet p, Movie m) {
        super(p);
        movies = new Movie[]{m};
        for (int i = 0; i < this.movies.length; i++) {
            Movie mov = movies[i];
            mov.play();
            mov.pause();
        }
    }

    public Video(PApplet p, Movie[] movies) {
        super(p);
        this.movies = movies;
        for (int i = 0; i < this.movies.length; i++) {
            Movie mov = movies[i];
            mov.play();
            mov.pause();
        }
    }

    public void show() {
        try {
            this.parent.background(0);
            if (this.forward == true) {
                this.fade = true;
                if (this.alpha == 0) {
                    this.movies[this.index].pause();
                    this.playing = false;
                    this.index++;
                    this.fade = false;
                    this.forward = false;
                }
            }
            if (this.back == true) {
                this.fade = true;
                if (this.alpha == 0) {
                    this.movies[this.index].pause();
                    this.playing = false;
                    this.index--;
                    this.fade = false;
                    this.back = false;
                }
            }
            if (this.isShowing) {
                this.parent.tint(255, alpha);
                this.parent.image(this.movies[this.index], 0, 0, this.parent.width, this.parent.height);
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.index = 0;
        }
        if (this.fade) {
            this.alpha -= this.fadeSpeed;
        } else {
            this.alpha += this.fadeSpeed;
        }
        if (this.alpha > 255) this.alpha = 255;
        if (this.alpha < 0) this.alpha = 0;
    }


    public void setSpeed(float newSpeed) {
        this.speed = newSpeed;
        this.movies[this.index].speed(this.speed);
        this.movies[this.index].play();
    }

    public void setVolume(float newVolume) {
        this.volume = newVolume;
        this.movies[this.index].volume(this.volume);
    }

    public void setAlpha(float newAlpha) {
        this.alpha = newAlpha;
    }

    public void setPlaying() {
        this.playing = !this.playing;
        if (this.playing == true) {
            this.movies[this.index].play();
        } else {
            this.movies[this.index].pause();
        }
    }

    public void reset() {
        this.movies[this.index].jump(0);
        this.movies[this.index].play();
        this.playing = true;
    }



    public int getFrame() { //this method just grabs the current frame index;
        double frame = ceil(this.movies[this.index].time() * 30) - 1;
        return (int) frame;
    }
    /*
  this method returns the length of the movie in frames;
     that is Duration * Frame Rate
     ex.: 10 seconds * 60 frames/second = 600 frames total;
     */
    public int getLength() {
        float length = this.movies[this.index].duration() * this.movies[this.index].frameRate;
        return (int) length;
    }


    public void movieEvent(Movie m) { // I don't know if this needs to be here
        m.read();
    }
}
