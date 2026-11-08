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
 * Sistema visual de ChipGuard con la paleta de la Universidad Peruana Cayetano Heredia:
 * granate UPCH para acción y conjunto B, amarillo/dorado UPCH para conjunto A y resaltado, gris UPCH para texto
 * secundario. Rojo vivo = fraude, verde = seguro. Tipografía Geist incluida en el proyecto.
 */
public final class Tema {

    private Tema() {}

    // ── Superficies y texto ──
    public static final Color CREMA = new Color(0xFFFFFF);          // superficie principal (blanco)
    public static final Color CELESTE_FONDO = new Color(0xF5F5F7);  // fondo de la ventana (gris Apple)
    public static final Color AZUL_NOCHE = new Color(0x1D1D1F);     // texto principal y paneles oscuros
    public static final Color AZUL_NOCHE_2 = new Color(0x2C2C2E);   // paneles oscuros secundarios
    public static final Color TEXTO_SUAVE = new Color(0x6B6B6A);    // texto secundario (gris UPCH oscurecido)
    public static final Color TEXTO_OSCURO_SUAVE = new Color(0xA1A1A6); // texto secundario sobre oscuro
    public static final Color GRIS = new Color(0xC7C7CC);
    public static final Color GRIS_CLARO = new Color(0xE8E8ED);     // botones grises, rellenos
    public static final Color FILETE = new Color(0xD2D2D7);
    public static final Color BLANCO = new Color(0xFFFFFF);
    public static final Color FONDO_PAGINA = new Color(0xFBFBFD);   // fondo de los capítulos

    // ── Colores de sistema (con significado) ──
    public static final Color NARANJA = new Color(0xD8A200);        // conjunto A (dorado UPCH)
    public static final Color NARANJA_TEXTO = new Color(0x8A6500);
    public static final Color NARANJA_SUAVE = new Color(0xFEF7D6);
    public static final Color DORADO = new Color(0xFCD703);         // resaltado activo (amarillo UPCH)
    public static final Color AZUL = new Color(0xA90A2E);           // acción y conjunto B (granate UPCH)
    public static final Color AZUL_TEXTO = new Color(0x8C0826);
    public static final Color AZUL_SUAVE = new Color(0xFBE9EC);
    public static final Color VERDE = new Color(0x248A3D);          // seguro (con texto blanco)
    public static final Color VERDE_SUAVE = new Color(0xE4F6E8);
    public static final Color ROJO = new Color(0xE5372C);           // fraude
    public static final Color ROJO_OSCURO = new Color(0xC4231A);
    public static final Color ROJO_SUAVE = new Color(0xFDECEB);
    // versiones para fondos oscuros
    public static final Color VERDE_CLARO = new Color(0x30D158);
    public static final Color ROJO_CLARO = new Color(0xFF6961);
    public static final Color AZUL_CLARO = new Color(0xF08DA2);

    public static final Color GRIS_UPCH = new Color(0x9D9D9C);

    // ── Universidad y equipo ──
    public static final String UNIVERSIDAD = "Universidad Peruana Cayetano Heredia";
    public static final String DOCENTE = "Dr. Ing. CIP. Illesca Cangalaya, Omar Felix";
    public static final String ORCID_DOCENTE = "0000-0002-0144-6878";
    /** Apellido y nombre de cada integrante. */
    public static final String[][] INTEGRANTES = {{"Martel", "Jordy"}, {"Sanga", "Angeles"}, {"Tangoa", "Richard"},
        {"Vidalon", "Eduardo"}};
    public static final java.awt.image.BufferedImage LOGO = imagen("logo-upch.png");
    public static final java.awt.image.BufferedImage ESCUDO = imagen("escudo-upch.png");

    private static java.awt.image.BufferedImage imagen(String nombre) {
        try (InputStream in = Tema.class.getResourceAsStream("/imagenes/" + nombre)) {
            if (in != null) {
                return javax.imageio.ImageIO.read(in);
            }
            File f = new File("recursos/imagenes/" + nombre);
            return f.exists() ? javax.imageio.ImageIO.read(f) : null;
        } catch (Exception e) {
            return null; // sin la imagen la app sigue funcionando
        }
    }

