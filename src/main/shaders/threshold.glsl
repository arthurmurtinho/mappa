#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float cutoff;
uniform float smoothness;
uniform float invertMode;

void main() {
    vec4 col = texture2D(texture, vertTexCoord.st);

    // 1. Calcula a luminância real do pixel original
    float luma = dot(col.rgb, vec3(0.2126, 0.7152, 0.0722));

    // 2. Define os limites para o cálculo de transição suave
    // smoothness = 0.0 significa um corte seco. Maior que isso abre uma janela de transição.
    float edgeWindow = smoothness * 0.25;
    float lowEdge = cutoff - edgeWindow;
    float highEdge = cutoff + edgeWindow + 0.001; // Evita divisão por zero se smoothness for 0

    // Calcula o fator de threshold com base na suavidade da borda
    float result = smoothstep(lowEdge, highEdge, luma);

    // 3. Aplica o invertMode
    // Se invertMode for 0.0 -> luma alto vira branco (1.0), luma baixo vira preto (0.0)
    // Se invertMode for 1.0 -> inverte completamente o comportamento
    if (invertMode > 0.5) {
        result = 1.0 - result;
    }

    // Renderiza o resultado binarizado preservando o canal alfa original do frame
    gl_FragColor = vec4(vec3(result), col.a);
}