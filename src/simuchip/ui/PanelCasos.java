package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Capítulo 2: cómo pasa. La línea de tiempo del fraude, la biometría como cerradura cuya llave tiene
 * el vendedor, las tres formas en que se escapa y lo que ya existe frente al hueco que queda.
 */
public final class PanelCasos extends PanelEscenas {

    private record Estacion(Iconos.Tipo icono, String titulo, String texto, boolean alarma) {}

    private static final Estacion[] ESTACIONES = {
        new Estacion(Iconos.Tipo.DOCUMENTO, "Tu identidad se expone",
            "Fotocopias en trámites, datos filtrados, la huella que dejas en un lector.", false),
        new Estacion(Iconos.Tipo.TIENDA, "Sacan un chip a tu nombre",
            "Con trampa en la venta. Las tres formas, en un momento.", false),
        new Estacion(Iconos.Tipo.TELEFONO, "Lo usan para extorsionar",
            "Desde ese número amenazan a comerciantes o estafan a familias.", false),
        new Estacion(Iconos.Tipo.PERSONA, "La policía llega a ti",
            "El registro dice que el chip es tuyo. Tú nunca te enteraste.", true)
    };

    public PanelCasos() {
        super(List.of(
            new Escena("Así sacan un chip a tu nombre.",
                "Casos ilustrativos, armados con lo que OSIPTEL y la prensa han reportado.", true),
            new Escena("La biometría es una buena cerradura.",
                "En cada línea nueva, el lector compara tu huella en vivo con RENIEC. Con solo una fotocopia, en una "
                    + "tienda que cumple, no se puede. El problema: la llave la tiene el vendedor, no tú."),
            new Escena("Tres formas en que se escapa.",
                "La huella responde «¿coincide con el DNI?», y lo responde en la tienda, donde manda el vendedor. "
                    + "Nadie le pregunta al dueño del DNI si quiere esa línea.", true),
            new Escena("Lo que ya existe, y el hueco que queda.",
                "ChipGuard no cambia la cerradura: te pone un timbre en el celular.", true)
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        switch (i) {
            case 0 -> lineaDeTiempo(g, a, t);
            case 1 -> cerradura(g, a, t);
            case 2 -> tresFormas(g, a, t);
            default -> existeYHueco(g, a, t);
        }
    }

    // ── escena 0 ──
    private void lineaDeTiempo(Graphics2D g, Rectangle a, double t) {
        int n = ESTACIONES.length;
        double paso = a.width / (double) n;
        double y = a.y + 70;
        double avance = tramo(t, 0.1, 1.8);
        double x0 = a.x + paso / 2;
        double x1 = a.x + paso * (n - 0.5);
        g.setStroke(new BasicStroke(2f));
        g.setColor(Tema.FILETE);
        g.draw(new Line2D.Double(x0, y, x1, y));
        g.setColor(Tema.AZUL_NOCHE);
        g.draw(new Line2D.Double(x0, y, x0 + (x1 - x0) * avance, y));
        g.setStroke(new BasicStroke(1f));
        for (int k = 0; k < n; k++) {
            Estacion e = ESTACIONES[k];
            double cx = a.x + paso * (k + 0.5);
            double p = tramo(t, 0.1 + 1.8 * (cx - x0) / Math.max(1, x1 - x0), 0.45);
            if (p <= 0) {
                continue;
            }
            double r = 44 * (0.75 + 0.25 * p);
            Tema.sombra(g, cx - r, y - r, r * 2, r * 2, r * 2, (int) (30 * p));
            g.setColor(e.alarma() ? Tema.ROJO : Tema.BLANCO);
            g.fill(new Ellipse2D.Double(cx - r, y - r, r * 2, r * 2));
            Iconos.dibujar(g, e.icono(), cx, y, 36, e.alarma() ? Tema.BLANCO : Tema.AZUL_NOCHE);
            int al = (int) (255 * p);
            int ancho = (int) paso - 40;
            int tx = (int) (cx - ancho / 2.0);
            g.setFont(Tema.titulo(22f));
            g.setColor(Tema.conAlfa(e.alarma() ? Tema.ROJO_OSCURO : Tema.AZUL_NOCHE, al));
            Tema.parrafo(g, e.titulo(), tx, (int) y + 70, ancho, 1.1f);
            g.setFont(Tema.letra(Font.PLAIN, 17f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.parrafo(g, e.texto(), tx, (int) y + 106, ancho, 1.35f);
        }
    }

    // ── escena 1: la llave del vendedor abre el candado... y salen más chips de la cuenta ──
    private void cerradura(Graphics2D g, Rectangle a, double t) {
        double cx = a.getCenterX();
        double cy = a.y + a.height * 0.42;
        double w = Math.min(220, a.width * 0.3);
        double h = w * 0.82;
        double abre = tramo(t, 1.3, 0.5);
        // arco del candado (se levanta al abrir)
        g.setStroke(new BasicStroke((float) (w * 0.11), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(0x8E8E93));
        double ar = w * 0.62;
        g.draw(new Arc2D.Double(cx - ar / 2, cy - h / 2 - ar * 0.55 - abre * 34, ar, ar, 0, 180, Arc2D.OPEN));
        g.draw(new Line2D.Double(cx + ar / 2, cy - h / 2 - ar * 0.05 - abre * 34, cx + ar / 2, cy - h / 2 + 4));
        g.draw(new Line2D.Double(cx - ar / 2, cy - h / 2 - ar * 0.05 - abre * 34, cx - ar / 2,
                cy - h / 2 + 4 - abre * 34));
        g.setStroke(new BasicStroke(1f));
        // cuerpo
        Tema.sombra(g, cx - w / 2, cy - h / 2, w, h, 28, 30);
        Tema.rectRedondo(g, cx - w / 2, cy - h / 2, w, h, 28, Tema.AZUL_NOCHE);
        Iconos.dibujar(g, Iconos.Tipo.HUELLA, cx, cy - 4, w * 0.36, Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, "Biometría · RENIEC", cx, cy + h / 2 + 26);

        // la llave del vendedor entra desde la izquierda
        double llega = tramo(t, 0.3, 0.9);
        double kx = a.x + 20 + (cx - w / 2 - 170 - a.x - 20) * llega;
        llave(g, kx, cy, Tema.NARANJA_TEXTO);
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(Tema.NARANJA_TEXTO);
        Tema.textoCentrado(g, "la llave: el vendedor", kx + 40, cy + 46);

        // al abrir, salen chips: 1 pedido y 2 de más
        String[] etiquetas = {"el tuyo", "de más", "de más"};
        for (int k = 0; k < 3; k++) {
            double p = tramo(t, 1.8 + k * 0.45, 0.5);
            if (p <= 0) {
                continue;
            }
            double sx = cx + w / 2 + 40 + p * (60 + k * 86);
            double sy = cy - 30 + k * 6;
            boolean demas = k > 0;
            Tema.sombra(g, sx - 26, sy - 34, 52, 68, 10, 18);
            Tema.rectRedondo(g, sx - 26, sy - 34, 52, 68, 10, demas ? Tema.ROJO_SUAVE : Tema.BLANCO);
            Iconos.dibujar(g, Iconos.Tipo.CHIP, sx, sy, 30, demas ? Tema.ROJO_OSCURO : Tema.AZUL_NOCHE);
            g.setFont(Tema.letra(Font.BOLD, 13.5f));
            g.setColor(demas ? Tema.ROJO_OSCURO : Tema.VERDE);
            Tema.textoCentrado(g, etiquetas[k], sx, sy + 52);
        }
        // y tú, lejos, sin enterarte
        double fin = tramo(t, 3.4, 0.5);
        if (fin > 0) {
            double px = cx + w / 2 + 230;
            double py = cy + 110;
            Iconos.dibujar(g, Iconos.Tipo.PERSONA, px, py, 40, Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * fin)));
            g.setFont(Tema.letra(Font.BOLD, 15f));
            g.setColor(Tema.conAlfa(Tema.ROJO_OSCURO, (int) (255 * fin)));
            String s = "Tú: no te enteras";
            Tema.texto(g, s, px - 40 - g.getFontMetrics().stringWidth(s), py + 6);
        }
    }

    private void llave(Graphics2D g, double x, double cy, Color c) {
        g.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(c);
        g.draw(new Ellipse2D.Double(x - 16, cy - 16, 32, 32));
        g.draw(new Line2D.Double(x + 16, cy, x + 96, cy));
        g.draw(new Line2D.Double(x + 76, cy, x + 76, cy + 16));
        g.draw(new Line2D.Double(x + 92, cy, x + 92, cy + 12));
        g.setStroke(new BasicStroke(1f));
    }

    // ── escena 2 ──
    private void tresFormas(Graphics2D g, Rectangle a, double t) {
        Object[][] formas = {
            {Iconos.Tipo.TIENDA, "El vendedor tramposo",
                "Pones el dedo para 1 chip y usan esa validación para activar más líneas a tu nombre. Tu huella es "
                    + "real, pero las líneas no las pediste.", "OSIPTEL, citado por el Congreso (2025)"},
            {Iconos.Tipo.HUELLA, "Huellas falsas",
                "Moldes de silicona o plástico que engañan al lector. Por eso OSIPTEL propuso lectores de «huella viva».",
                "El Imparcial (2026)"},
            {Iconos.Tipo.ALERTA, "Puntos que no validan",
                "Puestos donde ni se pasa la huella: ahí basta una copia del DNI. OSIPTEL admitió que siguen existiendo.",
                "Infobae (noviembre de 2025)"}
        };
        double col = (a.width - 2 * 40) / 3.0;
        for (int k = 0; k < 3; k++) {
            double p = tramo(t, 0.2 + k * 0.35, 0.5);
            int al = (int) (255 * p);
            int x = (int) (a.x + k * (col + 40));
            int y = a.y + (int) ((1 - p) * 20);
            g.setColor(Tema.conAlfa(Tema.ROJO_SUAVE, al));
            g.fill(new Ellipse2D.Double(x, y, 64, 64));
            Iconos.dibujar(g, (Iconos.Tipo) formas[k][0], x + 32, y + 32, 30, Tema.conAlfa(Tema.ROJO_OSCURO, al));
            g.setFont(Tema.titulo(24f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, (String) formas[k][1], x, y + 104);
            g.setFont(Tema.letra(Font.PLAIN, 17f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            int altoTexto = Tema.parrafo(g, (String) formas[k][2], x, y + 118, (int) col, 1.4f);
            g.setFont(Tema.letra(Font.PLAIN, 13.5f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.texto(g, "Fuente: " + formas[k][3], x, y + 140 + altoTexto);
        }
        double fin = tramo(t, 1.6, 0.5);
        g.setColor(Tema.conAlfa(Tema.CELESTE_FONDO, (int) (255 * fin)));
        int yb = a.y + a.height - 66;
        g.fill(new RoundRectangle2D.Double(a.x - 20, yb, a.width + 40, 62, 20, 20));
        g.setFont(Tema.letra(Font.BOLD, 17f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * fin)));
        Tema.texto(g, "Caso real: una galería del Centro de Lima activaba hasta 20 líneas por persona; cerca de 1500 "
                + "personas suplantadas (Infobae, 2024).", a.x, yb + 38);
    }

    // ── escena 3 ──
    private void existeYHueco(Graphics2D g, Rectangle a, double t) {
        String[] existe = {
            "En tienda, tu huella se valida contra RENIEC en cada línea nueva.",
            "Por web o app, el chip llega por delivery y se activa con tu huella en la app del operador.",
            "Desde 2025, quien tiene más de 10 líneas debe validar su identidad.",
            "Con «Checa tus líneas» de OSIPTEL puedes ver tus líneas, si entras tú a buscar."
        };
        String[] hueco = {
            "A ti, el dueño del DNI, nadie te avisa cuando registran un chip con tu identidad.",
            "Si el vendedor hace trampa o la huella es falsa, nadie te pregunta a ti.",
            "Te enteras cuando ya hay un problema: una citación o una llamada."
        };
        int col = (a.width - 60) / 2;
        columna(g, a.x, a.y, col, "Lo que ya existe", existe, Iconos.Tipo.VISTO, Tema.VERDE, tramo(t, 0.2, 0.5));
        columna(g, a.x + col + 60, a.y, col, "El hueco que queda", hueco, Iconos.Tipo.ALERTA, Tema.ROJO,
                tramo(t, 0.7, 0.5));
        g.setFont(Tema.letra(Font.PLAIN, 13f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "Fuentes: OSIPTEL (normas de contratación móvil); Res. 000116-2025-CD, según LP Derecho y "
                + "La República.", a.x, a.y + a.height - 6);
    }

    private void columna(Graphics2D g, int x, int y, int ancho, String titulo, String[] items, Iconos.Tipo icono,
            Color color, double p) {
        int al = (int) (255 * p);
        g.setFont(Tema.titulo(24f));
        g.setColor(Tema.conAlfa(color, al));
        Tema.texto(g, titulo, x, y + 22);
        int yy = y + 46;
        for (String item : items) {
            Iconos.dibujar(g, icono, x + 11, yy + 12, 20, Tema.conAlfa(color, al));
            g.setFont(Tema.letra(Font.PLAIN, 17f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            yy += Tema.parrafo(g, item, x + 36, yy, ancho - 36, 1.35f) + 12;
        }
    }
}
