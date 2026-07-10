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
uniform float glitchIntensity;
uniform float noiseSuck;
uniform float tubeCurvature;

// Substituição da textura de ruído por uma função pseudo-aleatória matemática estável
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p) {
    // Simula a leitura caótica temporal baseada no iGlobalTime
    float s = hash(p + vec2(1.0, 2.0 * cos(iGlobalTime)) * iGlobalTime * 8.0);
    s *= s;
    return s;
}

float onOff(float a, float b, float c) {
    // Amarra a probabilidade do pulo à intensidade do glitch do Java
    return step(mix(0.95, c, glitchIntensity), sin(iGlobalTime + a * cos(iGlobalTime * b)));
}

float ramp(float y, float start, float end) {
    float inside = step(start, y) - step(end, y);
    float fact = (y - start) / (end - start) * inside;
    return (1.0 - fact) * inside;
}

float stripes(vec2 uv) {
    float noi = noise(uv * vec2(0.5, 1.0) + vec2(1.0, 3.0));
    return ramp(mod(uv.y * 4.0 + iGlobalTime / 2.0 + sin(iGlobalTime + sin(iGlobalTime * 0.63)), 1.0), 0.5, 0.6) * noi;
}

vec3 getVideo(vec2 uv) {
    vec2 look = uv;
    float window = 1.0 / (1.0 + 20.0 * (look.y - mod(iGlobalTime / 4.0, 1.0)) * (look.y - mod(iGlobalTime / 4.0, 1.0)));

    // Distorção horizontal rasgando a tela baseada no glitchIntensity
    look.x = look.x + sin(look.y * 10.0 + iGlobalTime) / 50.0 * onOff(4.0, 4.0, 0.3) * (1.0 + cos(iGlobalTime * 80.0)) * window * glitchIntensity;

    // Deslocamento vertical (pulo de tracking) baseado no glitchIntensity
    float vShift = 0.4 * onOff(2.0, 3.0, 0.9) * (sin(iGlobalTime) * sin(iGlobalTime * 20.0) + (0.5 + 0.1 * sin(iGlobalTime * 200.0) * cos(iGlobalTime))) * glitchIntensity;
    look.y = mod(look.y + vShift, 1.0);

    return texture2D(texture, look).rgb;
}

vec2 screenDistort(vec2 uv) {
    uv -= vec2(0.5, 0.5);
    // Controla dinamicamente a curvatura esférica CRT baseado no uniform do Java
    float distortFactor = mix(0.0, 2.0, tubeCurvature);
    uv = uv * 1.2 * (1.0 / 1.2 + distortFactor * uv.x * uv.x * uv.y * uv.y);
    uv += vec2(0.5, 0.5);
    return uv;
}

void main() {
    vec2 uv = vertTexCoord.st;

    // 1. Aplica distorção geométrica de tubo
    uv = screenDistort(uv);

    vec3 video = vec3(0.0);

    // Garante estabilidade nas bordas da tela se curvar demais
    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
        video = vec3(0.0);
    } else {
        // 2. Extrai o sinal de vídeo com glitches
        video = getVideo(uv);

        // 3. Aplica os efeitos de vinheta e scanlines clássicos do ShaderToy original
        float vigAmt = 3.0 + 0.3 * sin(iGlobalTime + 5.0 * cos(iGlobalTime * 5.0));
        float vignette = (1.0 - vigAmt * (uv.y - 0.5) * (uv.y - 0.5)) * (1.0 - vigAmt * (uv.x - 0.5) * (uv.x - 0.5));

        // Adiciona as barras estáticas escaladas pelo noiseSuck
        video += stripes(uv) * noiseSuck * 1.5;
        video += (noise(uv * 2.0) / 2.0) * noiseSuck;
        video *= vignette;

        // Scanlines verticais dinâmicas
        video *= (12.0 + mod(uv.y * 30.0 + iGlobalTime, 1.0)) / 13.0;
    }

    // Amostra o alfa original para manter transparências de superfícies
    float alpha = texture2D(texture, vertTexCoord.st).a;
    gl_FragColor = vec4(video, alpha);
}