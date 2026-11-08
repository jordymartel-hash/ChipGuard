package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Dibuja un diagrama sagital animado: dos óvalos (A naranja, B azul) y flechas que crecen una por una.
 * Lo usan las escenas de los capítulos Funciones y El modelo.
 */
public final class Diagrama {

    /** Opciones de una figura. Todo es opcional. */
    public static final class Opciones {
        String tituloA = "A";
        String tituloB = "B";
        Color colorA = Tema.NARANJA;
        Color colorB = Tema.AZUL;
        Set<Integer> flechasRojas = Set.of();
        Set<Integer> flechasPunteadas = Set.of();
        Map<Integer, Color> anilloA = new HashMap<>();
        Map<Integer, Color> anilloB = new HashMap<>();
        boolean contar = true;
        boolean inversa;
        double inicio = 0.25;
        double separacion = 0.28;
        Map<Integer, String> notaB = new HashMap<>();
        Map<Integer, String> notaA = new HashMap<>();

        public Opciones titulos(String a, String b) {
            tituloA = a;
            tituloB = b;
            return this;
        }

        public Opciones colores(Color a, Color b) {
            colorA = a;
            colorB = b;
            return this;
        }

        public Opciones rojas(Integer... indices) {
            flechasRojas = Set.of(indices);
            return this;
        }

        public Opciones punteadas(Integer... indices) {
            flechasPunteadas = Set.of(indices);
            return this;
        }

        public Opciones anilloA(int i, Color c) {
            anilloA.put(i, c);
            return this;
        }

        public Opciones anilloB(int i, Color c) {
            anilloB.put(i, c);
            return this;
        }

        public Opciones notaA(int i, String texto) {
            notaA.put(i, texto);
            return this;
        }

        public Opciones notaB(int i, String texto) {
            notaB.put(i, texto);
            return this;
        }

        public Opciones sinContar() {
            contar = false;
            return this;
        }

        public Opciones conInversa() {
            inversa = true;
            return this;
        }

        public Opciones ritmo(double inicio, double separacion) {
            this.inicio = inicio;
            this.separacion = separacion;
            return this;
        }
    }

    private Diagrama() {}

    public static double tramo(double t, double desde, double dura) {
        double p = Math.max(0, Math.min(1, (t - desde) / dura));
        return 1 - Math.pow(1 - p, 3);
    }

    /**
     * @param flechas pares {índice en A, índice en B}
     * @param t       segundos desde que empezó la escena (las flechas aparecen una a una)
     */
    public static void dibujar(Graphics2D g, double x, double y, double w, double h, String[] a, String[] b,
            int[][] flechas, double t, Opciones o) {
        double cxA = x + w * 0.24;
        double cxB = x + w * 0.76;
        double cy = y + h / 2 + 14;
        double anchoOvalo = Math.min(w * 0.3, 230);
        double altoOvalo = h - 40;
        Tema.ovalo(g, cxA, cy, anchoOvalo, altoOvalo, Tema.conAlfa(o.colorA, 30), o.colorA);
        Tema.ovalo(g, cxB, cy, anchoOvalo, altoOvalo, Tema.conAlfa(o.colorB, 26), o.colorB);
        g.setFont(Tema.letra(Font.BOLD, 17f));
        g.setColor(oscurecer(o.colorA));
        Tema.textoCentrado(g, o.tituloA, cxA, cy - altoOvalo / 2 - 16);
        g.setColor(oscurecer(o.colorB));
        Tema.textoCentrado(g, o.tituloB, cxB, cy - altoOvalo / 2 - 16);

        Point2D[] pa = posiciones(cxA, cy, altoOvalo, a.length);
        Point2D[] pb = posiciones(cxB, cy, altoOvalo, b.length);
        double radio = Math.min(30, (altoOvalo - 60) / Math.max(a.length, b.length) / 2.4);
        double[] anchoA = anchos(g, a, radio);
        double[] anchoB = anchos(g, b, radio);

        int[] salen = new int[a.length];
        int[] llegan = new int[b.length];
        for (int[] f : flechas) {
            salen[f[0]]++;
            llegan[f[1]]++;
        }

        // flechas
        for (int k = 0; k < flechas.length; k++) {
            int[] f = flechas[k];
            double p = tramo(t, o.inicio + k * o.separacion, 0.5);
            Color c = o.flechasRojas.contains(k) ? Tema.ROJO : Tema.AZUL_NOCHE;
            double dy = o.inversa ? -8 : 0;
            Tema.flecha(g, pa[f[0]].getX() + anchoA[f[0]] / 2 + 4, pa[f[0]].getY() + dy,
                    pb[f[1]].getX() - anchoB[f[1]] / 2 - 6, pb[f[1]].getY() + dy, c,
                    o.flechasRojas.contains(k) ? 4f : 3f, o.flechasPunteadas.contains(k), p);
        }
        if (o.inversa) {
            double inicioInv = o.inicio + flechas.length * o.separacion + 0.4;
            for (int k = 0; k < flechas.length; k++) {
                int[] f = flechas[k];
                double p = tramo(t, inicioInv + k * 0.18, 0.5);
                Tema.flecha(g, pb[f[1]].getX() - anchoB[f[1]] / 2 - 4, pb[f[1]].getY() + 8,
                        pa[f[0]].getX() + anchoA[f[0]] / 2 + 6, pa[f[0]].getY() + 8, Tema.NARANJA_TEXTO, 3f, true, p);
            }
        }

        // nodos
        double fin = o.inicio + flechas.length * o.separacion + 0.3;
        for (int i = 0; i < a.length; i++) {
            Color anillo = o.anilloA.getOrDefault(i, null);
            nodo(g, pa[i], anchoA[i], radio, a[i], o.colorA, Tema.AZUL_NOCHE, anillo, t >= fin);
            if (o.contar) {
                insignia(g, pa[i], anchoA[i], radio, salen[i], salen[i] != 1, t >= fin);
            }
            nota(g, o.notaA.get(i), pa[i].getX() - anchoA[i] / 2 - 10, pa[i].getY(), true, t >= fin);
        }
        for (int i = 0; i < b.length; i++) {
            boolean sola = llegan[i] == 0;
            Color anillo = o.anilloB.getOrDefault(i, null);
            nodo(g, pb[i], anchoB[i], radio, b[i], sola ? Tema.GRIS : o.colorB, sola ? Tema.AZUL_NOCHE : Tema.BLANCO,
                    anillo, t >= fin);
            if (o.contar) {
                insignia(g, pb[i], anchoB[i], radio, llegan[i], llegan[i] != 1, t >= fin);
            }
            nota(g, o.notaB.get(i), pb[i].getX() + anchoB[i] / 2 + 14, pb[i].getY(), false, t >= fin);
        }
    }

