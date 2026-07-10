#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 texOffset; // Usa o offset nativo automático do Processing

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float intensity;
uniform float sobDirection;
uniform float tintMode;

// Função auxiliar para extrair a luminância de um pixel com deslocamento
float getLuma(vec2 uv, vec2 offset) {
    vec3 c = texture2D(texture, uv + offset).rgb;
    return dot(c, vec3(0.2126, 0.7152, 0.0722));
}

void main() {
    vec2 uv = vertTexCoord.st;
    vec2 step = texOffset;

    // Amostragem da matriz 3x3 ao redor do pixel central
    float tleft  = getLuma(uv, vec2(-step.s,  step.t));
    float tomtop = getLuma(uv, vec2(0.0,      step.t));
    float tright = getLuma(uv, vec2( step.s,  step.t));

    float mleft  = getLuma(uv, vec2(-step.s,  0.0));
    float mcenter= getLuma(uv, vec2(0.0,      0.0)); // Pixel original
    float mright = getLuma(uv, vec2( step.s,  0.0));

    float bleft  = getLuma(uv, vec2(-step.s, -step.t));
    float bbottom= getLuma(uv, vec2(0.0,     -step.t));
    float bright = getLuma(uv, vec2( step.s, -step.t));

    // 1. Calcula os Gradientes do Kernel Sobel
    float gx = -1.0 * tleft - 2.0 * mleft - 1.0 * bleft + 1.0 * tright + 2.0 * mright + 1.0 * bright;
    float gy = -1.0 * tleft - 2.0 * tomtop - 1.0 * tright + 1.0 * bleft + 2.0 * bbottom + 1.0 * bright;

    // 2. Controla a influência dos eixos com base no sobDirection
    // 0.0 -> Apenas Eixo X (Bordas Verticais)
    // 0.5 -> Ambos os Eixos (Sobel Completo)
    // 1.0 -> Apenas Eixo Y (Bordas Horizontais)
    float factorX = smoothstep(0.7, 0.3, sobDirection);
    float factorY = smoothstep(0.3, 0.7, sobDirection);

    float edgeMag = sqrt((gx * gx * factorX) + (gy * gy * factorY));

    // Aplica o ganho de intensidade vindo do Java
    edgeMag *= (intensity * 4.0);
    edgeMag = clamp(edgeMag, 0.0, 1.0);

    // 3. Aplica o Modo de Cor (Tint)
    vec4 baseColor = texture2D(texture, uv);
    vec3 edgeWhite = vec3(edgeMag);
    vec3 edgeColored = baseColor.rgb * edgeMag * 2.0; // Estoura a cor original nas bordas

    vec3 finalColor = mix(edgeWhite, edgeColored, tintMode);

    gl_FragColor = vec4(finalColor, baseColor.a);
}