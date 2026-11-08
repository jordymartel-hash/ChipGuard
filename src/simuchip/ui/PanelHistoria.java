package simuchip.ui;

import static simuchip.ui.Personitas.ANA;
import static simuchip.ui.Personitas.EXTORSIONADOR;
import static simuchip.ui.Personitas.POLICIA;
import static simuchip.ui.Personitas.VENDEDOR;
import static simuchip.ui.Personitas.VICTIMA;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import simuchip.ui.Personitas.Cara;
import simuchip.ui.Personitas.Look;

/** Capítulo 1: la historia de Ana, contada con personitas. Casi sin texto: el que narra eres tú. */
public final class PanelHistoria extends PanelEscenas {

    public PanelHistoria() {
        super(List.of(
            new Escena("ChipGuard.", "¿A quién le pertenece tu chip? Funciones inyectivas, sobreyectivas y biyectivas "
                + "aplicadas a las líneas registradas a tu nombre. Proyecto del curso de Matemática Discreta."),
            new Escena("Ella es Ana.", "Tiene 34 años, una bodega en Comas y un solo chip. Hoy va a una tienda de su barrio a sacar uno nuevo para su negocio. Lo que pasa en los próximos minutos le va a cambiar la semana.", true),
            new Escena("«Uy, no salió. Ponga su huella otra vez.»",
                "Para sacar un chip piden tu DNI y tu huella. El vendedor dice que la primera «no salió» y Ana la pone otra vez. Con esa segunda huella, válida para RENIEC, él registra un segundo chip a nombre de Ana. OSIPTEL advierte justamente sobre esta trampa.", true),
            new Escena("El chip extra se vende por la espalda.",
                "Ana se va feliz con su chip. El otro chip, a nombre de Ana, se vende por la espalda a un extorsionador. Para el sistema, ese chip es de Ana; para la ley, la responsable es Ana.", true),
            new Escena("«Paga o te va a ir mal.»",
                "Desde ese chip llaman a Don Julio, un comerciante, y le exigen dinero. Don Julio denuncia y entrega el número. Ese número, en el registro, está a nombre de Ana.",
                true),
            new Escena("La policía rastrea el número… y llega a Ana.",
                "La policía hace lo correcto: rastrea el número en el registro de la operadora. Pero el registro dice que el chip es de Ana. Ella es citada, investigada y asustada, mientras el verdadero culpable sigue libre.", true),
            new Escena("Y no es solo Ana.",
                "En 2025, OSIPTEL encontró cientos de miles de líneas cuyos datos no cuadran con quien las usa, y unas 200 mil personas con más de 10 líneas a su nombre. Muchas de ellas, como Ana, no lo saben.", true)
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        double piso = a.y + a.height - 24;
        double alto = Math.min(250, a.height * 0.62);
        if (i == 0) {
            Tema.portadaEquipo(g, a, tramo(t, 0.1, 0.6));
            return;
        }
        g.setColor(Tema.FILETE);
        g.fillRect(a.x, (int) piso, a.width, 2);
        switch (i - 1) { // la escena 0 es la portada
            case 0 -> anaLlega(g, a, piso, alto, t);
            case 1 -> dobleHuella(g, a, piso, alto, t);
            case 2 -> ventaExtra(g, a, piso, alto, t);
            case 3 -> extorsion(g, a, piso, alto, t);
            case 4 -> policia(g, a, piso, alto, t);
            default -> multitud(g, a, t);
        }
    }

    // ── 0: Ana camina hacia la tienda ──
    private void anaLlega(Graphics2D g, Rectangle a, double piso, double alto, double t) {
        double xt = a.x + a.width * 0.62;
        double wt = Math.min(380, a.width * 0.3);
        Personitas.persona(g, VENDEDOR, xt + wt / 2, piso - alto * 0.25, alto, Cara.FELIZ, 1);
        Personitas.tienda(g, xt, piso, wt, "Chips al toque");
        double p = tramo(t, 0, 1.4);
        double x = a.x + 80 + (xt - 120 - a.x - 80) * p;
        double salto = Math.abs(Math.sin(p * Math.PI * 4)) * 8 * (1 - p);
        Personitas.persona(g, ANA, x, piso - salto, alto, Cara.FELIZ, 1);
        Personitas.celular(g, x + alto * 0.27, piso - alto * 0.5, alto * 0.2, null, Tema.AZUL, 1);
        Personitas.burbuja(g, "¡Hoy saco mi chip nuevo!", x, piso - alto - 10, 340, Tema.BLANCO, Tema.AZUL_NOCHE,
                tramo(t, 1.5, 0.4));
        Personitas.burbuja(g, "¡Bienvenida! DNI y huella, por favor.", xt + wt / 2, piso - alto * 1.25 - 10, 380,
                Tema.AZUL_SUAVE, Tema.AZUL_NOCHE, tramo(t, 2.4, 0.4));
    }

    // ── 1: la huella dos veces y el chip extra ──
    private void dobleHuella(Graphics2D g, Rectangle a, double piso, double alto, double t) {
        double wt = Math.min(420, a.width * 0.36);
        double xt = a.getCenterX() - wt / 2 + 60;
        double yMostrador = piso - wt * 0.42;
        Personitas.persona(g, VENDEDOR, xt + wt * 0.6, piso - alto * 0.25, alto,
                t > 1.2 && t < 3.6 ? Cara.SERIA : Cara.FELIZ, 1);
        Personitas.tienda(g, xt, piso, wt, "Chips al toque");
        Personitas.persona(g, ANA, xt - 70, piso, alto, Cara.FELIZ, 1);
        // las dos huellas sobre el mostrador
        for (int k = 0; k < 2; k++) {
            double inicio = k == 0 ? 0.4 : 2.4;
            double p = tramo(t, inicio, 0.5);
            double lat = 1 + 0.15 * Math.sin(Math.max(0, t - inicio) * 8) * (1 - tramo(t, inicio + 0.6, 0.4));
            Personitas.huella(g, xt + wt * (0.25 + k * 0.17), yMostrador - 26, 40 * lat, k == 0 ? Tema.VERDE : Tema.ROJO,
                    p);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(Tema.conAlfa(k == 0 ? Tema.VERDE : Tema.ROJO_OSCURO, (int) (255 * p)));
            Tema.textoCentrado(g, k == 0 ? "huella 1" : "huella 2", xt + wt * (0.25 + k * 0.17), yMostrador - 54);
        }
        Personitas.burbuja(g, "Uy, no salió. Póngala otra vez.", xt + wt * 0.6, piso - alto * 1.25 - 10, 360,
                Tema.AZUL_SUAVE, Tema.AZUL_NOCHE, tramo(t, 1.2, 0.4) * (1 - tramo(t, 4.3, 0.2)));
        // aparecen dos chips: uno para Ana, otro se esconde bajo el mostrador
        double p = tramo(t, 3.4, 0.9);
        if (t > 3.4) {
            double x1 = xt + wt * 0.3 + (xt - 40 - xt - wt * 0.3) * p;
            Personitas.chip(g, x1, yMostrador - 40 + 30 * p, 44, 1, 0, "de Ana");
            double x2 = xt + wt * 0.45 + (xt + wt + 70 - xt - wt * 0.45) * p;
            Personitas.chip(g, x2, yMostrador - 40 + 120 * p, 44, 1, tramo(t, 3.8, 0.4), "extra");
        }
        Personitas.burbuja(g, "Listo, señito. Su chip.", xt + wt * 0.6, piso - alto * 1.25 - 10, 360,
                Tema.AZUL_SUAVE, Tema.AZUL_NOCHE, t > 4.6 ? tramo(t, 4.6, 0.3) : 0);
        etiqueta(g, "Ana no sabe que se registraron 2 chips con su DNI.", a.getCenterX(), piso + 2,
                tramo(t, 5.2, 0.5));
    }

    // ── 2: Ana feliz a la izquierda; detrás de la tienda, el chip extra cambia de manos ──
    private void ventaExtra(Graphics2D g, Rectangle a, double piso, double alto, double t) {
        double xAna = a.x + a.width * 0.15;
        Personitas.persona(g, ANA, xAna, piso, alto, Cara.FELIZ, 1);
        Personitas.celular(g, xAna + alto * 0.27, piso - alto * 0.5, alto * 0.2, null, Tema.VERDE, 1);
        Personitas.burbuja(g, "¡Ya tengo mi chip!", xAna, piso - alto - 10, 300, Tema.BLANCO, Tema.AZUL_NOCHE,
                tramo(t, 0.3, 0.4));
        // pared que separa
        g.setColor(Tema.GRIS_CLARO);
        g.fillRect((int) (a.x + a.width * 0.36), (int) (piso - alto * 1.3), 10, (int) (alto * 1.3));
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, "mientras tanto, atrás de la tienda…", a.x + a.width * 0.68, a.y + 14);

        double xv = a.x + a.width * 0.55;
        double xe = a.x + a.width * 0.85;
        Personitas.persona(g, VENDEDOR, xv, piso, alto, Cara.MALA, 1);
        Personitas.persona(g, EXTORSIONADOR, xe, piso, alto, Cara.MALA, tramo(t, 0.6, 0.6));
        double p = tramo(t, 1.4, 1.0);
        double yMano = piso - alto * 0.45;
        Personitas.chip(g, xv + 40 + (xe - xv - 80) * p, yMano, 40, 1, 1, "a nombre de Ana");
        // el billete va en sentido contrario
        double xb = xe - 40 - (xe - xv - 80) * p;
        if (t > 1.4) {
            Tema.rectRedondo(g, xb - 30, yMano + 46, 60, 30, 6, new Color(0x3FA34D));
            g.setFont(Tema.letra(Font.BOLD, 13f));
            g.setColor(Tema.BLANCO);
            Tema.textoCentrado(g, "S/ 50", xb, yMano + 66);
        }
        Personitas.burbuja(g, "Chip limpio. No es mío, es de una tal Ana.", xv, piso - alto - 10, 330,
                Tema.BLANCO, Tema.AZUL_NOCHE, tramo(t, 2.6, 0.4));
    }

    // ── 3: la llamada extorsiva ──
    private void extorsion(Graphics2D g, Rectangle a, double piso, double alto, double t) {
        double xe = a.x + a.width * 0.2;
        double xv = a.x + a.width * 0.78;
        Personitas.persona(g, EXTORSIONADOR, xe, piso, alto, Cara.MALA, 1);
        Personitas.celular(g, xe + alto * 0.27, piso - alto * 0.55, alto * 0.22, null, Tema.ROJO, 1);
        Personitas.persona(g, VICTIMA, xv, piso, alto, t > 1.6 ? Cara.ASUSTADA : Cara.FELIZ, 1);
        double yCel = piso - alto * 0.55;
        Personitas.celular(g, xv - alto * 0.3, yCel, alto * 0.42, "…935", Tema.ROJO, tramo(t, 0.6, 0.3));
        Tema.flecha(g, xe + alto * 0.45, yCel, xv - alto * 0.5, yCel, Tema.ROJO, 3f, true, tramo(t, 0.3, 0.8));
        Personitas.burbuja(g, "Paga 5 mil soles o te va a ir mal.", xe + 40, piso - alto - 10, 340, Tema.ROJO,
                Tema.BLANCO, tramo(t, 1.0, 0.4));
        etiqueta(g, "Llama el 987 654 935  ·  registrado a nombre de: Ana", xv - alto * 0.3, yCel + alto * 0.36,
                tramo(t, 2.0, 0.5));
    }

    // ── 4: la policía llega a Ana; el culpable se va ──
    private void policia(Graphics2D g, Rectangle a, double piso, double alto, double t) {
        double xAna = a.x + a.width * 0.25;
        Personitas.persona(g, ANA, xAna, piso, alto, t > 1.8 ? Cara.ASUSTADA : Cara.FELIZ, 1);
        double p = tramo(t, 0, 1.2);
        double xp = a.x + a.width * 0.75 - (a.width * 0.18) * p;
        Personitas.persona(g, POLICIA, xp, piso, alto, Cara.SERIA, 1);
        Personitas.burbuja(g, "Señora, el número que extorsiona está a su nombre.", xp, piso - alto - 10, 360,
                Tema.BLANCO, Tema.AZUL_NOCHE, tramo(t, 1.2, 0.4));
        Personitas.burbuja(g, "¿Yo? ¡Si yo no fui!", xAna, piso - alto - 10, 260, Tema.ROJO, Tema.BLANCO,
                tramo(t, 2.4, 0.4));
        double q = tramo(t, 2.8, 2.5);
        double xe = a.x + a.width * 0.86 + a.width * 0.1 * q;
        Personitas.persona(g, EXTORSIONADOR, xe, piso, alto * 0.6, Cara.MALA, 1 - Math.max(0, (q - 0.6) * 2.5));
        etiqueta(g, "el culpable, libre", a.x + a.width * 0.88, piso + 2, tramo(t, 3.0, 0.4) * (1 - q * 0.5));
    }

    // ── 5: muchas Anas y el contador de OSIPTEL ──
    private void multitud(Graphics2D g, Rectangle a, double t) {
        int cols = 14;
        int filas = 3;
        double paso = Math.min(a.width * 0.6 / cols, 62);
        double alto = paso * 1.25;
        Look[] looks = {ANA, VICTIMA, Personitas.POLICIA, VENDEDOR};
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < cols; c++) {
                int n = f * cols + c;
                Look l = looks[n % looks.length];
                Look triste = new Look(l.nombre(), Tema.ROJO, l.piel(), l.pelo(), l.accesorio() == Personitas.Accesorio.GORRA
                        ? Personitas.Accesorio.CORTO : l.accesorio());
                Personitas.persona(g, triste, a.x + paso / 2 + c * paso, a.y + 30 + alto + f * (alto + 8), alto,
                        Cara.TRISTE, tramo(t, 0.1 + n * 0.035, 0.3));
            }
        }
        double xc = a.x + a.width * 0.66 + 40;
        double p = tramo(t, 0.4, 2.4);
        g.setFont(Tema.titulo(76f));
        g.setColor(Tema.ROJO);
        Tema.texto(g, String.format("%,d", Math.round(309454 * p)).replace(',', ' '), xc, a.y + 110);
        g.setFont(Tema.letra(Font.PLAIN, 20f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.parrafo(g, "líneas con datos inconsistentes (mayo–octubre 2025)", (int) xc, a.y + 128,
                (int) (a.x + a.width - xc), 1.3f);
        double q = tramo(t, 2.4, 0.6);
        g.setFont(Tema.titulo(44f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * q)));
        Tema.texto(g, "~200 000", xc, a.y + 250);
        g.setFont(Tema.letra(Font.PLAIN, 20f));
        Tema.parrafo(g, "personas con más de 10 líneas a su nombre", (int) xc, a.y + 268,
                (int) (a.x + a.width - xc), 1.3f);
        g.setFont(Tema.letra(Font.PLAIN, 13f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "Fuentes: OSIPTEL vía Congreso de la República y Canal N (2025).", xc, a.y + a.height - 6);
    }

    private static void etiqueta(Graphics2D g, String texto, double cx, double y, double alfa) {
        if (alfa <= 0) {
            return;
        }
        Font f = Tema.letra(Font.BOLD, 16f);
        int w = g.getFontMetrics(f).stringWidth(texto) + 24;
        Tema.pastilla(g, texto, (int) (cx - w / 2.0), (int) y + 8, Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * alfa)),
                Tema.conAlfa(Tema.BLANCO, (int) (255 * alfa)), f);
    }
}
