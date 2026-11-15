package simuchip.ui;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 * Capítulo 2: el objetivo del trabajo y su marco teórico, como en el informe (§3 y §4). Los objetivos
 * específicos dicen en qué capítulo se cumplen; el marco teórico muestra cada definición con su diagrama.
 */
public final class PanelObjetivos extends PanelEscenas {

    /** Verbo, qué se hace y el capítulo donde se ve. Del informe, §3.2. */
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
        {"Biyectiva", "existe f⁻¹ : B → A", "Inyectiva y sobreyectiva a la vez: uno a uno, y se puede volver."},
        {"Palomar", "|A| > |B| → no inyectiva", "4 chips y 3 DNI: a algún DNI le tocan dos. Es la base del tope de líneas."}
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
            new Escena("Nuestro objetivo.",
                "Modelar el registro de líneas móviles del Perú como una función entre conjuntos finitos, para explicar "
                    + "por qué permite la extorsión con chips a nombre de terceros, y proponer y simular una solución "
                    + "con las propiedades de las funciones."),
            new Escena("El marco teórico.",
                "Cinco definiciones del curso (Rosen, 2019). En el capítulo siguiente las aplicamos a los chips.", true)
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        if (i == 0) {
            especificos(g, a, t);
        } else {
            marco(g, a, t);
        }
    }

    private void especificos(Graphics2D g, Rectangle a, double t) {
        double cabecera = tramo(t, 0.1, 0.4);
        g.setFont(Tema.titulo(21f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * cabecera)));
        int y0 = a.y + 22;
        Tema.texto(g, "Objetivos específicos", a.x, y0);
        int alto = Math.min(124, (a.height - 50) / ESPECIFICOS.length);
        Font verbo = Tema.titulo(28f);
        Font cuerpo = Tema.letra(Font.PLAIN, 17f);
        Font pildora = Tema.medio(14f);
        double[] retraso = {0.25, 0.37, 0.47, 0.55};
        for (int k = 0; k < ESPECIFICOS.length; k++) {
            double p = tramo(t, retraso[k], 0.45);
            int al = (int) (255 * p);
            int y = y0 + 22 + k * alto + (int) ((1 - p) * 10);
            g.setColor(Tema.conAlfa(Tema.FILETE, al));
            g.fillRect(a.x, y, a.width, 1);
            // la píldora del capítulo, a la derecha de la fila
            g.setFont(pildora);
            FontMetrics fp = g.getFontMetrics();
            int wp = fp.stringWidth(ESPECIFICOS[k][2]) + 24;
            int hp = fp.getHeight() + 8;
            Tema.pastilla(g, ESPECIFICOS[k][2], a.x + a.width - wp, y + 20, Tema.conAlfa(Tema.GRIS_CLARO, al),
                    Tema.conAlfa(Tema.AZUL_NOCHE, al), pildora);
            g.setFont(verbo);
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            int base = y + 20 + hp / 2 + g.getFontMetrics().getAscent() / 2 - 3;
            Tema.texto(g, ESPECIFICOS[k][0], a.x, base);
            g.setFont(cuerpo);
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.parrafo(g, ESPECIFICOS[k][1], a.x, base + 10, a.width - 8, 1.3f);
        }
    }

    private void marco(Graphics2D g, Rectangle a, double t) {
        int hueco = 28;
        int ancho = (a.width - hueco * (CONCEPTOS.length - 1)) / CONCEPTOS.length;
        // el diagrama se queda con lo que no usa el texto: así la frase más larga nunca baja hasta los botones
        Font frase = Tema.letra(Font.PLAIN, 15.5f);
        FontMetrics fm = g.getFontMetrics(frase);
        int interlinea = Math.round(fm.getHeight() * 1.35f);
        int lineas = 0;
        for (String[] c : CONCEPTOS) {
            lineas = Math.max(lineas, Tema.partir(c[2], fm, ancho).size());
        }
        int altoDiagrama = Math.max(150, Math.min(290, a.height - (90 + lineas * interlinea)));
        Font nombre = Tema.titulo(Math.min(24f, ancho / 7f)); // «Sobreyectiva» cabe en su columna
        for (int k = 0; k < CONCEPTOS.length; k++) {
            double inicio = 0.15 + k * 0.5;
            double p = tramo(t, inicio, 0.45);
            if (p <= 0) {
                continue;
            }
            int x = a.x + k * (ancho + hueco);
            Graphics2D c = (Graphics2D) g.create();
            c.setComposite(AlphaComposite.SrcOver.derive((float) p));
            c.translate(0, (1 - p) * 10);
            Diagrama.Opciones o = new Diagrama.Opciones().ritmo(inicio + 0.2, 0.16);
            if (k == 3) {
                o.conInversa();
            }
            o.sinContar();
            if (k == 4) {
                o.anilloB(1, Tema.DORADO); // el DNI al que le tocan dos: el elemento bajo revisión
            }
            Diagrama.dibujar(c, x, a.y, ancho, altoDiagrama, NODOS_A[k], NODOS_B[k], FLECHAS[k], t, o);
            int y = a.y + altoDiagrama + 40;
            c.setFont(nombre);
            c.setColor(Tema.AZUL_NOCHE);
            Tema.texto(c, CONCEPTOS[k][0], x, y);
            c.setFont(Tema.medio(Math.min(17f, ancho / 9f)));
            Tema.texto(c, CONCEPTOS[k][1], x, y + 32);
            c.setFont(frase);
            c.setColor(Tema.TEXTO_SUAVE);
            Tema.parrafo(c, CONCEPTOS[k][2], x, y + 46, ancho, 1.35f);
            c.dispose();
        }
    }
}
