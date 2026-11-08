package simuchip.ui;

import static simuchip.ui.Personitas.ANA;
import static simuchip.ui.Personitas.EXTORSIONADOR;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import simuchip.ui.Personitas.Accesorio;
import simuchip.ui.Personitas.Cara;
import simuchip.ui.Personitas.Look;

/**
 * Capítulo 2: la matemática dentro de la historia. Chips a la izquierda, personitas a la derecha y flechas:
 * función, inyectiva, sobreyectiva, biyectiva, el problema, el palomar y la solución.
 */
public final class PanelMatematica extends PanelEscenas {

    static final Look LUIS = new Look("Luis", new Color(0x2E9BD6), Personitas.PIEL_1, Personitas.PELO_CASTANO,
            Accesorio.CORTO);
    static final Look ROSA = new Look("Rosa", new Color(0x9C5BD9), Personitas.PIEL_3, Personitas.PELO_NEGRO,
            Accesorio.MONO);
    static final Look PEDRO = new Look("Pedro", new Color(0x34A47A), Personitas.PIEL_2, Personitas.PELO_NEGRO,
            Accesorio.CORTO);
    private static final String[] NUM = {"…412", "…087", "…935", "…260"};
    private static final int AZUL = 0;
    private static final int ROJA = 1;
    private static final int VERDE = 2;

