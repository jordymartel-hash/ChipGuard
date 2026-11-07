package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Capítulo 2: cómo pasa. Una línea de tiempo de cuatro estaciones que se dibuja sola,
 * y debajo la respuesta a «¿y la biometría?»: lo que ya existe y el hueco que queda.
 */
public final class PanelCasos extends JComponent implements Capitulo {

    private record Estacion(Iconos.Tipo icono, String titulo, String texto, boolean alarma) {}

    private static final Estacion[] ESTACIONES = {
        new Estacion(Iconos.Tipo.DOCUMENTO, "Tu DNI circula",
            "Dejaste una fotocopia en un trámite, o lo perdiste.", false),
        new Estacion(Iconos.Tipo.TIENDA, "Alguien saca un chip",
            "En un punto de venta que no cumple las reglas, o con un cómplice que presta su huella.", false),
        new Estacion(Iconos.Tipo.TELEFONO, "Lo usan para extorsionar",
            "Desde ese número amenazan a comerciantes o estafan a familias.", false),
        new Estacion(Iconos.Tipo.PERSONA, "La policía llega a ti",
            "El registro dice que el chip es tuyo. Tú nunca te enteraste.", true)
    };

    private static final String[] EXISTE = {
        "Al contratar en tienda se valida tu huella contra RENIEC.",
        "Por web o app, el chip llega por delivery y se activa con tu huella en la app del operador.",
        "Desde 2025, quien tiene más de 10 líneas debe validar su identidad."
    };
    private static final String[] HUECO = {
        "A ti, el dueño del DNI, nadie te avisa cuando registran un chip con tu identidad.",
        "Si en un punto de venta se salta o se engaña la verificación, nadie te pregunta a ti.",
        "Te enteras cuando ya hay un problema: una citación o una llamada."
    };

    private final Timer reloj;
    private long inicio = System.currentTimeMillis();
    private final Boton siguiente;

    public PanelCasos(Runnable irSiguiente) {
        setLayout(null);
        siguiente = new Boton("Ver la matemática", Boton.Estilo.PRIMARIO).conIcono(Iconos.Tipo.SIGUIENTE)
                .alHacerClic(irSiguiente);
        siguiente.setFont(Tema.medio(16f));
        add(siguiente);
        reloj = new Timer(16, e -> {
            repaint();
            if (segundos() > 3.4) {
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

    private double tramo(double desde, double dura) {
        double t = Math.max(0, Math.min(1, (segundos() - desde) / dura));
        return 1 - Math.pow(1 - t, 3);
    }

    @Override
    public void doLayout() {
        int ancho = siguiente.getPreferredSize().width;
        siguiente.setBounds(getWidth() - 88 - ancho, 66, ancho, 46);
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
        g.setFont(Tema.titulo(Math.min(52f, w / 27f)));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "Así sacan un chip a tu nombre.", x, 106);
        g.setFont(Tema.letra(Font.PLAIN, 18f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "Casos ilustrativos, armados a partir de las medidas que OSIPTEL tuvo que dictar.", x, 142);

        dibujarLineaDeTiempo(g, x, 222, w - 2 * x);
        int yBio = Math.max(440, h - 296);
        dibujarBiometria(g, x, yBio, w - 2 * x, h - yBio - 24);
        g.dispose();
    }

    private void dibujarLineaDeTiempo(Graphics2D g, int x, int y, int ancho) {
        int n = ESTACIONES.length;
        double paso = ancho / (double) n;
        double avance = tramo(0.1, 1.8);
        // la línea se dibuja de izquierda a derecha
        double x0 = x + paso / 2;
        double x1 = x + paso * (n - 0.5);
        g.setStroke(new BasicStroke(2f));
        g.setColor(Tema.FILETE);
        g.draw(new Line2D.Double(x0, y, x1, y));
        g.setColor(Tema.AZUL_NOCHE);
        g.draw(new Line2D.Double(x0, y, x0 + (x1 - x0) * avance, y));
        g.setStroke(new BasicStroke(1f));

        for (int i = 0; i < n; i++) {
            Estacion e = ESTACIONES[i];
            double cx = x + paso * (i + 0.5);
            double llega = (cx - x0) / Math.max(1, x1 - x0);
            double p = tramo(0.1 + 1.8 * llega, 0.45);
            if (p <= 0) {
                continue;
            }
            double r = 40 * (0.75 + 0.25 * p);
            Tema.sombra(g, cx - r, y - r, r * 2, r * 2, r * 2, (int) (30 * p));
            g.setColor(e.alarma() ? Tema.ROJO : Tema.BLANCO);
            g.fill(new Ellipse2D.Double(cx - r, y - r, r * 2, r * 2));
            Iconos.dibujar(g, e.icono(), cx, y, 34, e.alarma() ? Tema.BLANCO : Tema.AZUL_NOCHE);

            int alfa = (int) (255 * p);
            g.setFont(Tema.letra(Font.BOLD, 20f));
            g.setColor(Tema.conAlfa(e.alarma() ? Tema.ROJO_OSCURO : Tema.AZUL_NOCHE, alfa));
            int anchoTexto = (int) paso - 36;
            int tx = (int) (cx - anchoTexto / 2.0);
            Tema.parrafo(g, e.titulo(), tx, y + 62, anchoTexto, 1.1f);
            g.setFont(Tema.letra(Font.PLAIN, 16f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, alfa));
            Tema.parrafo(g, e.texto(), tx, y + 94, anchoTexto, 1.35f);
        }
    }

    private void dibujarBiometria(Graphics2D g, int x, int y, int ancho, int alto) {
        double p = tramo(1.9, 0.6);
        int alfa = (int) (255 * p);
        int dy = (int) ((1 - p) * 16);
        Tema.rectRedondo(g, x - 32, y + dy, ancho + 64, alto, 28, Tema.conAlfa(Tema.CELESTE_FONDO, alfa));
        g.setFont(Tema.titulo(30f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, alfa));
        Tema.texto(g, "¿Y la biometría?", x, y + 52 + dy);

        int col = (ancho - 48) / 2;
        columna(g, x, y + 76 + dy, col, "Lo que ya existe", EXISTE, Iconos.Tipo.VISTO, Tema.VERDE, alfa);
        columna(g, x + col + 48, y + 76 + dy, col, "El hueco que queda", HUECO, Iconos.Tipo.ALERTA, Tema.ROJO, alfa);

        g.setFont(Tema.letra(Font.PLAIN, 13f));
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, alfa));
        String fuente = "Fuente: OSIPTEL, normas de contratación móvil y Res. 000116-2025-CD";
        Tema.texto(g, fuente, x + ancho - g.getFontMetrics().stringWidth(fuente), y + 50 + dy);
    }

    private void columna(Graphics2D g, int x, int y, int ancho, String titulo, String[] items, Iconos.Tipo icono,
            Color color, int alfa) {
        g.setFont(Tema.letra(Font.BOLD, 17f));
        g.setColor(Tema.conAlfa(color, alfa));
        Tema.texto(g, titulo, x, y + 16);
        int yy = y + 36;
        for (String item : items) {
            Iconos.dibujar(g, icono, x + 10, yy + 11, 18, Tema.conAlfa(color, alfa));
            g.setFont(Tema.letra(Font.PLAIN, 15.5f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, alfa));
            yy += Tema.parrafo(g, item, x + 32, yy, ancho - 32, 1.3f) + 10;
        }
    }
}
