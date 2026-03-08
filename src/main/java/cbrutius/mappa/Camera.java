package cbrutius.mappa;
import processing.core.*;
import processing.video.*;
import java.util.ArrayList;
import KinectPV2.*;

import static processing.core.PConstants.P3D;

public class Camera extends Generator {
    Capture[] capture;
    int index = 0;
    ArrayList<PImage> frameBuffer;
    int frameCounter = 0;
    int directionSpeed = 1;
    KinectPV2 kinect;

    public Camera(PApplet p) {
        super(p);
        this.parent = this.p.createGraphics(this.p.width, this.p.height, P3D);
        this.capture = new Capture[1];
        this.capture[0] = new Capture(this.p, 640, 480, "pipeline:autovideosrc"); //basic capture if none are passed;
        for (Capture c : this.capture) {
            c.start();
        }
        frameBuffer = new ArrayList<>();
        this.kinectSetup();
        PApplet.printArray(Capture.list());
    }

    public Camera(PApplet p, Capture[] cap) {
        super(p);
        this.parent = this.p.createGraphics(this.p.width, this.p.height, P3D);
        this.capture = cap;
        for (Capture c : this.capture) {
            c.start();
        }
        frameBuffer = new ArrayList<>();
        this.kinectSetup();
        PApplet.printArray(Capture.list());
    }

    private void kinectSetup() {
        this.kinect = new KinectPV2(this.p);
        this.kinect.enableColorImg(true);
        this.kinect.enableBodyTrackImg(true);
        this.kinect.enableDepthImg(true);
        this.kinect.enableInfraredImg(true);
        this.kinect.enableHDFaceDetection(true);
        this.kinect.init();
    }

    public void basicCapture() {
        if (this.isShowing) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            this.parent.image(this.capture[this.index], 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }

    public void kinectColor() {
        if (this.isShowing) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            this.parent.image(kinect.getColorImage(), 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }


    public void switchCamera() {
        this.index = (this.index + 1) % Capture.list().length;
    }

    public void looper(float size, boolean loop, float velocity) {
        if (this.isShowing) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            if (loop) {
                if (this.frameBuffer.size() < size*60) {
                    this.frameCounter++;
                    PApplet.println("number of frames: " + frameCounter);
                    this.capture[this.index].loadPixels();
                    PImage frame = this.capture[this.index].copy();
                    this.frameBuffer.add(frame);
                    this.parent.image(this.capture[this.index], 0, 0, this.p.width, this.p.height);
                } else {
                    float frameVelocity = velocity / 100;
                    this.frameCounter += frameVelocity;
                    if (this.frameCounter >= size*60) {
                        this.frameCounter = 0;
                    }
                    PApplet.println("frame velocity: " + frameVelocity);
//                    int currentFrame = frameCounter++ % (int) size*60;
                    PApplet.println("frame counter: " + frameCounter);
//                    PApplet.println("current frame: " + currentFrame);
                    PApplet.println("size of buffer: " + frameBuffer.size());
                    PImage frame = this.frameBuffer.get(this.frameCounter);
                    this.parent.image(frame, 0, 0, this.p.width, this.p.height);
                }
            } else {
                this.frameCounter = 0;
                this.frameBuffer.clear();
                this.parent.image(this.capture[this.index], 0, 0, this.p.width, this.p.height);
            }
//            this.parent.image(this.capture[this.index], 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }
    public void looper(float size, boolean loop, float velocity, boolean alternate) {
        if (this.isShowing) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            if (loop) {
                if (this.frameBuffer.size() < size*60) {
                    this.frameCounter++;
                    PApplet.println("number of frames: " + frameCounter);
                    this.capture[this.index].loadPixels();
                    PImage frame = this.capture[this.index].copy();
                    this.frameBuffer.add(frame);
                    this.parent.image(this.capture[this.index], 0, 0, this.p.width, this.p.height);
                } else {
                    float frameVelocity = velocity / 100;
//                    this.frameCounter += frameVelocity;
                    if (alternate) {
                        if (this.frameCounter >= (size*60)-frameVelocity) {
                            this.directionSpeed = -1;
                        }
                        if (this.frameCounter <= 0+frameVelocity) {
                            this.directionSpeed = 1;
                        }
                        this.frameCounter += directionSpeed * frameVelocity;
                    }
                    PApplet.println("frame velocity: " + directionSpeed * frameVelocity);
//                    int currentFrame = frameCounter++ % (int) size*60;
                    PApplet.println("frame counter: " + frameCounter);
//                    PApplet.println("current frame: " + currentFrame);
                    PApplet.println("size of buffer: " + frameBuffer.size());
                    try {
                        PImage frame = this.frameBuffer.get(this.frameCounter);
                        this.parent.image(frame, 0, 0, this.p.width, this.p.height);
                    }
                    catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        this.frameCounter--;
                    }
                }
            } else {
                this.frameCounter = 0;
                this.frameBuffer.clear();
                this.parent.image(this.capture[this.index], 0, 0, this.p.width, this.p.height);
            }
//            this.parent.image(this.capture[this.index], 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }

    void captureEvent(Capture video) { // movie thread
        video.read();
    }
}
