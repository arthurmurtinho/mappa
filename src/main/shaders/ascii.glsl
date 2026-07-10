#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
varying vec4 vertTexCoord;

uniform vec2 iResolution;
uniform sampler2D texture;
uniform float pixelScale; // Agora ele vai ser usado e não será removido!

#define iChannel0 texture

float character(float n, vec2 p)
{
    p = floor(p * vec2(4.0, -4.0) + 2.5);
    if (clamp(p.x, 0.0, 4.0) == p.x && clamp(p.y, 0.0, 4.0) == p.y)
    {
        float power = exp2(p.x + 5.0 * p.y);
        if (mod(floor(n / power), 2.0) == 1.0) return 1.0;
    }
    return 0.0;
}

void main() {
    vec2 uv = vertTexCoord.st * iResolution.xy;

    // USANDO O PIXELSCALE: Substituímos o 8.0 fixo pela variável controlada pelo Java
    vec3 col = texture2D(iChannel0, floor(uv / pixelScale) * pixelScale / iResolution.xy).rgb;

    float gray = 0.3 * col.r + 0.59 * col.g + 0.11 * col.b;

    float n = 4096.0;               // .
    if (gray > 0.2) n = 65600.0;    // :
    if (gray > 0.3) n = 332772.0;   // *
    if (gray > 0.4) n = 15255086.0; // o
    if (gray > 0.5) n = 23385164.0; // &
    if (gray > 0.6) n = 15252014.0; // 8
    if (gray > 0.7) n = 13199452.0; // @
    if (gray > 0.8) n = 11512810.0; // #

    // USANDO O PIXELSCALE AQUI TAMBÉM: Ajusta a proporção da matriz do caractere
    vec2 p = mod(uv / (pixelScale / 2.0), 2.0) - vec2(1.0);

    col = col * character(n, p);
    gl_FragColor = vec4(col, 1.0);
}