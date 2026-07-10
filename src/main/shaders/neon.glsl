#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Dois controles independentes vindo do Java (ambos padronizados de 0.0 a 1.0)
uniform float brightness;
uniform float glowRadius;

void main() {
    vec2 uv = vertTexCoord.st;
    vec2 step = vec2(1.0) / iResolution;

    vec3 centerCol = texture2D(texture, uv).rgb;
    vec3 sum = vec3(0.0);

    // Mapeia o raio para determinar o espaçamento do borrão (espalhamento do glow)
    float sampleDist = glowRadius * 5.0;

    // Amostragem fixa em cruz (rápida para tempo real) para gerar o efeito de borrão/glow
    for (int i = -4; i <= 4; i++) {
        sum += texture2D(texture, uv + vec2(float(i), 0.0) * step * sampleDist).rgb;
        sum += texture2D(texture, uv + vec2(0.0, float(i)) * step * sampleDist).rgb;
    }

    // Média da amostragem multiplicada pelo fator de brilho escolhido pelo usuário
    vec3 glow = (sum / 18.0) * (brightness * 2.5);

    // Soma o contorno brilhante (glow) em cima da imagem original
    gl_FragColor = vec4(centerCol + glow, 1.0);
}