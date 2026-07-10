#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

// Constantes de conversão RGB para YIQ (conforme o seu PDF)
const vec4 KRGBToYPrime = vec4(0.299, 0.587, 0.114, 0.0);
const vec4 KRGBToI      = vec4(0.596, -0.275, -0.321, 0.0);
const vec4 KRGBToQ      = vec4(0.212, -0.523, 0.311, 0.0);

const vec4 KYIQToR      = vec4(1.0, 0.956, 0.621, 0.0);
const vec4 KYIQToG      = vec4(1.0, -0.272, -0.647, 0.0);
const vec4 KYIQToB      = vec4(1.0, 1.107, 1.704, 0.0);

varying vec4 vertTexCoord;
uniform sampler2D texture;

// Nossos parâmetros independentes (0.0 a 1.0)
uniform float hueRotation;
uniform float mixOriginal;

void main() {
    // Amostra o pixel original
    vec4 color = texture2D(texture, vertTexCoord.st);

    // 1. Converte o RGB original para o espaço YIQ
    float yPrime = dot(color, KRGBToYPrime);
    float i      = dot(color, KRGBToI);
    float q      = dot(color, KRGBToQ);

    // 2. Calcula o ângulo de rotação em radianos baseado no uniform do Java
    float hue = hueRotation * 6.28318530; // 2 * PI (0.0 a 1.0 mapeia 0 a 360 graus)

    // Rotaciona os componentes cromáticos I e Q
    float sinHue = sin(hue);
    float cosHue = cos(hue);

    float rI = (i * cosHue) - (q * sinHue);
    float rQ = (i * sinHue) + (q * cosHue);

    // 3. Reconstrói o RGB a partir do YIQ rotacionado
    vec4 yIQ = vec4(yPrime, rI, rQ, 0.0);
    vec3 shiftedColor;
    shiftedColor.r = dot(yIQ, KYIQToR);
    shiftedColor.g = dot(yIQ, KYIQToG);
    shiftedColor.b = dot(yIQ, KYIQToB);

    // Garante estabilidade nos limites de cor da GPU
    shiftedColor = clamp(shiftedColor, 0.0, 1.0);

    // Mistura com a cor original baseado no slider de intensidade do usuário
    vec3 finalColor = mix(color.rgb, shiftedColor, mixOriginal);

    gl_FragColor = vec4(finalColor, color.a);
}