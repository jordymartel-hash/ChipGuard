package simuchip.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 * Capítulo 2: el objetivo del trabajo y su marco teórico, como en el informe (§3 y §4). Una idea por
 * pantalla: el objetivo general en sus tres verbos, los cuatro específicos como un recorrido por la
 * exposición y las cinco definiciones del curso en dos escenas, cada una con su diagrama.
 */
public final class PanelObjetivos extends PanelEscenas {

    /** El objetivo general (informe §3.1) partido en sus tres verbos. El color dice de qué trata cada uno. */
    static final String[][] GENERAL = {
        {"Modelar", "el registro de líneas móviles del Perú como una función entre conjuntos finitos."},
        {"Explicar", "por qué ese registro permite la extorsión con chips a nombre de terceros."},
        {"Proponer y simular", "una solución basada en las propiedades de las funciones: ChipGuard."}
    };
    private static final Iconos.Tipo[] ICONO_GENERAL = {Iconos.Tipo.CHIP, Iconos.Tipo.LUPA, Iconos.Tipo.ESCUDO};
    private static final Color[] TINTE_GENERAL = {Tema.NARANJA_SUAVE, Tema.ROJO_SUAVE, Tema.VERDE_SUAVE};
    private static final Color[] TINTA_GENERAL = {Tema.NARANJA_TEXTO, Tema.ROJO_OSCURO, Tema.VERDE};

    /** Verbo, qué se hace y el capítulo donde se cumple. Del informe, §3.2. */
    static final String[][] ESPECIFICOS = {
        {"Representar", "el registro y el uso real de las líneas como dos funciones, y clasificarlas.", "La matemática"},
        {"Explicar", "inyectiva, sobreyectiva, biyectiva y el palomar con casos cotidianos.", "La matemática"},
        {"Diseñar", "ChipGuard: qué es, qué ofrece, su interfaz y su arquitectura.", "Qué es · En acción · Por dentro"},
        {"Desarrollar", "una app en Java que presenta el caso y simula el efecto de ChipGuard, con pruebas.", "La tienda"}
    };

    /** Nombre, fórmula (solo con símbolos que Geist dibuja) y la frase en palabras. Del informe, §4. */
    static final String[][] CONCEPTOS = {
        {"Función", "f : A → B", "De cada elemento de A sale exactamente una flecha."},
        {"Inyectiva", "si x ≠ y, f(x) ≠ f(y)", "A cada elemento de B le llega como máximo una flecha."},
        {"Sobreyectiva", "imagen de f = B", "Cada elemento de B recibe al menos una flecha."},
        {"Biyectiva", "existe f⁻¹ : B → A", "Inyectiva y sobreyectiva a la vez: uno a uno, y se puede volver de B a A."},
        {"Palomar", "|A| > |B| → no es inyectiva", "4 chips y 3 DNI: a algún DNI le tocan dos. Es la base del tope de líneas por persona."}
    };

    private static final String[][] NODOS_A = {{"1", "2", "3"}, {"1", "2", "3"}, {"1", "2", "3", "4"}, {"1", "2", "3"},
        {"1", "2", "3", "4"}};
    private static final String[][] NODOS_B = {{"a", "b", "c"}, {"a", "b", "c", "d"}, {"a", "b", "c"}, {"a", "b", "c"},
        {"a", "b", "c"}};
    private static final int[][][] FLECHAS = {
        {{0, 0}, {1, 1}, {2, 1}},
        {{0, 0}, {1, 1}, {2, 3}},
        {{0, 0}, {1, 1}, {2, 2}, {3, 2}},
        {{0, 0}, {1, 1}, {2, 2}},
        {{0, 0}, {1, 1}, {2, 1}, {3, 2}}
    };

