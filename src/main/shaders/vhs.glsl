#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Nossos quatro uniforms vindos do Java
uniform float iGlobalTime;
uniform float tapeNoise;
uniform float distortion;
uniform float chromaticAberration;

// Geradores de ruído pseudo-aleatório analógico
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

void main() {
    vec2 uv = vertTexCoord.st;

    // 1. Distorção de Linha (Ondulação horizontal típica de VHS)
    // Se distortion for 0.0, a ondulação é zerada
    float waveOffset = sin(uv.y * 10.0 + iGlobalTime * 5.0) * cos(uv.y * 30.0 + iGlobalTime * 2.0);
    waveOffset += noise(vec2(uv.y * 100.0, iGlobalTime * 10.0)) * 0.5;
    uv.x += waveOffset * (distortion * 0.015);

    // Barra de tracking (aquela faixa estática que sobe na tela)
    float trackingBar = smoothstep(0.1, 0.0, abs(fract(uv.y - iGlobalTime * 0.2) - 0.5));
    uv.x += sin(uv.y * 100.0) * trackingBar * (distortion * 0.01);

    // 2. Aberração Cromática (Separação RGB nas bordas)
    float shift = chromaticAberration * 0.012;
    vec4 rCol = texture2D(texture, vec2(uv.x + shift, uv.y));
    vec4 gCol = texture2D(texture, uv);
    vec4 bCol = texture2D(texture, vec2(uv.x - shift, uv.y));

    vec3 color = vec3(rCol.r, gCol.g, bCol.b);

    // 3. Adiciona o Grão de Fita Magnética (Tape Noise)
    float grain = hash(uv + vec2(iGlobalTime)) * tapeNoise * 0.25;
    color += vec3(grain);

    // Adiciona linhas de varredura analógica sutis (Scanlines)
    float scanline = sin(uv.y * iResolution.y * 1.5) * 0.04 * tapeNoise;
    color -= vec3(scanline);

    gl_FragColor = vec4(color, gCol.a);
}