    /** Dibuja una imagen escalada a la altura dada, con buena calidad. Devuelve el ancho usado. */
    public static int dibujarImagen(Graphics2D g, java.awt.image.BufferedImage img, double x, double y, double alto) {
        if (img == null) {
            return 0;
        }
        int w = (int) Math.round(img.getWidth() * alto / img.getHeight());
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, (int) x, (int) y, w, (int) alto, null);
        return w;
    }

    /** Tarjeta de portada: logo UPCH, docente con ORCID e integrantes con sus iniciales, centrada en el área. */
    public static void portadaEquipo(Graphics2D g, java.awt.Rectangle a, double alfa) {
        java.awt.Composite antes = g.getComposite();
        g.setComposite(java.awt.AlphaComposite.SrcOver.derive((float) Math.max(0, Math.min(1, alfa))));
        int pad = 36;
        int w = Math.min(a.width, 640);
        int h = 446;
        int x = (int) a.getCenterX() - w / 2;
        int y = (int) Math.max(a.y, a.getCenterY() - h / 2.0 - 10);
        tarjeta(g, x, y, w, h, 28, BLANCO);
        java.awt.Shape clipAntes = g.getClip();
        g.clip(new RoundRectangle2D.Double(x, y, w, h, 28, 28));
        rectRedondo(g, x, y, w, 6, 0, AZUL);          // filete granate arriba
        rectRedondo(g, x, y + h - 6, w, 6, 0, DORADO); // y amarillo abajo
        g.setClip(clipAntes);

        int cy = y + pad;
        dibujarImagen(g, LOGO, x + pad, cy, 76);
        cy += 76 + 26;
        g.setColor(FILETE);
        g.fillRect(x + pad, cy, w - 2 * pad, 1);
        cy += 26;

        etiquetaSeccion(g, "DOCENTE", x + pad, cy);
        cy += 12;
        g.setFont(titulo(21f));
        g.setColor(AZUL_NOCHE);
        cy += parrafo(g, DOCENTE, x + pad, cy, w - 2 * pad, 1.1f) + 4;
        insigniaOrcid(g, x + pad, cy, 18);
        g.setFont(codigo(14f));
        g.setColor(TEXTO_SUAVE);
        texto(g, "ORCID " + ORCID_DOCENTE, x + pad + 26, cy + 14);
        cy += 56;

        etiquetaSeccion(g, "INTEGRANTES", x + pad, cy);
        cy += 20;
        int col = (w - 2 * pad) / 2;
        for (int k = 0; k < INTEGRANTES.length; k++) {
            int ix = x + pad + (k % 2) * col;
            int iy = cy + (k / 2) * 66;
            String ap = INTEGRANTES[k][0];
            String no = INTEGRANTES[k][1];
            g.setColor(AZUL);
            g.fill(new Ellipse2D.Double(ix, iy, 48, 48));
            g.setFont(letra(Font.BOLD, 17f));
            g.setColor(BLANCO);
            textoCentrado(g, "" + no.charAt(0) + ap.charAt(0), ix + 24, iy + 24);
            g.setFont(titulo(19f));
            g.setColor(AZUL_NOCHE);
            texto(g, ap, ix + 62, iy + 21);
            g.setFont(letra(Font.PLAIN, 16f));
            g.setColor(TEXTO_SUAVE);
            texto(g, no, ix + 62, iy + 42);
        }
        g.setComposite(antes);
    }

    private static void etiquetaSeccion(Graphics2D g, String s, int x, int y) {
        g.setFont(letra(Font.BOLD, 12f).deriveFont(Map.of(TextAttribute.TRACKING, 0.12f)));
        g.setColor(AZUL);
        texto(g, s, x, y);
    }

    /** Círculo verde con «iD», como el de ORCID. */
    private static void insigniaOrcid(Graphics2D g, int x, int y, int d) {
        g.setColor(new Color(0xA6CE39));
        g.fill(new Ellipse2D.Double(x, y, d, d));
        g.setFont(letra(Font.BOLD, d * 0.5f));
        g.setColor(BLANCO);
        textoCentrado(g, "iD", x + d / 2.0, y + d / 2.0);
    }

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
