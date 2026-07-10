#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform vec2 pos;
uniform float radius;
uniform float feather;

void main() {
    vec2 uv = vertTexCoord.st;

    // Amostra a cor original do frame
    vec4 col = texture2D(texture, uv);

    // Corrige a proporção do aspecto para o feixe da lanterna ser um círculo perfeito
    vec2 aspect = vec2(iResolution.x / iResolution.y, 1.0);
    vec2 lanternPos = pos * aspect;
    vec2 pixelPos = uv * aspect;

    // Calcula a distância do pixel atual até o centro da lanterna
    float dist = length(pixelPos - lanternPos);

    // Mapeia o raio de forma esteticamente útil
    float r = mix(0.0, 1.2, radius);

    // Calcula a janela de suavização baseada no feather
    // Se feather for 0.0, highEdge fica colado em r (corte seco).
    float width = mix(0.001, 0.4, feather);
    float lowEdge = r - width;
    float highEdge = r;

    // smoothstep invertido: 1.0 dentro do raio (revelado), 0.0 fora (escuridão)
    float mask = 1.0 - smoothstep(lowEdge, highEdge, dist);

    // Aplica a máscara sobre a cor preservando o alfa original da superfície
    vec3 finalRGB = col.rgb * mask;

    gl_FragColor = vec4(finalRGB, col.a);
}