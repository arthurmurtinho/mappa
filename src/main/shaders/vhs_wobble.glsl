#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float iGlobalTime;
uniform float wobbleForce;
uniform float noiseJitter;
uniform float colorSplit;

const float range = 0.05;
const float noiseQuality = 250.0;

float rand(vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

float verticalBar(float pos, float uvY, float offset) {
    float edge0 = (pos - range);
    float edge1 = (pos + range);
    float x = smoothstep(edge0, pos, uvY) * offset;
    x -= smoothstep(pos, edge1, uvY) * offset;
    return x;
}

void main() {
    vec2 uv = vertTexCoord.st;

    // 1. Aplica o ondulamento (Wobble) baseado no wobbleForce
    float offsetIntensity = 0.02 * wobbleForce;
    for (float i = 0.0; i < 0.71; i += 0.1313) {
        float d = mod(iGlobalTime * i, 1.7);
        float o = sin(1.0 - tan(iGlobalTime * 0.24 * i));
        o *= offsetIntensity;
        uv.x += verticalBar(d, uv.y, o);
    }

    // 2. Aplica a trepidação de linha (Jitter) baseado no noiseJitter
    float uvY = uv.y;
    uvY *= noiseQuality;
    uvY = float(int(uvY)) * (1.0 / noiseQuality);
    // Alterado o multiplicador temporal para o ruído não ficar lento demais no Processing
    float noiseVal = rand(vec2(iGlobalTime * 0.1, uvY));
    uv.x += noiseVal * (0.015 * noiseJitter);

    // 3. Aplica o deslocamento cromático flutuante baseado no colorSplit
    float factor = colorSplit * 1.5;
    vec2 offsetR = vec2(0.006 * sin(iGlobalTime), 0.0) * factor;
    vec2 offsetG = vec2(0.0073 * (cos(iGlobalTime * 0.97)), 0.0) * factor;

    // Amostra os canais de forma independente
    float r = texture2D(texture, uv + offsetR).r;
    float g = texture2D(texture, uv + offsetG).g;
    float b = texture2D(texture, uv).b;

    // Pega o alfa original do pixel central para preservar recortes de vídeo
    float alpha = texture2D(texture, vertTexCoord.st).a;

    gl_FragColor = vec4(r, g, b, alpha);
}