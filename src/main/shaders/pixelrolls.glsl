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
uniform float pixelSize;
uniform float speed;
uniform float rollFrequency;
uniform float direction; // Novo controle de direção

void main() {
    vec2 uv = vertTexCoord.st;
    float time = iGlobalTime * speed * 4.0;
    float freq = mix(2.0, 20.0, rollFrequency);

    // Mapeia o uniform 'direction' em pesos para os eixos X e Y
    // direction = 0.0 -> pesoX = 1.0, pesoY = 0.0 (Varredura Horizontal)
    // direction = 0.5 -> pesoX = 0.0, pesoY = 1.0 (Varredura Vertical)
    // direction = 1.0 -> pesoX = 1.0, pesoY = 1.0 (Varredura Cruzada/Diagonal)
    float pesoX = smoothstep(0.6, 0.2, abs(direction - 0.0)) + step(0.8, direction);
    float pesoY = smoothstep(0.1, 0.5, direction);

    // Calcula a oscilação baseada na combinação dos eixos escolhidos
    float waveX = sin(uv.x * freq + time) * pesoX;
    float waveY = sin(uv.y * freq + time) * pesoY;

    // Combina as ondas e normaliza para o intervalo [0.0, 1.0]
    float divisor = max(pesoX + pesoY, 1.0);
    float rollCondition = ((waveX + waveY) / divisor) * 0.5 + 0.5;

    // Define o tamanho do bloco baseado no pixelSize
    float maxPixels = mix(iResolution.x, 16.0, pixelSize);
    vec2 blockUV = floor(uv * maxPixels) / maxPixels;

    // Aplica o efeito de rolamento mesclando as coordenadas
    vec2 targetUV = mix(uv, blockUV, rollCondition * step(0.1, pixelSize));
    targetUV = clamp(targetUV, 0.0, 1.0);

    vec4 col = texture2D(texture, targetUV);

    // Escurece sutilmente as linhas divisórias das faixas de rolamento
    col.rgb *= mix(1.0, 0.85, rollCondition * 0.5 * pixelSize);

    gl_FragColor = vec4(col.rgb, col.a);
}