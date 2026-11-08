package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.InputStream;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sistema visual de ChipGuard: minimalismo estilo Apple.
 * Fondo blanco y gris claro, texto casi negro, un azul de acción y colores de sistema con significado
 * (rojo = fraude, verde = seguro, naranja = conjunto A). Tipografía Geist incluida en el proyecto.
 */
public final class Tema {

    private Tema() {}

    // ── Superficies y texto ──
    public static final Color CREMA = new Color(0xFFFFFF);          // superficie principal (blanco)
    public static final Color CELESTE_FONDO = new Color(0xF5F5F7);  // fondo de la ventana (gris Apple)
    public static final Color AZUL_NOCHE = new Color(0x1D1D1F);     // texto principal y paneles oscuros
    public static final Color AZUL_NOCHE_2 = new Color(0x2C2C2E);   // paneles oscuros secundarios
    public static final Color TEXTO_SUAVE = new Color(0x6E6E73);    // texto secundario
    public static final Color TEXTO_OSCURO_SUAVE = new Color(0xA1A1A6); // texto secundario sobre oscuro
    public static final Color GRIS = new Color(0xC7C7CC);
    public static final Color GRIS_CLARO = new Color(0xE8E8ED);     // botones grises, rellenos
    public static final Color FILETE = new Color(0xD2D2D7);
    public static final Color BLANCO = new Color(0xFFFFFF);
    public static final Color FONDO_PAGINA = new Color(0xFBFBFD);   // fondo de los capítulos

    // ── Colores de sistema (con significado) ──
    public static final Color NARANJA = new Color(0xFF9F0A);        // conjunto A
    public static final Color NARANJA_TEXTO = new Color(0xB25000);
    public static final Color NARANJA_SUAVE = new Color(0xFFF2DF);
    public static final Color DORADO = new Color(0xFFD60A);         // resaltado activo
    public static final Color AZUL = new Color(0x0071E3);           // acción y conjunto B
    public static final Color AZUL_TEXTO = new Color(0x0066CC);
    public static final Color AZUL_SUAVE = new Color(0xE8F2FD);
    public static final Color VERDE = new Color(0x248A3D);          // seguro (con texto blanco)
    public static final Color VERDE_SUAVE = new Color(0xE4F6E8);
    public static final Color ROJO = new Color(0xE5372C);           // fraude
    public static final Color ROJO_OSCURO = new Color(0xC4231A);
    public static final Color ROJO_SUAVE = new Color(0xFDECEB);
    // versiones para fondos oscuros
    public static final Color VERDE_CLARO = new Color(0x30D158);
    public static final Color ROJO_CLARO = new Color(0xFF6961);
    public static final Color AZUL_CLARO = new Color(0x409CFF);

    // ── Tipografía ──
    private static final Font REGULAR = cargar("Geist-Regular.ttf");
    private static final Font MEDIO = cargar("Geist-Medium.ttf");
    private static final Font SEMI = cargar("Geist-SemiBold.ttf");
    private static final Font NEGRITA = cargar("Geist-Bold.ttf");
    private static final Font MONO = cargar("GeistMono-Regular.ttf");

    private static Font cargar(String nombre) {
        try (InputStream in = Tema.class.getResourceAsStream("/fuentes/" + nombre)) {
            if (in != null) {
                return Font.createFont(Font.TRUETYPE_FONT, in);
            }
        } catch (Exception e) {
            // se intenta desde el disco
        }
        try {
            File f = new File("recursos/fuentes/" + nombre);
            if (f.exists()) {
                return Font.createFont(Font.TRUETYPE_FONT, f);
            }
        } catch (Exception e) {
            // sin la fuente: se usa la del sistema
        }
        return null;
    }

    private static Font o(Font f, int estilo) {
        return f != null ? f : new Font(Font.SANS_SERIF, estilo, 1);
    }

    /** Texto normal: PLAIN = Regular, BOLD = SemiBold (como en las apps de Apple). */
    public static Font letra(int estilo, float tamano) {
        Font base = (estilo & Font.BOLD) != 0 ? o(SEMI, Font.BOLD) : o(REGULAR, Font.PLAIN);
        return base.deriveFont(tamano);
    }

    public static Font medio(float tamano) {
        return o(MEDIO, Font.PLAIN).deriveFont(tamano);
    }

    /** Titulares: Bold con tracking negativo. */
    public static Font titulo(float tamano) {
        return o(NEGRITA, Font.BOLD).deriveFont(Map.of(TextAttribute.SIZE, tamano, TextAttribute.TRACKING,
                tamano >= 40 ? -0.025f : -0.01f));
    }

