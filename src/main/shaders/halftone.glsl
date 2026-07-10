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
uniform float angles;
uniform float mixBackground;

void main() {
    vec2 uv = vertTexCoord.st;

    // 1. Define a frequência da grade baseado no tamanho do ponto
    // Mapeia 0.0 -> pontos pequenininhos (alta freq), 1.0 -> pontos gigantes (baixa freq)
    float frequency = mix(120.0, 15.0, dotSize);

    // 2. Calcula a rotação da malha (ângulo em radianos)
    float angleRad = angles * 3.14159265; // Vai de 0 a 180 graus
    mat2 rotationMatrix = mat2(cos(angleRad), -sin(angleRad), sin(angleRad), cos(angleRad));

    // Transforma a coordenada UV para o espaço rotacionado e escalado da grade
    vec2 st = uv * iResolution / iResolution.y;
    vec2 rotatedSt = rotationMatrix * (st * frequency);

    // Isola o centro de cada célula da grade de pontos
    vec2 frame = fract(rotatedSt) - 0.5;

    // Desfaz a rotação para pegar a amostragem de cor correta do frame original
    mat2 invRotationMatrix = mat2(cos(-angleRad), -sin(-angleRad), sin(-angleRad), cos(-angleRad));
    vec2 sampleUV = (invRotationMatrix * floor(rotatedSt)) / frequency;
    sampleUV.x *= iResolution.y / iResolution.x;
    sampleUV = clamp(sampleUV, 0.0, 1.0);

    // 3. Pega a cor e calcula o brilho (luminância) do centro da célula
    vec4 baseColor = texture2D(texture, sampleUV);
    float luma = dot(baseColor.rgb, vec3(0.2126, 0.7152, 0.0722));

    // O raio do círculo é proporcional ao brilho do pixel naquela região
    float circleRadius = luma * 0.65;

    // Desenha o círculo com anti-aliasing suave na borda
    float dist = length(frame);
    float alpha = smoothstep(circleRadius, circleRadius - 0.05, dist);

    // Mistura de fundo:
    // mixBackground = 0.0 -> Círculos brancos sobre fundo preto (P&B clássico)
    // mixBackground = 1.0 -> Círculos coloridos com a cor original do frame
    vec3 dotColor = mix(vec3(1.0), baseColor.rgb, mixBackground);
    vec3 finalColor = mix(vec3(0.0), dotColor, alpha);

    gl_FragColor = vec4(finalColor, baseColor.a);
}