    public PanelMatematica() {
        super(List.of(
            new Escena("El registro es una función.",
                "Cada chip apunta a un solo dueño: nunca a cero, nunca a dos. Eso es una función: f(chip) = su dueño."),
            new Escena("Esto NO sería una función.",
                "Un chip con dos dueños: ¿a quién busca la policía? Un chip sin dueño: nadie responde por él."),
            new Escena("Inyectiva: nadie comparte.",
                "A cada persona le llega como mucho un chip. Que Pedro no tenga ninguno no importa."),
            new Escena("Sobreyectiva: nadie se queda fuera.",
                "Todas las personas tienen al menos un chip. Ana tiene dos, y eso se vale."),
            new Escena("Biyectiva: parejas perfectas.",
                "Un chip, una persona, y de vuelta. Con la inversa vas del chip a la persona sin perderte. "
                    + "Eso es lo que necesita la policía."),
            new Escena("El problema: dos flechas que no coinciden.",
                "Cada chip tiene dos flechas: a nombre de quién está (el registro) y quién lo usa de verdad. "
                    + "En el …935 apuntan a personas distintas."),
            new Escena("3 cajones por DNI: el palomar.",
                "Si cada DNI tuviera solo 3 cajones para chips y llegan 5, 2 no caben. Más cosas que cajones: "
                    + "algo se queda fuera. Ese es el principio del palomar."),
            new Escena("La solución: que las dos flechas lleguen a la misma persona.",
                "Que ningún chip se active sin que su dueño lo confirme desde su celular. Así, quien figura en el "
                    + "registro es quien lo usa: chip ↔ persona real.")
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        switch (i) {
            case 0 -> conjuntos(g, a, 4, new Look[] {ANA, LUIS, ROSA}, new int[][] {{0, 0, AZUL}, {1, 1, AZUL},
                {2, 0, AZUL}, {3, 2, AZUL}}, t, "f(…412) = Ana   ·   f(…935) = Ana");
            case 1 -> {
                conjuntos(g, a, 3, new Look[] {ANA, LUIS, ROSA}, new int[][] {{0, 0, ROJA}, {0, 1, ROJA},
                    {1, 2, AZUL}}, t, "Un chip, dos dueños   ·   un chip sin dueño");
                double p = tramo(t, 1.4, 0.4);
                double[] c = centroChip(a, 3, 2);
                Tema.borde(g, c[0] - 46, c[1] - 52, 92, 104, 20, Tema.conAlfa(Tema.ROJO, (int) (255 * p)), 3f);
                g.setFont(Tema.titulo(40f));
                g.setColor(Tema.conAlfa(Tema.ROJO, (int) (255 * p)));
                Tema.textoCentrado(g, "?", c[0] + 80, c[1] + 14);
            }
            case 2 -> conjuntos(g, a, 3, new Look[] {ANA, LUIS, ROSA, PEDRO}, new int[][] {{0, 0, VERDE},
                {1, 1, VERDE}, {2, 2, VERDE}}, t, "Una flecha como máximo por persona");
            case 3 -> conjuntos(g, a, 4, new Look[] {ANA, LUIS, ROSA}, new int[][] {{0, 0, AZUL}, {1, 0, AZUL},
                {2, 1, AZUL}, {3, 2, AZUL}}, t, "Nadie se queda sin flecha");
            case 4 -> {
                conjuntos(g, a, 3, new Look[] {ANA, LUIS, ROSA}, new int[][] {{0, 0, VERDE}, {1, 1, VERDE},
                    {2, 2, VERDE}}, t, "Del chip a la persona, y de la persona al chip");
                // las flechas de vuelta (la inversa)
                for (int k = 0; k < 3; k++) {
                    double[] c = centroChip(a, 3, k);
                    double[] p = centroPersona(a, 3, k);
                    Tema.flecha(g, p[0] - 50, p[1] + 16, c[0] + 40, c[1] + 16, Tema.conAlfa(Tema.VERDE, 150), 2f,
                            true, tramo(t, 1.6 + k * 0.2, 0.5));
                }
            }
            case 5 -> problema(g, a, t, false);
            case 6 -> palomar(g, a, t);
            default -> problema(g, a, t, true);
        }
    }

    // ── chips a la izquierda, personitas a la derecha, flechas animadas ──
    private static double[] centroChip(Rectangle a, int n, int k) {
        double paso = Math.min(110, (a.height - 80) / (double) n);
        double y0 = a.y + 50 + (a.height - 80 - paso * n) / 2 + paso / 2;
        return new double[] {a.x + a.width * 0.18, y0 + k * paso};
    }

    private static double[] centroPersona(Rectangle a, int n, int k) {
        double paso = Math.min(130, (a.height - 80) / (double) n);
        double y0 = a.y + 50 + (a.height - 80 - paso * n) / 2 + paso / 2;
        return new double[] {a.x + a.width * 0.78, y0 + k * paso};
    }

    private void conjuntos(Graphics2D g, Rectangle a, int chips, Look[] gente, int[][] flechas, double t,
            String pie) {
        g.setFont(Tema.letra(Font.BOLD, 16f));
        g.setColor(Tema.NARANJA_TEXTO);
        Tema.textoCentrado(g, "CHIPS", a.x + a.width * 0.18, a.y + 18);
        g.setColor(Tema.AZUL_TEXTO);
        Tema.textoCentrado(g, "PERSONAS (DNI)", a.x + a.width * 0.78, a.y + 18);
        Tema.rectRedondo(g, a.x + a.width * 0.06, a.y + 34, a.width * 0.24, a.height - 70, 40, Tema.NARANJA_SUAVE);
        Tema.rectRedondo(g, a.x + a.width * 0.64, a.y + 34, a.width * 0.3, a.height - 70, 40, Tema.AZUL_SUAVE);
        for (int k = 0; k < chips; k++) {
            double[] c = centroChip(a, chips, k);
            Personitas.chip(g, c[0], c[1] - 10, 40, tramo(t, k * 0.1, 0.3), 0, NUM[k]);
        }
        double paso = Math.min(130, (a.height - 80) / (double) gente.length);
        for (int k = 0; k < gente.length; k++) {
            double[] p = centroPersona(a, gente.length, k);
            double alto = Math.min(100, paso * 0.85);
            Personitas.persona(g, gente[k], p[0] - 10, p[1] + alto / 2, alto, Cara.FELIZ, tramo(t, k * 0.1, 0.3));
            g.setFont(Tema.letra(Font.BOLD, 17f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.texto(g, gente[k].nombre(), p[0] + alto * 0.3, p[1] + 6);
        }
        for (int f = 0; f < flechas.length; f++) {
            double[] c = centroChip(a, chips, flechas[f][0]);
            double[] p = centroPersona(a, gente.length, flechas[f][1]);
            Color col = flechas[f][2] == ROJA ? Tema.ROJO : flechas[f][2] == VERDE ? Tema.VERDE : Tema.AZUL;
            Tema.flecha(g, c[0] + 34, c[1] - 10, p[0] - 56, p[1], col, 3f, false, tramo(t, 0.5 + f * 0.25, 0.5));
        }
        double q = tramo(t, 0.6 + flechas.length * 0.25, 0.4);
        g.setFont(Tema.letra(Font.BOLD, 18f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * q)));
        Tema.textoCentrado(g, pie, a.getCenterX(), a.y + a.height - 6);
    }

    // ── el chip …935 con sus dos flechas: registro y uso real ──
    private void problema(Graphics2D g, Rectangle a, double t, boolean solucion) {
        double cx = a.getCenterX();
        double yChip = a.y + a.height * 0.72;
        double yGente = a.y + a.height * 0.42;
        double xIzq = a.x + a.width * 0.2;
        double xDer = a.x + a.width * 0.8;
        double alto = Math.min(190, a.height * 0.42);
        Personitas.chip(g, cx, yChip, 60, 1, solucion ? 0 : 1, "…935");
        Personitas.persona(g, ANA, xIzq, yGente + alto / 2, alto, solucion ? Cara.FELIZ : Cara.ASUSTADA, 1);
        etiquetaBajo(g, "Ana", xIzq, yGente + alto / 2 + 26);
        if (solucion) {
            Personitas.celular(g, xIzq + alto * 0.42, yGente, alto * 0.42, "✓ Fui yo", Tema.VERDE,
                    tramo(t, 0.3, 0.4));
        } else {
            Personitas.persona(g, EXTORSIONADOR, xDer, yGente + alto / 2, alto, Cara.MALA, 1);
            etiquetaBajo(g, "quien lo usa", xDer, yGente + alto / 2 + 26);
        }
        Tema.flecha(g, cx - 40, yChip - 20, xIzq + alto * 0.3, yGente + 10, Tema.AZUL, 3.5f, false,
                tramo(t, 0.4, 0.6));
        rotulo(g, "registro", (cx + xIzq) / 2 - 30, (yChip + yGente) / 2 - 10, Tema.AZUL_TEXTO, tramo(t, 0.8, 0.3));
        double xFin = solucion ? xIzq + alto * 0.35 : xDer - alto * 0.3;
        Tema.flecha(g, cx + (solucion ? -10 : 40), yChip - 20, xFin, yGente + (solucion ? 40 : 10),
                solucion ? Tema.VERDE : Tema.ROJO, 3.5f, !solucion, tramo(t, 1.2, 0.6));
        rotulo(g, "uso real", solucion ? (cx + xIzq) / 2 + 20 : (cx + xDer) / 2 + 30,
                (yChip + yGente) / 2 + (solucion ? 30 : -10), solucion ? Tema.VERDE : Tema.ROJO_OSCURO,
                tramo(t, 1.6, 0.3));
        double p = tramo(t, 2.0, 0.5);
        g.setFont(Tema.titulo(90f));
        g.setColor(Tema.conAlfa(solucion ? Tema.VERDE : Tema.ROJO, (int) (255 * p)));
        Tema.textoCentrado(g, solucion ? "=" : "≠", cx, yGente);
        g.setFont(Tema.letra(Font.BOLD, 18f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
        Tema.textoCentrado(g, solucion ? "registro = uso real: el chip lleva a la persona correcta"
                : "registro ≠ uso real: la policía llega a la persona equivocada", cx, a.y + a.height - 4);
    }

    private static void etiquetaBajo(Graphics2D g, String s, double cx, double y) {
        g.setFont(Tema.letra(Font.BOLD, 16f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, s, cx, y);
    }

    private static void rotulo(Graphics2D g, String s, double cx, double cy, Color c, double alfa) {
        if (alfa <= 0) {
            return;
        }
        Font f = Tema.letra(Font.BOLD, 15f);
        int w = g.getFontMetrics(f).stringWidth(s) + 24;
        Tema.pastilla(g, s, (int) (cx - w / 2.0), (int) (cy - 14), Tema.conAlfa(Tema.BLANCO, (int) (255 * alfa)),
                Tema.conAlfa(c, (int) (255 * alfa)), f);
    }

    // ── el palomar: 3 cajones bajo el DNI de Ana; los chips 4 y 5 rebotan ──
    private void palomar(Graphics2D g, Rectangle a, double t) {
        double cx = a.getCenterX();
        double cw = 52;
        double dw = Math.min(300, a.width * 0.5);
        double dh = dw * 0.63;
        Personitas.dni(g, cx - dw / 2, a.y, dw, dh, "Ana", 1, ANA);
        double bw = cw * 1.7;
        double bh = cw * 2.1;
        double yCaj = a.y + dh + 40;
        double yFila = a.y + a.height - cw * 1.3 - 20;
        int llenos = 0;
        for (int c = 0; c < 3; c++) {
            double x = cx + (c - 1) * bw * 1.25 - bw / 2;
            boolean lleno = t > 0.4 + c * 0.6 + 0.5;
            llenos += lleno ? 1 : 0;
            Tema.rectRedondo(g, x, yCaj, bw, bh, 18, lleno ? Tema.AZUL_SUAVE : Tema.GRIS_CLARO);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(Tema.AZUL_TEXTO);
            Tema.textoCentrado(g, "cajón " + (c + 1), x + bw / 2, yCaj + bh + 22);
        }
        String[] num = {"…412", "…087", "…935", "…260", "…771"};
        for (int k = 0; k < 5; k++) {
            double x0 = cx + (k - 2) * cw * 2.2;
            double ida = tramo(t, 0.4 + k * 0.6, 0.5);
            double x = x0;
            double y;
            double rojo = 0;
            if (k < 3) {
                x = x0 + (cx + (k - 1) * bw * 1.25 - x0) * ida;
                y = yFila + (yCaj + bh / 2 - yFila) * ida;
            } else {
                double vuelta = tramo(t, 0.4 + k * 0.6 + 0.5, 0.45);
                double tope = yCaj + bh + cw;
                y = yFila + (tope - yFila) * ida - (tope - yFila) * vuelta;
                rojo = vuelta > 0 ? 1 : 0;
            }
            Personitas.chip(g, x, y, cw, 1, rojo, rojo > 0 ? "No cabe" : num[k]);
        }
        g.setFont(Tema.letra(Font.PLAIN, 16f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, "Cajones llenos: " + llenos + " de 3", cx, yCaj - 14);
    }
}
