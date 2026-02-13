package cbrutius.mappa;
import processing.core.PApplet;
import processing.opengl.PShader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Shade extends Processor{
    PShader shader;
    boolean loaded = false;
    String home = System.getProperty("user.home");
    String libPath = this.home + "/Documents/Processing/libraries/mappa/library/shaders";

    public Shade (PApplet p) {
        super(p);
        shader = new PShader(p);
    }

    public void blur(float sigma, int size, float offset) {
        if(!loaded){
            reload(libPath + "/blur.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("sigma", sigma);
        shader.set("blurSize", size);
        shader.set("texOffset", offset);
        parent.shader(shader);
        parent.endDraw();
    }

    public void hue(float hue) {
        if(!loaded){
            reload(libPath + "/hue.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("hue", hue);
        parent.shader(shader);
        parent.endDraw();
    }

    public void pixelate(float pixelsX, float pixelsY) {
        if(!loaded){
            reload(libPath + "/pixelate.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("pixels", pixelsX, pixelsY);
        parent.shader(shader);
        parent.endDraw();
    }

    public void channels(float rbias, float gbias, float bbias, float rmult, float gmult, float bmult) {
        if(!loaded) {
            reload(libPath + "/channels.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("rbias", rbias);
        shader.set("gbias", gbias);
        shader.set("bbias", bbias);
        shader.set("rmult", rmult);
        shader.set("gmult", gmult);
        shader.set("bmult", bmult);
        parent.shader(shader);
        parent.endDraw();
    }

    public void threshold(float threshold) {
        if(!loaded){
            reload(libPath + "/threshold.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("threshold", threshold);
        parent.shader(shader);
        parent.endDraw();
    }

    public void neon(float brt, float rad) { //not working
        if(!loaded){
            reload(libPath + "/neon.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("brt", brt);
        shader.set("rad", rad);
        parent.shader(shader);
        parent.endDraw();
    }

    public void edges() {
        if(!loaded){
            reload(libPath + "/edges.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        parent.shader(shader);
        parent.endDraw();
    }

    public void wrap(float radius, float radTwist) {
        if(!loaded){
            reload(libPath + "/wrap.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("radius", radius);
        shader.set("radTwist", radTwist);
        parent.shader(shader);
        parent.endDraw();
    }

    public void deform(float time, float mouse, float turns) { //doesn't work, multiple fields for the sets
        if(!loaded){
            reload(libPath + "/deform.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("time", time);
        shader.set("mouse", mouse);
        shader.set("turns", turns);
        parent.shader(shader);
        parent.endDraw();
    }

    public void pixelRolls(float time, float pixels, float rollRate, float rollAmount) { // many fields, needs fixing
        if(!loaded){
            reload(libPath + "/pixelRolls.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("time", time);
        shader.set("pixels", pixels);
        shader.set("rollRate", rollRate);
        shader.set("rollAmount", rollAmount);
        parent.shader(shader);
        parent.endDraw();
    }

    public void patches(float row, float col) {
        if(!loaded){
            reload(libPath + "/patches.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("row", row);
        shader.set("col", col);
        parent.shader(shader);
        parent.endDraw();
    }

    public void modColor(float modr, float modg, float modb) {
        if(!loaded){
            reload(libPath + "/modcolor.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("modr", modr);
        shader.set("modg", modg);
        shader.set("modb", modb);
        parent.shader(shader);
        parent.endDraw();
    }

    public void halftone(float pixelsPerRow) { //broken due to casting
        if(!loaded){
            reload(libPath + "/halftone.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("pixelsPerRow", pixelsPerRow);
        parent.shader(shader);
        parent.endDraw();
    }

    public void halftoneCMYK(float density, float frequency) {
        if(!loaded){
            reload(libPath + "/halftone_cmyk.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("density", density);
        shader.set("frequency", frequency);
        parent.shader(shader);
        parent.endDraw();
    }

    public void inversion() {
        if(!loaded){
            reload(libPath + "/invert.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        parent.shader(shader);
        parent.endDraw();
    }

    public void bilateralFilter(float resolution, float sigma) { //many fields!
        if(!loaded){
            reload(libPath + "/bilateral_filter.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("resolution", resolution);
        shader.set("sigma", sigma);
        parent.shader(shader);
        parent.endDraw();
    }

    public void vhs(float resolution, float time) { //many fields
        if(!loaded){
            reload(libPath + "/vhs.glsl");
            PApplet.println("Shader loaded!");
        }
        parent.beginDraw();
        shader.set("iResolution", resolution);
        shader.set("iGlobalTime", time);
        parent.shader(shader);
        parent.endDraw();
    }

    void reload(String shaderName) {
        shader = p.loadShader(shaderName);
        loaded = true;
    }

    private String loadResourceText(String path) {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) return null;
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

