package simuchip.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Capítulo 1: el problema. Un DNI al que se le van pegando chips; tres no son de su dueña.
 * Al lado, el titular y las cifras de OSIPTEL contando hacia arriba.
 */
public final class PanelProblema extends JComponent implements Capitulo {

    private static final double[] LLEGADA = {0.35, 1.35, 2.05, 2.75};   // segundos
    private static final double DURACION = 4.6;

    private final Timer reloj;
    private long inicio = System.currentTimeMillis();
    private final Boton siguiente;
    private final Boton otraVez = new Boton("Ver de nuevo", Boton.Estilo.SECUNDARIO).conIcono(Iconos.Tipo.REINICIAR);

    public PanelProblema(Runnable irSiguiente) {
        setLayout(null);
        siguiente = new Boton("Cómo pasa", Boton.Estilo.PRIMARIO).conIcono(Iconos.Tipo.SIGUIENTE).alHacerClic(irSiguiente);
        siguiente.setFont(Tema.medio(16f));
        otraVez.alHacerClic(this::alMostrar);
        add(siguiente);
        add(otraVez);
        reloj = new Timer(16, e -> {
            repaint();
            if (segundos() > DURACION) {
                ((Timer) e.getSource()).stop();
            }
        });
    }

    @Override
    public void alMostrar() {
        inicio = System.currentTimeMillis();
        if (!Animador.instantaneo) {
            reloj.restart();
        }
        repaint();
    }

    private double segundos() {
        return Animador.instantaneo ? 99 : (System.currentTimeMillis() - inicio) / 1000.0;
    }

    /** Avance de 0 a 1 entre dos instantes, con frenado exponencial. */
    private double tramo(double desde, double dura) {
        double t = Math.max(0, Math.min(1, (segundos() - desde) / dura));
        return 1 - Math.pow(1 - t, 3);
    }

    @Override
    public void doLayout() {
        int x = 88;
        int y = getHeight() - 104;
        siguiente.setBounds(x, y, siguiente.getPreferredSize().width, 46);
        otraVez.setBounds(x + siguiente.getPreferredSize().width + 12, y, otraVez.getPreferredSize().width, 46);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Tema.BLANCO);
        g.fillRect(0, 0, w, h);

