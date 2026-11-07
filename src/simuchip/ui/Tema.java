package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/** Colores, letras y dibujos comunes. La misma paleta que la presentación. */
public final class Tema {

    private Tema() {}

    // Paleta
    public static final Color CREMA = new Color(0xFFF8EE);
    public static final Color CELESTE_FONDO = new Color(0xEEF4FB);
    public static final Color AZUL_NOCHE = new Color(0x1E2A44);
    public static final Color AZUL_NOCHE_2 = new Color(0x2D3B58);
    public static final Color TEXTO_SUAVE = new Color(0x4A5568);
    public static final Color GRIS = new Color(0xB9C6D8);
    public static final Color GRIS_CLARO = new Color(0xE4E8EE);
    public static final Color NARANJA = new Color(0xE8832E);
    public static final Color NARANJA_TEXTO = new Color(0xB85A12);
    public static final Color NARANJA_SUAVE = new Color(0xFDE3CC);
    public static final Color DORADO = new Color(0xF6B26B);
    public static final Color AZUL = new Color(0x3D7DD8);
    public static final Color AZUL_TEXTO = new Color(0x2559A8);
    public static final Color AZUL_SUAVE = new Color(0xD9E7F8);
    public static final Color VERDE = new Color(0x1F7A52);
    public static final Color VERDE_SUAVE = new Color(0xD3EFE1);
    public static final Color ROJO = new Color(0xC8423B);
    public static final Color ROJO_OSCURO = new Color(0x9E2F29);
    public static final Color ROJO_SUAVE = new Color(0xFBE4E2);
    public static final Color BLANCO = new Color(0xFFFDF9);

    // Letras (SansSerif usa las fuentes del sistema y soporta ✓ ✗ ⁻¹)
    public static Font letra(int estilo, float tamano) {
        return new Font(Font.SANS_SERIF, estilo, 1).deriveFont(tamano);
    }

    public static Font codigo(float tamano) {
        return new Font(Font.MONOSPACED, Font.PLAIN, 1).deriveFont(tamano);
    }

    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /** Tarjeta redondeada con una sombra suave. */
    public static void tarjeta(Graphics2D g, double x, double y, double w, double h, double radio, Color fondo) {
        for (int i = 3; i >= 1; i--) {
            g.setColor(new Color(30, 42, 68, 10 + i * 4));
            g.fill(new RoundRectangle2D.Double(x, y + i * 2, w, h, radio, radio));
        }
        g.setColor(fondo);
        g.fill(new RoundRectangle2D.Double(x, y, w, h, radio, radio));
    }

    public static void rectRedondo(Graphics2D g, double x, double y, double w, double h, double radio, Color fondo) {
        g.setColor(fondo);
        g.fill(new RoundRectangle2D.Double(x, y, w, h, radio, radio));
    }

    public static void borde(Graphics2D g, double x, double y, double w, double h, double radio, Color color,
            float grosor) {
        Stroke antes = g.getStroke();
        g.setStroke(new BasicStroke(grosor));
        g.setColor(color);
        g.draw(new RoundRectangle2D.Double(x, y, w, h, radio, radio));
        g.setStroke(antes);
    }

    public static void ovalo(Graphics2D g, double cx, double cy, double w, double h, Color relleno, Color borde) {
        Ellipse2D forma = new Ellipse2D.Double(cx - w / 2, cy - h / 2, w, h);
        g.setColor(relleno);
        g.fill(forma);
        Stroke antes = g.getStroke();
        g.setStroke(new BasicStroke(4f));
        g.setColor(borde);
        g.draw(forma);
        g.setStroke(antes);
    }

    /** Texto centrado en (cx, cy). */
    public static void textoCentrado(Graphics2D g, String texto, double cx, double cy) {
        FontMetrics fm = g.getFontMetrics();
        float x = (float) (cx - fm.stringWidth(texto) / 2.0);
        float y = (float) (cy + (fm.getAscent() - fm.getDescent()) / 2.0);
        g.drawString(texto, x, y);
    }

    /** Escribe un párrafo que se parte en líneas según el ancho. Devuelve la altura usada. */
    public static int parrafo(Graphics2D g, String texto, int x, int y, int ancho, float interlineado) {
        FontMetrics fm = g.getFontMetrics();
        int alto = Math.round(fm.getHeight() * interlineado);
        List<String> lineas = partir(texto, fm, ancho);
        int yy = y + fm.getAscent();
        for (String linea : lineas) {
            g.drawString(linea, x, yy);
            yy += alto;
        }
        return lineas.size() * alto;
    }

    public static List<String> partir(String texto, FontMetrics fm, int ancho) {
        List<String> lineas = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        for (String palabra : texto.split(" ")) {
            String prueba = actual.isEmpty() ? palabra : actual + " " + palabra;
            if (fm.stringWidth(prueba) > ancho && !actual.isEmpty()) {
                lineas.add(actual.toString());
                actual = new StringBuilder(palabra);
            } else {
                actual = new StringBuilder(prueba);
            }
        }
        if (!actual.isEmpty()) {
            lineas.add(actual.toString());
        }
        return lineas;
    }

    /** Pastilla de texto (etiqueta con fondo). Devuelve su ancho. */
    public static int pastilla(Graphics2D g, String texto, int x, int y, Color fondo, Color letra, Font font) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(texto) + 24;
        int h = fm.getHeight() + 8;
        rectRedondo(g, x, y, w, h, h, fondo);
        g.setColor(letra);
        g.drawString(texto, x + 12, y + 4 + fm.getAscent());
        return w;
    }

    /**
     * Flecha de (x1,y1) a (x2,y2). "avance" va de 0 a 1 para animarla creciendo.
     */
    public static void flecha(Graphics2D g, double x1, double y1, double x2, double y2, Color color, float grosor,
            boolean punteada, double avance) {
        if (avance <= 0) {
            return;
        }
        double xf = x1 + (x2 - x1) * avance;
        double yf = y1 + (y2 - y1) * avance;
        Stroke antes = g.getStroke();
        g.setStroke(punteada
                ? new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {10f, 8f}, 0f)
                : new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(color);
        double ang = Math.atan2(y2 - y1, x2 - x1);
        double punta = 10 + grosor * 2.2;
        // la línea termina donde empieza la punta
        double xl = xf - Math.cos(ang) * punta * 0.6;
        double yl = yf - Math.sin(ang) * punta * 0.6;
        g.draw(new Line2D.Double(x1, y1, xl, yl));
        g.setStroke(antes);
        Path2D cabeza = new Path2D.Double();
        cabeza.moveTo(xf, yf);
        cabeza.lineTo(xf - Math.cos(ang - 0.42) * punta, yf - Math.sin(ang - 0.42) * punta);
        cabeza.lineTo(xf - Math.cos(ang + 0.42) * punta, yf - Math.sin(ang + 0.42) * punta);
        cabeza.closePath();
        g.fill(cabeza);
    }

    public static Color conAlfa(Color c, int alfa) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alfa)));
    }

    /** Insignia ✓ / ✗ redonda. */
    public static void insignia(Graphics2D g, boolean ok, int cx, int cy, int radio) {
        g.setColor(ok ? VERDE : ROJO);
        g.fill(new Ellipse2D.Double(cx - radio, cy - radio, radio * 2, radio * 2));
        g.setColor(BLANCO);
        g.setFont(letra(Font.BOLD, radio * 1.25f));
        textoCentrado(g, ok ? "✓" : "✗", cx, cy);
    }
}
