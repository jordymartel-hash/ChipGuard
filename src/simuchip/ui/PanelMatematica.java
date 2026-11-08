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
                "Una función es una regla que a cada elemento del primer conjunto le asigna exactamente un "
                    + "elemento del segundo. Aquí el primer conjunto son los chips y el segundo, las personas. El "
                    + "registro de la operadora le asigna a cada chip un solo dueño: nunca a cero, nunca a dos. "
                    + "Por eso decimos que f(chip) = su dueño. Fíjate: a Ana le llegan dos flechas, y eso está permitido."),
            new Escena("Esto NO sería una función.",
                "Para ser función, de cada chip tiene que salir una flecha, y solo una. Si un chip apuntara a "
                    + "dos dueños, ¿a quién busca la policía? Y si un chip no tuviera dueño, nadie respondería por "
                    + "él: sería un chip anónimo, justo lo que buscan los extorsionadores."),
            new Escena("Inyectiva: nadie comparte.",
                "Una función es inyectiva cuando a dos chips distintos les tocan dueños distintos: a cada persona "
                    + "le llega como mucho una flecha. Que Pedro no tenga ninguna no importa. En símbolos: "
                    + "si c₁ ≠ c₂, entonces f(c₁) ≠ f(c₂)."),
            new Escena("Caso real: los asientos de un bus.",
                "Pasajeros → asientos. Cada pasajero tiene su propio asiento, así que es inyectiva. Sobran "
                    + "asientos vacíos y no pasa nada. Pero si la empresa vende el mismo asiento dos veces "
                    + "(sobreventa), a ese asiento le llegan dos flechas y deja de ser inyectiva: dos personas "
                    + "pelean por el mismo lugar."),
            new Escena("Sobreyectiva: nadie se queda fuera.",
                "Una función es sobreyectiva cuando todos los elementos del segundo conjunto reciben al menos "
                    + "una flecha: no sobra nadie. Aquí cada persona tiene al menos un chip. Ana tiene dos, y eso se "
                    + "vale. En símbolos: para toda persona p existe un chip c tal que f(c) = p."),
            new Escena("Caso real: los grupos de esta expo.",
                "Alumnos → grupos. El profesor reparte a todos y ningún grupo puede quedar vacío: todos reciben "
                    + "a alguien, así que es sobreyectiva. Y como hay más alumnos que grupos, a algún grupo le "
                    + "tocan varios: no puede ser inyectiva."),
            new Escena("Biyectiva: parejas perfectas.",
                "Si una función es inyectiva y sobreyectiva a la vez, es biyectiva: cada chip con una persona y "
                    + "cada persona con un chip. Solo entonces existe la inversa f⁻¹, que va de vuelta de la persona "
                    + "al chip sin perderse. Eso es lo que necesita la policía para llegar a la persona correcta."),
            new Escena("Caso real: tú y tu DNI.",
                "Personas → DNI. Cada persona tiene un DNI y cada DNI es de una sola persona: es biyectiva. Por "
                    + "eso funciona en las dos direcciones. Con tu DNI te encuentran a ti, y contigo encuentran tu "
                    + "DNI. Si alguien usa tu DNI para sacar un chip, rompe esa pareja perfecta."),
            new Escena("El problema: dos flechas que no coinciden.",
                "Cada chip tiene en realidad dos flechas. La del registro dice a nombre de quién está y la del uso "
                    + "real dice quién lo usa de verdad. En el chip …935, la del registro apunta a Ana y la del uso "
                    + "real, al extorsionador. La policía sigue la primera y llega a una inocente."),
            new Escena("3 cajones por DNI: el palomar.",
                "Principio del palomar: si metes más objetos que cajones, algún cajón tiene que quedarse con más de "
                    + "uno o algo queda fuera. Si cada DNI tuviera solo 3 cajones para chips y llegan 5, dos no "
                    + "caben. Así funciona un tope de líneas por persona: nadie puede acumular chips sin límite."),
            new Escena("La solución: que las dos flechas lleguen a la misma persona.",
                "Si ningún chip se activa sin que su dueño lo confirme desde su celular, la flecha del registro y "
                    + "la del uso real terminan en la misma persona. Entonces cada chip lleva a alguien real que "
                    + "dijo «sí, soy yo»: chip ↔ persona, como una biyección. Esa es la idea de ChipGuard.")
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
            case 3 -> bus(g, a, t);
            case 4 -> conjuntos(g, a, 4, new Look[] {ANA, LUIS, ROSA}, new int[][] {{0, 0, AZUL}, {1, 0, AZUL},
                {2, 1, AZUL}, {3, 2, AZUL}}, t, "Nadie se queda sin flecha");
            case 5 -> grupos(g, a, t);
            case 6 -> {
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
            case 7 -> dniCaso(g, a, t);
            case 8 -> problema(g, a, t, false);
            case 9 -> palomar(g, a, t);
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

    // ── caso inyectiva: pasajeros → asientos; al final, una sobreventa ──
    private void bus(Graphics2D g, Rectangle a, double t) {
        Look[] gente = {ANA, LUIS, ROSA, PEDRO};
        double xBus = a.x + a.width * 0.45;
        double wBus = a.width * 0.5;
        double hBus = Math.min(a.height - 60, 470);
        double yBus = a.y + 30;
        Tema.rectRedondo(g, xBus, yBus, wBus, hBus, 46, new Color(0xFFD25A));
        Tema.rectRedondo(g, xBus + 16, yBus + 16, wBus - 32, hBus - 32, 34, Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(Tema.NARANJA_TEXTO);
        Tema.textoCentrado(g, "BUS LIMA → ICA", xBus + wBus / 2, yBus + 40);
        double sw = (wBus - 120) / 2;
        double sh = (hBus - 110) / 3;
        double[][] asiento = new double[6][];
        for (int k = 0; k < 6; k++) {
            double sx = xBus + 40 + (k % 2) * (sw + 40);
            double sy = yBus + 64 + (k / 2) * sh;
            asiento[k] = new double[] {sx + sw / 2, sy + sh / 2 - 6};
            Tema.rectRedondo(g, sx, sy, sw, sh - 14, 16, Tema.GRIS_CLARO);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.texto(g, "asiento " + (k + 1), sx + 10, sy + 22);
        }
        int[] destino = {0, 3, 4, 1};
        double paso = Math.min(110, (a.height - 40) / 4.0);
        for (int k = 0; k < 4; k++) {
            double px = a.x + a.width * 0.12;
            double py = a.y + 40 + k * paso + paso * 0.5;
            Personitas.persona(g, gente[k], px, py + 40, 80, Cara.FELIZ, 1);
            double[] s2 = asiento[destino[k]];
            Tema.flecha(g, px + 40, py, s2[0] - 30, s2[1], Tema.VERDE, 2.6f, false, tramo(t, 0.3 + k * 0.3, 0.5));
            double p = tramo(t, 0.8 + k * 0.3, 0.3);
            Personitas.persona(g, gente[k], s2[0], s2[1] + 30, 60, Cara.FELIZ, p);
        }
        // la sobreventa: Juana recibe el asiento 1, que ya es de Ana
        double q = tramo(t, 2.6, 0.5);
        if (q > 0) {
            Look juana = new Look("Juana", new Color(0x1FA2A0), Personitas.PIEL_2, Personitas.PELO_CASTANO, Accesorio.MONO);
            double px = a.x + a.width * 0.12;
            double py = a.y + a.height - 20;
            Personitas.persona(g, juana, px, py, 80, Cara.TRISTE, q);
            Tema.flecha(g, px + 40, py - 40, asiento[0][0] - 30, asiento[0][1] + 20, Tema.ROJO, 3f, true, q);
            Font f = Tema.letra(Font.BOLD, 16f);
            Tema.pastilla(g, "¡sobreventa! 2 flechas al asiento 1", (int) (xBus + 20), (int) (yBus + hBus + 8),
                    Tema.conAlfa(Tema.ROJO, (int) (255 * q)), Tema.BLANCO, f);
        }
    }

    // ── caso sobreyectiva: 8 alumnos → 4 grupos, ninguno vacío ──
    private void grupos(Graphics2D g, Rectangle a, double t) {
        Look[] base = {ANA, LUIS, ROSA, PEDRO};
        Color[] colorGrupo = {Tema.AZUL, Tema.VERDE, Tema.NARANJA, new Color(0x9C5BD9)};
        double xG = a.x + a.width * 0.62;
        double wG = a.width * 0.34;
        double hG = (a.height - 40) / 4.0;
        int[] cuenta = new int[4];
        for (int k = 0; k < 8; k++) {
            int grupo = k % 4;
            double col = k / 4;
            double px = a.x + a.width * (0.08 + col * 0.16);
            double py = a.y + 30 + (k % 4) * hG + hG * (k / 4 == 0 ? 0.3 : 0.75);
            Look l = base[(k + k / 4) % 4];
            Personitas.persona(g, l, px, py + 32, 70, Cara.FELIZ, 1);
            double p = tramo(t, 0.3 + k * 0.2, 0.5);
            double gy = a.y + 30 + grupo * hG + hG * 0.5;
            Tema.flecha(g, px + 30, py - 14, xG - 8, gy + (k / 4 == 0 ? -10 : 10), colorGrupo[grupo], 2.4f, false, p);
            if (p >= 1) {
                cuenta[grupo]++;
            }
        }
        for (int k = 0; k < 4; k++) {
            double gy = a.y + 30 + k * hG;
            Tema.rectRedondo(g, xG, gy + 6, wG, hG - 12, 22, Tema.conAlfa(colorGrupo[k], 40));
            g.setFont(Tema.titulo(22f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.texto(g, "Grupo " + (k + 1), xG + 22, gy + hG / 2 + 8);
            g.setFont(Tema.letra(Font.BOLD, 17f));
            g.setColor(cuenta[k] > 0 ? Tema.VERDE : Tema.ROJO);
            Tema.texto(g, cuenta[k] + (cuenta[k] == 1 ? " alumno" : " alumnos"), xG + wG - 120, gy + hG / 2 + 8);
        }
    }

    // ── caso biyectiva: persona ↔ DNI ──
    private void dniCaso(Graphics2D g, Rectangle a, double t) {
        Look[] gente = {ANA, LUIS, ROSA};
        double paso = (a.height - 20) / 3.0;
        double dw = Math.min(230, a.width * 0.36);
        double dh = dw * 0.63;
        for (int k = 0; k < 3; k++) {
            double cy = a.y + 10 + k * paso + paso / 2;
            double px = a.x + a.width * 0.12;
            Personitas.persona(g, gente[k], px, cy + 50, 100, Cara.FELIZ, 1);
            double xd = a.x + a.width - dw - 10;
            Personitas.dni(g, xd, cy - dh / 2, dw, dh, gente[k].nombre(), tramo(t, 0.2 + k * 0.2, 0.4), gente[k]);
            Tema.flecha(g, px + 50, cy - 8, xd - 10, cy - 8, Tema.VERDE, 3f, false, tramo(t, 0.6 + k * 0.3, 0.5));
            Tema.flecha(g, xd - 10, cy + 12, px + 50, cy + 12, Tema.conAlfa(Tema.VERDE, 150), 2f, true,
                    tramo(t, 1.6 + k * 0.3, 0.5));
        }
    }

    // ── el chip …935 con sus dos flechas: registro y uso real ──
    private void problema(Graphics2D g, Rectangle a, double t, boolean solucion) {
        if (solucion) {
            solucion(g, a, t);
            return;
        }
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
            Personitas.celular(g, xIzq + alto * 0.62, yGente, alto * 0.42, "✓ Fui yo", Tema.VERDE,
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

    /** Las dos flechas (registro y uso real) suben en V desde el chip y llegan a Ana, una por cada lado. */
    private void solucion(Graphics2D g, Rectangle a, double t) {
        double cx = a.getCenterX();
        double alto = Math.min(200, a.height * 0.42);
        double yGente = a.y + alto / 2 + 20;
        double yChip = a.y + a.height - 110;
        Personitas.persona(g, ANA, cx, yGente + alto / 2, alto, Cara.FELIZ, 1);
        etiquetaBajo(g, "Ana", cx, yGente + alto / 2 + 26);
        double q = tramo(t, 0.3, 0.4);
        double hc = alto * 0.62;
        double xc = cx + alto * 0.62;
        Personitas.celular(g, xc, yGente + 4, hc, "Fui yo", Tema.VERDE, q);
        Iconos.dibujar(g, Iconos.Tipo.VISTO, xc, yGente - hc * 0.18, hc * 0.24, Tema.conAlfa(Tema.BLANCO, (int) (255 * q)));
        Personitas.chip(g, cx, yChip, 60, 1, 0, "…935");

        double yIni = yChip - 40;
        double yFin = yGente + alto * 0.62;
        double abre = alto * 0.42;
        Tema.flecha(g, cx - 18, yIni, cx - abre, yFin, Tema.AZUL, 3.5f, false, tramo(t, 0.4, 0.6));
        Tema.flecha(g, cx + 18, yIni, cx + abre, yFin, Tema.VERDE, 3.5f, false, tramo(t, 1.2, 0.6));
        double yMed = (yIni + yFin) / 2;
        double dx = (abre - 18) / 2 + 18;
        rotulo(g, "registro", cx - dx - 74, yMed, Tema.AZUL_TEXTO, tramo(t, 0.8, 0.3));
        rotulo(g, "uso real", cx + dx + 74, yMed, Tema.VERDE, tramo(t, 1.6, 0.3));

        double p = tramo(t, 2.0, 0.5);
        g.setFont(Tema.titulo(44f));
        g.setColor(Tema.conAlfa(Tema.VERDE, (int) (255 * p)));
        Tema.textoCentrado(g, "=", cx, yMed);
        g.setFont(Tema.letra(Font.BOLD, 18f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
        Tema.textoCentrado(g, "registro = uso real: el chip lleva a la persona correcta", cx, a.y + a.height - 4);
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
