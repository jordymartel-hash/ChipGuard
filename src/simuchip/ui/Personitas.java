package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** Personitas, burbujas, DNI, chips y celulares: el elenco dibujado a mano de toda la expo. */
final class Personitas {

    private Personitas() {}

    enum Accesorio { NADA, MONO, CAPUCHA, GORRA, MANDIL, CORTO }

    enum Cara { FELIZ, SERIA, TRISTE, ASUSTADA, MALA }

    /** Cómo se ve una persona. */
    record Look(String nombre, Color camisa, Color piel, Color pelo, Accesorio accesorio) {}

    static final Color PIEL_1 = new Color(0xF2C9A5);
    static final Color PIEL_2 = new Color(0xD9A37C);
    static final Color PIEL_3 = new Color(0xB57C55);
    static final Color PELO_NEGRO = new Color(0x2B2224);
    static final Color PELO_CASTANO = new Color(0x6B4430);

    static final Look ANA = new Look("Ana", new Color(0xFF5E8A), PIEL_2, PELO_NEGRO, Accesorio.MONO);
    static final Look VENDEDOR = new Look("Vendedor", new Color(0x5E6AD2), PIEL_1, PELO_CASTANO, Accesorio.MANDIL);
    static final Look EXTORSIONADOR = new Look("Extorsionador", new Color(0x3A3A44), PIEL_3, PELO_NEGRO,
            Accesorio.CAPUCHA);
    static final Look POLICIA = new Look("Policía", new Color(0x2F6B3A), PIEL_2, PELO_NEGRO, Accesorio.GORRA);
    static final Look VICTIMA = new Look("Don Julio", new Color(0xF2A541), PIEL_1, new Color(0x9A9A9A),
            Accesorio.CORTO);

