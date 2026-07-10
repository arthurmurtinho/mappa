#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;
uniform float intensity; // Recebe o valor padronizado do Java

void main() {
    vec2 uv = vertTexCoord.st;

    // Calcula o tamanho de um pixel individual na tela
    vec2 step = vec2(1.0) / iResolution;

    // Força do desfoque baseada no parâmetro de intensidade
    float blurRadius = intensity * 10.0;

    vec4 col = vec4(0.0);
    float totalWeight = 0.0;

    // Amostragem em matriz 5x5 ao redor do pixel atual
    for (float x = -2.0; x <= 2.0; x += 1.0) {
        for (float y = -2.0; y <= 2.0; y += 1.0) {
            // Peso linear simples baseado na distância do centro (box/gaussian approximation)
            float weight = 1.0 / (1.0 + length(vec2(x, y)));

            // Pega o pixel vizinho deslocado
            vec2 offset = vec2(x, y) * step * blurRadius;
            col += texture2D(texture, uv + offset) * weight;

            totalWeight += weight;
        }
    }

    // Normaliza a cor final para não estourar o brilho
    gl_FragColor = col / totalWeight;
}