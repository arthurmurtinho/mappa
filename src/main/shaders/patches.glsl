#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float patchCount;
uniform float distortion;
uniform float timeSync;
uniform float iGlobalTime; // Alimentado automaticamente se timeSync > 0

// Função geradora de ruído pseudo-aleatório rápida para quebrar os blocos
float hash2D(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

void main() {
    vec2 uv = vertTexCoord.st;

    // 1. Determina a escala da grade de remendos
    // Mapeia 0.0 -> blocos grandes (frequência 10), 1.0 -> micro blocos (frequência 150)
    float gridFreq = mix(10.0, 150.0, patchCount);

    // Calcula a proporção da tela para manter os blocos perfeitamente quadrados
    vec2 aspect = vec2(iResolution.x / iResolution.y, 1.0);
    vec2 blockCoord = floor(uv * aspect * gridFreq);

    // 2. Adiciona o componente temporal se o usuário quiser animação
    float timeOffset = timeSync > 0.0 ? floor(iGlobalTime * mix(2.0, 15.0, timeSync)) : 0.0;

    // Gera um valor aleatório único para cada bloco baseado na sua posição e no tempo
    float blockNoise = hash2D(blockCoord + vec2(timeOffset));

    // 3. Calcula o deslocamento UV interno (Distorção/Refração)
    // Se distortion for 0.0, não há deslocamento de pixel dentro do bloco
    vec2 uvOffset = vec2(blockNoise - 0.5) * (distortion * 0.08);

    vec2 targetUV = uv + uvOffset;
    targetUV = clamp(targetUV, 0.0, 1.0); // Previne estouro de borda na GPU

    // Amostra a cor final
    vec4 baseColor = texture2D(texture, targetUV);

    // Opcional: Adiciona uma variação sutil de brilho por bloco para marcar as quinas
    baseColor.rgb *= (0.9 + 0.2 * blockNoise * distortion);

    gl_FragColor = vec4(baseColor.rgb, baseColor.a);
}