    public PanelObjetivos() {
        super(List.of(
            new Escena("Nuestro objetivo.", "Lo que buscamos, en tres verbos."),
            new Escena("Objetivos específicos.", "Cada uno se cumple en un capítulo de esta presentación.", true),
            new Escena("El marco teórico.",
                "La función y sus dos propiedades, como las define el curso (Rosen, 2019).", true),
            new Escena("Biyectiva y palomar.",
                "Cuándo existe la inversa, y por qué hace falta un tope de líneas por persona.", true)
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        switch (i) {
            case 0 -> general(g, a, t);
            case 1 -> especificos(g, a, t);
            case 2 -> conceptos(g, a, t, 0, 1, 2);
            default -> conceptos(g, a, t, 3, 4);
        }
    }

    /** Los tres verbos en una columna, unidos por una línea que baja mientras aparecen. */
    private void general(Graphics2D g, Rectangle a, double t) {
        int alto = Math.min(190, a.height / GENERAL.length);
        int y0 = a.y + Math.max(0, (a.height - alto * GENERAL.length) / 2);
        int d = 72;
        int cx = a.x + d / 2;
        int xt = a.x + d + 28;
        double linea = tramo(t, 0.3, 1.2);
        g.setColor(Tema.FILETE);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int desde = y0 + d / 2;
        int hasta = y0 + (GENERAL.length - 1) * alto + d / 2;
        g.drawLine(cx, desde, cx, (int) (desde + (hasta - desde) * linea));
        for (int k = 0; k < GENERAL.length; k++) {
            double p = tramo(t, 0.2 + k * 0.45, 0.5);
            if (p <= 0) {
                continue;
            }
            Graphics2D c = (Graphics2D) g.create();
            c.setComposite(AlphaComposite.SrcOver.derive((float) p));
            int y = y0 + k * alto;
            double r = d / 2.0 * (0.9 + 0.1 * p);
            Tema.rectRedondo(c, cx - r, y + d / 2.0 - r, 2 * r, 2 * r, 2 * r, TINTE_GENERAL[k]);
            Iconos.dibujar(c, ICONO_GENERAL[k], cx, y + d / 2.0, 34, TINTA_GENERAL[k]);
            c.translate(0, (1 - p) * 10);
            c.setFont(Tema.titulo(34f));
            c.setColor(Tema.AZUL_NOCHE);
            FontMetrics fv = c.getFontMetrics();
            Tema.texto(c, GENERAL[k][0], xt, y + d / 2.0 - 6);
            c.setFont(Tema.letra(Font.PLAIN, 19f));
            c.setColor(Tema.TEXTO_SUAVE);
            Tema.parrafo(c, GENERAL[k][1], xt, y + d / 2 + fv.getDescent(), a.x + a.width - xt, 1.3f);
            c.dispose();
        }
    }

    /** Los cuatro objetivos como un recorrido: una línea que avanza y un paso por objetivo. */
    private void especificos(Graphics2D g, Rectangle a, double t) {
        int n = ESPECIFICOS.length;
        int col = a.width / n;
        int d = 46;
        int yl = a.y + 10 + d / 2;
        int x0 = a.x + d / 2;
        int x1 = a.x + (n - 1) * col + d / 2;
        double avance = tramo(t, 0.2, 1.6);
        g.setColor(Tema.FILETE);
        g.fillRect(x0, yl - 1, x1 - x0, 2);
        g.setColor(Tema.conAlfa(Tema.AZUL, 160));
        g.fillRect(x0, yl - 1, (int) ((x1 - x0) * avance), 2);
        Font numero = Tema.titulo(20f);
        Font verbo = Tema.titulo(30f);
        Font cuerpo = Tema.letra(Font.PLAIN, 18f);
        Font pildora = Tema.medio(15f);
        for (int k = 0; k < n; k++) {
            double p = tramo(t, 0.25 + k * 0.4, 0.5);
            if (p <= 0) {
                continue;
            }
            Graphics2D c = (Graphics2D) g.create();
            c.setComposite(AlphaComposite.SrcOver.derive((float) p));
            int x = a.x + k * col;
            double r = d / 2.0 * (0.85 + 0.15 * p);
            Tema.rectRedondo(c, x + d / 2.0 - r, yl - r, 2 * r, 2 * r, 2 * r, Tema.AZUL);
            c.setFont(numero);
            c.setColor(Tema.BLANCO);
            Tema.textoCentrado(c, String.valueOf(k + 1), x + d / 2.0, yl);
            c.translate(0, (1 - p) * 10);
            int ancho = col - 36;
            int y = yl + d / 2 + 24;
            c.setFont(verbo);
            c.setColor(Tema.AZUL_NOCHE);
            y += c.getFontMetrics().getAscent();
            Tema.texto(c, ESPECIFICOS[k][0], x, y);
            c.setFont(cuerpo);
            c.setColor(Tema.TEXTO_SUAVE);
            y += 14 + Tema.parrafo(c, ESPECIFICOS[k][1], x, y + 12, ancho, 1.3f);
            // una píldora por capítulo donde se cumple; si no caben en la fila, bajan a la siguiente
            c.setFont(pildora);
            FontMetrics fp = c.getFontMetrics();
            int alto = fp.getHeight() + 8;
            int xp = x;
            for (String capitulo : ESPECIFICOS[k][2].split(" · ")) {
                int wp = fp.stringWidth(capitulo) + 24;
                if (xp > x && xp + wp > x + ancho) {
                    xp = x;
                    y += alto + 8;
                }
                Tema.pastilla(c, capitulo, xp, y + 14, Tema.GRIS_CLARO, Tema.AZUL_NOCHE, pildora);
                xp += wp + 8;
            }
            c.dispose();
        }
    }

    /** Las definiciones indicadas, en columnas: diagrama arriba; nombre, fórmula y frase debajo. */
    private void conceptos(Graphics2D g, Rectangle a, double t, int... cuales) {
        int n = cuales.length;
        int hueco = n > 2 ? 48 : 72;
        int ancho = (a.width - hueco * (n - 1)) / n;
        Font nombre = Tema.titulo(30f);
        Font formula = Tema.medio(21f);
        Font frase = Tema.letra(Font.PLAIN, 18f);
        FontMetrics fm = g.getFontMetrics(frase);
        int interlinea = Math.round(fm.getHeight() * 1.3f);
        int lineas = 0;
        for (int k : cuales) {
            lineas = Math.max(lineas, Tema.partir(CONCEPTOS[k][2], fm, ancho).size());
        }
        // el diagrama se queda con lo que no usa el texto: la frase más larga nunca baja hasta los botones
        int texto = g.getFontMetrics(nombre).getHeight() + g.getFontMetrics(formula).getHeight() + 28
                + lineas * interlinea;
        int altoDiagrama = Math.max(170, Math.min(340, a.height - texto));
        int anchoDiagrama = Math.min(ancho, 470);
        for (int j = 0; j < n; j++) {
            int k = cuales[j];
            double inicio = 0.15 + j * 0.6;
            double p = tramo(t, inicio, 0.45);
            if (p <= 0) {
                continue;
            }
            int x = a.x + j * (ancho + hueco);
            Graphics2D c = (Graphics2D) g.create();
            c.setComposite(AlphaComposite.SrcOver.derive((float) p));
            c.translate(0, (1 - p) * 10);
            Diagrama.Opciones o = new Diagrama.Opciones().ritmo(inicio + 0.2, 0.18).sinContar();
            if (k == 3) {
                o.conInversa();
            }
            if (k == 4) {
                o.anilloB(1, Tema.DORADO); // el DNI al que le tocan dos: el elemento bajo revisión
            }
            Diagrama.dibujar(c, x, a.y, anchoDiagrama, altoDiagrama, NODOS_A[k], NODOS_B[k], FLECHAS[k], t, o);
            int y = a.y + altoDiagrama + 12;
            c.setFont(nombre);
            c.setColor(Tema.AZUL_NOCHE);
            y += c.getFontMetrics().getAscent();
            Tema.texto(c, CONCEPTOS[k][0], x, y);
            c.setFont(formula);
            y += c.getFontMetrics().getHeight() + 2;
            Tema.texto(c, CONCEPTOS[k][1], x, y);
            c.setFont(frase);
            c.setColor(Tema.TEXTO_SUAVE);
            Tema.parrafo(c, CONCEPTOS[k][2], x, y + 12, ancho, 1.3f);
            c.dispose();
        }
    }
}
