#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float wrapFrequency;
uniform float offset;
uniform float mirrorMode;

void main() {
    vec2 uv = vertTexCoord.st;

    // 1. Calcula a escala/frequência do envelopamento (de 1 a 8 repetições)
    float freq = mix(1.0, 8.0, wrapFrequency);

    // Aplica a escala centralizada e adiciona o deslocamento (offset)
    vec2 wrappedUV = (uv - 0.5) * freq + vec2(offset * freq);

    // Torna a coordenada cíclica usando fract (0.0 a 1.0 repetidamente)
    vec2 grid = fract(wrappedUV);

    // 2. Aplica o Modo Espelho (Mirror) baseado no mirrorMode
    // Se a célula atual da grade for ímpar, inverte a coordenada para espelhar
    vec2 cell = floor(wrappedUV);
    vec2 mirrorUV = vec2(
            mod(cell.x, 2.0) == 0.0 ? grid.x : 1.0 - grid.x,
            mod(cell.y, 2.0) == 0.0 ? grid.y : 1.0 - grid.y
    );

    // Transiciona suavemente entre o modo mosaico puro e o modo espelhado
    vec2 finalUV = mix(grid, mirrorUV, mirrorMode);

    // Garante segurança matemática nas bordas da textura
    finalUV = clamp(finalUV, 0.0, 1.0);

    gl_FragColor = texture2D(texture, finalUV);
}