    public static Font codigo(float tamano) {
        return o(MONO, Font.PLAIN).deriveFont(tamano);
    }

    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * Dibuja texto. Si la fuente no tiene algún símbolo (⁻, ∀, ∈…), ese carácter se dibuja con la
     * fuente del sistema para que nunca salga un cuadradito.
     */
    public static void texto(Graphics2D g, String s, double x, double y) {
        if (s == null || s.isEmpty()) {
            return;
        }
        Font f = g.getFont();
        if (f.canDisplayUpTo(s) == -1) {
            g.drawString(s, (float) x, (float) y);
            return;
        }
        AttributedString as = new AttributedString(s);
        as.addAttribute(TextAttribute.FONT, f);
        Font alterna = new Font(Font.SANS_SERIF, f.isBold() ? Font.BOLD : Font.PLAIN, 1).deriveFont(f.getSize2D());
        for (int i = 0; i < s.length();) {
            int cp = s.codePointAt(i);
            int n = Character.charCount(cp);
            if (!f.canDisplay(cp)) {
                as.addAttribute(TextAttribute.FONT, alterna, i, i + n);
            }
            i += n;
        }
        g.drawString(as.getIterator(), (float) x, (float) y);
    }

    /** Sombra suave con desplazamiento (simula desenfoque con capas). */
    public static void sombra(Graphics2D g, double x, double y, double w, double h, double radio, int fuerza) {
        for (int i = 6; i >= 1; i--) {
            g.setColor(new Color(0, 0, 0, Math.max(1, fuerza / (i + 1))));
            double e = i * 2.2;
            g.fill(new RoundRectangle2D.Double(x - e / 2, y + 4 + e / 3, w + e, h + e / 2, radio + e, radio + e));
        }
    }

    /** Superficie blanca con sombra suave, como las tarjetas de apple.com. */
    public static void tarjeta(Graphics2D g, double x, double y, double w, double h, double radio, Color fondo) {
        sombra(g, x, y, w, h, radio, 18);
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
        g.setStroke(new BasicStroke(2f));
        g.setColor(conAlfa(borde, 150));
        g.draw(forma);
        g.setStroke(antes);
    }

    public static void textoCentrado(Graphics2D g, String texto, double cx, double cy) {
        FontMetrics fm = g.getFontMetrics();
        double x = cx - fm.stringWidth(texto) / 2.0;
        double y = cy + (fm.getAscent() - fm.getDescent()) / 2.0;
        texto(g, texto, x, y);
    }

    /** Párrafo que se parte en líneas según el ancho. Devuelve la altura usada. */
    public static int parrafo(Graphics2D g, String texto, int x, int y, int ancho, float interlineado) {
        FontMetrics fm = g.getFontMetrics();
        int alto = Math.round(fm.getHeight() * interlineado);
        List<String> lineas = partir(texto, fm, ancho);
        int yy = y + fm.getAscent();
        for (String linea : lineas) {
            texto(g, linea, x, yy);
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

    /** Etiqueta con fondo en forma de píldora. Devuelve su ancho. */
    public static int pastilla(Graphics2D g, String texto, int x, int y, Color fondo, Color letra, Font font) {
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(texto) + 24;
        int h = fm.getHeight() + 8;
        rectRedondo(g, x, y, w, h, h, fondo);
        g.setColor(letra);
        texto(g, texto, x + 12, y + 4 + fm.getAscent());
        return w;
    }

    /** Flecha de (x1,y1) a (x2,y2); "avance" de 0 a 1 la anima creciendo. */
    public static void flecha(Graphics2D g, double x1, double y1, double x2, double y2, Color color, float grosor,
            boolean punteada, double avance) {
        if (avance <= 0) {
            return;
        }
        double xf = x1 + (x2 - x1) * avance;
        double yf = y1 + (y2 - y1) * avance;
        Stroke antes = g.getStroke();
        g.setStroke(punteada
                ? new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {9f, 7f}, 0f)
                : new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(color);
        double ang = Math.atan2(y2 - y1, x2 - x1);
        double punta = 9 + grosor * 2;
        double xl = xf - Math.cos(ang) * punta * 0.6;
        double yl = yf - Math.sin(ang) * punta * 0.6;
        g.draw(new Line2D.Double(x1, y1, xl, yl));
        g.setStroke(antes);
        Path2D cabeza = new Path2D.Double();
        cabeza.moveTo(xf, yf);
        cabeza.lineTo(xf - Math.cos(ang - 0.4) * punta, yf - Math.sin(ang - 0.4) * punta);
        cabeza.lineTo(xf - Math.cos(ang + 0.4) * punta, yf - Math.sin(ang + 0.4) * punta);
        cabeza.closePath();
        g.fill(cabeza);
    }

    public static Color conAlfa(Color c, int alfa) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alfa)));
    }

    /** Insignia redonda con un ícono dibujado: visto (verde) o equis (rojo). */
    public static void insignia(Graphics2D g, boolean ok, int cx, int cy, int radio) {
        g.setColor(ok ? VERDE : ROJO);
        g.fill(new Ellipse2D.Double(cx - radio, cy - radio, radio * 2, radio * 2));
        Iconos.dibujar(g, ok ? Iconos.Tipo.VISTO : Iconos.Tipo.EQUIS, cx, cy, radio * 1.1, BLANCO);
    }
}