    private static Color oscurecer(Color c) {
        if (c.equals(Tema.NARANJA)) {
            return Tema.NARANJA_TEXTO;
        }
        if (c.equals(Tema.AZUL)) {
            return Tema.AZUL_TEXTO;
        }
        return c.darker();
    }

    private static Point2D[] posiciones(double cx, double cy, double alto, int n) {
        Point2D[] p = new Point2D[n];
        double util = alto - 70;
        for (int i = 0; i < n; i++) {
            p[i] = new Point2D.Double(cx, cy - util / 2 + util / n * (i + 0.5));
        }
        return p;
    }

    private static double[] anchos(Graphics2D g, String[] textos, double radio) {
        double[] r = new double[textos.length];
        g.setFont(Tema.letra(Font.BOLD, (float) (radio * 0.75)));
        for (int i = 0; i < textos.length; i++) {
            r[i] = Math.max(radio * 2, g.getFontMetrics().stringWidth(textos[i]) + radio * 1.1);
        }
        return r;
    }

    private static void nodo(Graphics2D g, Point2D p, double ancho, double radio, String texto, Color relleno,
            Color letra, Color anillo, boolean mostrarAnillo) {
        double x = p.getX() - ancho / 2;
        double y = p.getY() - radio;
        Tema.sombra(g, x, y, ancho, radio * 2, radio * 2, 16);
        Tema.rectRedondo(g, x, y, ancho, radio * 2, radio * 2, relleno);
        if (anillo != null && mostrarAnillo) {
            g.setStroke(new BasicStroke(4f));
            g.setColor(anillo);
            g.draw(new java.awt.geom.RoundRectangle2D.Double(x - 6, y - 6, ancho + 12, radio * 2 + 12, radio * 2 + 12,
                    radio * 2 + 12));
            g.setStroke(new BasicStroke(1f));
        }
        g.setFont(Tema.letra(Font.BOLD, (float) (radio * 0.75)));
        g.setColor(letra);
        Tema.textoCentrado(g, texto, p.getX(), p.getY());
    }

    private static void insignia(Graphics2D g, Point2D p, double ancho, double radio, int n, boolean alerta,
            boolean visible) {
        if (!visible) {
            return;
        }
        double d = Math.max(20, radio * 0.75);
        double x = p.getX() + ancho / 2 - d * 0.55;
        double y = p.getY() - radio - d * 0.35;
        g.setColor(alerta ? Tema.ROJO : Tema.AZUL_NOCHE);
        g.fill(new Ellipse2D.Double(x, y, d, d));
        g.setColor(Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, (float) (d * 0.55)));
        Tema.textoCentrado(g, String.valueOf(n), x + d / 2, y + d / 2);
    }

    private static void nota(Graphics2D g, String texto, double x, double y, boolean aLaIzquierda, boolean visible) {
        if (texto == null || !visible) {
            return;
        }
        g.setFont(Tema.letra(Font.BOLD, 14.5f));
        g.setColor(Tema.ROJO_OSCURO);
        double ancho = g.getFontMetrics().stringWidth(texto);
        Tema.texto(g, texto, aLaIzquierda ? x - ancho : x, y + 5);
    }
}
