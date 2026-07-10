#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform sampler2D texture;

// Três parâmetros independentes vindos do Java (0.0 a 1.0)
uniform float brightness;
uniform float contrast;
uniform float saturation;

void main() {
    vec4 col = texture2D(texture, vertTexCoord.st);
    vec3 rgb = col.rgb;

    // 1. Aplica o Brilho (Brightness)
    // Se brightness for 0.5 -> multiplicador é 1.0 (neutro). 0.0 fica preto, 1.0 dobra o brilho.
    rgb *= (brightness * 2.0);

    // 2. Aplica o Contraste (Contrast)
    // O contraste empurra os pixels para longe ou para perto do cinza médio (0.5)
    // Se contrast for 0.5 -> fator é 1.0 (neutro).
    float contrastFactor = (contrast * 2.0);
    rgb = (rgb - 0.5) * contrastFactor + 0.5;

    // 3. Aplica a Saturação (Saturation)
    // Coeficientes de luminância padrão (Luma) para calcular a escala de cinza real
    float luma = dot(rgb, vec3(0.2126, 0.7152, 0.0722));
    vec3 grayscale = vec3(luma);

    // Se saturation for 0.5 -> mix é 0.5, resultando na cor original.
    // Mapeamos para que 0.0 seja totalmente preto e branco e 1.0 seja cores super vivas.
    float satFactor = saturation * 2.0;
    rgb = mix(grayscale, rgb, satFactor);

    // Garante que os valores de cor não saiam dos limites da GPU (0.0 a 1.0)
    rgb = clamp(rgb, 0.0, 1.0);

    gl_FragColor = vec4(rgb, col.a);
}