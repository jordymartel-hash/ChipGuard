package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Pestaña 3: la solución contada como historia. Ana, un celular simulado y lo que ve la operadora.
 * Primero pasa el problema (sin protección); luego se rebobina con ChipGuard.
 */
public final class PanelChipGuard extends JComponent implements Capitulo {

    @Override
    public boolean avanzar() {
        if (escena < ESCENAS.size() - 1) {
            irAEscena(escena + 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean retroceder() {
        if (escena > 0) {
            irAEscena(escena - 1);
            return true;
        }
        return false;
    }


    private enum Pantalla { BLOQUEO, LLAMADA, APP, ALERTA, BLOQUEADO, HUELLA_FALLA, MAPA, RESUMEN }

    private enum EstadoLinea { OK, OK_HUELLA, FRAUDE, PENDIENTE, BLOQUEADA }

    private record Escena(String titulo, String texto, String mate, String hora, Pantalla pantalla,
            List<EstadoLinea> lineas, boolean extorsionador, String nota) {}

    private static final List<Escena> ESCENAS = List.of(
        new Escena("Conoce a Ana",
            "Ana tiene 34 años y una bodega en Comas. Tiene un solo chip. Hace meses dejó una fotocopia de su DNI "
                + "en una tienda para un trámite… y esa copia terminó en malas manos.",
            "f⁻¹(Ana) = { L1 }  →  1 chip a su nombre", "9:41", Pantalla.BLOQUEO,
            List.of(EstadoLinea.OK), false, "Todo en orden: 1 chip, usado por Ana."),
        new Escena("3:12 a. m.: alguien saca un chip con su DNI",
            "En un puesto ambulante registran una línea nueva con la fotocopia del DNI de Ana. Nadie le pide la "
                + "huella. Ana está durmiendo y su celular no dice nada.",
            "f(L2) = Ana, pero quien lo usa es u(L2) = extorsionador.  f ≠ u", "3:12", Pantalla.BLOQUEO,
            List.of(EstadoLinea.OK, EstadoLinea.FRAUDE), true, "Ana NO sabe que existe L2."),
        new Escena("La extorsión… y buscan a Ana",
            "Desde L2 extorsionan a un comerciante. La policía rastrea el número y llega a la dueña en papel: Ana. "
                + "Ella no sabía que tenía dos chips.",
            "Rastrear con f lleva a f(L2) = Ana: una inocente", "10:05", Pantalla.LLAMADA,
            List.of(EstadoLinea.OK, EstadoLinea.FRAUDE), true, "El sistema apunta a Ana. El culpable sigue libre."),
        new Escena("Rebobinemos… ahora con ChipGuard",
            "Ana registra su huella una sola vez en la app. Desde ese momento ningún chip nace con su DNI sin que "
                + "ella lo apruebe desde su celular.",
            "Cada chip ↔ un contrato con su huella: g es biyectiva", "9:41", Pantalla.APP,
            List.of(EstadoLinea.OK_HUELLA), false, "L1 ya tiene contrato con la huella de Ana."),
        new Escena("3:12 a. m. otra vez",
            "Intentan sacar el chip con la misma fotocopia. Esta vez el celular de Ana vibra. "
                + "Toca un botón en el celular: ¿fuiste tú?",
            "La línea queda PENDIENTE: no entra al conjunto L sin la aprobación de Ana", "3:12", Pantalla.ALERTA,
            List.of(EstadoLinea.OK_HUELLA, EstadoLinea.PENDIENTE), true, "L2 espera la huella de Ana."),
        new Escena("Bloqueado",
            "El chip nunca se activó. El extorsionador se quedó sin línea y la operadora y OSIPTEL reciben el "
                + "reporte con la tienda y la hora.",
            "L2 no entra a L: la biyección chip ↔ contrato se mantiene", "3:13", Pantalla.BLOQUEADO,
            List.of(EstadoLinea.OK_HUELLA, EstadoLinea.BLOQUEADA), true, "Fraude detenido antes de nacer."),
        new Escena("Mi mapa de chips",
            "Cuando quiera, Ana ve TODOS los chips a su nombre, como un diagrama. Si aparece uno que no reconoce, "
                + "lo da de baja con un toque.",
            "La app le entrega a Ana la inversa: f⁻¹(Ana) en su bolsillo, con tope |f⁻¹(Ana)| ≤ 3", "9:41",
            Pantalla.MAPA, List.of(EstadoLinea.OK_HUELLA), false, "Lo que ve la operadora = lo que ve Ana."),
        new Escena("Lo que proponemos",
            "ChipGuard: 1) alerta en tu celular por cada chip con tu DNI, 2) se activa solo con tu huella, "
                + "3) tu DNI bloqueado por defecto y 4) tu mapa de chips siempre visible.",
            "Problema: f ≠ u.   Solución: g biyectiva + la aprobación del dueño real", "9:41", Pantalla.RESUMEN,
            List.of(EstadoLinea.OK_HUELLA), false, "Cada chip lleva a una persona real.")
    );

    private int escena;
    private boolean eligioSi;
    private final Boton anterior = new Boton("Anterior", Boton.Estilo.SECUNDARIO).conIcono(Iconos.Tipo.ANTERIOR);
    private final Boton siguiente = new Boton("Siguiente", Boton.Estilo.PRIMARIO).conIcono(Iconos.Tipo.SIGUIENTE);
    private final Avisos avisos = new Avisos(this, 2);
    private long entradaEscena = System.currentTimeMillis();
    private final Timer vibracion = new Timer(16, e -> repaint());
    private Rectangle2D zonaBloquear;
    private Rectangle2D zonaSi;
    private int yAvisos = 400;

    public PanelChipGuard() {
        setLayout(null);
        anterior.setFont(Tema.letra(Font.BOLD, 16f));
        siguiente.setFont(Tema.letra(Font.BOLD, 16f));
        anterior.alHacerClic(() -> irAEscena(escena - 1));
        siguiente.alHacerClic(() -> irAEscena(escena + 1));
        add(anterior);
        add(siguiente);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (ESCENAS.get(escena).pantalla() != Pantalla.ALERTA) {
                    return;
                }
                if (zonaBloquear != null && zonaBloquear.contains(e.getPoint())) {
                    eligioSi = false;
                    irAEscena(escena + 1);
                    avisos.mostrar("Elegiste: No fui yo", "Ana bloqueó el chip desde su celular.", Tema.ROJO);
                } else if (zonaSi != null && zonaSi.contains(e.getPoint())) {
                    eligioSi = true;
                    irAEscena(escena + 1);
                    avisos.mostrar("Elegiste: Sí, fui yo", "Se pide la huella de quien está en la tienda…",
                            Tema.DORADO);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean sobre = ESCENAS.get(escena).pantalla() == Pantalla.ALERTA
                        && ((zonaBloquear != null && zonaBloquear.contains(e.getPoint()))
                                || (zonaSi != null && zonaSi.contains(e.getPoint())));
                setCursor(Cursor.getPredefinedCursor(sobre ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });
        addMouseMotionListener((MouseAdapter) getMouseListeners()[0]);
        irAEscena(0);
    }

    public void irAEscena(int nueva) {
        escena = Math.max(0, Math.min(ESCENAS.size() - 1, nueva));
        avisos.limpiarTodo();
        entradaEscena = System.currentTimeMillis();
        if (ESCENAS.get(escena).pantalla() == Pantalla.ALERTA) {
            avisos.mostrar("Notificación nueva", "ChipGuard: alguien quiere sacar un chip con tu DNI", Tema.DORADO);
            if (!Animador.instantaneo) {
                vibracion.start();
            }
        } else {
            vibracion.stop();
        }
        if (escena == 0) {
            eligioSi = false;
        }
        anterior.setVisible(escena > 0);
        siguiente.setTexto(escena == ESCENAS.size() - 1 ? "Ver otra vez" : "Siguiente");
        siguiente.conIcono(escena == ESCENAS.size() - 1 ? Iconos.Tipo.REINICIAR : Iconos.Tipo.SIGUIENTE);
        if (escena == ESCENAS.size() - 1) {
            siguiente.alHacerClic(() -> irAEscena(0));
        } else {
            siguiente.alHacerClic(() -> irAEscena(escena + 1));
        }
        doLayout();
        repaint();
    }

    @Override
    public void doLayout() {
        int x = 48;
        int y = getHeight() - 92;
        anterior.setBounds(x, y, anterior.getPreferredSize().width, 48);
        int xs = escena > 0 ? x + anterior.getPreferredSize().width + 14 : x;
        siguiente.setBounds(xs, y, siguiente.getPreferredSize().width, 48);
    }

    // ─────────────────────────── dibujo ───────────────────────────

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Tema.FONDO_PAGINA);
        g.fillRect(0, 0, w, h);

        Escena e = ESCENAS.get(escena);
        int colAncho = Math.min(560, (int) (w * 0.37));
        dibujarRelato(g, e, 48, colAncho, h);

        int altoCel = Math.min(h - 70, 640);
        int anchoCel = (int) (altoCel * 0.49);
        int xCel = 48 + colAncho + 48;
        int yCel = (h - altoCel) / 2 - 6;
        if (e.pantalla() == Pantalla.ALERTA && !Animador.instantaneo) {
            long t = System.currentTimeMillis() - entradaEscena;
            if (t < 900) {
                xCel += (int) (Math.sin(t / 18.0) * 7 * (1 - t / 900.0));
            } else {
                vibracion.stop();
            }
        }
        dibujarCelular(g, e, xCel, yCel, anchoCel, altoCel);

        int xOp = 48 + colAncho + 48 + anchoCel + 48;
        int anchoOp = w - xOp - 44;
        if (anchoOp >= 300) {
            dibujarOperadora(g, e, xOp, 60, anchoOp, h - 140);
        }
        avisos.dibujar(g, 48 + colAncho, yAvisos, Math.min(380, colAncho));
        g.dispose();
    }

    private void dibujarRelato(Graphics2D g, Escena e, int x, int ancho, int h) {
        // progreso
        // progreso: rojo = escenas del problema, verde = escenas con ChipGuard
        for (int i = 0; i < ESCENAS.size(); i++) {
            Color c = i <= 2 ? Tema.ROJO : Tema.VERDE;
            g.setColor(i == escena ? c : i < escena ? Tema.conAlfa(c, 110) : Tema.GRIS_CLARO);
            Tema.rectRedondo(g, x + i * 30, 48, i == escena ? 26 : 20, 6, 6, g.getColor());
        }

        g.setFont(Tema.titulo(36f));
        g.setColor(Tema.AZUL_NOCHE);
        int y = 76;
        y += Tema.parrafo(g, e.titulo(), x, y, ancho, 1.05f) + 14;
        g.setFont(Tema.letra(Font.PLAIN, 18f));
        g.setColor(Tema.TEXTO_SUAVE);
        y += Tema.parrafo(g, e.texto(), x, y, ancho, 1.3f) + 20;

        g.setFont(Tema.letra(Font.BOLD, 16f));
        int altoMate = 36 + Tema.partir(e.mate(), g.getFontMetrics(), ancho - 40).size() * 24;
        Tema.tarjeta(g, x, y, ancho, altoMate, 18, Tema.AZUL_NOCHE);
        g.setColor(Tema.BLANCO);
        Tema.parrafo(g, e.mate(), x + 20, y + 16, ancho - 40, 1.2f);
        // los botones van justo debajo: sin vacío en la columna
        int yBotones = y + altoMate + 28;
        yAvisos = yBotones + 76;
        if (anterior.getY() != yBotones) {
            anterior.setLocation(anterior.getX(), yBotones);
            siguiente.setLocation(siguiente.getX(), yBotones);
        }
    }

    private void dibujarCelular(Graphics2D g, Escena e, int x, int y, int w, int h) {
        // cuerpo
        Tema.sombra(g, x, y, w, h, 54, 40);
        g.setColor(new Color(0x10141E));
        g.fill(new RoundRectangle2D.Double(x, y, w, h, 54, 54));
        int sx = x + 12;
        int sy = y + 12;
        int sw = w - 24;
        int sh = h - 24;
        RoundRectangle2D pantalla = new RoundRectangle2D.Double(sx, sy, sw, sh, 42, 42);
        java.awt.Shape antes = g.getClip();
        g.clip(pantalla);
        boolean oscuro = e.pantalla() == Pantalla.BLOQUEO || e.pantalla() == Pantalla.LLAMADA
                || e.pantalla() == Pantalla.ALERTA;
        if (oscuro) {
            g.setPaint(new GradientPaint(sx, sy, new Color(0x2A3A63), sx, sy + sh, new Color(0x0E1424)));
        } else {
            g.setPaint(new GradientPaint(sx, sy, Tema.BLANCO, sx, sy + sh, new Color(0xF1F4F9)));
        }
        g.fill(pantalla);
        // barra de estado
        g.setFont(Tema.letra(Font.BOLD, 13f));
        g.setColor(oscuro ? Tema.CREMA : Tema.AZUL_NOCHE);
        Tema.texto(g, e.hora(), sx + 26, sy + 28);
        Tema.rectRedondo(g, sx + sw - 50, sy + 17, 26, 12, 4, oscuro ? Tema.CREMA : Tema.AZUL_NOCHE);
        g.setColor(new Color(0x10141E));
        g.fill(new RoundRectangle2D.Double(sx + sw / 2.0 - 46, sy + 10, 92, 24, 24, 24));

        switch (e.pantalla()) {
            case BLOQUEO -> pantallaBloqueo(g, e, sx, sy, sw, sh);
            case LLAMADA -> pantallaLlamada(g, sx, sy, sw, sh);
            case APP -> pantallaApp(g, sx, sy, sw, sh);
            case ALERTA -> pantallaAlerta(g, sx, sy, sw, sh);
            case BLOQUEADO -> {
                if (eligioSi) {
                    pantallaHuellaFalla(g, sx, sy, sw, sh);
                } else {
                    pantallaBloqueado(g, sx, sy, sw, sh);
                }
            }
            case MAPA -> pantallaMapa(g, sx, sy, sw, sh);
            default -> pantallaResumen(g, sx, sy, sw, sh);
        }
        g.setClip(antes);
    }

    private void pantallaBloqueo(Graphics2D g, Escena e, int sx, int sy, int sw, int sh) {
        g.setColor(Tema.CREMA);
        g.setFont(Tema.letra(Font.PLAIN, 64f));
        Tema.textoCentrado(g, e.hora(), sx + sw / 2.0, sy + sh * 0.24);
        g.setFont(Tema.letra(Font.PLAIN, 15f));
        Tema.textoCentrado(g, "viernes, 13 de noviembre", sx + sw / 2.0, sy + sh * 0.24 + 48);
        g.setColor(Tema.conAlfa(Tema.CREMA, 150));
        g.setFont(Tema.letra(Font.ITALIC, 15f));
        Tema.textoCentrado(g, "Sin notificaciones", sx + sw / 2.0, sy + sh * 0.62);
        if (escena == 1) {
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(Tema.ROJO_CLARO);
            Tema.textoCentrado(g, "…mientras le sacan un chip", sx + sw / 2.0, sy + sh * 0.62 + 26);
        }
    }

    private void pantallaLlamada(Graphics2D g, int sx, int sy, int sw, int sh) {
        double cx = sx + sw / 2.0;
        g.setColor(Tema.conAlfa(Tema.CREMA, 170));
        g.setFont(Tema.letra(Font.PLAIN, 14f));
        Tema.textoCentrado(g, "Llamada entrante", cx, sy + 80);
        g.setColor(Tema.CREMA);
        g.setFont(Tema.letra(Font.BOLD, 26f));
        Tema.textoCentrado(g, "Comisaría PNP", cx, sy + 116);
        g.setFont(Tema.letra(Font.PLAIN, 14f));
        Tema.textoCentrado(g, "Investigación por extorsión", cx, sy + 142);
        // SMS
        int bx = sx + 16;
        int by = sy + (int) (sh * 0.42);
        int bw = sw - 32;
        Tema.rectRedondo(g, bx, by, bw, 118, 20, Tema.conAlfa(Tema.CREMA, 235));
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 13f));
        Tema.texto(g, "MENSAJE · PNP", bx + 14, by + 22);
        g.setFont(Tema.letra(Font.PLAIN, 13.5f));
        Tema.parrafo(g, "Su línea 987 654 102 fue usada en una extorsión. Acérquese a la comisaría.", bx + 14,
                by + 32, bw - 28, 1.2f);
        // botones de llamada
        double yb = sy + sh - 84;
        g.setColor(Tema.ROJO);
        g.fill(new Ellipse2D.Double(cx - 90, yb, 60, 60));
        g.setColor(Tema.VERDE);
        g.fill(new Ellipse2D.Double(cx + 30, yb, 60, 60));
        g.setColor(Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 22f));
        Iconos.dibujar(g, Iconos.Tipo.EQUIS, cx - 60, yb + 30, 26, Tema.BLANCO);
        Iconos.dibujar(g, Iconos.Tipo.TELEFONO, cx + 60, yb + 30, 26, Tema.BLANCO);
    }

    private void cabeceraApp(Graphics2D g, int sx, int sy, int sw, String titulo) {
        Tema.rectRedondo(g, sx, sy + 40, sw, 56, 0, Tema.AZUL);
        g.setColor(Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 18f));
        Tema.texto(g, titulo, sx + 20, sy + 75);
    }

    private void escudo(Graphics2D g, double cx, double cy, double r, Color color) {
        java.awt.geom.Path2D p = new java.awt.geom.Path2D.Double();
        p.moveTo(cx, cy - r);
        p.lineTo(cx + r * 0.85, cy - r * 0.6);
        p.quadTo(cx + r * 0.8, cy + r * 0.6, cx, cy + r);
        p.quadTo(cx - r * 0.8, cy + r * 0.6, cx - r * 0.85, cy - r * 0.6);
        p.closePath();
        g.setColor(color);
        g.fill(p);
        g.setColor(Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, (float) r));
        Iconos.dibujar(g, Iconos.Tipo.VISTO, cx, cy, r * 0.9, Tema.BLANCO);
    }

    private void pantallaApp(Graphics2D g, int sx, int sy, int sw, int sh) {
        cabeceraApp(g, sx, sy, sw, "ChipGuard");
        double cx = sx + sw / 2.0;
        escudo(g, cx, sy + 160, 46, Tema.VERDE);
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 18f));
        Tema.textoCentrado(g, "Tu DNI está protegido", cx, sy + 236);
        g.setFont(Tema.letra(Font.PLAIN, 13.5f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, "Ningún chip se activa sin tu huella", cx, sy + 260);
        int y = sy + 292;
        Tema.tarjeta(g, sx + 14, y, sw - 28, 70, 16, Tema.BLANCO);
        g.setColor(Tema.VERDE);
        g.fill(new Ellipse2D.Double(sx + 28, y + 22, 26, 26));
        g.setColor(Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 14f));
        Iconos.dibujar(g, Iconos.Tipo.VISTO, sx + 41, y + 35, 16, Tema.BLANCO);
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 14f));
        Tema.texto(g, "L1 · 987 654 101", sx + 64, y + 30);
        g.setFont(Tema.letra(Font.PLAIN, 12.5f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "Tuyo · con tu huella", sx + 64, y + 50);
        g.setFont(Tema.letra(Font.BOLD, 14f));
        g.setColor(Tema.AZUL_TEXTO);
        Tema.textoCentrado(g, "Chips a tu nombre: 1 de 3", cx, y + 104);
    }

    private void pantallaAlerta(Graphics2D g, int sx, int sy, int sw, int sh) {
        double cx = sx + sw / 2.0;
        g.setColor(Tema.conAlfa(Tema.CREMA, 120));
        g.setFont(Tema.letra(Font.PLAIN, 54f));
        Tema.textoCentrado(g, "3:12", cx, sy + 110);
        int bx = sx + 12;
        int by = sy + 160;
        int bw = sw - 24;
        int bh = 250;
        Tema.tarjeta(g, bx, by, bw, bh, 22, Tema.BLANCO);
        Tema.rectRedondo(g, bx + 14, by + 14, 22, 22, 6, Tema.AZUL);
        Iconos.dibujar(g, Iconos.Tipo.ESCUDO, bx + 25, by + 25, 14, Tema.BLANCO);
        g.setColor(Tema.TEXTO_SUAVE);
        g.setFont(Tema.letra(Font.BOLD, 11.5f));
        Tema.texto(g, "CHIPGUARD · ahora", bx + 44, by + 30);
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 17f));
        Tema.texto(g, "¿Estás sacando un chip?", bx + 14, by + 62);
        g.setFont(Tema.letra(Font.PLAIN, 13.5f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.parrafo(g, "Intentan registrar una línea con tu DNI en: Puesto ambulante · Av. Abancay, 3:12 a. m.",
                bx + 14, by + 72, bw - 28, 1.2f);
        zonaBloquear = new Rectangle2D.Double(bx + 14, by + bh - 104, bw - 28, 42);
        zonaSi = new Rectangle2D.Double(bx + 14, by + bh - 54, bw - 28, 42);
        Tema.rectRedondo(g, zonaBloquear.getX(), zonaBloquear.getY(), zonaBloquear.getWidth(), 42, 42, Tema.ROJO);
        Tema.rectRedondo(g, zonaSi.getX(), zonaSi.getY(), zonaSi.getWidth(), 42, 42, Tema.VERDE_SUAVE);
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(Tema.BLANCO);
        Tema.textoCentrado(g, "No fui yo · Bloquear", zonaBloquear.getCenterX(), zonaBloquear.getCenterY());
        g.setColor(Tema.VERDE);
        Tema.textoCentrado(g, "Sí, fui yo (poner huella)", zonaSi.getCenterX(), zonaSi.getCenterY());
        g.setColor(Tema.conAlfa(Tema.CREMA, 180));
        g.setFont(Tema.letra(Font.ITALIC, 13f));
        Tema.textoCentrado(g, "Toca una opción", cx, by + bh + 34);
    }

    private void pantallaBloqueado(Graphics2D g, int sx, int sy, int sw, int sh) {
        cabeceraApp(g, sx, sy, sw, "ChipGuard");
        double cx = sx + sw / 2.0;
        g.setColor(Tema.VERDE);
        g.fill(new Ellipse2D.Double(cx - 56, sy + 130, 112, 112));
        g.setColor(Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 64f));
        Iconos.dibujar(g, Iconos.Tipo.VISTO, cx, sy + 186, 60, Tema.BLANCO);
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 22f));
        Tema.textoCentrado(g, "Chip bloqueado", cx, sy + 284);
        g.setFont(Tema.letra(Font.PLAIN, 14f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.parrafo(g, "La línea nunca se activó. Enviamos el reporte a la operadora y a OSIPTEL.", sx + 26,
                sy + 300, sw - 52, 1.25f);
    }

    private void pantallaHuellaFalla(Graphics2D g, int sx, int sy, int sw, int sh) {
        cabeceraApp(g, sx, sy, sw, "ChipGuard");
        double cx = sx + sw / 2.0;
        double cy = sy + 190;
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Tema.ROJO);
        for (int i = 0; i < 5; i++) {
            double r = 14 + i * 11;
            g.draw(new Arc2D.Double(cx - r, cy - r, r * 2, r * 2, 200 - i * 6, 220 + i * 8, Arc2D.OPEN));
        }
        g.setStroke(new BasicStroke(1f));
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 21f));
        Tema.textoCentrado(g, "La huella no coincide", cx, sy + 284);
        g.setFont(Tema.letra(Font.PLAIN, 14f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.parrafo(g, "Quien está en la tienda no es Ana. El chip quedó bloqueado y se reportó.", sx + 26,
                sy + 300, sw - 52, 1.25f);
    }

    private void pantallaMapa(Graphics2D g, int sx, int sy, int sw, int sh) {
        cabeceraApp(g, sx, sy, sw, "Mi mapa de chips");
        double cx = sx + sw / 2.0;
        double yDni = sy + sh * 0.62;
        double[][] chips = {{cx - 70, sy + 160}, {cx + 70, sy + 160}};
        // chip activo
        Tema.flecha(g, chips[0][0], chips[0][1] + 26, cx - 18, yDni - 30, Tema.AZUL_NOCHE, 3f, false, 1);
        g.setColor(Tema.NARANJA);
        g.fill(new Ellipse2D.Double(chips[0][0] - 28, chips[0][1] - 28, 56, 56));
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 16f));
        Tema.textoCentrado(g, "L1", chips[0][0], chips[0][1]);
        g.setColor(Tema.VERDE);
        g.fill(new Ellipse2D.Double(chips[0][0] + 14, chips[0][1] - 32, 18, 18));
        // chip bloqueado (fantasma)
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] {5f, 5f}, 0f));
        g.setColor(Tema.GRIS);
        g.draw(new Ellipse2D.Double(chips[1][0] - 28, chips[1][1] - 28, 56, 56));
        g.setStroke(new BasicStroke(3f));
        g.setColor(Tema.ROJO);
        g.draw(new Line2D.Double(chips[1][0] - 16, chips[1][1] - 16, chips[1][0] + 16, chips[1][1] + 16));
        g.draw(new Line2D.Double(chips[1][0] + 16, chips[1][1] - 16, chips[1][0] - 16, chips[1][1] + 16));
        g.setStroke(new BasicStroke(1f));
        g.setFont(Tema.letra(Font.PLAIN, 12f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, "L2 bloqueado", chips[1][0], chips[1][1] + 44);
        // DNI de Ana
        Tema.rectRedondo(g, cx - 80, yDni - 30, 160, 60, 60, Tema.AZUL);
        g.setColor(Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 16f));
        Tema.textoCentrado(g, "Ana · tu DNI", cx, yDni);
        g.setColor(Tema.AZUL_TEXTO);
        g.setFont(Tema.letra(Font.BOLD, 14f));
        Tema.textoCentrado(g, "1 de 3 chips permitidos", cx, yDni + 56);
        Tema.rectRedondo(g, sx + 18, sy + sh - 70, sw - 36, 44, 44, Tema.ROJO_SUAVE);
        g.setColor(Tema.ROJO_OSCURO);
        g.setFont(Tema.letra(Font.BOLD, 13f));
        Tema.textoCentrado(g, "¿No es tuyo? Darlo de baja", cx, sy + sh - 48);
    }

    private void pantallaResumen(Graphics2D g, int sx, int sy, int sw, int sh) {
        cabeceraApp(g, sx, sy, sw, "ChipGuard");
        String[] puntos = {"Alerta por cada chip con tu DNI", "Se activa solo con tu huella",
            "DNI bloqueado por defecto", "Tu mapa de chips, siempre"};
        int y = sy + 130;
        for (String p : puntos) {
            g.setColor(Tema.VERDE);
            g.fill(new Ellipse2D.Double(sx + 22, y - 18, 28, 28));
            g.setColor(Tema.BLANCO);
            g.setFont(Tema.letra(Font.BOLD, 16f));
            Iconos.dibujar(g, Iconos.Tipo.VISTO, sx + 36, y - 4, 17, Tema.BLANCO);
            g.setColor(Tema.AZUL_NOCHE);
            g.setFont(Tema.letra(Font.BOLD, 14.5f));
            Tema.parrafo(g, p, sx + 62, y - 16, sw - 80, 1.15f);
            y += 66;
        }
        escudo(g, sx + sw / 2.0, sy + sh - 90, 38, Tema.VERDE);
    }

    private void dibujarOperadora(Graphics2D g, Escena e, int x, int y, int w, int h) {
        Tema.tarjeta(g, x, y, w, h, 24, Tema.BLANCO);
        g.setFont(Tema.titulo(21f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "Lo que ve la operadora", x + 22, y + 44);
        g.setFont(Tema.letra(Font.PLAIN, 14f));
        g.setColor(Tema.AZUL_TEXTO);
        Tema.texto(g, "f⁻¹(Ana): los chips con su DNI", x + 22, y + 70);

        double xL = x + w * 0.22;
        double xP = x + w * 0.74;
        double yAna = y + h * 0.42;
        List<EstadoLinea> lineas = e.lineas();
        for (int i = 0; i < lineas.size(); i++) {
            EstadoLinea est = lineas.get(i);
            double yl = y + 150 + i * 120;
            String nombre = "L" + (i + 1);
            boolean bloqueada = est == EstadoLinea.BLOQUEADA;
            boolean pendiente = est == EstadoLinea.PENDIENTE;
            if (!bloqueada && !pendiente) {
                Tema.flecha(g, xL + 44, yl, xP - 78, yAna, Tema.AZUL_NOCHE, 3f, false, 1);
            } else if (pendiente) {
                Tema.flecha(g, xL + 44, yl, xP - 78, yAna, Tema.GRIS, 2.5f, true, 1);
            }
            Color fondo = switch (est) {
                case FRAUDE -> Tema.ROJO_SUAVE;
                case PENDIENTE, BLOQUEADA -> Tema.GRIS_CLARO;
                default -> Tema.NARANJA;
            };
            Tema.rectRedondo(g, xL - 44, yl - 20, 88, 40, 40, fondo);
            g.setColor(est == EstadoLinea.FRAUDE ? Tema.ROJO_OSCURO : Tema.AZUL_NOCHE);
            g.setFont(Tema.letra(Font.BOLD, 17f));
            Tema.textoCentrado(g, nombre + (pendiente ? " ?" : ""), xL, yl);
            if (est == EstadoLinea.OK_HUELLA) {
                g.setColor(Tema.VERDE);
                g.fill(new Ellipse2D.Double(xL - 36, yl - 7, 14, 14));
            }
            if (bloqueada) {
                g.setStroke(new BasicStroke(3f));
                g.setColor(Tema.ROJO);
                g.draw(new Line2D.Double(xL - 50, yl, xL + 50, yl));
                g.setStroke(new BasicStroke(1f));
            }
            g.setFont(Tema.letra(Font.PLAIN, 13f));
            g.setColor(Tema.TEXTO_SUAVE);
            String etiqueta = switch (est) {
                case FRAUDE -> "lo usa el extorsionador";
                case PENDIENTE -> "esperando a Ana";
                case BLOQUEADA -> "bloqueado";
                case OK_HUELLA -> "con huella de Ana";
                default -> "lo usa Ana";
            };
            Tema.textoCentrado(g, etiqueta, xL, yl + 36);
        }
        Tema.rectRedondo(g, xP - 76, yAna - 26, 152, 52, 52, Tema.AZUL);
        g.setColor(Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 18f));
        Tema.textoCentrado(g, "Ana", xP, yAna);

        if (e.extorsionador()) {
            double yE = y + h - 120;
            Tema.rectRedondo(g, xP - 86, yE - 22, 172, 44, 44, Tema.ROJO);
            g.setColor(Tema.BLANCO);
            g.setFont(Tema.letra(Font.BOLD, 16f));
            Tema.textoCentrado(g, "Extorsionador", xP, yE);
            int i = lineas.indexOf(EstadoLinea.FRAUDE);
            if (i >= 0) {
                Tema.flecha(g, xL + 44, y + 150 + i * 120 + 6, xP - 88, yE, Tema.ROJO, 3f, true, 1);
            }
        }
        // nota inferior
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(escena <= 2 ? Tema.ROJO_OSCURO : Tema.VERDE);
        Tema.parrafo(g, e.nota(), x + 22, y + h - 58, w - 44, 1.2f);
    }
}
