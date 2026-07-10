#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;

// Três controles independentes (0.0 a 1.0)
uniform float rControl;
uniform float gControl;
uniform float bControl;

void main() {
    vec4 col = texture2D(texture, vertTexCoord.st);

    // Mapeia 0.0 -> 0.0 (desligado), 0.5 -> 1.0 (normal), 1.0 -> 2.5 (ganho/estouro)
    // Isso simula o "Multiplier" do shader original dentro de uma única variável!
    vec3 factor = vec3(
            rControl < 0.5 ? (rControl * 2.0) : (1.0 + (rControl - 0.5) * 3.0),
            gControl < 0.5 ? (gControl * 2.0) : (1.0 + (gControl - 0.5) * 3.0),
            bControl < 0.5 ? (bControl * 2.0) : (1.0 + (bControl - 0.5) * 3.0)
    );

    vec3 finalRGB = vec3(
            col.r * factor.r,
            col.g * factor.g,
            col.b * factor.b
    );

    gl_FragColor = vec4(finalRGB, col.a);
}