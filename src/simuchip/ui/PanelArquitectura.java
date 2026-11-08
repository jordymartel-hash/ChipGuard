package simuchip.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 * Capítulo: la arquitectura de ChipGuard. Las seis piezas, el viaje de una solicitud paso a paso,
 * qué pasa si el dueño dice «No fui yo», los casos sin smartphone, qué datos guarda y con qué se construye.
 */
public final class PanelArquitectura extends PanelEscenas {

    // nodos: posición relativa en el área, ícono, nombre, detalle y si ChipGuard lo agrega
    private static final double[][] POS = {{0.1, 0.45}, {0.37, 0.45}, {0.37, 0.1}, {0.64, 0.45}, {0.9, 0.45},
        {0.64, 0.84}};
    private static final Iconos.Tipo[] ICONO = {Iconos.Tipo.TIENDA, Iconos.Tipo.CHIP, Iconos.Tipo.HUELLA,
        Iconos.Tipo.ESCUDO, Iconos.Tipo.TELEFONO, Iconos.Tipo.DOCUMENTO};
    private static final String[] NOMBRE = {"Tienda", "Operadora", "RENIEC", "Servidor ChipGuard",
        "App del dueño", "OSIPTEL"};
    private static final String[] DETALLE = {"lector de huella", "activa las líneas", "valida la huella",
        "pide permiso al dueño", "en su celular", "registro y reportes"};
    private static final boolean[] NUEVO = {false, false, false, true, true, false};
    private static final int TIENDA = 0;
    private static final int OPERADORA = 1;
    private static final int RENIEC = 2;
    private static final int SERVIDOR = 3;
    private static final int APP = 4;
    private static final int OSIPTEL = 5;

    /** Un paso del viaje: de, a, desfase vertical de la flecha y rótulo. */
    private record Paso(int de, int a, int desfase, String rotulo, String explicacion) {}

    private static final List<Paso> VIAJE = List.of(
        new Paso(TIENDA, OPERADORA, 0, "DNI + huella", "La tienda envía la solicitud: DNI, huella, tienda y hora."),
        new Paso(OPERADORA, RENIEC, 0, "¿huella válida?", "La operadora valida la huella con RENIEC (esto ya existe)."),
        new Paso(OPERADORA, SERVIDOR, -16, "¿el dueño autoriza?", "NUEVO: antes de activar, pregunta a ChipGuard. La línea queda en espera."),
        new Paso(SERVIDOR, APP, -16, "alerta push", "ChipGuard le avisa al dueño del DNI en su celular, al instante."),
        new Paso(APP, SERVIDOR, 16, "Sí, fui yo", "El dueño responde con su huella en el celular."),
        new Paso(SERVIDOR, OPERADORA, 16, "activar", "ChipGuard responde a la operadora: la línea se activa."),
        new Paso(SERVIDOR, OSIPTEL, 0, "registro", "Queda el historial: qué tienda, a qué hora y quién aprobó.")
    );

    private static final List<Paso> RECHAZO = List.of(
        VIAJE.get(0), VIAJE.get(1), VIAJE.get(2), VIAJE.get(3),
        new Paso(APP, SERVIDOR, 16, "No fui yo", "El dueño toca «No fui yo»."),
        new Paso(SERVIDOR, OPERADORA, 16, "bloquear", "La línea nunca se activa: el chip queda muerto."),
        new Paso(SERVIDOR, OSIPTEL, 0, "reporte", "OSIPTEL recibe la denuncia con la tienda y la hora. Con 3 reportes, la tienda se suspende.")
    );

