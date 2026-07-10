#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float pixelSize;
uniform float aspectRatio;
uniform float colorDepth;

void main() {
    vec2 uv = vertTexCoord.st;

    // 1. Calcula o tamanho do bloco (frequência)
    // Se pixelSize for 0.0, definimos uma frequência altíssima (bypassa o efeito)
    float maxPixels = mix(iResolution.x, 8.0, pixelSize);

    // 2. Modifica a proporção do pixel baseado no aspectRatio
    // 0.5 = Quadrado perfeito. Menor que 0.5 = estica horizontal, Maior = estica vertical.
    vec2 aspectAdjust = vec2(1.0);
    if (aspectRatio < 0.5) {
        aspectAdjust.x = mix(0.1, 1.0, aspectRatio * 2.0);
    } else if (aspectRatio > 0.5) {
        aspectAdjust.y = mix(1.0, 0.1, (aspectRatio - 0.5) * 2.0);
    }

    vec2 size = vec2(maxPixels) * aspectAdjust;

    // Achata as coordenadas UV para criar o efeito mosaico de pixel
    vec2 blockUV = floor(uv * size) / size;
    blockUV = clamp(blockUV, 0.0, 1.0);

    // Amostra a cor do pixel central do bloco
    vec4 col = texture2D(texture, blockUV);

    // 3. Redução de Profundidade de Cor (Color Depth / Posterização)
    // Se colorDepth for 1.0, mantém original. Se for decrescendo, vai limitando os tons.
    if (colorDepth < 1.0) {
        float steps = mix(2.0, 32.0, colorDepth);
        col.r = floor(col.r * steps) / steps;
        col.g = floor(col.g * steps) / steps;
        col.b = floor(col.b * steps) / steps;
    }

    gl_FragColor = vec4(col.rgb, col.a);
}