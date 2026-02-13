package cbrutius.mappa;
import deadpixel.keystone.*;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Manager {
    Keystone keystone;
    PApplet p;
    JSONArray bindings;
    int offscreenNumber = 0;
    SecondScreen controlScreen;

    public Manager(PApplet p) {
        this.p = p;
        this.keystone = new Keystone(p);
        Path path = Paths.get("./data/bindings.json");
        if (!Files.exists(path)) {
            if (!Files.exists(path)) {
                PApplet.println("File does not exist. Creating...");
                bindingGenerator();
                this.bindings = p.loadJSONArray("./data/bindings.json");
                PApplet.println("File created successfully.");
            }
        } else {
            PApplet.println("Loading bindings.json...");
            this.bindings = p.loadJSONArray("./data/bindings.json");
        }

    }

    public Keystone manage() {
        this.offscreenNumber++;
        return this.keystone;
    }

    public void keyControls() {
        eventListener();
    }

//    public void setKey(String k, String command) { // this is useless
//        this.bindings.append(new JSONObject().setString("key", k).setString("command", command));
//        p.saveJSONArray(bindings, "./data/bindings.json");
//    }


    private void createSecondScreen() {
        controlScreen = new SecondScreen();
    }

    private void eventListener() {
        try {
            if (p.keyPressed) {
                String key = Character.toString(p.key);
                String action = "";
                for (int i = 0; i < bindings.size(); i++) {
                    JSONObject obj = this.bindings.getJSONObject(i);
                    String s = obj.getString("key");
                    if (s.equals(key)) {
                        action = obj.getString("command");
                    }
                }
                switch (action) {
                    case "save":
                        keystone.save("data/keystone.xml");
                        break;
                    case "calibrate":
                        keystone.toggleCalibration();
                        break;
                    case "load":
                        keystone.load("data/keystone.xml");
                        break;
                }
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void bindingGenerator() {
        JSONArray bindings = new JSONArray();
        String[] commandNames = {"save", "calibrate", "load"};
        String[] commandKeys = {"s", "c", "l"};
        for (int i = 0; i <= commandNames.length - 1; i++) {
            JSONObject binding = new JSONObject();
            binding.setString("command",commandNames[i]);
            binding.setString("key",commandKeys[i]);
            bindings.setJSONObject(i, binding);
        }
        p.saveJSONArray(bindings, "./data/bindings.json");
    }

    public static void screenSetup(PApplet sa) {
        String[] args = {"Control_Screen"};
        PApplet.runSketch(args, sa);
    }
}
