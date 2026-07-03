package cbrutius.mappa;
import processing.core.*;
import processing.core.PConstants;
import static processing.core.PConstants.P3D;


public class Quote extends Generator {
    String current;
    String[] memory;
    int index = 0;
    int h , s, b = 0;
    int baseSize = 200;
    int currentSize = 200;
    PVector position;

    public Quote(PApplet p, String fileName) {
        super(p);
        this.parent = p.createGraphics(this.p.width, this.p.height, P3D);
        this.memory = p.loadStrings(fileName);
//        this.current = wrapText(this.memory[0], this.parent.width);
        this.current = this.memory[0];
        this.position = new PVector((float) this.parent.width/2, (float) this.parent.height/2);
        this.adjustFontSize();
    }

    public void  setTextColor(int h, int s, int b) {
        this.h = h;
        this.s = s;
        this.b = b;
    }

    public void setSize(int s) {
        this.baseSize = s;
        this.adjustFontSize();
    }

    private void adjustFontSize() {
        if (this.current == null || this.current.isEmpty()) return;

        int targetSize = this.baseSize;
        this.parent.beginDraw(); // Necessário para inicializar o contexto de texto do PGraphics
        this.parent.textSize(targetSize);

        // Reduz o tamanho da fonte até que a altura estimada do bloco de texto caiba no parent
        // Estipulamos um limite mínimo de fonte (ex: 12) para evitar loops infinitos
        while (calculateTextHeight(this.current, this.parent.width, targetSize) > this.parent.height && targetSize > 12) {
            targetSize -= 4; // Diminui o tamanho gradativamente
        }

        this.parent.endDraw();
        this.currentSize = targetSize;
    }

    /**
     * Calcula a altura aproximada que o texto ocupará após a quebra de linha automática
     */
    private float calculateTextHeight(String txt, float maxWidth, float fontSize) {
        this.parent.textSize(fontSize);

        // O Processing define o textLeading padrão como aproximadamente 1.275 * fontSize
        // se nenhuma fonte customizada for carregada, mas podemos pegar o valor exato:
        float currentLeading = this.parent.textLeading;

        String[] words = txt.split(" ");
        float spaceWidth = this.parent.textWidth(" ");
        float currentLineWidth = 0;
        int lineCount = 1;

        for (String w : words) {
            float wordWidth = this.parent.textWidth(w);

            // Se a palavra sozinha for maior que a largura máxima, força uma quebra
            if (currentLineWidth + wordWidth > maxWidth) {
                lineCount++;
                currentLineWidth = wordWidth + spaceWidth;
            } else {
                currentLineWidth += wordWidth + spaceWidth;
            }
        }

        // A primeira linha ocupa (ascent + descent).
        // As linhas subsequentes são empurradas pelo valor do textLeading.
        float firstLineHeight = this.parent.textAscent() + this.parent.textDescent();
        float totalHeight = firstLineHeight + ((lineCount - 1) * currentLeading);

        // Adiciona uma margem de segurança de 10% da altura da fonte para evitar que
        // pequenas variações de renderização ou caracteres como 'g', 'j', 'y' cortem.
        float safetyMargin = fontSize * 0.1f;

        return totalHeight + safetyMargin;
    }

    public void run() {
        this.parent.beginDraw();
        this.parent.background(0, this.offscreen_alpha);
        if (this.isShowing) {
            this.parent.colorMode(PConstants.HSB);
            this.parent.fill(this.h, this.s, this.b);
            this.parent.textAlign(PConstants.CENTER, PConstants.CENTER);
            this.parent.textSize(this.currentSize);
            this.parent.text(this.current, 0, 0, this.parent.width, this.parent.height);
        }
        this.parent.endDraw();
    }

    private String wrapText(String txt, float maxWidth) { //unused
        String[] words = txt.split(" ");
        String result = "";
        String line = "";
        for (String w : words) {
            if (this.parent.textWidth(line + w) < maxWidth) {
                line += w + " ";
            } else {
                result += line + "\n";
                line = w + " ";
            }
        }
        result += line;
        return result;
    }

    public void nextQuote() {
        this.index++;
        if  (this.index >= this.memory.length) index = 0;
        this.current =  this.memory[this.index];
        this.adjustFontSize();
    }

    public void previousQuote() {
        this.index--;
        if (this.index < 0) this.index = this.memory.length - 1;
        this.current = this.memory[this.index];
        this.adjustFontSize();
    }

    public void status() {
        PApplet.println("visible: " + this.isShowing);
        PApplet.println("base size: " + this.baseSize);
        PApplet.println("current size: " + this.currentSize);
        PApplet.println("position: " + this.position);
        PApplet.println("current: " + this.current);
    }

}
