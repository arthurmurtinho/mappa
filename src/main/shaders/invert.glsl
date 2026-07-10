#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;

// Dois parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float invertIntensity;
uniform float channelLock;

void main() {
    // Amostra o pixel original com seu alfa
    vec4 col = texture2D(texture, vertTexCoord.st);

    // 1. Calcula o negativo puro dos componentes RGB
    vec3 invertedRGB = vec3(1.0) - col.rgb;

    // 2. Cria uma variação baseada no channelLock para mudar o comportamento da inversão
    // Se channelLock for maior que 0.7, inverte preferencialmente canais alternados
    if (channelLock > 0.3 && channelLock <= 0.7) {
        invertedRGB = vec3(invertedRGB.r, col.g, invertedRGB.b); // Preserva o verde original
    } else if (channelLock > 0.7) {
        invertedRGB = vec3(col.r, invertedRGB.g, col.b); // Preserva vermelho e azul originais
    }

    // 3. Aplica a intensidade (mistura entre a cor original e a invertida)
    vec3 finalRGB = mix(col.rgb, invertedRGB, invertIntensity);

    gl_FragColor = vec4(finalRGB, col.a);
}