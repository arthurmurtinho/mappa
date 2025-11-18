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