    public PanelArquitectura() {
        super(List.of(
            new Escena("ChipGuard por dentro.",
                "Seis piezas. Cuatro ya existen hoy en el Perú; ChipGuard agrega dos: un servidor y una app.", true),
            new Escena("El viaje de una solicitud.",
                "Desde que pones tu huella en la tienda hasta que el chip se activa: siete pasos, en segundos.", true),
            new Escena("Si el dueño dice «No fui yo».",
                "El mismo viaje, pero ahora el chip lo pidió otra persona.", true),
            new Escena("¿Y si no responde o no tiene smartphone?",
                "La regla de oro: si el dueño no aprueba, la línea no se activa.", true),
            new Escena("Qué guarda ChipGuard (y qué no).",
                "La huella nunca sale de RENIEC. ChipGuard solo necesita saber a quién avisar.", true),
            new Escena("Con qué se construye.",
                "Tecnología común y probada: nada que no se use ya en apps de bancos.", true)
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        switch (i) {
            case 0 -> {
                int[][] lazos = {{TIENDA, OPERADORA}, {OPERADORA, RENIEC}, {OPERADORA, SERVIDOR}, {SERVIDOR, APP},
                    {SERVIDOR, OSIPTEL}};
                for (int k = 0; k < lazos.length; k++) {
                    double[] o = borde(a, lazos[k][0], lazos[k][1], 0);
                    double[] d = borde(a, lazos[k][1], lazos[k][0], 0);
                    boolean nuevo = NUEVO[lazos[k][0]] || NUEVO[lazos[k][1]];
                    Tema.flecha(g, o[0], o[1], d[0], d[1], Tema.conAlfa(nuevo ? Tema.VERDE : Tema.GRIS, 200), 2.5f,
                            true, tramo(t, 1.9 + k * 0.15, 0.5));
                }
                double q = tramo(t, 2.6, 0.4);
                Tema.rectRedondo(g, a.x, a.y + a.height * 0.78, 24, 24, 8, Tema.conAlfa(Tema.BLANCO, (int) (255 * q)));
                Tema.borde(g, a.x, a.y + a.height * 0.78, 24, 24, 8, Tema.conAlfa(Tema.FILETE, (int) (255 * q)), 1.5f);
                Tema.rectRedondo(g, a.x, a.y + a.height * 0.78 + 40, 24, 24, 8, Tema.conAlfa(Tema.VERDE_SUAVE, (int) (255 * q)));
                g.setFont(Tema.letra(Font.PLAIN, 18f));
                g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * q)));
                Tema.texto(g, "ya existe hoy en el Perú", a.x + 38, a.y + a.height * 0.78 + 18);
                Tema.texto(g, "lo que agrega ChipGuard", a.x + 38, a.y + a.height * 0.78 + 58);
                for (int n = 0; n < NOMBRE.length; n++) {
                    nodo(g, a, n, tramo(t, 0.2 + n * 0.3, 0.4), null);
                }
            }
            case 1 -> viaje(g, a, t, VIAJE, Tema.AZUL);
            case 2 -> viaje(g, a, t, RECHAZO, Tema.ROJO);
            case 3 -> tarjetas(g, a, t, new String[][] {
                {"No responde en 10 min", "La línea NO se activa. Puede volver a intentarlo cuando el dueño apruebe."},
                {"No tiene smartphone", "Le llega un SMS con un código, o una llamada automática que pide marcar 1 o 2."},
                {"No tiene celular", "Va a una agencia de la operadora y aprueba con su huella, en persona."}
            }, new Iconos.Tipo[] {Iconos.Tipo.ALERTA, Iconos.Tipo.TELEFONO, Iconos.Tipo.TIENDA});
            case 4 -> guarda(g, a, t);
            default -> pila(g, a, t);
        }
    }

    // ─────────────────────────── nodos y flechas ───────────────────────────

    private static double[] centro(Rectangle a, int n) {
        return new double[] {a.x + a.width * POS[n][0], a.y + a.height * POS[n][1]};
    }

    private static final int ANCHO_NODO = 270;
    private static final int ALTO_NODO = 100;

    private void nodo(Graphics2D g, Rectangle a, int n, double alfa, Color resalte) {
        if (alfa <= 0) {
            return;
        }
        int al = (int) (255 * alfa);
        double[] c = centro(a, n);
        double x = c[0] - ANCHO_NODO / 2.0;
        double y = c[1] - ALTO_NODO / 2.0;
        Tema.sombra(g, x, y, ANCHO_NODO, ALTO_NODO, 22, (int) (26 * alfa));
        Tema.rectRedondo(g, x, y, ANCHO_NODO, ALTO_NODO, 22, Tema.conAlfa(NUEVO[n] ? Tema.VERDE_SUAVE : Tema.BLANCO, al));
        if (resalte != null) {
            Tema.borde(g, x, y, ANCHO_NODO, ALTO_NODO, 22, resalte, 3f);
        }
        Iconos.dibujar(g, ICONO[n], x + 36, c[1], 34, Tema.conAlfa(NUEVO[n] ? Tema.VERDE : Tema.AZUL_NOCHE, al));
        // el nombre se achica si no entra en la caja
        float tam = 21f;
        while (tam > 14f && g.getFontMetrics(Tema.titulo(tam)).stringWidth(NOMBRE[n]) > ANCHO_NODO - 80) {
            tam -= 1f;
        }
        g.setFont(Tema.titulo(tam));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
        Tema.texto(g, NOMBRE[n], x + 64, c[1] - 4);
        g.setFont(Tema.letra(Font.PLAIN, 15f));
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
        Tema.texto(g, DETALLE[n], x + 64, c[1] + 18);
        if (NUEVO[n]) {
            Font f = Tema.letra(Font.BOLD, 12f);
            Tema.pastilla(g, "NUEVO", (int) (x + ANCHO_NODO - 70), (int) (y - 12), Tema.conAlfa(Tema.VERDE, al),
                    Tema.conAlfa(Tema.BLANCO, al), f);
        }
    }

    /** Punto del borde del nodo n en dirección al nodo m (para que la flecha no se meta dentro de la caja). */
    private static double[] borde(Rectangle a, int n, int m, int desfase) {
        double[] c = centro(a, n);
        double[] d = centro(a, m);
        double dx = d[0] - c[0];
        double dy = d[1] - c[1];
        if (Math.abs(dx) * ALTO_NODO > Math.abs(dy) * ANCHO_NODO) {
            return new double[] {c[0] + Math.signum(dx) * (ANCHO_NODO / 2.0 + 6), c[1] + desfase};
        }
        return new double[] {c[0] + desfase * 3, c[1] + Math.signum(dy) * (ALTO_NODO / 2.0 + 6)};
    }

    private void viaje(Graphics2D g, Rectangle a, double t, List<Paso> pasos, Color colorFinal) {
        double durPaso = 1.3;
        int activo = Math.min(pasos.size() - 1, (int) (t / durPaso));
        for (int n = 0; n < NOMBRE.length; n++) {
            boolean enJuego = pasos.get(activo).de() == n || pasos.get(activo).a() == n;
            nodo(g, a, n, 1, enJuego && t < pasos.size() * durPaso ? Tema.AZUL : null);
        }
        for (int k = 0; k < pasos.size(); k++) {
            Paso p = pasos.get(k);
            double avance = tramo(t, k * durPaso, durPaso * 0.7);
            if (avance <= 0) {
                continue;
            }
            boolean malo = colorFinal == Tema.ROJO && k >= 4;
            Color col = malo ? Tema.ROJO : (k >= 4 ? Tema.VERDE : Tema.AZUL);
            double[] o = borde(a, p.de(), p.a(), p.desfase());
            double[] d = borde(a, p.a(), p.de(), p.desfase());
            Tema.flecha(g, o[0], o[1], d[0], d[1], col, 3f, false, avance);
            // el paquete que viaja
            if (avance < 1) {
                double px = o[0] + (d[0] - o[0]) * avance;
                double py = o[1] + (d[1] - o[1]) * avance;
                g.setColor(col);
                g.fill(new java.awt.geom.Ellipse2D.Double(px - 9, py - 9, 18, 18));
            }
            double mx = (o[0] + d[0]) / 2;
            double my = (o[1] + d[1]) / 2 + (p.desfase() < 0 ? -26 : p.desfase() > 0 ? 24 : -22);
            if (p.a() == RENIEC || p.a() == OSIPTEL) {
                my = (o[1] + d[1]) / 2;
                mx += 70;
            }
            Font f = Tema.letra(Font.BOLD, 14f);
            String r = (k + 1) + "  " + p.rotulo();
            int w = g.getFontMetrics(f).stringWidth(r) + 24;
            Tema.pastilla(g, r, (int) (mx - w / 2.0), (int) (my - 14), Tema.conAlfa(col, (int) (255 * avance)),
                    Tema.BLANCO, f);
        }
        // la explicación del paso activo, abajo a la izquierda
        Paso p = pasos.get(activo);
        g.setFont(Tema.titulo(24f));
        g.setColor(Tema.AZUL_NOCHE);
        int y = (int) (a.y + a.height * 0.72);
        Tema.texto(g, "Paso " + (activo + 1) + " de " + pasos.size(), a.x, y);
        g.setFont(Tema.letra(Font.PLAIN, 18f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.parrafo(g, p.explicacion(), a.x, y + 12, (int) (a.width * 0.42), 1.35f);
    }

    // ─────────────────────────── tarjetas, datos y tecnología ───────────────────────────

    private void tarjetas(Graphics2D g, Rectangle a, double t, String[][] datos, Iconos.Tipo[] iconos) {
        int n = datos.length;
        int sep = 28;
        int w = (a.width - sep * (n - 1)) / n;
        int h = Math.min(320, a.height - 20);
        for (int k = 0; k < n; k++) {
            double p = tramo(t, 0.2 + k * 0.4, 0.5);
            int al = (int) (255 * p);
            int x = a.x + k * (w + sep);
            int y = a.y + 10 + (int) ((1 - p) * 20);
            Tema.sombra(g, x, y, w, h, 28, (int) (24 * p));
            Tema.rectRedondo(g, x, y, w, h, 28, Tema.conAlfa(Tema.BLANCO, al));
            Iconos.dibujar(g, iconos[k], x + 52, y + 60, 48, Tema.conAlfa(Tema.AZUL, al));
            g.setFont(Tema.titulo(26f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            int yy = y + 120;
            yy += Tema.parrafo(g, datos[k][0], x + 30, yy, w - 60, 1.15f) + 10;
            g.setFont(Tema.letra(Font.PLAIN, 18f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.parrafo(g, datos[k][1], x + 30, yy, w - 60, 1.4f);
        }
    }

    private void guarda(Graphics2D g, Rectangle a, double t) {
        String[] si = {"Tu DNI, cifrado", "El token de tu celular (para avisarte)", "El historial: tienda, hora y respuesta"};
        String[] no = {"Tu huella (la valida RENIEC)", "Tus llamadas o mensajes", "Tu ubicación"};
        int w = (a.width - 40) / 2;
        for (int c = 0; c < 2; c++) {
            int x = a.x + c * (w + 40);
            boolean guarda = c == 0;
            Tema.rectRedondo(g, x, a.y + 10, w, Math.min(330, a.height - 20), 30,
                    guarda ? Tema.VERDE_SUAVE : Tema.ROJO_SUAVE);
            g.setFont(Tema.titulo(30f));
            g.setColor(guarda ? Tema.VERDE : Tema.ROJO_OSCURO);
            Tema.texto(g, guarda ? "Sí guarda" : "Nunca guarda", x + 36, a.y + 64);
            String[] lista = guarda ? si : no;
            for (int k = 0; k < lista.length; k++) {
                double p = tramo(t, 0.3 + (c * 3 + k) * 0.25, 0.4);
                int y = a.y + 130 + k * 64;
                Iconos.dibujar(g, guarda ? Iconos.Tipo.VISTO : Iconos.Tipo.EQUIS, x + 52, y - 7, 26,
                        Tema.conAlfa(guarda ? Tema.VERDE : Tema.ROJO, (int) (255 * p)));
                g.setFont(Tema.letra(Font.PLAIN, 21f));
                g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
                Tema.texto(g, lista[k], x + 84, y);
            }
        }
    }

    private void pila(Graphics2D g, Rectangle a, double t) {
        String[][] capas = {
            {"App del dueño", "Android e iOS · alerta push · aprobar con huella del celular · mapa de mis chips"},
            {"Notificaciones", "Firebase Cloud Messaging (Android) y APNs (iPhone) · SMS de respaldo"},
            {"Servidor ChipGuard", "API REST en Java (Spring Boot) · decide: esperar, activar o bloquear"},
            {"Base de datos", "PostgreSQL · DNI cifrado · historial de solicitudes · tope de 3 chips por DNI"},
            {"Conexión con operadoras y OSIPTEL", "API segura (HTTPS + firma digital) · RENIEC sigue validando la huella"}
        };
        Color[] tono = {Tema.VERDE_SUAVE, Tema.AZUL_SUAVE, Tema.VERDE_SUAVE, Tema.AZUL_SUAVE, Tema.NARANJA_SUAVE};
        int alto = Math.min(84, (a.height - 20) / capas.length);
        for (int k = 0; k < capas.length; k++) {
            double p = tramo(t, 0.2 + k * 0.3, 0.45);
            int al = (int) (255 * p);
            int y = a.y + k * alto;
            int dx = (int) ((1 - p) * 40);
            Tema.rectRedondo(g, a.x + dx, y, a.width, alto - 12, 20, Tema.conAlfa(tono[k], al));
            g.setFont(Tema.titulo(22f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, capas[k][0], a.x + 28 + dx, y + (alto - 12) / 2.0 + 8);
            g.setFont(Tema.letra(Font.PLAIN, 17f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.texto(g, capas[k][1], a.x + a.width * 0.32 + dx, y + (alto - 12) / 2.0 + 6);
        }
    }
}
