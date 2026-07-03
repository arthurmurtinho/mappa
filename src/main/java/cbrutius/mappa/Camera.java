package cbrutius.mappa;
import processing.core.*;
import processing.video.*;
import java.util.ArrayList;
import KinectPV2.*;
import processing.core.PVector;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static processing.core.PApplet.*;
import static processing.core.PConstants.*;

public class Camera extends Generator {
    Capture[] capture;
    int index = 0;
    ArrayList<PImage> frameBuffer;
    int frameCounter = 0;
    int directionSpeed = 1;
    KinectPV2 kinect;
//    Object kinect; //this  is the substitute in reflection
    boolean usingKinect = true;
    float smoothedX = 0;
    float smoothedY = 0;
    float lerpAmount = 0.15f;

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
    /* This is the quick fix for the kinect in camera class. if
    the user doesn't import KinectPV2, it should handle the error instead of crashing the sketch
     */
    private void kinectSetup() {
        try {
            this.kinect = new KinectPV2(this.p);
            this.kinect.enableColorImg(true);
            this.kinect.enableBodyTrackImg(true);
            this.kinect.enableDepthImg(true);
            this.kinect.enableInfraredImg(true);
            this.kinect.enableFaceDetection(true);
            this.kinect.enableHDFaceDetection(true);
            this.kinect.enablePointCloud(true);
//            this.kinect.ena
            this.kinect.init();
        } catch (NoClassDefFoundError e) {
            usingKinect = false;
            System.out.println("KinectPV2 Lib not imported");
        }
    }

      /* this is a way to use reflection, instead of just catching the error
      but its waaaaay more effort to adapt, so its here as a promise!*/
    /*
    private void kinectSetup() {
        try {
            Class<?> clazz = Class.forName("KinectPV2.KinectPV2");
            kinect = clazz.getConstructor(PApplet.class).newInstance(this.p);

            clazz.getMethod("enableColorImg", boolean.class).invoke(kinect, true);
            clazz.getMethod("enableBodyTrackImg", boolean.class).invoke(kinect, true);
            clazz.getMethod("enableDepthImg", boolean.class).invoke(kinect, true);
            clazz.getMethod("enableInfraredImg", boolean.class).invoke(kinect, true);
            clazz.getMethod("enableHDFaceDetection", boolean.class).invoke(kinect, true);
            clazz.getMethod("init").invoke(kinect);
            usingKinect = true;
            System.out.println("Kinect initialized");
        } catch (Exception e) {
            System.out.println("Kinect not available");
            usingKinect = false;
        }

    }
*/