    /** Dibuja a la persona parada con los pies en (cx, pies). "alto" es la altura total. */
    static void persona(Graphics2D g, Look l, double cx, double pies, double alto, Cara cara, double alfa) {
        if (alfa <= 0) {
            return;
        }
        int a = (int) (255 * Math.min(1, alfa));
        double r = alto * 0.165;
        double cy = pies - alto + r * 1.05;
        double torsoY = cy + r * 1.05;
        double torsoAlto = alto * 0.38;
        double torsoAncho = alto * 0.36;
        double piernaAncho = alto * 0.105;
        double piernaY = torsoY + torsoAlto - 10;
        double piernaAlto = pies - piernaY - alto * 0.04;
        Color piel = l.piel();
        Color pielSombra = piel.darker();
        Color pantalon = new Color(0x2E3350);
        // sombra en el piso
        g.setColor(new Color(0, 0, 0, (int) (28 * Math.min(1, alfa))));
        g.fill(new Ellipse2D.Double(cx - torsoAncho * 0.75, pies - alto * 0.025, torsoAncho * 1.5, alto * 0.05));
        // piernas y zapatos
        g.setColor(Tema.conAlfa(pantalon, a));
        double xi = cx - torsoAncho * 0.34;
        double xd = cx + torsoAncho * 0.34 - piernaAncho;
        g.fill(new RoundRectangle2D.Double(xi, piernaY, piernaAncho, piernaAlto, piernaAncho * 0.6, piernaAncho * 0.6));
        g.fill(new RoundRectangle2D.Double(xd, piernaY, piernaAncho, piernaAlto, piernaAncho * 0.6, piernaAncho * 0.6));
        g.setColor(Tema.conAlfa(new Color(0x1C1C22), a));
        g.fill(new RoundRectangle2D.Double(xi - piernaAncho * 0.25, pies - alto * 0.065, piernaAncho * 1.35, alto * 0.065,
                alto * 0.06, alto * 0.06));
        g.fill(new RoundRectangle2D.Double(xd - piernaAncho * 0.1, pies - alto * 0.065, piernaAncho * 1.35, alto * 0.065,
                alto * 0.06, alto * 0.06));
        // capucha por detrás de la cabeza
        if (l.accesorio() == Accesorio.CAPUCHA) {
            g.setColor(Tema.conAlfa(l.camisa().darker(), a));
            g.fill(new Ellipse2D.Double(cx - r * 1.35, cy - r * 1.3, r * 2.7, r * 2.8));
        }
        // brazos con manos
        double brazo = alto * 0.09;
        double largoBrazo = torsoAlto * 0.82;
        g.setColor(Tema.conAlfa(l.camisa().darker(), a));
        g.fill(new RoundRectangle2D.Double(cx - torsoAncho / 2 - brazo * 0.65, torsoY + r * 0.2, brazo, largoBrazo,
                brazo, brazo));
        g.fill(new RoundRectangle2D.Double(cx + torsoAncho / 2 - brazo * 0.35, torsoY + r * 0.2, brazo, largoBrazo,
                brazo, brazo));
        g.setColor(Tema.conAlfa(piel, a));
        double mano = brazo * 1.05;
        g.fill(new Ellipse2D.Double(cx - torsoAncho / 2 - brazo * 0.65 - (mano - brazo) / 2,
                torsoY + r * 0.2 + largoBrazo - mano * 0.55, mano, mano));
        g.fill(new Ellipse2D.Double(cx + torsoAncho / 2 - brazo * 0.35 - (mano - brazo) / 2,
                torsoY + r * 0.2 + largoBrazo - mano * 0.55, mano, mano));
        // cuello
        g.setColor(Tema.conAlfa(pielSombra, a));
        g.fill(new RoundRectangle2D.Double(cx - r * 0.28, cy + r * 0.6, r * 0.56, r * 0.7, 6, 6));
        // torso con cuello en V
        g.setColor(Tema.conAlfa(l.camisa(), a));
        g.fill(new RoundRectangle2D.Double(cx - torsoAncho / 2, torsoY, torsoAncho, torsoAlto, torsoAncho * 0.5,
                torsoAncho * 0.5));
        Path2D cuelloV = new Path2D.Double();
        cuelloV.moveTo(cx - r * 0.32, torsoY + 1);
        cuelloV.lineTo(cx, torsoY + r * 0.42);
        cuelloV.lineTo(cx + r * 0.32, torsoY + 1);
        cuelloV.closePath();
        g.setColor(Tema.conAlfa(pielSombra, a));
        g.fill(cuelloV);
        if (l.accesorio() == Accesorio.MANDIL) {
            g.setColor(Tema.conAlfa(Tema.BLANCO, a));
            g.fill(new RoundRectangle2D.Double(cx - torsoAncho * 0.3, torsoY + torsoAlto * 0.22, torsoAncho * 0.6,
                    torsoAlto * 0.85, 10, 10));
            g.setColor(Tema.conAlfa(Tema.GRIS_CLARO, a));
            g.fill(new RoundRectangle2D.Double(cx - torsoAncho * 0.15, torsoY + torsoAlto * 0.5, torsoAncho * 0.3,
                    torsoAlto * 0.18, 6, 6));
        }
        if (l.accesorio() == Accesorio.GORRA) {
            g.setColor(Tema.conAlfa(Tema.DORADO, a));
            g.fill(new Ellipse2D.Double(cx + torsoAncho * 0.1, torsoY + torsoAlto * 0.22, r * 0.42, r * 0.42));
        }
        // orejas y cabeza
        g.setColor(Tema.conAlfa(pielSombra, a));
        g.fill(new Ellipse2D.Double(cx - r * 1.12, cy - r * 0.05, r * 0.38, r * 0.45));
        g.fill(new Ellipse2D.Double(cx + r * 0.74, cy - r * 0.05, r * 0.38, r * 0.45));
        g.setColor(Tema.conAlfa(piel, a));
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r * 1.04));
        pelo(g, l, cx, cy, r, a);
        // mejillas
        if (cara == Cara.FELIZ || cara == Cara.ASUSTADA) {
            g.setColor(new Color(0xFF, 0x7A, 0x8A, (int) (70 * Math.min(1, alfa))));
            g.fill(new Ellipse2D.Double(cx - r * 0.78, cy + r * 0.2, r * 0.36, r * 0.22));
            g.fill(new Ellipse2D.Double(cx + r * 0.42, cy + r * 0.2, r * 0.36, r * 0.22));
        }
        // ojos: blanco, pupila y brillo (simples si la persona es chiquita)
        double ojoY = cy - r * 0.02;
        Color oscuro = Tema.conAlfa(new Color(0x231F20), a);
        g.setStroke(new BasicStroke((float) Math.max(1.5, r * 0.09), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (cara == Cara.MALA) {
            g.setColor(oscuro);
            g.draw(new java.awt.geom.Line2D.Double(cx - r * 0.58, ojoY - r * 0.3, cx - r * 0.16, ojoY - r * 0.12));
            g.draw(new java.awt.geom.Line2D.Double(cx + r * 0.58, ojoY - r * 0.3, cx + r * 0.16, ojoY - r * 0.12));
        }
        for (int s = -1; s <= 1; s += 2) {
            double ox = cx + s * r * 0.38;
            if (r > 14) {
                double ow = r * (cara == Cara.ASUSTADA ? 0.36 : 0.3);
                g.setColor(Tema.conAlfa(Tema.BLANCO, a));
                g.fill(new Ellipse2D.Double(ox - ow / 2, ojoY - ow * 0.6, ow, ow * 1.2));
                double pw = ow * 0.62;
                g.setColor(oscuro);
                g.fill(new Ellipse2D.Double(ox - pw / 2, ojoY - pw * 0.45, pw, pw * 1.05));
                g.setColor(Tema.conAlfa(Tema.BLANCO, a));
                g.fill(new Ellipse2D.Double(ox + pw * 0.05, ojoY - pw * 0.35, pw * 0.35, pw * 0.35));
            } else {
                double ojoR = Math.max(1.6, r * 0.13);
                g.setColor(oscuro);
                g.fill(new Ellipse2D.Double(ox - ojoR, ojoY - ojoR, 2 * ojoR, 2 * ojoR));
            }
        }
        // boca
        g.setColor(oscuro);
        double by = cy + r * 0.42;
        switch (cara) {
            case FELIZ -> {
                Path2D sonrisa = new Path2D.Double();
                sonrisa.moveTo(cx - r * 0.3, by - r * 0.06);
                sonrisa.quadTo(cx, by + r * 0.32, cx + r * 0.3, by - r * 0.06);
                sonrisa.closePath();
                g.setColor(Tema.conAlfa(new Color(0x8C2F39), a));
                g.fill(sonrisa);
            }
            case MALA -> g.draw(new Arc2D.Double(cx - r * 0.35, by - r * 0.25, r * 0.7, r * 0.35, 210, 90, Arc2D.OPEN));
            case TRISTE -> g.draw(new Arc2D.Double(cx - r * 0.3, by - r * 0.02, r * 0.6, r * 0.4, 20, 140, Arc2D.OPEN));
            case ASUSTADA -> {
                g.setColor(Tema.conAlfa(new Color(0x8C2F39), a));
                g.fill(new Ellipse2D.Double(cx - r * 0.14, by - r * 0.12, r * 0.28, r * 0.34));
            }
            default -> g.draw(new java.awt.geom.Line2D.Double(cx - r * 0.25, by, cx + r * 0.25, by));
        }
        g.setStroke(new BasicStroke(1f));
    }

    private static void pelo(Graphics2D g, Look l, double cx, double cy, double r, int a) {
        g.setColor(Tema.conAlfa(l.pelo(), a));
        switch (l.accesorio()) {
            case MONO -> {
                g.fill(new Arc2D.Double(cx - r * 1.06, cy - r * 1.1, r * 2.12, r * 1.6, 0, 180, Arc2D.CHORD));
                g.fill(new Ellipse2D.Double(cx - r * 0.42, cy - r * 1.72, r * 0.84, r * 0.84));
                // mechones a los lados
                g.fill(new RoundRectangle2D.Double(cx - r * 1.06, cy - r * 0.4, r * 0.3, r * 0.9, r * 0.3, r * 0.3));
                g.fill(new RoundRectangle2D.Double(cx + r * 0.76, cy - r * 0.4, r * 0.3, r * 0.9, r * 0.3, r * 0.3));
            }
            case CAPUCHA -> {
                g.setColor(Tema.conAlfa(l.camisa().darker(), a));
                g.fill(new Arc2D.Double(cx - r * 1.12, cy - r * 1.22, r * 2.24, r * 1.66, 0, 180, Arc2D.CHORD));
            }
            case GORRA -> {
                g.setColor(Tema.conAlfa(l.camisa().darker(), a));
                g.fill(new Arc2D.Double(cx - r * 1.04, cy - r * 1.18, r * 2.08, r * 1.54, 0, 180, Arc2D.CHORD));
                g.fill(new RoundRectangle2D.Double(cx - r * 0.2, cy - r * 0.5, r * 1.45, r * 0.24, 6, 6));
                g.setColor(Tema.conAlfa(Tema.DORADO, a));
                g.fill(new Ellipse2D.Double(cx - r * 0.15, cy - r * 0.97, r * 0.3, r * 0.3));
            }
            default -> {
                g.fill(new Arc2D.Double(cx - r * 1.04, cy - r * 1.08, r * 2.08, r * 1.36, 0, 180, Arc2D.CHORD));
                // flequillo
                for (int k = 0; k < 3; k++) {
                    g.fill(new Ellipse2D.Double(cx - r * 0.75 + k * r * 0.45, cy - r * 0.68, r * 0.6, r * 0.42));
                }
            }
        }
    }

    /** Burbuja de diálogo; la colita apunta a (px, py). Devuelve la altura. */
    static int burbuja(Graphics2D g, String texto, double px, double py, int ancho, Color fondo, Color letra,
            double alfa) {
        if (alfa <= 0) {
            return 0;
        }
        int a = (int) (255 * Math.min(1, alfa));
        Font f = Tema.letra(Font.BOLD, 19f);
        g.setFont(f);
        var lineas = Tema.partir(texto, g.getFontMetrics(), ancho - 32);
        int maxAncho = 0;
        for (String s : lineas) {
            maxAncho = Math.max(maxAncho, g.getFontMetrics().stringWidth(s));
        }
        int w = maxAncho + 32;
        int lh = g.getFontMetrics().getHeight();
        int h = lineas.size() * lh + 22;
        double x = Math.max(8, px - w / 2.0);
        double y = py - h - 16;
        Tema.sombra(g, x, y, w, h, 20, (int) (22 * alfa));
        g.setColor(Tema.conAlfa(fondo, a));
        g.fill(new RoundRectangle2D.Double(x, y, w, h, 22, 22));
        Path2D cola = new Path2D.Double();
        cola.moveTo(px - 10, y + h - 1);
        cola.lineTo(px, py - 2);
        cola.lineTo(px + 10, y + h - 1);
        cola.closePath();
        g.fill(cola);
        g.setColor(Tema.conAlfa(letra, a));
        for (int i = 0; i < lineas.size(); i++) {
            Tema.texto(g, lineas.get(i), x + 16, y + 11 + g.getFontMetrics().getAscent() + i * lh);
        }
        return h;
    }

    /** Celular pequeño con un texto en pantalla. */
    static void celular(Graphics2D g, double cx, double cy, double alto, String pantalla, Color fondoPantalla,
            double alfa) {
        int a = (int) (255 * Math.min(1, alfa));
        double w = alto * 0.52;
        double x = cx - w / 2;
        double y = cy - alto / 2;
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, a));
        g.fill(new RoundRectangle2D.Double(x, y, w, alto, w * 0.28, w * 0.28));
        g.setColor(Tema.conAlfa(fondoPantalla, a));
        g.fill(new RoundRectangle2D.Double(x + 4, y + 4, w - 8, alto - 8, w * 0.22, w * 0.22));
        if (pantalla != null) {
            g.setFont(Tema.letra(Font.BOLD, (float) Math.max(11, alto * 0.11)));
            g.setColor(Tema.conAlfa(Tema.BLANCO, a));
            Tema.textoCentrado(g, pantalla, cx, cy);
        }
    }

    /** Mostrador de la tienda con su toldo a rayas. */
    static void tienda(Graphics2D g, double x, double yPiso, double w, String nombre) {
        double alto = w * 0.42;
        double y = yPiso - alto;
        // toldo alto, por encima de la cabeza del vendedor, con dos postes
        double yToldo = y - alto * 1.6;
        g.setColor(new Color(0xA86F38));
        g.fill(new RoundRectangle2D.Double(x + 4, yToldo, 8, alto * 1.6, 4, 4));
        g.fill(new RoundRectangle2D.Double(x + w - 12, yToldo, 8, alto * 1.6, 4, 4));
        int franjas = 8;
        for (int i = 0; i < franjas; i++) {
            g.setColor(i % 2 == 0 ? Tema.ROJO : Tema.BLANCO);
            g.fill(new RoundRectangle2D.Double(x - 10 + i * (w + 20) / franjas, yToldo, (w + 20) / franjas + 1,
                    alto * 0.24, 6, 6));
        }
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.titulo((float) (w * 0.06)));
        Tema.textoCentrado(g, nombre, x + w / 2, yToldo - 12);
        // mostrador
        Tema.sombra(g, x, y, w, alto, 16, 20);
        g.setColor(new Color(0xC98B4E));
        g.fill(new RoundRectangle2D.Double(x, y, w, alto, 16, 16));
        g.setColor(new Color(0xA86F38));
        g.fill(new RoundRectangle2D.Double(x - 8, y - 6, w + 16, 14, 8, 8));
    }

    /** Sombra y bola de huella que late. */
    static void huella(Graphics2D g, double cx, double cy, double tam, Color color, double alfa) {
        Iconos.dibujar(g, Iconos.Tipo.HUELLA, cx, cy, tam, Tema.conAlfa(color, (int) (255 * Math.min(1, alfa))));
    }

    /** La tarjeta del DNI con foto genérica y el nombre dado. */
    static void dni(Graphics2D g, double x, double y, double dw, double dh, String nombre, double aparece) {
        dni(g, x, y, dw, dh, nombre, aparece, null);
    }

    /** El DNI con la cara de "foto" en la foto (o la silueta genérica si es null). */
    static void dni(Graphics2D g, double x, double y, double dw, double dh, String nombre, double aparece, Look foto) {
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
        if (foto != null) {
            java.awt.Shape antes = g.getClip();
            g.clip(new RoundRectangle2D.Double(fx, fy, fw, fh, 14, 14));
            persona(g, foto, fx + fw / 2, fy + fh * 1.55, fh * 1.45, Cara.SERIA, 1);
            g.setClip(antes);
        }
        g.setColor(foto != null ? new Color(0, 0, 0, 0) : new Color(0xA9B7CB));
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
        Tema.texto(g, nombre, fx + fw + dw * 0.06, fy + dh * 0.16);
        g.setFont(Tema.codigo((float) (dw * 0.048)));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "DNI 41234560", fx + fw + dw * 0.06, fy + dh * 0.3);
        for (int i = 0; i < 3; i++) {
            Tema.rectRedondo(g, fx + fw + dw * 0.06, fy + dh * (0.4 + i * 0.09), dw * (0.42 - i * 0.08), 6, 6,
                    new Color(0xD5DEEB));
        }
    }

    /** Un chip SIM: tarjeta blanca con esquina cortada y contacto dorado. */
    static void chip(Graphics2D g, double cx, double cy, double ancho, double opacidad, double rojo, String etiqueta) {
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
