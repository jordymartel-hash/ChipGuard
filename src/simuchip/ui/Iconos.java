package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** Íconos vectoriales con un mismo trazo (redondeado), centrados en (cx, cy) dentro de un tamaño t. */
public final class Iconos {

    public enum Tipo { VISTO, EQUIS, PLAY, PAUSA, SIGUIENTE, ANTERIOR, REINICIAR, ALERTA, ESCUDO, CHIP, HUELLA,
        DOCUMENTO, TELEFONO, LUPA, PERSONA, TIENDA }

    private Iconos() {}

    public static void dibujar(Graphics2D g, Tipo tipo, double cx, double cy, double t, Color color) {
        Stroke antes = g.getStroke();
        g.setColor(color);
        float trazo = (float) Math.max(1.6, t * 0.11);
        g.setStroke(new BasicStroke(trazo, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double h = t / 2;
        switch (tipo) {
            case VISTO -> {
                Path2D p = new Path2D.Double();
                p.moveTo(cx - h * 0.62, cy + h * 0.02);
                p.lineTo(cx - h * 0.16, cy + h * 0.46);
                p.lineTo(cx + h * 0.66, cy - h * 0.44);
                g.draw(p);
            }
            case EQUIS -> {
                double e = h * 0.5;
                g.draw(new Line2D.Double(cx - e, cy - e, cx + e, cy + e));
                g.draw(new Line2D.Double(cx + e, cy - e, cx - e, cy + e));
            }
            case PLAY -> {
                Path2D p = new Path2D.Double();
                p.moveTo(cx - h * 0.4, cy - h * 0.6);
                p.lineTo(cx + h * 0.62, cy);
                p.lineTo(cx - h * 0.4, cy + h * 0.6);
                p.closePath();
                g.fill(p);
            }
            case PAUSA -> {
                g.fill(new RoundRectangle2D.Double(cx - h * 0.5, cy - h * 0.6, h * 0.34, h * 1.2, 3, 3));
                g.fill(new RoundRectangle2D.Double(cx + h * 0.16, cy - h * 0.6, h * 0.34, h * 1.2, 3, 3));
            }
            case SIGUIENTE, ANTERIOR -> {
                double s = tipo == Tipo.SIGUIENTE ? 1 : -1;
                g.draw(new Line2D.Double(cx - h * 0.6 * s, cy, cx + h * 0.55 * s, cy));
                Path2D p = new Path2D.Double();
                p.moveTo(cx + h * 0.1 * s, cy - h * 0.45);
                p.lineTo(cx + h * 0.58 * s, cy);
                p.lineTo(cx + h * 0.1 * s, cy + h * 0.45);
                g.draw(p);
            }
            case REINICIAR -> {
                g.draw(new Arc2D.Double(cx - h * 0.6, cy - h * 0.6, h * 1.2, h * 1.2, 70, 290, Arc2D.OPEN));
                Path2D p = new Path2D.Double();
                p.moveTo(cx + h * 0.05, cy - h * 0.85);
                p.lineTo(cx + h * 0.28, cy - h * 0.56);
                p.lineTo(cx - h * 0.02, cy - h * 0.36);
                g.draw(p);
            }
            case ALERTA -> {
                Path2D p = new Path2D.Double();
                p.moveTo(cx, cy - h * 0.75);
                p.lineTo(cx + h * 0.8, cy + h * 0.65);
                p.lineTo(cx - h * 0.8, cy + h * 0.65);
                p.closePath();
                g.draw(p);
                g.draw(new Line2D.Double(cx, cy - h * 0.2, cx, cy + h * 0.18));
                g.fill(new Ellipse2D.Double(cx - trazo * 0.6, cy + h * 0.38, trazo * 1.2, trazo * 1.2));
            }
            case ESCUDO -> {
                Path2D p = new Path2D.Double();
                p.moveTo(cx, cy - h * 0.85);
                p.lineTo(cx + h * 0.72, cy - h * 0.55);
                p.quadTo(cx + h * 0.7, cy + h * 0.5, cx, cy + h * 0.88);
                p.quadTo(cx - h * 0.7, cy + h * 0.5, cx - h * 0.72, cy - h * 0.55);
                p.closePath();
                g.draw(p);
            }
            case CHIP -> {
                Path2D p = new Path2D.Double();
                p.moveTo(cx - h * 0.55, cy - h * 0.8);
                p.lineTo(cx + h * 0.25, cy - h * 0.8);
                p.lineTo(cx + h * 0.55, cy - h * 0.5);
                p.lineTo(cx + h * 0.55, cy + h * 0.8);
                p.lineTo(cx - h * 0.55, cy + h * 0.8);
                p.closePath();
                g.draw(p);
                g.draw(new RoundRectangle2D.Double(cx - h * 0.3, cy - h * 0.15, h * 0.6, h * 0.65, 4, 4));
            }
            case HUELLA -> {
                for (int i = 0; i < 4; i++) {
                    double r = h * (0.22 + i * 0.2);
                    g.draw(new Arc2D.Double(cx - r, cy - r + h * 0.1, r * 2, r * 2, 20 + i * 8, 200 - i * 12,
                            Arc2D.OPEN));
                }
            }
            case DOCUMENTO -> {
                g.draw(new RoundRectangle2D.Double(cx - h * 0.85, cy - h * 0.6, h * 1.7, h * 1.2, 6, 6));
                g.draw(new Ellipse2D.Double(cx - h * 0.62, cy - h * 0.3, h * 0.48, h * 0.48));
                g.draw(new Line2D.Double(cx + h * 0.02, cy - h * 0.18, cx + h * 0.6, cy - h * 0.18));
                g.draw(new Line2D.Double(cx + h * 0.02, cy + h * 0.12, cx + h * 0.45, cy + h * 0.12));
            }
            case TELEFONO -> {
                g.draw(new RoundRectangle2D.Double(cx - h * 0.45, cy - h * 0.85, h * 0.9, h * 1.7, h * 0.3, h * 0.3));
                g.draw(new Line2D.Double(cx - h * 0.12, cy + h * 0.6, cx + h * 0.12, cy + h * 0.6));
            }
            case LUPA -> {
                g.draw(new Ellipse2D.Double(cx - h * 0.7, cy - h * 0.7, h * 1.05, h * 1.05));
                g.draw(new Line2D.Double(cx + h * 0.22, cy + h * 0.22, cx + h * 0.75, cy + h * 0.75));
            }
            case PERSONA -> {
                g.draw(new Ellipse2D.Double(cx - h * 0.3, cy - h * 0.8, h * 0.6, h * 0.6));
                g.draw(new Arc2D.Double(cx - h * 0.7, cy - h * 0.05, h * 1.4, h * 1.4, 0, 180, Arc2D.OPEN));
            }
            case TIENDA -> {
                Path2D p = new Path2D.Double();
                p.moveTo(cx - h * 0.8, cy - h * 0.2);
                p.lineTo(cx - h * 0.65, cy - h * 0.7);
                p.lineTo(cx + h * 0.65, cy - h * 0.7);
                p.lineTo(cx + h * 0.8, cy - h * 0.2);
                g.draw(p);
                g.draw(new Line2D.Double(cx - h * 0.8, cy - h * 0.2, cx + h * 0.8, cy - h * 0.2));
                g.draw(new RoundRectangle2D.Double(cx - h * 0.65, cy - h * 0.2, h * 1.3, h * 1.0, 3, 3));
                g.draw(new Line2D.Double(cx - h * 0.15, cy + h * 0.8, cx - h * 0.15, cy + h * 0.3));
                g.draw(new Line2D.Double(cx + h * 0.15, cy + h * 0.8, cx + h * 0.15, cy + h * 0.3));
            }
            default -> { }
        }
        g.setStroke(antes);
    }
}
