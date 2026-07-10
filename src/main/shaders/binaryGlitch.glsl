#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Parâmetros que vêm do Java
uniform float iGlobalTime; // Para fazer o glitch mover-se sozinho
uniform float intensity;   // O nosso slider universal (0.0 a 1.0)

// Função matemática para gerar ruído analógico sem precisar de texturas externas
float hash(vec2 p) {
    float h = dot(p, vec2(127.1, 311.7));
    return fract(sin(h) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i + vec2(0.0, 0.0)), hash(i + vec2(1.0, 0.0)), u.x),
            mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

void main() {
    vec2 uv = vertTexCoord.st;

    // Se a intensidade for 0, desenha a imagem limpa e sai do shader imediatamente
    if (intensity <= 0.0) {
        gl_FragColor = texture2D(texture, uv);
        return;
    }

    // Configura a escala do glitch baseada na nossa intensidade
    float glitchBlockScale = 15.0;
    float timeScale = iGlobalTime * 2.0;

    // Gera linhas horizontais de distorção baseadas em ruído
    float lineNoise = noise(vec2(uv.y * glitchBlockScale, timeScale));

    // Cria um limite de corte (threshold). Quanto maior a intensidade, mais blocos falham
    float threshold = 1.0 - (intensity * 0.6);

    if (lineNoise > threshold) {
        // Aplica o deslocamento horizontal (Glitch)
        float displacement = (hash(vec2(floor(uv.y * glitchBlockScale), timeScale)) - 0.5) * (intensity * 0.15);
        uv.x = fract(uv.x + displacement);

        // Pequena aberração cromática opcional nas partes afetadas para dar mais realismo
        vec4 rCol = texture2D(texture, vec2(uv.x + 0.01 * intensity, uv.y));
        vec4 gCol = texture2D(texture, uv);
        vec4 bCol = texture2D(texture, vec2(uv.x - 0.01 * intensity, uv.y));

        gl_FragColor = vec4(rCol.r, gCol.g, bCol.b, 1.0);
    } else {
        // Desenha o pixel normal sem falha
        gl_FragColor = texture2D(texture, uv);
    }
}