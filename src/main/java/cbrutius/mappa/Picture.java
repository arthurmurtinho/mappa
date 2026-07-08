package cbrutius.mappa;
import processing.core.*;
import java.util.ArrayList;
import static processing.core.PConstants.P3D;

public class Picture extends Generator {
    ArrayList<PImage> images;
    int index = 0;
    boolean fade = false;
    int alpha = 255;
    int fadeSpeed = 1;
    boolean forward = false;
    boolean backward = false;

    // Variáveis de controle do Zoom e Pan (Posicionamento)
    float zoom = 1.0f;       // 1.0 = 100% (tamanho original)
    float offsetX = 0.0f;    // Deslocamento X para navegação interna
    float offsetY = 0.0f;    // Deslocamento Y para navegação interna

    // Suavização opcional (Interpolação/Lerp) para movimentos fluidos em live performance
    public float targetZoom = 1.0f;
    float targetOffsetX = 0.0f;
    float targetOffsetY = 0.0f;
    boolean easeEnabled = true;


    public Picture(PApplet p, ArrayList<PImage> images, boolean vertical) {
         super(p);
         this.parent = this.p.createGraphics(this.p.width, this.p.height, P3D);
         this.images = images;
         for (PImage img : images) {
             if (vertical) {
                 img.resize(this.parent.height, this.parent.width); //we only inverse the width and height parameters
             } else {
                 img.resize(this.parent.width, this.parent.height);
             }
         }
     }

     public Picture(PApplet p, PImage image, boolean vertical) {
        super(p);
        this.p = p;
        this.parent = this.p.createGraphics(this.p.width, this.p.height, P3D);
        this.images = new ArrayList<>();
        this.images.add(image);
        for (PImage img : images) {
             if (vertical) {
                 img.resize(this.parent.height, this.parent.width); //we only inverse the width and height parameters
             } else {
                 img.resize(this.parent.width, this.parent.height);
             }
         }
     }

     public void show() {
         this.parent.beginDraw();
         this.parent.colorMode(PConstants.HSB);
         this.parent.background(0);
         if (this.forward) {
             this.index++;
             this.forward = false;
         }
         if (this.backward) {
             this.index--;
             this.backward = false;
         }
         if (this.isShowing) {
             if (easeEnabled) {
                 zoom = PApplet.lerp(zoom, targetZoom, 0.1f);
                 offsetX = PApplet.lerp(offsetX, targetOffsetX, 0.1f);
                 offsetY = PApplet.lerp(offsetY, targetOffsetY, 0.1f);
             }
             this.parent.pushMatrix();
             // 1. Move a origem para o centro do PGraphics para o zoom acontecer a partir do centro
             this.parent.translate(this.parent.width / 2f, this.parent.height / 2f);

             // 2. Aplica o fator de escala (Zoom)
             this.parent.scale(zoom);

             // 3. Aplica o deslocamento (Pan) ajustado pelo zoom para o controle ficar intuitivo
             this.parent.translate(offsetX, offsetY);
             try {
                 this.parent.tint(this.alpha);
                 // 4. Desenha a imagem centralizada na nova origem modificada
                 this.parent.imageMode(PConstants.CENTER);
                 this.parent.image(this.images.get(this.index), 0, 0, parent.width, parent.height);
             } catch (IndexOutOfBoundsException e) {
                 index = 0;
             }
             this.parent.popMatrix();

             if (this.fade) {
                 this.alpha -= this.fadeSpeed;
             } else {
                 this.alpha = 255;
             }
             if (this.alpha <= 0) this.alpha = 0;
         }
         this.parent.endDraw();
    }

    public void slideShow() {
        this.parent.beginDraw();
        this.parent.colorMode(PConstants.HSB);
        this.parent.background(0);
        if (this.forward) {
            this.fade = true;
            if (this.alpha == 0) {
                this.index++;
                this.fade = false;
                this.forward = false;
            }
        }
        if (this.backward) {
            this.fade = true;
            if (this.alpha == 0) {
                this.index--;
                this.fade = false;
                this.backward = false;
            }
        }
        if (this.isShowing) {
            try {
                this.parent.tint(this.alpha);
                this.parent.image(this.images.get(this.index), 0, 0, parent.width, parent.height);
            } catch (IndexOutOfBoundsException e) {
                index = 0;
            }
            if (this.fade) {
                this.alpha -= this.fadeSpeed;
            } else {
                this.alpha = 255;
            }
            if (this.alpha <= 0) this.alpha = 0;
            this.parent.endDraw();
        }

    }

    // Métodos para o usuário/GUI controlar o Zoom
    public void setZoom(float z) {
        this.targetZoom = PApplet.max(0.1f, z); // Evita zoom negativo ou zero
        if (!easeEnabled) this.zoom = this.targetZoom;
    }

    public void setPan(float deltaX, float deltaY) {
        // Dividir pelo targetZoom (ou zoom) faz com que o movimento do mouse
        // seja compensado proporcionalmente ao tamanho atual da imagem.
        this.targetOffsetX += deltaX / this.targetZoom;
        this.targetOffsetY += deltaY / this.targetZoom;

        if (!easeEnabled) {
            this.offsetX = this.targetOffsetX;
            this.offsetY = this.targetOffsetY;
        }
    }

    // Mantém esse caso o usuário ainda queira resetar ou setar uma posição absoluta via código
    public void setAbsolutePan(float x, float y) {
        this.targetOffsetX = x;
        this.targetOffsetY = y;
        if (!easeEnabled) {
            this.offsetX = this.targetOffsetX;
            this.offsetY = this.targetOffsetY;
        }
    }

    public void setFade() {
        this.fade = !this.fade;
    }

    public void nextImage() {
        this.forward = true;
    }

    public void prevImage() {
        this.backward = true;
    }

    public void status() {
        PApplet.println("visible: " + this.isShowing);
        PApplet.println("current: " + this.index);
    }

    // Método utilitário caso queira ativar/desativar a transição suave
    public void setEasing(boolean enabled) {
        this.easeEnabled = enabled;
    }
}
