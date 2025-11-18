package cbrutius.mappa;
import deadpixel.keystone.*;
import org.w3c.dom.events.MouseEvent;
import processing.core.PApplet;
import processing.data.JSONObject;
import processing.data.XML;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Manager {
    Keystone keystone;
    PApplet p;
    JSONObject bindings;

    public Manager(PApplet p) {
        this.p = p;
        this.keystone = new Keystone(p);
        Path path = Paths.get("./data/bindings.json");
        if (!Files.exists(path)) {
            if (!Files.exists(path)) {
                PApplet.println("File does not exist. Creating...");
                bindingGenerator();
                bindings = p.loadJSONObject("./data/bindings.json");
                PApplet.println("File created successfully.");
            }
        } else {
            PApplet.println("Loading bindings.json...");
            bindings = p.loadJSONObject("./data/bindings.json");
        }
    }

    public Keystone manage() {
        return this.keystone;
    }

    public void keyControls(){
        eventListener();
    }

    public void eventListener() {
        try {
            if (p.keyPressed) {
                String key = Character.toString(p.key);
                int action = bindings.getInt(key);
                switch (action) {
                    case 0:
                        keystone.save("data/keystone.xml");
                        break;
                    case 1:
                        keystone.toggleCalibration();
                        break;
                    case 2:
                        keystone.load("data/keystone.xml");
                        break;
                }
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void bindingGenerator() {
        JSONObject bindings = new JSONObject();
        bindings.setInt("s", 0);
        bindings.setInt("c", 1);
        bindings.setInt("l", 2);
        p.saveJSONObject(bindings, "./data/bindings.json");
    }

}
