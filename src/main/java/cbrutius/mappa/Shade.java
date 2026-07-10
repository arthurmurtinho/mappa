package cbrutius.mappa;
import processing.core.PApplet;
import processing.opengl.PShader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Shade extends Processor{
    PShader shader;
    boolean loaded = false;
    String home = System.getProperty("user.home");
    String libPath = this.home + "/Documents/Processing/libraries/mappa/library/shaders";

    public Shade (PApplet p) {
        super(p);
        shader = new PShader(p);
    }

    /**
     * Aplica o efeito de texto ASCII sobre a superfície
     * @param res Valor entre 0.0 (caracteres bem pequenos/alta definição) e 1.0 (caracteres gigantes/super pixelado)
     */
    public void ascii(float res) {
        if(!loaded){
            // Corrigido para carregar o ascii.glsl em vez do blur
            reload(libPath + "/ascii.glsl");
            PApplet.println("ASCII Shader loaded!");
        }
        parent.beginDraw();

        // 1. iResolution precisa de LARGURA e ALTURA (pois é vec2 no GLSL)
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // 2. Mapeia o 'res' vindo do seu slider (0.0 a 1.0) para tamanhos de caractere úteis (de 4 a 24 pixels)
        // Se res for 0.0, a fonte fica pequena (4px). Se for 1.0, fica gigante (24px).
        float characterSize = PApplet.map(res, 0.0f, 1.0f, 4.0f, 24.0f);
        shader.set("pixelScale", characterSize);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o efeito de Desfoque Gaussiano (Blur) sobre a superfície.
     * @param intensity Valor entre 0.0 (sem desfoque) e 1.0 (desfoque máximo)
     */
    public void blur(float intensity) {
        if (!loaded) {
            reload(libPath + "/blur.glsl");
            PApplet.println("Blur Shader loaded!");
        }
        parent.beginDraw();
        // Passa a resolução para o vec2 do shader calcular as coordenadas dos pixels vizinhos
        shader.set("iResolution", (float) parent.width, (float) parent.height);
        // Garante que o valor fique estritamente entre 0.0 e 1.0 para proteger a GPU
        float clampedIntensity = PApplet.constrain(intensity, 0.0f, 1.0f);
        shader.set("intensity", clampedIntensity);
        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o filtro Bilateral (Suavização de superfícies preservando bordas/estilo cartoon)
     * @param intensity Valor entre 0.0 (imagem original limpa) e 1.0 (suavização máxima)
     */
    public void bilateral(float intensity) {
        if (!loaded) {
            reload(libPath + "/bilateral_filter.glsl");
            PApplet.println("Bilateral Filter Shader loaded!");
        }

        parent.beginDraw();

        // Trava o valor entre 0 e 1 por segurança
        float clamped = PApplet.constrain(intensity, 0.0f, 1.0f);

        // Passa a resolução do PGraphics para o vec2 do shader
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Mapeamento dos Sigmas baseado na intensidade:
        // Sigma espacial ideal varia de 1.0 (sutil) a 5.0 (espalhado)
        float sSpatial = PApplet.map(clamped, 0.0f, 1.0f, 1.0f, 5.0f);
        // Sigma de cor ideal varia de 0.05 (rígido) a 0.3 (suave)
        float sColor = PApplet.map(clamped, 0.0f, 1.0f, 0.05f, 0.3f);

        shader.set("sigmaSpatial", sSpatial);
        shader.set("sigmaColor", sColor);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o efeito de Glitch Digital / Sinal Corrompido por blocos.
     * @param intensity Valor entre 0.0 (sinal limpo perfeito) e 1.0 (sinal severamente corrompido)
     */
    public void binaryGlitch(float intensity) {
        if (!loaded) {
            reload(libPath + "/binaryGlitch.glsl");
            PApplet.println("Binary Glitch Shader loaded!");
        }

        parent.beginDraw();

        // Força o valor a respeitar os limites seguros de 0.0 a 1.0
        float clamped = PApplet.constrain(intensity, 0.0f, 1.0f);

        // Envia os segundos passados para criar a animação contínua
        shader.set("iGlobalTime", this.p.millis() / 1000.0f);

        // Envia o nosso parâmetro normalizado
        shader.set("intensity", clamped);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o efeito de Bloom HDR (Aura de luz em áreas de alto brilho).
     * @param threshold Ponto de corte do brilho (0.0 = tudo brilha, 1.0 = apenas luzes extremas brilham)
     * @param intensity Força/Opacidade do brilho espalhado (0.0 a 1.0)
     * @param radius Raio de expansão da aura de luz (0.0 a 1.0)
     */
    public void bloomHDR(float threshold, float intensity, float radius) {
        if (!loaded) {
            reload(libPath + "/bloomHDR.glsl");
            PApplet.println("Bloom HDR Shader loaded!");
        }

        parent.beginDraw();

        // Garante as escalas normatizadas seguras
        float cThreshold = PApplet.constrain(threshold, 0.0f, 1.0f);
        float cIntensity = PApplet.constrain(intensity, 0.0f, 1.0f);
        float cRadius = PApplet.constrain(radius, 0.0f, 1.0f);

        // Passa a resolução necessária para o cálculo de pixels vizinhos
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Envia os múltiplos parâmetros configurados
        shader.set("threshold", cThreshold);
        shader.set("intensity", cIntensity);
        shader.set("radius", cRadius);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Altera o Brilho, Contraste e Saturação da superfície projetada.
     * @param brightness Brilho da imagem (0.0 = Preto, 0.5 = Neutro, 1.0 = Dobro de Brilho)
     * @param contrast Contraste da imagem (0.0 = Cinza plano, 0.5 = Neutro, 1.0 = Alto Contraste)
     * @param saturation Saturação da cor (0.0 = Preto e Branco, 0.5 = Neutro, 1.0 = Cores Intensas)
     */
    public void brcosa(float brightness, float contrast, float saturation) {
        if (!loaded) {
            reload(libPath + "/brcosa.glsl");
            PApplet.println("Brcosa Shader loaded!");
        }

        parent.beginDraw();

        // Força os parâmetros a respeitarem a escala universal de 0.0 a 1.0
        float cBrt = PApplet.constrain(brightness, 0.0f, 1.0f);
        float cCon = PApplet.constrain(contrast, 0.0f, 1.0f);
        float cSat = PApplet.constrain(saturation, 0.0f, 1.0f);

        // Envia os 3 coeficientes de forma independente para a GPU
        shader.set("brightness", cBrt);
        shader.set("contrast", cCon);
        shader.set("saturation", cSat);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Controla os canais de cor RGB, permitindo desligar ou estourar a intensidade de cada um.
     * @param r Vermelho (0.0 = desligado, 0.5 = original, 1.0 = ganho máximo/estouro)
     * @param g Verde (0.0 = desligado, 0.5 = original, 1.0 = ganho máximo/estouro)
     * @param b Azul (0.0 = desligado, 0.5 = original, 1.0 = ganho máximo/estouro)
     */
    public void channels(float r, float g, float b) {
        if (!loaded) {
            reload(libPath + "/channels.glsl");
            PApplet.println("Channels Shader loaded!");
        }

        parent.beginDraw();

        float cR = PApplet.constrain(r, 0.0f, 1.0f);
        float cG = PApplet.constrain(g, 0.0f, 1.0f);
        float cB = PApplet.constrain(b, 0.0f, 1.0f);

        shader.set("rControl", cR);
        shader.set("gControl", cG);
        shader.set("bControl", cB);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica uma deformação óptica matemática por dupla polaridade reativa a um ponto central.
     * * @param speed   Velocidade de deslocamento temporal (0.0 = congelado, 1.0 = movimento rápido)
     * @param targetX Posição X do centro magnético do efeito (0.0 a 1.0, normalizado)
     * @param targetY Posição Y do centro magnético do efeito (0.0 a 1.0, normalizado)
     * @param turns   Quantidade de voltas/espirais da onda (0.0 = pouca distorção, 1.0 = alta complexidade geométrica)
     */
    public void deformPolar(float speed, float targetX, float targetY, float turns) {
        if (!loaded) {
            reload(libPath + "/deform_polar.glsl");
            PApplet.println("Deform Polar Shader loaded!");
        }

        parent.beginDraw();

        // Constrain para travar a entrada na escala universal de 0.0 a 1.0
        float cSpeed = PApplet.constrain(speed, 0.0f, 1.0f);
        float cX = PApplet.constrain(targetX, 0.0f, 1.0f);
        float cY = PApplet.constrain(targetY, 0.0f, 1.0f);
        float cTurns = PApplet.constrain(turns, 0.0f, 1.0f);

        // 1. Calcula o tempo incremental baseado na velocidade escolhida
        float calculatedTime = (this.p.millis() / 1000.0f) * (cSpeed * 2.0f);
        shader.set("time", calculatedTime);

        // 2. Passa o vec2 do ponto de atração (mouse)
        shader.set("mouse", cX, cY);

        // 3. Mapeia a quantidade de voltas para um range estético expressivo (ex: de 1.0 a 10.0)
        float mappedTurns = PApplet.map(cTurns, 0.0f, 1.0f, 1.0f, 10.0f);
        shader.set("turns", mappedTurns);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica uma distorção espacial fluida em formato de vórtice ou lente d'água.
     * * @param intensity Força da distorção líquida (0.0 = imagem normal estática, 1.0 = distorção máxima)
     * @param speed     Velocidade de rotação/ondulação do vórtice (0.0 = congelado, 1.0 = movimento rápido)
     * @param scale     Frequência e quantidade de ondas (0.0 = poucas ondas largas, 1.0 = muitas ondas finas)
     */
    public void deformVortex(float intensity, float speed, float scale) {
        if (!loaded) {
            reload(libPath + "/deform_vortex.glsl");
            PApplet.println("Deform Vortex Shader loaded!");
        }

        parent.beginDraw();

        // Garante que todos os parâmetros respeitem os limites de 0.0 a 1.0
        float cIntensity = PApplet.constrain(intensity, 0.0f, 1.0f);
        float cSpeed = PApplet.constrain(speed, 0.0f, 1.0f);
        float cScale = PApplet.constrain(scale, 0.0f, 1.0f);

        // Passa a resolução necessária para normalizar as coordenadas dos pixels vizinhos
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Alimenta o timer em segundos multiplicado pela velocidade
        float calculatedTime = (this.p.millis() / 1000.0f) * (cSpeed * 3.0f);
        shader.set("iGlobalTime", calculatedTime);

        // Envia a intensidade e a escala mapeada de forma independente
        shader.set("intensity", cIntensity);
        shader.set("scale", cScale);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o filtro de contorno Laplaciano (linhas finas e de alta frequência).
     * @param intensity     Força/Brilho das linhas detectadas (0.0 a 1.0)
     * @param mixBackground Mistura com o fundo (0.0 = apenas linhas brancas no fundo preto, 1.0 = linhas sobrepostas à imagem original)
     */
    public void edge(float intensity, float mixBackground) {
        if (!loaded) {
            reload(libPath + "/edges.glsl");
            PApplet.println("Laplacian Edge Shader loaded!");
        }

        parent.beginDraw();

        // Trava as entradas na régua segura de 0.0 a 1.0
        float cInt = PApplet.constrain(intensity, 0.0f, 1.0f);
        float cMix = PApplet.constrain(mixBackground, 0.0f, 1.0f);

        // Alimenta as duas variáveis na GPU
        shader.set("intensity", cInt);
        shader.set("mixBackground", cMix);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o efeito de Retícula de Impressão / Quadrinhos (Halftone).
     * @param dotSize       Tamanho dos pontos (0.0 = micro-pontos detalhados, 1.0 = círculos enormes abstratos)
     * @param angles        Rotação da malha de pontos (0.0 a 1.0 = gira de 0° a 180°)
     * @param mixBackground Modo de cor (0.0 = Preto e Branco purista, 1.0 = Pontos coloridos baseados na imagem original)
     */
    public void halftone(float dotSize, float angles, float mixBackground) {
        if (!loaded) {
            reload(libPath + "/halftone.glsl");
            PApplet.println("Halftone Shader loaded!");
        }

        parent.beginDraw();

        // Força o constrain de segurança na escala 0-1
        float cSize = PApplet.constrain(dotSize, 0.0f, 1.0f);
        float cAngles = PApplet.constrain(angles, 0.0f, 1.0f);
        float cMix = PApplet.constrain(mixBackground, 0.0f, 1.0f);

        // Passa a resolução crucial para manter os círculos perfeitamente redondos em telas wide
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Envia os uniforms independentes
        shader.set("dotSize", cSize);
        shader.set("angles", cAngles);
        shader.set("mixBackground", cMix);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o efeito de Retícula de Impressão Colorida CMYK (Estilo HQ Vintage / Comic Book).
     * @param dotSize       Tamanho dos pontos (0.0 = micro-retículas finas, 1.0 = macro-círculos pop-art)
     * @param moireShift    Deslocamento angular (0.0 = rosetas perfeitas de impressão, 1.0 = distorção psicodélica de Moiré)
     * @param mixBackground Modo de fundo (0.0 = simula fundo de papel branco brilhante, 1.0 = funde os pontos com a imagem original)
     */
    public void halftoneCMYK(float dotSize, float moireShift, float mixBackground) {
        if (!loaded) {
            reload(libPath + "/halftone_cmyk.glsl");
            PApplet.println("Halftone CMYK Shader loaded!");
        }

        parent.beginDraw();

        // Travas de segurança na escala universal 0.0 a 1.0
        float cSize = PApplet.constrain(dotSize, 0.0f, 1.0f);
        float cShift = PApplet.constrain(moireShift, 0.0f, 1.0f);
        float cMix = PApplet.constrain(mixBackground, 0.0f, 1.0f);

        // Envia a resolução do canvas (essencial para os círculos não achatarem em resoluções panorâmicas)
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Alimenta os uniforms independentes
        shader.set("dotSize", cSize);
        shader.set("moireShift", cShift);
        shader.set("mixBackground", cMix);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Desloca a matriz cromática (Hue/Matriz de Cor) da superfície projetada.
     * @param hueRotation Rotação da paleta de cores (0.0 a 1.0 mapeia uma volta completa de 0° a 360° no círculo cromático)
     * @param mixOriginal Intensidade/Mistura do efeito (0.0 = mantém a cor original, 1.0 = cor 100% alterada pelo Hue)
     */
    public void hue(float hueRotation, float mixOriginal) {
        if (!loaded) {
            reload(libPath + "/hue.glsl");
            PApplet.println("Hue Shader loaded!");
        }

        parent.beginDraw();

        // Constrain para manter as réguas dentro do limite seguro
        float cHue = PApplet.constrain(hueRotation, 0.0f, 1.0f);
        float cMix = PApplet.constrain(mixOriginal, 0.0f, 1.0f);

        // Envia os parâmetros independentes para a GPU
        shader.set("hueRotation", cHue);
        shader.set("mixOriginal", cMix);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o filtro de Inversão de Cores (Efeito Negativo dinâmico).
     * @param invertIntensity Quantidade da inversão (0.0 = imagem normal, 1.0 = negativo total)
     * @param channelLock     Modo de canais (0.0 = inverte RGB completo, >0.3 = isola e cria inversões parciais cromáticas)
     */
    public void inversion(float invertIntensity, float channelLock) {
        if (!loaded) {
            reload(libPath + "/invert.glsl");
            PApplet.println("Invert Shader loaded!");
        }

        parent.beginDraw();

        // Protege as réguas com o constrain padrão do Processing
        float cIntensity = PApplet.constrain(invertIntensity, 0.0f, 1.0f);
        float cLock = PApplet.constrain(channelLock, 0.0f, 1.0f);

        // Alimenta os uniforms independentes na GPU
        shader.set("invertIntensity", cIntensity);
        shader.set("channelLock", cLock);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica um efeito de máscara de lanterna/holofote, revelando a imagem por partes.
     * @param targetX Posição X do centro da lanterna (0.0 a 1.0, normalizado)
     * @param targetY Posição Y do centro da lanterna (0.0 a 1.0, normalizado)
     * @param radius  Tamanho/Raio do feixe de revelação (0.0 = totalmente escuro, 1.0 = abre quase a tela inteira)
     * @param feather Suavidade da borda do feixe (0.0 = corte rígido/circular, 1.0 = degradê macio e orgânico)
     */
    public void lantern(float targetX, float targetY, float radius, float feather) {
        if (!loaded) {
            reload(libPath + "/lantern.glsl");
            PApplet.println("Lantern Reveal Shader loaded!");
        }

        parent.beginDraw();

        // Constrains padrão de segurança para as réguas da Gui
        float cX = PApplet.constrain(targetX, 0.0f, 1.0f);
        float cY = PApplet.constrain(targetY, 0.0f, 1.0f);
        float cRadius = PApplet.constrain(radius, 0.0f, 1.0f);
        float cFeather = PApplet.constrain(feather, 0.0f, 1.0f);

        // Envia a resolução para manter o feixe perfeitamente esférico
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Passa os parâmetros independentes para a GPU
        shader.set("pos", cX, cY);
        shader.set("radius", cRadius);
        shader.set("feather", cFeather);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica uma operação de módulo matemático sobre os canais de cor (efeito psicodélico de curvas de nível cromáticas).
     * @param colorFrequency Quantidade de repetições/quebras no gradiente de cor (0.0 = sutil, 1.0 = faixas densas de cor)
     * @param brightnessGain Compensação de brilho e ganho do efeito gerado (0.0 a 1.0)
     * @param chromaShift    Separação cromática das bordas do módulo (0.0 = canais sintonizados, 1.0 = franjas coloridas desalinhadas)
     */
    public void modcolor(float colorFrequency, float brightnessGain, float chromaShift) {
        if (!loaded) {
            reload(libPath + "/modcolor.glsl");
            PApplet.println("Modcolor Shader loaded!");
        }

        parent.beginDraw();

        // Protege as réguas com o constrain padrão do Processing
        float cFreq = PApplet.constrain(colorFrequency, 0.0f, 1.0f);
        float cBrt  = PApplet.constrain(brightnessGain, 0.0f, 1.0f);
        float cShift = PApplet.constrain(chromaShift, 0.0f, 1.0f);

        // Alimenta os uniforms independentes na GPU
        shader.set("colorFrequency", cFreq);
        shader.set("brightnessGain", cBrt);
        shader.set("chromaShift", cShift);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o efeito Neon / Glow sobre a superfície projetada.
     * @param brightness Intensidade do brilho do Neon (0.0 a 1.0)
     * @param glowRadius Raio de espalhamento/expansão do brilho (0.0 a 1.0)
     */
    public void neon(float brightness, float glowRadius) {
        if (!loaded) {
            reload(libPath + "/neon.glsl");
            PApplet.println("Neon Shader loaded!");
        }

        parent.beginDraw();

        // Garante que ambos os parâmetros respeitem a escala segura de 0.0 a 1.0
        float clampedBrt = PApplet.constrain(brightness, 0.0f, 1.0f);
        float clampedRad = PApplet.constrain(glowRadius, 0.0f, 1.0f);

        // Passa a resolução essencial para os cálculos de pixel da GPU
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Alimenta os dois uniforms de forma independente
        shader.set("brightness", clampedBrt);
        shader.set("glowRadius", clampedRad);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Quebra a imagem em blocos proceduralmente gerando um efeito de mosaico/retalhos geométricos.
     * @param patchCount Density/Quantidade de blocos (0.0 = blocos massivos abstratos, 1.0 = micro-blocos densos)
     * @param distortion Intensidade da refração/deslocamento interno de cor em cada bloco (0.0 a 1.0)
     * @param timeSync   Velocidade de tremeluzência/mudança dos blocos (0.0 = blocos estáticos fixos, 1.0 = mutação caótica rápida)
     */
    public void patches(float patchCount, float distortion, float timeSync) {
        if (!loaded) {
            reload(libPath + "/patches.glsl");
            PApplet.println("Patches Shader loaded!");
        }

        parent.beginDraw();

        // Travas de segurança para garantir a régua universal 0.0 a 1.0
        float cCount = PApplet.constrain(patchCount, 0.0f, 1.0f);
        float cDist  = PApplet.constrain(distortion, 0.0f, 1.0f);
        float cSync  = PApplet.constrain(timeSync, 0.0f, 1.0f);

        // Envia a resolução essencial para manter os blocos quadrados em qualquer aspecto de tela
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Passa o relógio interno em segundos para animar o ruído da GPU
        shader.set("iGlobalTime", this.p.millis() / 1000.0f);

        // Envia as três variáveis independentes mapeadas
        shader.set("patchCount", cCount);
        shader.set("distortion", cDist);
        shader.set("timeSync", cSync);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o filtro de Pixelização (Estética Retro-Gaming / Mosaico 8-bit).
     * @param pixelSize   Tamanho dos macro-pixels (0.0 = Resolução original, 1.0 = Blocos gigantes abstratos)
     * @param aspectRatio Proporção do formato dos blocos (0.0 = esticado horizontal, 0.5 = quadrados perfeitos, 1.0 = esticado vertical)
     * @param colorDepth  Profundidade/Degraus de cor (0.0 = Paleta altamente limitada e facetada, 1.0 = Cores reais e suaves)
     */
    public void pixelate(float pixelSize, float aspectRatio, float colorDepth) {
        if (!loaded) {
            reload(libPath + "/pixelate.glsl");
            PApplet.println("Pixelate Shader loaded!");
        }

        parent.beginDraw();

        // Travas padrão de segurança na régua 0-1
        float cSize = PApplet.constrain(pixelSize, 0.0f, 1.0f);
        float cAspect = PApplet.constrain(aspectRatio, 0.0f, 1.0f);
        float cDepth = PApplet.constrain(colorDepth, 0.0f, 1.0f);

        // Passa a resolução da tela para guiar o tamanho dos blocos
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Alimenta os uniforms independentes
        shader.set("pixelSize", cSize);
        shader.set("aspectRatio", cAspect);
        shader.set("colorDepth", cDepth);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o efeito de barras de pixelização dinâmicas com controle direcional de varredura.
     * @param pixelSize     Tamanho máximo dos blocos de pixel dentro das ondas (0.0 a 1.0)
     * @param speed         Velocidade de rolamento e deslocamento das faixas (0.0 = estático, 1.0 = movimento rápido)
     * @param rollFrequency Quantidade/Frequência das faixas de rolamento (0.0 = poucas faixas largas, 1.0 = muitas linhas finas)
     * @param direction     Direção do fluxo (0.0 = Varredura Horizontal, 0.5 = Varredura Vertical, 1.0 = Cruzada/Diagonal)
     */
    public void pixelrolls(float pixelSize, float speed, float rollFrequency, float direction) {
        if (!loaded) {
            reload(libPath + "/pixelrolls.glsl");
            PApplet.println("Pixel Rolls Shader loaded!");
        }

        parent.beginDraw();

        float cSize = PApplet.constrain(pixelSize, 0.0f, 1.0f);
        float cSpeed = PApplet.constrain(speed, 0.0f, 1.0f);
        float cFreq = PApplet.constrain(rollFrequency, 0.0f, 1.0f);
        float cDir = PApplet.constrain(direction, 0.0f, 1.0f);

        shader.set("iResolution", (float) parent.width, (float) parent.height);
        shader.set("iGlobalTime", this.p.millis() / 1000.0f);

        shader.set("pixelSize", cSize);
        shader.set("speed", cSpeed);
        shader.set("rollFrequency", cFreq);
        shader.set("direction", cDir);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o filtro Sobel de detecção de bordas estruturais com controle direcional e tonal.
     * @param intensity    Brilho/Força dos contornos gerados (0.0 a 1.0)
     * @param sobDirection Orientação do gradiente (0.0 = apenas linhas verticais, 0.5 = completo/ambos, 1.0 = apenas linhas horizontais)
     * @param tintMode     Estilização cromática (0.0 = contornos puramente brancos sobre fundo preto, 1.0 = linhas neon coloridas)
     */
    public void sobel(float intensity, float sobDirection, float tintMode) {
        if (!loaded) {
            reload(libPath + "/sobel.glsl");
            PApplet.println("Sobel Edge Shader loaded!");
        }

        parent.beginDraw();

        // Filtros de segurança padrão para a Gui
        float cInt = PApplet.constrain(intensity, 0.0f, 1.0f);
        float cDir = PApplet.constrain(sobDirection, 0.0f, 1.0f);
        float cTint = PApplet.constrain(tintMode, 0.0f, 1.0f);

        // Alimenta os uniforms independentes na GPU
        shader.set("intensity", cInt);
        shader.set("sobDirection", cDir);
        shader.set("tintMode", cTint);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica o filtro de Binarização / Alto Contraste Gráfico (Threshold).
     * @param cutoff     Ponto de corte do limiar (0.0 = tela branca, 0.5 = equilíbrio, 1.0 = tela preta)
     * @param smoothness Suavização da borda de transição (0.0 = bordas rígidas e serrilhadas, 1.0 = bordas macias e esfumaçadas)
     * @param invertMode Inversão de polaridade (0.0 = padrão [claro fica branco], 1.0 = invertido [escuro fica branco])
     */
    public void threshold(float cutoff, float smoothness, float invertMode) {
        if (!loaded) {
            reload(libPath + "/threshold.glsl");
            PApplet.println("Threshold Shader loaded!");
        }

        parent.beginDraw();

        // Travas padrão de segurança de 0.0f a 1.0f para os sliders da GUI
        float cCut = PApplet.constrain(cutoff, 0.0f, 1.0f);
        float cSmooth = PApplet.constrain(smoothness, 0.0f, 1.0f);
        float cInv = PApplet.constrain(invertMode, 0.0f, 1.0f);

        // Envia os uniforms para a GPU
        shader.set("cutoff", cCut);
        shader.set("smoothness", cSmooth);
        shader.set("invertMode", cInv);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica a simulação estética de fita de vídeo analógica corrompida (Efeito VHS / Lo-Fi).
     * @param tapeNoise           Quantidade de grão estático e scanlines analógicas na imagem (0.0 a 1.0)
     * @param distortion          Força da ondulação e trepidação horizontal das linhas (0.0 a 1.0)
     * @param chromaticAberration Intensidade da separação física dos canais RGB nas bordas (0.0 a 1.0)
     */
    public void vhs(float tapeNoise, float distortion, float chromaticAberration) {
        if (!loaded) {
            reload(libPath + "/vhs.glsl");
            PApplet.println("VHS Shader loaded!");
        }

        parent.beginDraw();

        // Limita os inputs nos ranges de segurança 0.0f a 1.0f
        float cNoise = PApplet.constrain(tapeNoise, 0.0f, 1.0f);
        float cDist  = PApplet.constrain(distortion, 0.0f, 1.0f);
        float cAb    = PApplet.constrain(chromaticAberration, 0.0f, 1.0f);

        // Passa a resolução para desenhar as scanlines na proporção exata dos pixels da tela
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Alimenta o timer contínuo da GPU em segundos
        shader.set("iGlobalTime", this.p.millis() / 1000.0f);

        // Envia os múltiplos parâmetros independentes configurados
        shader.set("tapeNoise", cNoise);
        shader.set("distortion", cDist);
        shader.set("chromaticAberration", cAb);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica uma simulação avançada de sinal de TV CRT e fita VHS corrompida (Glitch de Tracking).
     * @param glitchIntensity Frequência e violência dos rasgos horizontais e pulos de tela (0.0 a 1.0)
     * @param noiseSuck       Intensidade do grão analógico e das barras estáticas de ruído (0.0 a 1.0)
     * @param tubeCurvature   Curvatura da tela simulando um monitor de tubo esférico antigo (0.0 = plano, 1.0 = tubo curvo máximo)
     */
    public void vhsGlitch(float glitchIntensity, float noiseSuck, float tubeCurvature) {
        if (!loaded) {
            reload(libPath + "/vhs_glitch.glsl");
            PApplet.println("VHS Glitch Shader loaded!");
        }

        parent.beginDraw();

        // Filtros de segurança padrão para a Gui
        float cGlitch = PApplet.constrain(glitchIntensity, 0.0f, 1.0f);
        float cNoise  = PApplet.constrain(noiseSuck, 0.0f, 1.0f);
        float cTube   = PApplet.constrain(tubeCurvature, 0.0f, 1.0f);

        // Envia a resolução do canvas (vital para o cálculo de proporção do monitor de tubo)
        shader.set("iResolution", (float) parent.width, (float) parent.height);

        // Alimenta o relógio incremental em segundos para ditar o caos temporal
        shader.set("iGlobalTime", this.p.millis() / 1000.0f);

        // Alimenta os três parâmetros independentes
        shader.set("glitchIntensity", cGlitch);
        shader.set("noiseSuck", cNoise);
        shader.set("tubeCurvature", cTube);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica uma distorção analógica focada na instabilidade de tração da fita (Ondulamento/Wobble e Tremor).
     * @param wobbleForce Força do entortamento ondulado das linhas verticais (0.0 a 1.0)
     * @param noiseJitter Intensidade do tremor elétrico horizontal serrilhado por linha (0.0 a 1.0)
     * @param colorSplit  Nível de separação cromática flutuante dos canais RGB (0.0 a 1.0)
     */
    public void vhsWobble(float wobbleForce, float noiseJitter, float colorSplit) {
        if (!loaded) {
            reload(libPath + "/vhs_wobble.glsl");
            PApplet.println("VHS Wobble Shader loaded!");
        }

        parent.beginDraw();

        // Travas padrão de segurança na escala 0-1
        float cWobble = PApplet.constrain(wobbleForce, 0.0f, 1.0f);
        float cJitter = PApplet.constrain(noiseJitter, 0.0f, 1.0f);
        float cSplit  = PApplet.constrain(colorSplit, 0.0f, 1.0f);

        // Passa os parâmetros estruturais
        shader.set("iResolution", (float) parent.width, (float) parent.height);
        shader.set("iGlobalTime", this.p.millis() / 1000.0f);

        shader.set("wobbleForce", cWobble);
        shader.set("noiseJitter", cJitter);
        shader.set("colorSplit", cSplit);

        parent.shader(shader);
        parent.endDraw();
    }

    /**
     * Aplica um efeito de envelopamento, repetição cíclica e espelhamento geométrico (Wrap/Caleidoscópio).
     * @param wrapFrequency Quantidade de dobras/repetições da imagem na tela (0.0 = tamanho original, 1.0 = mosaico denso)
     * @param offset        Deslocamento e rolagem da textura dentro do envelopamento (0.0 a 1.0)
     * @param mirrorMode    Estilo das emendas (0.0 = repetição de ladrilhos secos, 1.0 = espelhamento infinito suave)
     */
    public void wrap(float wrapFrequency, float offset, float mirrorMode) {
        if (!loaded) {
            reload(libPath + "/wrap.glsl");
            PApplet.println("Wrap Shader loaded! Core Library Complete!");
        }

        parent.beginDraw();

        // Travas padrão de segurança na régua 0-1
        float cFreq = PApplet.constrain(wrapFrequency, 0.0f, 1.0f);
        float cOffset = PApplet.constrain(offset, 0.0f, 1.0f);
        float cMirror = PApplet.constrain(mirrorMode, 0.0f, 1.0f);

        // Alimenta os uniforms independentes
        shader.set("wrapFrequency", cFreq);
        shader.set("offset", cOffset);
        shader.set("mirrorMode", cMirror);

        parent.shader(shader);
        parent.endDraw();
    }



    void reload(String shaderName) {
        shader = p.loadShader(shaderName);
        loaded = true;
    }

    private String loadResourceText(String path) {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) return null;
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

