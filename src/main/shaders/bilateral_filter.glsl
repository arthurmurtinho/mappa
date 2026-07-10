#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Parâmetros dinâmicos que receberemos do Java (0.0 a 1.0)
uniform float sigmaSpatial;
uniform float sigmaColor;

// Funções de distribuição gaussiana normalizada
float normpdf(in float x, in float sigma) {
    return 0.39894 * exp(-0.5 * x * x / (sigma * sigma)) / sigma;
}

float normpdf3(in vec3 v, in float sigma) {
    return 0.39894 * exp(-0.5 * dot(v, v) / (sigma * sigma)) / sigma;
}

void main(void) {
    vec2 uv = vertTexCoord.st;
    vec3 centerColor = texture2D(texture, uv).rgb;

    vec2 step = vec2(1.0) / iResolution;

    vec3 final_colour = vec3(0.0);
    float Z = 0.0;

    // Matriz de amostragem fixa 9x9 (Segura para performance a 60FPS)
    // O raio da janela varia de -4 a +4
    for (int i = -4; i <= 4; ++i) {
        for (int j = -4; j <= 4; ++j) {

            // Coordenada do pixel vizinho
            vec2 offset = vec2(float(i), float(j)) * step;
            vec3 neighborColor = texture2D(texture, uv + offset).rgb;

            // 1. Fator Espacial: Proximidade geométrica dos pixels
            float factorSpatial = normpdf(length(vec2(float(i), float(j))), sigmaSpatial);

            // 2. Fator de Cor: Proximidade de similaridade cromática (preserva bordas)
            float factorColor = normpdf3(neighborColor - centerColor, sigmaColor);

            float weight = factorSpatial * factorColor;

            final_colour += neighborColor * weight;
            Z += weight;
        }
    }

    // Normaliza para manter o brilho original da imagem
    gl_FragColor = vec4(final_colour / Z, 1.0);
}