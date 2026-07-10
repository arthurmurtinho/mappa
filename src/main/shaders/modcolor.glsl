#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float colorFrequency;
uniform float brightnessGain;
uniform float chromaShift;

void main() {
    vec4 col = texture2D(texture, vertTexCoord.st);

    // Mapeia a frequência do módulo de forma utilizável:
    // Em 0.0, o divisor é grande (imagem quase normal). Em 1.0, o divisor é minúsculo (caos de cores).
    float freq = mix(1.0, 0.1, colorFrequency);

    // Fatores de deslocamento para criar franjas cromáticas baseadas no chromaShift
    vec3 shift = vec3(0.0, chromaShift * 0.05, chromaShift * 0.1);

    // Aplica a operação de módulo de forma independente em cada canal
    vec3 modRGB = vec3(
            mod(col.r + shift.r, freq) / freq,
            mod(col.g + shift.g, freq) / freq,
            mod(col.b + shift.b, freq) / freq
    );

    // Aplica o ganho de brilho para calibração final
    modRGB *= (0.5 + brightnessGain * 1.5);

    // Garante estabilidade nos limites de cor da GPU
    modRGB = clamp(modRGB, 0.0, 1.0);

    gl_FragColor = vec4(modRGB, col.a);
}