#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;
uniform vec2 iResolution;

// Múltiplos parâmetros vindos do Java (todos de 0.0 a 1.0)
uniform float threshold;
uniform float intensity;
uniform float radius;

void main() {
    vec2 uv = vertTexCoord.st;
    vec2 step = vec2(1.0) / iResolution;

    vec4 baseColor = texture2D(texture, uv);

    // 1. Extrai a luminância do pixel original (conversão padrão para escala de cinza)
    float luminance = dot(baseColor.rgb, vec3(0.2126, 0.7152, 0.0722));

    // Mapeia o threshold invertido: se o threshold do Java for 0.0, quase tudo brilha. Se for 1.0, só o que for muito branco brilha.
    float mappedThreshold = mix(0.2, 0.95, threshold);

    vec3 bloom = vec3(0.0);
    float totalWeight = 0.0;

    // 2. Desfoque Gaussiano rápido (Matriz 5x5 em cruz com espaçamento dinâmico baseado no radius)
    float blurSpread = radius * 6.0;

    for (int i = -3; i <= 3; i++) {
        for (int j = -3; j <= 3; j++) {
            vec2 offset = vec2(float(i), float(j)) * step * blurSpread;
            vec4 sampleCol = texture2D(texture, uv + offset);

            // Verifica se o pixel vizinho passa no teste de brilho HDR
            float sampleLuminance = dot(sampleCol.rgb, vec3(0.2126, 0.7152, 0.0722));
            if (sampleLuminance > mappedThreshold) {
                float weight = 1.0 / (1.0 + length(vec2(float(i), float(j))));
                bloom += sampleCol.rgb * weight;
                totalWeight += weight;
            }
        }
    }

    if (totalWeight > 0.0) {
        bloom = (bloom / totalWeight) * (intensity * 3.0);
    }

    // 3. Mistura (Blend) estilo HDR: adiciona o bloom e garante que a cor não estoure bizarramente
    vec3 finalColor = baseColor.rgb + bloom;

    // Mapeamento de tom (Tone Mapping) simples para dar o visual HDR e evitar clipar cores puras
    finalColor = vec3(1.0) - exp(-finalColor * 1.2);

    gl_FragColor = vec4(finalColor, baseColor.a);
}