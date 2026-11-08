package simuchip.ui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/** Capítulo 5: el caso de los chips escrito como funciones, paso a paso. */
public final class PanelModelo extends PanelEscenas {

    private static final String[] LINEAS = {"L1", "L2", "L3"};
    private static final String[] PERSONAS = {"Ana", "Beto", "Extorsionador"};

    public PanelModelo() {
        super(List.of(
            new Escena("Tres conjuntos.",
                "L son las líneas (chips). P son las personas con DNI, incluido el extorsionador. C son los "
                    + "contratos validados con huella."),
            new Escena("f: el dueño en papel.",
                "f(línea) es el DNI con el que se registró. Es función, porque cada chip tiene un solo titular. "
                    + "No es inyectiva, y está bien: Ana puede tener dos chips legales."),
            new Escena("u: quien lo usa de verdad.",
                "L3 figura a nombre de Ana, pero lo usa el extorsionador: f(L3) ≠ u(L3). Ese es el problema. "
                    + "Tener varios chips no lo es."),
            new Escena("El tope es el palomar al revés.",
                "|f⁻¹(p)| ≤ k: a cada persona le llegan como máximo k flechas. Con k = 3, un DNI robado no puede "
                    + "acumular cientos de chips: el cuarto se rechaza."),
            new Escena("La solución: g biyectiva.",
                "Cada chip ↔ un contrato con la huella de quien lo usa y el «sí» de su dueño. Como g es biyectiva, "
                    + "existe g⁻¹: desde cualquier chip llegas a una persona real.")
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        switch (i) {
            case 0 -> conjuntos(g, a, t);
            case 1 -> Diagrama.dibujar(g, a.x, a.y, a.width, a.height, LINEAS, PERSONAS,
                    new int[][] {{0, 0}, {1, 0}, {2, 1}}, t,
                    new Diagrama.Opciones().titulos("L · chips", "P · personas").colores(Tema.NARANJA, Tema.AZUL)
                            .notaB(0, "2 chips"));
            case 2 -> Diagrama.dibujar(g, a.x, a.y, a.width, a.height, LINEAS, PERSONAS,
                    new int[][] {{0, 0}, {1, 1}, {2, 0}, {2, 2}}, t,
                    new Diagrama.Opciones().titulos("L · chips", "P · personas").sinContar().rojas(3).punteadas(3)
                            .anilloA(2, Tema.ROJO).notaA(2, "f ≠ u"));
            case 3 -> Diagrama.dibujar(g, a.x, a.y, a.width, a.height, new String[] {"L1", "L2", "L3", "L4"},
                    new String[] {"Ana", "Beto"}, new int[][] {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, t,
                    new Diagrama.Opciones().titulos("L · chips", "P · personas").rojas(3).punteadas(3)
                            .notaA(3, "rechazado").anilloB(0, Tema.ROJO));
            default -> Diagrama.dibujar(g, a.x, a.y, a.width, a.height, LINEAS, new String[] {"C1", "C2", "C3"},
                    new int[][] {{0, 0}, {1, 1}, {2, 2}}, t,
                    new Diagrama.Opciones().titulos("L · chips", "C · contratos con huella")
                            .colores(Tema.NARANJA, Tema.VERDE).conInversa());
        }
    }

    private void conjuntos(Graphics2D g, Rectangle a, double t) {
        Object[][] grupos = {
            {"L", "líneas (chips)", Tema.NARANJA, Iconos.Tipo.CHIP, new String[] {"L1", "L2", "L3"}},
            {"P", "personas (DNI)", Tema.AZUL, Iconos.Tipo.PERSONA, new String[] {"Ana", "Beto", "Extors."}},
            {"C", "contratos con huella", Tema.VERDE, Iconos.Tipo.HUELLA, new String[] {"C1", "C2", "C3"}}
        };
        double ancho = a.width / 3.0;
        for (int k = 0; k < 3; k++) {
            double p = tramo(t, 0.2 + k * 0.3, 0.5);
            double cx = a.x + ancho * (k + 0.5);
            double cy = a.getCenterY() + (1 - p) * 30;
            java.awt.Color c = (java.awt.Color) grupos[k][2];
            int al = (int) (255 * p);
            Tema.ovalo(g, cx, cy, ancho * 0.7, a.height * 0.7, Tema.conAlfa(c, (int) (30 * p)), Tema.conAlfa(c, al));
            Iconos.dibujar(g, (Iconos.Tipo) grupos[k][3], cx, cy - a.height * 0.2, 40, Tema.conAlfa(c, al));
            String[] elementos = (String[]) grupos[k][4];
            g.setFont(Tema.letra(Font.BOLD, 18f));
            for (int e = 0; e < elementos.length; e++) {
                g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
                Tema.textoCentrado(g, elementos[e], cx, cy - a.height * 0.04 + e * 40);
            }
            g.setFont(Tema.titulo(40f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.textoCentrado(g, (String) grupos[k][0], cx, a.y + 20);
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.textoCentrado(g, (String) grupos[k][1], cx, a.y + 52);
        }
    }
}
