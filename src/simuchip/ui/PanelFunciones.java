package simuchip.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.List;

/** Capítulo 3: la teoría de funciones, escena por escena, con dibujos animados. */
public final class PanelFunciones extends PanelEscenas {

    public PanelFunciones() {
        super(List.of(
            new Escena("Una función es una máquina.",
                "Entra algo y sale exactamente una cosa. Nunca cero, nunca dos. Esta máquina duplica: si entra 3, sale 6."),
            new Escena("Se dibuja con dos óvalos y flechas.",
                "El óvalo de la izquierda es el dominio (A): lo que entra. El de la derecha es el codominio (B): lo que "
                    + "podría salir. La imagen son los de B que sí reciben flecha."),
            new Escena("Esto no es una función.",
                "Del 1 salen dos flechas: la máquina dio dos respuestas. Del 3 no sale ninguna: se quedó callada. "
                    + "Cualquiera de las dos cosas la descalifica."),
            new Escena("Inyectiva: nadie comparte.",
                "A ninguna letra de B le llegan dos flechas. Como el código universitario: cada alumno tiene el suyo y "
                    + "nadie lo comparte. La «d» sin flecha no importa.   x₁ ≠ x₂ ⇒ f(x₁) ≠ f(x₂)"),
            new Escena("Sobreyectiva: nadie se queda solo.",
                "Toda letra de B recibe al menos una flecha. Como 30 alumnos en 25 carpetas sin dejar ninguna vacía: "
                    + "algunos tendrán que compartir.   ∀ y ∈ B, ∃ x ∈ A : f(x) = y"),
            new Escena("Biyectiva: parejas perfectas.",
                "Inyectiva y sobreyectiva a la vez. Solo entonces existe la inversa f⁻¹: puedes ir y volver sin "
                    + "perderte. Como DNI y persona: con uno llegas al otro."),
            new Escena("Cuatro preguntas para clasificar cualquier función.",
                "Todo se reduce a contar flechas: las que salen de A y las que llegan a B.", true),
            new Escena("Las funciones están en tu día a día.",
                "Muchos sistemas funcionan solo si su función cumple una propiedad. Cuando se rompe, aparece un "
                    + "problema real.", true)
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        String[] tres = {"1", "2", "3"};
        String[] abc = {"a", "b", "c"};
        switch (i) {
            case 0 -> maquina(g, a, t);
            case 1 -> {
                Diagrama.dibujar(g, a.x, a.y + 10, a.width, a.height - 70, new String[] {"Ana", "Luis", "Sofía"},
                        new String[] {"rojo", "azul", "verde"}, new int[][] {{0, 0}, {1, 1}, {2, 0}}, t,
                        new Diagrama.Opciones().titulos("A · dominio", "B · codominio").sinContar());
                double p = tramo(t, 1.4, 0.5);
                g.setFont(Tema.letra(Font.BOLD, 17f));
                g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
                Tema.textoCentrado(g, "Imagen = { rojo, azul }   ·   «verde» no recibe flecha", a.getCenterX(),
                        a.y + a.height - 22);
            }
            case 2 -> Diagrama.dibujar(g, a.x, a.y, a.width, a.height, tres, abc,
                    new int[][] {{0, 0}, {0, 1}, {1, 2}}, t,
                    new Diagrama.Opciones().rojas(1).anilloA(0, Tema.ROJO).anilloA(2, Tema.ROJO)
                            .notaA(0, "2 flechas").notaA(2, "ninguna"));
            case 3 -> Diagrama.dibujar(g, a.x, a.y, a.width, a.height, tres, new String[] {"a", "b", "c", "d"},
                    new int[][] {{0, 0}, {1, 1}, {2, 2}}, t, new Diagrama.Opciones());
            case 4 -> Diagrama.dibujar(g, a.x, a.y, a.width, a.height, new String[] {"1", "2", "3", "4"}, abc,
                    new int[][] {{0, 0}, {1, 0}, {2, 1}, {3, 2}}, t,
                    new Diagrama.Opciones().anilloB(0, Tema.AZUL));
            case 5 -> Diagrama.dibujar(g, a.x, a.y, a.width, a.height, tres, abc,
                    new int[][] {{0, 0}, {1, 1}, {2, 2}}, t, new Diagrama.Opciones().conInversa());
            case 6 -> resumen(g, a, t);
            default -> diaADia(g, a, t);
        }
    }

    // ── escena 0: la máquina procesa números en bucle ──
    private void maquina(Graphics2D g, Rectangle a, double t) {
        double cy = a.getCenterY() - 30;
        double xEntrada = a.x + a.width * 0.1;
        double xSalida = a.x + a.width * 0.9;
        double mw = a.width * 0.42;
        double mh = Math.min(220, a.height * 0.42);
        double mx = a.getCenterX() - mw / 2;
        double ciclo = 2.6;
        int vuelta = Animador.instantaneo ? 2 : (int) (t / ciclo);
        double f = Animador.instantaneo ? 1 : (t % ciclo) / ciclo;
        int entrada = vuelta % 4 + 1;

        // la máquina (late cuando procesa)
        double latido = f > 0.35 && f < 0.55 ? Math.sin((f - 0.35) / 0.2 * Math.PI) : 0;
        Tema.sombra(g, mx, cy - mh / 2, mw, mh, 36, 30);
        Tema.rectRedondo(g, mx - latido * 6, cy - mh / 2 - latido * 6, mw + latido * 12, mh + latido * 12, 36,
                Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.PLAIN, 16f));
        g.setColor(Tema.TEXTO_OSCURO_SUAVE);
        Tema.textoCentrado(g, "máquina f", a.getCenterX(), cy - mh * 0.2);
        g.setFont(Tema.titulo(46f));
        g.setColor(Tema.BLANCO);
        Tema.textoCentrado(g, "f(x) = 2x", a.getCenterX(), cy + mh * 0.1);

        // bola que entra
        if (f < 0.4) {
            double p = tramo(f * ciclo, 0, 0.9);
            double x = xEntrada + (mx + 20 - xEntrada) * p;
            bola(g, x, cy, String.valueOf(entrada), Tema.NARANJA, Tema.AZUL_NOCHE, 1 - Math.max(0, (p - 0.85) * 6));
        }
        // bola que sale
        if (f > 0.5) {
            double p = tramo((f - 0.5) * ciclo, 0, 0.8);
            double x = mx + mw - 20 + (xSalida - (mx + mw - 20)) * p;
            bola(g, x, cy, String.valueOf(entrada * 2), Tema.AZUL, Tema.BLANCO, Math.min(1, p * 3));
        }
        // historial de pares calculados
        g.setFont(Tema.codigo(20f));
        int hechos = Animador.instantaneo ? 4 : Math.min(4, vuelta + (f > 0.8 ? 1 : 0));
        StringBuilder s = new StringBuilder();
        for (int k = 1; k <= hechos; k++) {
            s.append(k > 1 ? "     " : "").append(k).append(" → ").append(k * 2);
        }
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, s.toString(), a.getCenterX(), cy + mh / 2 + 70);
        g.setFont(Tema.letra(Font.PLAIN, 16f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, "cada entrada → exactamente una salida", a.getCenterX(), cy + mh / 2 + 108);
    }

    private void bola(Graphics2D g, double cx, double cy, String texto, Color fondo, Color letra, double alfa) {
        bola(g, cx, cy, texto, fondo, letra, alfa, 46);
    }

    private void bola(Graphics2D g, double cx, double cy, String texto, Color fondo, Color letra, double alfa,
            double r) {
        int al = (int) (255 * Math.max(0, Math.min(1, alfa)));
        Tema.sombra(g, cx - r, cy - r, r * 2, r * 2, r * 2, (int) (22 * alfa));
        g.setColor(Tema.conAlfa(fondo, al));
        g.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        g.setFont(Tema.titulo((float) (r * 0.87)));
        g.setColor(Tema.conAlfa(letra, al));
        Tema.textoCentrado(g, texto, cx, cy);
    }

    // ── escena 6: resumen en una tabla limpia ──
    private void resumen(Graphics2D g, Rectangle a, double t) {
        String[][] filas = {
            {"Función", "De cada elemento de A sale una flecha", "¿Alguien de A tiene 0 o 2?", "cualquiera"},
            {"Inyectiva", "Nadie de B comparte flecha", "¿Alguien de B recibe 2?", "|A| ≤ |B|"},
            {"Sobreyectiva", "Nadie de B se queda solo", "¿Alguien de B recibe 0?", "|A| ≥ |B|"},
            {"Biyectiva", "Parejas perfectas, con inversa f⁻¹", "¿Las dos de arriba dan «no»?", "|A| = |B|"}
        };
        Color[] tonos = {Tema.AZUL_NOCHE, Tema.AZUL_TEXTO, Tema.NARANJA_TEXTO, Tema.VERDE};
        double[] col = {0, 0.2, 0.55, 0.85};
        int alto = Math.min(84, (a.height - 50) / 4);
        g.setFont(Tema.letra(Font.BOLD, 14f));
        g.setColor(Tema.TEXTO_SUAVE);
        String[] cab = {"Tipo", "En palabras", "Pregunta clave", "Tamaños"};
        for (int c = 0; c < 4; c++) {
            Tema.texto(g, cab[c], a.x + a.width * col[c], a.y + 18);
        }
        for (int f = 0; f < filas.length; f++) {
            double p = tramo(t, 0.3 + f * 0.25, 0.45);
            int y = a.y + 34 + f * alto;
            g.setColor(Tema.FILETE);
            g.fillRect(a.x, y, a.width, 1);
            int al = (int) (255 * p);
            g.setFont(Tema.titulo(26f));
            g.setColor(Tema.conAlfa(tonos[f], al));
            Tema.texto(g, filas[f][0], a.x, y + alto / 2.0 + 9);
            g.setFont(Tema.letra(Font.PLAIN, 18f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, filas[f][1], a.x + a.width * col[1], y + alto / 2.0 + 7);
            Tema.texto(g, filas[f][2], a.x + a.width * col[2], y + alto / 2.0 + 7);
            g.setFont(Tema.codigo(18f));
            Tema.texto(g, filas[f][3], a.x + a.width * col[3], y + alto / 2.0 + 7);
        }
    }

    // ── escena 8: cuatro sistemas del día a día ──
    private void diaADia(Graphics2D g, Rectangle a, double t) {
        Object[][] filas = {
            {Iconos.Tipo.PERSONA, "Persona → DNI", "Biyectiva", "Si falla: suplantación de identidad"},
            {Iconos.Tipo.DOCUMENTO, "Alumno → código universitario", "Inyectiva", "Si falla: se mezclan notas y pagos"},
            {Iconos.Tipo.TIENDA, "Boleto → asiento", "Inyectiva", "Si falla: sobreventa en buses y vuelos"},
            {Iconos.Tipo.ESCUDO, "Mensaje → mensaje cifrado", "Biyectiva", "Si falla: ya no se puede descifrar"}
        };
        int alto = Math.min(84, (a.height - 10) / 4);
        for (int f = 0; f < filas.length; f++) {
            double p = tramo(t, 0.3 + f * 0.3, 0.45);
            int y = a.y + f * alto;
            int al = (int) (255 * p);
            int dx = (int) ((1 - p) * 30);
            if (f > 0) {
                g.setColor(Tema.FILETE);
                g.fillRect(a.x, y, a.width, 1);
            }
            Iconos.dibujar(g, (Iconos.Tipo) filas[f][0], a.x + 26 + dx, y + alto / 2.0, 30,
                    Tema.conAlfa(Tema.AZUL_NOCHE, al));
            g.setFont(Tema.titulo(25f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, (String) filas[f][1], a.x + 70 + dx, y + alto / 2.0 + 9);
            boolean biy = filas[f][2].equals("Biyectiva");
            Font fp = Tema.letra(Font.BOLD, 15f);
            Tema.pastilla(g, (String) filas[f][2], (int) (a.x + a.width * 0.48) + dx, (int) (y + alto / 2.0 - 15),
                    Tema.conAlfa(biy ? Tema.VERDE_SUAVE : Tema.AZUL_SUAVE, al),
                    Tema.conAlfa(biy ? Tema.VERDE : Tema.AZUL_TEXTO, al), fp);
            g.setFont(Tema.letra(Font.PLAIN, 17f));
            g.setColor(Tema.conAlfa(Tema.ROJO_OSCURO, al));
            Tema.texto(g, (String) filas[f][3], a.x + a.width * 0.64 + dx, y + alto / 2.0 + 6);
        }
    }
}
