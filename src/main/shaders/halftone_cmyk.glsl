#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float dotSize;
uniform float moireShift;
uniform float mixBackground;

// Função interna para calcular a intensidade do ponto em uma malha rotacionada
float getGridPoint(vec2 uv, float angle, float frequency, float channelValue) {
    mat2 rotation = mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
    vec2 st = (uv * iResolution / iResolution.y) * frequency;
    vec2 rotatedSt = rotation * st;
    vec2 frame = fract(rotatedSt) - 0.5;

    // O raio do ponto depende do valor de intensidade do canal de cor
    float radius = channelValue * 0.68;
    return smoothstep(radius, radius - 0.05, length(frame));
}

void main() {
    vec2 uv = vertTexCoord.st;
    vec4 baseColor = texture2D(texture, uv);

    // 1. Conversão matemática de RGB para CMYK purista
    float r = baseColor.r;
    float g = baseColor.g;
    float b = baseColor.b;

    float k = 1.0 - max(max(r, g), b);
    // Evita divisão por zero se a imagem for totalmente preta
    float c = (k == 1.0) ? 0.0 : (1.0 - r - k) / (1.0 - k);
    float m = (k == 1.0) ? 0.0 : (1.0 - g - k) / (1.0 - k);
    float y = (k == 1.0) ? 0.0 : (1.0 - b - k) / (1.0 - k);

    // 2. Define a frequência (densidade dos pontos)
    float frequency = mix(100.0, 18.0, dotSize);

    // 3. Ângulos clássicos da indústria CMYK + deslocamento dinâmico de Moiré do usuário
    float shiftRad = moireShift * 0.5; // Deslocamento sutil para criar o padrão psicodélico
    float angleC = 0.2617 + shiftRad;  // 15 graus
    float angleM = 1.3089 - shiftRad;  // 75 graus
    float angleY = 0.0000 + shiftRad;  // 0 graus
    float angleK = 0.7853;             // 45 graus (fixo como âncora)

    // Extrai as quatro retículas independentes na GPU
    float dotC = getGridPoint(uv, angleC, frequency, c);
    float dotM = getGridPoint(uv, angleM, frequency, m);
    float dotY = getGridPoint(uv, angleY, frequency, y);
    float dotK = getGridPoint(uv, angleK, frequency, k);

    // 4. Reconstrói a imagem misturando os pontos sobre um fundo branco (simulando papel)
    vec3 cmykComposite = vec3(1.0);
    cmykComposite -= vec3(0.0, 1.0, 1.0) * dotC; // Subtrai Ciano
    cmykComposite -= vec3(1.0, 0.0, 1.0) * dotM; // Subtrai Magenta
    cmykComposite -= vec3(1.0, 1.0, 0.0) * dotY; // Subtrai Amarelo
    cmykComposite -= vec3(1.0, 1.0, 1.0) * dotK; // Subtrai Preto (Key)
    cmykComposite = clamp(cmykComposite, 0.0, 1.0);

    // Mistura de fundo: 0.0 = visual de jornal impresso em papel branco, 1.0 = mesclado com a opacidade da imagem
    vec3 finalColor = mix(cmykComposite, baseColor.rgb * cmykComposite * 1.5, mixBackground);

    gl_FragColor = vec4(finalColor, baseColor.a);
}