        int x = 88;
        int ancho = (int) Math.min(640, w * 0.44);
        // titular
        double entra = tramo(0, 0.6);
        int dy = (int) ((1 - entra) * 18);
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * entra)));
        g.setFont(Tema.titulo(Math.min(58f, w / 25f)));
        int y = 56 + dy;
        y += Tema.parrafo(g, "Puede haber chips", x, y, ancho + 200, 1.02f);
        y += Tema.parrafo(g, "a tu nombre.", x, y, ancho + 200, 1.02f);
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * entra)));
        y += Tema.parrafo(g, "Y tú no lo sabes.", x, y, ancho, 1.02f) + 16;

        g.setFont(Tema.letra(Font.PLAIN, 18f));
        g.setColor(Tema.AZUL_NOCHE);
        y += Tema.parrafo(g, "En el Perú se registran líneas con el DNI de otras personas para extorsionar. Cuando la "
                + "policía rastrea el número, llega al dueño en papel: alguien que nunca se enteró.", x, y, ancho,
                1.35f) + 18;

        // cifras como una ficha técnica: número que cuenta + descripción
        String[][] cifras = {
            {"309454", "líneas con datos inconsistentes, de mayo a octubre de 2025"},
            {"102000", "líneas dadas de baja por registros inconsistentes"},
            {"200000", "personas con más de 10 líneas a su nombre"}
        };
        for (int i = 0; i < cifras.length; i++) {
            double p = tramo(0.5 + i * 0.25, 1.6);
            int valor = (int) Math.round(Integer.parseInt(cifras[i][0]) * p);
            String numero = formatear(valor) + (i == 1 ? "+" : i == 2 && p >= 1 ? "" : "");
            if (i == 2) {
                numero = "~" + formatear(valor);
            }
            g.setColor(Tema.FILETE);
            g.fillRect(x, y, ancho, 1);
            g.setFont(Tema.titulo(30f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.texto(g, numero, x, y + 42);
            g.setFont(Tema.letra(Font.PLAIN, 16f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.parrafo(g, cifras[i][1], x + 170, y + 18, ancho - 170, 1.25f);
            y += 54;
        }
        g.setFont(Tema.letra(Font.PLAIN, 13f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "Fuente: OSIPTEL, notas de prensa de 2025.", x, y + 20);
        int yBotones = y + 40;
        if (siguiente.getY() != yBotones) {
            siguiente.setLocation(x, yBotones);
            otraVez.setLocation(x + siguiente.getWidth() + 12, yBotones);
        }

        dibujarDni(g, w, h);
        g.dispose();
    }

    private static String formatear(int n) {
        return String.format("%,d", n).replace(',', '\u2009').replace('.', '\u2009');
    }

    // ─────────────────────────── el DNI y sus chips ───────────────────────────

    private void dibujarDni(Graphics2D g, int w, int h) {
        double cx = w * 0.725;
        double cy = h * 0.40;
        double dw = Math.min(380, w * 0.24);
        double dh = dw * 0.63;
        double x = cx - dw / 2;
        double y = cy - dh / 2;

        // ranuras de los chips alrededor del DNI
        double[][] ranura = {
            {cx - dw * 0.86, cy - dh * 0.55}, {cx + dw * 0.86, cy - dh * 0.62},
            {cx + dw * 0.8, cy + dh * 0.72}, {cx - dw * 0.78, cy + dh * 0.74}
        };
        double[][] origen = {
            {cx - dw * 1.6, cy - dh * 1.4}, {cx + dw * 1.5, cy - dh * 1.5},
            {cx + dw * 1.5, cy + dh * 1.6}, {cx - dw * 1.5, cy + dh * 1.6}
        };

        // líneas de unión (debajo del DNI)
        for (int i = 0; i < 4; i++) {
            double p = tramo(LLEGADA[i] + 0.55, 0.45);
            if (p <= 0) {
                continue;
            }
            boolean ajeno = i > 0;
            Tema.flecha(g, ranura[i][0], ranura[i][1], cx + (ranura[i][0] < cx ? -dw * 0.32 : dw * 0.32),
                    cy + (ranura[i][1] < cy ? -dh * 0.2 : dh * 0.2),
                    ajeno ? Tema.conAlfa(Tema.ROJO, 200) : Tema.conAlfa(Tema.AZUL_NOCHE, 160), 2.2f, ajeno, p);
        }

        // la tarjeta del DNI
        double aparece = tramo(0, 0.7);
        Tema.sombra(g, x, y, dw, dh, 26, (int) (30 * aparece));
        g.setPaint(new GradientPaint((float) x, (float) y, new Color(0xF2F6FB), (float) x, (float) (y + dh),
                new Color(0xE4EBF5)));
        g.fill(new RoundRectangle2D.Double(x, y, dw, dh, 26, 26));
        // foto
        double fx = x + dw * 0.07;
        double fy = y + dh * 0.24;
        double fw = dw * 0.27;
        double fh = dh * 0.62;
        Tema.rectRedondo(g, fx, fy, fw, fh, 14, new Color(0xD5DEEB));
        g.setColor(new Color(0xA9B7CB));
        g.fill(new Ellipse2D.Double(fx + fw * 0.3, fy + fh * 0.16, fw * 0.4, fw * 0.4));
        Path2D hombros = new Path2D.Double();
        hombros.moveTo(fx + fw * 0.12, fy + fh);
        hombros.quadTo(fx + fw * 0.5, fy + fh * 0.48, fx + fw * 0.88, fy + fh);
        hombros.closePath();
        g.fill(hombros);
        // textos
        g.setColor(Tema.TEXTO_SUAVE);
        g.setFont(Tema.medio((float) (dw * 0.034)));
        Tema.texto(g, "DOCUMENTO NACIONAL DE IDENTIDAD", x + dw * 0.07, y + dh * 0.15);
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.titulo((float) (dw * 0.075)));
        Tema.texto(g, "Ana Quispe", fx + fw + dw * 0.06, fy + dh * 0.16);
        g.setFont(Tema.codigo((float) (dw * 0.048)));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "DNI 41234560", fx + fw + dw * 0.06, fy + dh * 0.3);
        for (int i = 0; i < 3; i++) {
            Tema.rectRedondo(g, fx + fw + dw * 0.06, fy + dh * (0.4 + i * 0.09), dw * (0.42 - i * 0.08), 6, 6,
                    new Color(0xD5DEEB));
        }

        // chips
        for (int i = 0; i < 4; i++) {
            double p = tramo(LLEGADA[i], 0.7);
            if (p <= 0) {
                continue;
            }
            double px = origen[i][0] + (ranura[i][0] - origen[i][0]) * p;
            double py = origen[i][1] + (ranura[i][1] - origen[i][1]) * p;
            boolean ajeno = i > 0;
            double rojo = ajeno ? tramo(LLEGADA[i] + 0.7, 0.35) : 0;
            chip(g, px, py, dw * 0.17, p, rojo, i == 0 ? "Tuyo" : "¿Tuyo?");
        }

        // contador
        int llegados = 0;
        for (double t : LLEGADA) {
            if (segundos() > t + 0.7) {
                llegados++;
            }
        }
        double base = cy + dh * 0.5 + 130;
        g.setFont(Tema.letra(Font.PLAIN, 16f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, "Chips a nombre de Ana", cx, base);
        g.setFont(Tema.titulo(56f));
        g.setColor(llegados > 1 ? Tema.ROJO : Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, String.valueOf(llegados), cx, base + 52);
        double fin = tramo(3.6, 0.6);
        if (fin > 0) {
            g.setFont(Tema.letra(Font.BOLD, 17f));
            g.setColor(Tema.conAlfa(Tema.ROJO_OSCURO, (int) (255 * fin)));
            Tema.textoCentrado(g, "3 de ellos los sacó otra persona", cx, base + 104);
        }
    }

    /** Un chip SIM: tarjeta blanca con esquina cortada y contacto dorado. */
    private void chip(Graphics2D g, double cx, double cy, double ancho, double opacidad, double rojo, String etiqueta) {
        double w = ancho;
        double h = ancho * 1.3;
        double x = cx - w / 2;
        double y = cy - h / 2;
        Path2D forma = new Path2D.Double();
        double corte = w * 0.28;
        forma.moveTo(x + 8, y);
        forma.lineTo(x + w - corte, y);
        forma.lineTo(x + w, y + corte);
        forma.lineTo(x + w, y + h - 8);
        forma.quadTo(x + w, y + h, x + w - 8, y + h);
        forma.lineTo(x + 8, y + h);
        forma.quadTo(x, y + h, x, y + h - 8);
        forma.lineTo(x, y + 8);
        forma.quadTo(x, y, x + 8, y);
        Tema.sombra(g, x, y, w, h, 10, (int) (26 * opacidad));
        g.setColor(Tema.conAlfa(Tema.BLANCO, (int) (255 * opacidad)));
        g.fill(forma);
        if (rojo > 0) {
            g.setStroke(new java.awt.BasicStroke((float) (2.5 * rojo)));
            g.setColor(Tema.conAlfa(Tema.ROJO, (int) (255 * rojo)));
            g.draw(forma);
            g.setStroke(new java.awt.BasicStroke(1f));
        }
        // contacto dorado
        double cw = w * 0.52;
        double ch = w * 0.42;
        g.setPaint(new GradientPaint((float) x, (float) y, new Color(0xF2D27A), (float) (x + w), (float) (y + h),
                new Color(0xC99A2E)));
        g.fill(new RoundRectangle2D.Double(cx - cw / 2, cy - ch / 2 + h * 0.08, cw, ch, 6, 6));
        g.setColor(Tema.conAlfa(new Color(0x8A6A1C), (int) (160 * opacidad)));
        g.drawLine((int) (cx - cw / 2), (int) (cy + h * 0.08), (int) (cx + cw / 2), (int) (cy + h * 0.08));
        g.drawLine((int) cx, (int) (cy - ch / 2 + h * 0.08), (int) cx, (int) (cy + ch / 2 + h * 0.08));
        // etiqueta
        if (opacidad >= 0.99) {
            Font f = Tema.letra(Font.BOLD, 13f);
            Color fondo = rojo > 0.5 ? Tema.ROJO : Tema.VERDE;
            int anchoEt = g.getFontMetrics(f).stringWidth(etiqueta) + 20;
            Tema.pastilla(g, etiqueta, (int) (cx - anchoEt / 2.0), (int) (y + h + 8), fondo, Tema.BLANCO, f);
        }
    }
}
