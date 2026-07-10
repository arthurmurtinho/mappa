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
uniform float intensity;
uniform float speed;
uniform float scale;

void main() {
    // Transforma a coordenada UV de (0 a 1) para (-1 a 1) centralizada
    vec2 p = -1.0 + 2.0 * vertTexCoord.st;

    // Calcula a distância ao centro (r) e o ângulo polar (a)
    float r = length(p);
    float a = atan(p.y, p.x);

    // Alimenta o tempo multiplicado pela velocidade escolhida pelo usuário
    float time = iGlobalTime * speed * 3.0;

    // Frequência das ondas baseada no parâmetro de escala
    float freq = mix(1.0, 12.0, scale);

    // A mágica da deformação: distorce o raio (r) e o ângulo (a) matematicamente
    // Se intensidade for 0.0, não distorce nada.
    float dr = r + (sin(r * freq - time) * 0.1 * intensity);
    float da = a + (cos(r * freq + time) * 0.2 * intensity);

    // Reconstrói as coordenadas UV com base no novo raio e ângulo deformados
    vec2 uv;
    uv.x = 0.5 + 0.5 * dr * cos(da);
    uv.y = 0.5 + 0.5 * dr * sin(da);

    // Garante que as novas coordenadas fiquem presas dentro do limite da textura (evita repetições feias)
    uv = clamp(uv, 0.0, 1.0);

    gl_FragColor = texture2D(texture, uv);
}