    public void basicCapture() {
        if (this.isShowing) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            this.parent.image(this.capture[this.index], 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }

    public void kinectColor() {
        if (this.isShowing && this.usingKinect) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            this.parent.image(this.kinect.getColorImage(), 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }

    public void kinectDepth() {
        if (this.isShowing && this.usingKinect) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            this.parent.image(this.kinect.getDepthImage(), 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }

    public void kinectBodyTrack() {
        if (this.isShowing && this.usingKinect) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            this.parent.image(this.kinect.getBodyTrackImage(), 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }

    public void kinectInfrared() {
        if (this.isShowing && this.usingKinect) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            this.parent.image(this.kinect.getInfraredImage(), 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }

    public void kinectPointCloud() {
        if (this.isShowing && this.usingKinect) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
            this.parent.image(this.kinect.getPointCloudDepthImage(), 0, 0, this.p.width, this.p.height);
            this.parent.endDraw();
        }
    }

    public void faceMap(PImage texture) {
        if (this.isShowing && this.usingKinect) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
//            this.drawFaceMap();
            this.drawFaceVertex(texture);
            this.parent.endDraw();
        }
    }

    public void faceCenter() {
        if (this.isShowing && this.usingKinect) {
            this.parent.beginDraw();
            this.parent.background(0, this.offscreen_alpha);
//            this.drawFaceMap();
            drawFaceCenterCamera();
            this.parent.endDraw();
        }
    }

    private void drawFaceCenterCamera() {
        this.kinect.generateFaceData();
        ArrayList<FaceData> faces = this.kinect.getFaceData();
                if (faces.size() > 0 && faces.get(0) != null) {
                    FaceData fd = faces.get(0);
                    KRectangle faceBound = fd.getBoundingRectColor();
//                    this.parent.noFill();
//                    this.parent.stroke(255, 0, 0);
//                    float fbX = map(faceBound.getX(), 0, 1920, 0, this.parent.width);
//                    float fbY = map(faceBound.getY(), 0, 1080, 0, this.parent.height);
//                    float fbW = map(faceBound.getWidth(), 0, 1920, 0, this.parent.width);
//                    float fbH = map(faceBound.getHeight(), 0, 1080, 0, this.parent.height);
    //                this.parent.circle(fbX, fbY, 10);
//                    this.parent.translate(fbW/2, fbH/2);
                    float faceCenterX = faceBound.getX() + (faceBound.getWidth() / 2);
                    float faceCenterY = faceBound.getY() + (faceBound.getHeight() / 2);
                    float targetX = map(faceCenterX, 0, 1920, 0, this.parent.width);
                    float targetY = map(faceCenterY, 0, 1080, 0, this.parent.height);
                    this.smoothedX = lerp(this.smoothedX, targetX, this.lerpAmount);
                    this.smoothedY = lerp(this.smoothedY, targetY, this.lerpAmount);

                    this.parent.pushMatrix();
//                    this.parent.translate(this.parent.width/2 - this.smoothedX, this.parent.height/2 - this.smoothedY);
                    this.parent.translate(this.parent.width / 2, this.parent.height / 2);
                    this.parent.scale(2f);
                    this.parent.imageMode(CENTER);
                    float imgCenterX = this.parent.width / 2;
                    float imgCenterY = this.parent.height / 2;
//                    this.parent.image(this.kinect.getColorImage(), 0, 0, this.parent.width, this.parent.height);
                    this.parent.image(
                            this.kinect.getColorImage(),
                            imgCenterX - this.smoothedX,
                            imgCenterY - this.smoothedY,
                            this.parent.width,
                            this.parent.height);
    //                this.parent.rect(fbX, fbY, fbW, fbH);
                    this.parent.popMatrix();
                } else {
                    // Fallback: If no face, just draw the image normally
                    this.parent.image(this.kinect.getColorImage(), 0, 0, this.parent.width, this.parent.height);
                }
        }

    private void drawFaceMap() {
        this.kinect.generateFaceData();
        this.parent.image(this.kinect.getColorImage(), 0, 0, this.p.width, this.p.height);
        ArrayList<FaceData> faces = this.kinect.getFaceData();
        faces.forEach(f -> {
            if (f != null) {
                this.parent.pushMatrix();
                FaceData fd = (FaceData) f;
                KRectangle faceBound= fd.getBoundingRectColor();
                this.parent.noFill();
                this.parent.stroke(255, 0, 0);
                float fbX = map(faceBound.getX(), 0, 1920, 0 , this.parent.width);
                float fbY = map(faceBound.getY(), 0, 1080, 0 , this.parent.height);
                float fbW = map(faceBound.getWidth(), 0, 1920, 0 , this.parent.width);
                float fbH = map(faceBound.getHeight(), 0, 1080, 0 , this.parent.height);
                this.parent.circle(fbX, fbY, 10);
                this.parent.rect(fbX, fbY, fbW, fbH);
                this.parent.popMatrix();
//                System.out.println(faceBound.getX() + " " + faceBound.getY() + " " + faceBound.getWidth() + " " + faceBound.getHeight());
//                System.out.println(fbX + " " + fbY + " " + fbW + " " + fbH);
            }
        });
    }

    private void OLDdrawFaceVertex(PImage texture) {
        ArrayList<HDFaceData> face = this.kinect.getHDFaceVertex();
        this.parent.image(this.kinect.getColorImage(), 0, 0, this.p.width, this.p.height);
        for (HDFaceData faceVertex : face) {
            if (faceVertex.isTracked()) {
                System.out.println("Face detected");
//                this.parent.textureMode(NORMAL);
//                this.parent.stroke(0,255,0);
                this.parent.beginShape(TRIANGLES);
                this.parent.texture(texture);
                for (int i = 0; i < KinectPV2.HDFaceVertexCount; i++) {
//                    PVector vertex = new PVector(faceVertex.getX(i), faceVertex.getY(i));
                    float x = map(faceVertex.getX(i),  0, 1920, 0 , this.parent.width);
                    float y = map(faceVertex.getY(i),   0, 1080, 0 , this.parent.height);
                    float u = x / texture.width;
                    float v = y / texture.height;
                    this.parent.vertex(x, y, u, v);
//                    this.parent.ellipse(x,y, 10,10);
                }
                this.parent.endShape();
            }
        }
    }

    private void drawFaceVertex(PImage texture){
        if(!usingKinect) return;

        ArrayList<HDFaceData> faces = kinect.getHDFaceVertex();
        PImage colorImg = kinect.getColorImage();
        parent.noStroke();
        parent.image(colorImg, 0, 0, parent.width, parent.height);

        for(HDFaceData faceVertex: faces){
            if(!faceVertex.isTracked()) continue;

            ArrayList<Vertex> verts = new ArrayList<>();
            for(int i=0;i<KinectPV2.HDFaceVertexCount;i++){
                float x=faceVertex.getX(i), y=faceVertex.getY(i);
                if(Float.isNaN(x)||Float.isNaN(y)||Float.isInfinite(x)||Float.isInfinite(y)) continue;

                // Scaled position on canvas
                float scaledX = map(x, 0, colorImg.width, 0, parent.width);
                float scaledY = map(y, 0, colorImg.height, 0, parent.height);

                // UV normalized 0-1
                float u = x / colorImg.width;
                float v = y / colorImg.height;

                verts.add(new Vertex(new PVector(scaledX,scaledY), u, v));
            }

            ArrayList<Tri> tris = delaunayTriangulation(verts);

            parent.beginShape(TRIANGLES);
            parent.texture(texture);
            for(Tri t: tris){
                parent.vertex(t.a.pos.x, t.a.pos.y, t.a.u*texture.width, t.a.v*texture.height);
                parent.vertex(t.b.pos.x, t.b.pos.y, t.b.u*texture.width, t.b.v*texture.height);
                parent.vertex(t.c.pos.x, t.c.pos.y, t.c.u*texture.width, t.c.v*texture.height);
            }
            parent.endShape();
        }
    }

    // Holds a vertex with position and UVs
    private class Vertex {
        PVector pos; // scaled position on canvas
        float u, v;  // UVs normalized 0-1
        Vertex(PVector pos, float u, float v) { this.pos = pos; this.u = u; this.v = v; }
    }

    // Triangle using Vertex references
    private class Tri {
        Vertex a, b, c;
        Circle circum;
        Tri(Vertex a, Vertex b, Vertex c) {
            this.a = a; this.b = b; this.c = c;
            this.circum = circumcircle(a.pos, b.pos, c.pos);
        }
        boolean containsInCircumcircle(PVector p) {
            float dx = circum.x - p.x;
            float dy = circum.y - p.y;
            return dx*dx + dy*dy < circum.r*circum.r;
        }
    }

    // Simple circle class for circumcircle
    private class Circle {
        float x, y, r;
        Circle(float x, float y, float r) { this.x = x; this.y = y; this.r = r; }
    }

    // Compute circumcircle of 3 points
    private Circle circumcircle(PVector a, PVector b, PVector c){
        float A = b.x - a.x, B = b.y - a.y, C = c.x - a.x, D = c.y - a.y;
        float E = A*(a.x+b.x)+B*(a.y+b.y);
        float F = C*(a.x+c.x)+D*(a.y+c.y);
        float G = 2*(A*(c.y-b.y)-B*(c.x-b.x));
        if(G==0) return new Circle(0,0,Float.MAX_VALUE); // collinear
        float cx = (D*E-B*F)/G;
        float cy = (A*F-C*E)/G;
        float r = dist(cx,cy,a.x,a.y);
        return new Circle(cx,cy,r);
    }

    // Delaunay triangulation for a list of vertices
    private ArrayList<Tri> delaunayTriangulation(ArrayList<Vertex> verts){
        ArrayList<Tri> tris = new ArrayList<>();

        // Super-triangle
        float minX=Float.MAX_VALUE,minY=Float.MAX_VALUE,maxX=-Float.MAX_VALUE,maxY=-Float.MAX_VALUE;
        for(Vertex v:verts){ minX=Math.min(minX,v.pos.x); minY=Math.min(minY,v.pos.y);
            maxX=Math.max(maxX,v.pos.x); maxY=Math.max(maxY,v.pos.y); }
        float dx=maxX-minX, dy=maxY-minY, delta=Math.max(dx,dy)*10;
        Vertex p1 = new Vertex(new PVector(minX-delta,minY-delta*3),0,0);
        Vertex p2 = new Vertex(new PVector(minX+dx/2,maxY+delta*3),0,0);
        Vertex p3 = new Vertex(new PVector(maxX+delta,minY-delta*3),0,0);
        tris.add(new Tri(p1,p2,p3));

        for(Vertex p: verts){
            ArrayList<Tri> bad = new ArrayList<>();
            for(Tri t: tris) if(t.containsInCircumcircle(p.pos)) bad.add(t);

            ArrayList<PVector[]> polygon = new ArrayList<>();
            for(Tri t: bad){
                polygon.add(new PVector[]{t.a.pos,t.b.pos});
                polygon.add(new PVector[]{t.b.pos,t.c.pos});
                polygon.add(new PVector[]{t.c.pos,t.a.pos});
            }

            tris.removeAll(bad);

            ArrayList<PVector[]> uniqueEdges = new ArrayList<>();
            for(PVector[] edge: polygon){
                boolean shared=false;
                for(PVector[] other: polygon){
                    if(edge==other) continue;
                    if((edge[0].equals(other[1])&&edge[1].equals(other[0]))||
                            (edge[0].equals(other[0])&&edge[1].equals(other[1]))){shared=true; break;}
                }
                if(!shared) uniqueEdges.add(edge);
            }

            for(PVector[] edge: uniqueEdges){
                Vertex va=null,vb=null;
                for(Vertex v: verts){
                    if(v.pos.equals(edge[0])) va=v;
                    if(v.pos.equals(edge[1])) vb=v;
                }
                if(va!=null && vb!=null) tris.add(new Tri(va,vb,p));
            }
        }

        // Remove triangles connected to super-triangle
        ArrayList<Tri> finalTris = new ArrayList<>();
        for(Tri t: tris){
            if(t.a!=p1&&t.a!=p2&&t.a!=p3&&t.b!=p1&&t.b!=p2&&t.b!=p3&&t.c!=p1&&t.c!=p2&&t.c!=p3)
                finalTris.add(t);
        }
        return finalTris;
    }


    public void switchCamera() {
        this.index = (this.index + 1) % Capture.list().length-1;
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
