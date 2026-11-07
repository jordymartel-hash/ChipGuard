package simuchip.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import simuchip.caso.Detective;
import simuchip.caso.Detective.Decision;
import simuchip.caso.Solicitud;

/**
 * Pestaña 4: juego. Atiendes una tienda de chips: aprueba o rechaza cada solicitud antes de que
 * se acabe el tiempo. Cada regla del juego es una de las funciones del caso.
 */
public final class PanelDetective extends JComponent {

    private static final int DURACION = 60;

    private record Jugada(Solicitud solicitud, boolean aprobo, Decision decision) {}

    private final Detective juego = new Detective(System.nanoTime());
    private final Avisos avisos = new Avisos(this, 1);
    private final List<Jugada> historial = new ArrayList<>();
    private final Boton aprobar = new Boton("✓  Aprobar  (A)", Boton.Estilo.EXITO);
    private final Boton rechazar = new Boton("✗  Rechazar  (R)", Boton.Estilo.PELIGRO);
    private final Boton empezar = new Boton("▶  Empezar  ·  60 segundos", Boton.Estilo.PRIMARIO);
    private final Timer reloj;
    private int segundos = DURACION;
    private boolean jugando;
    private boolean terminado;
    private long ultimoDestello;
    private boolean ultimoAcierto;

    public PanelDetective() {
        setLayout(null);
        for (Boton b : new Boton[] {aprobar, rechazar, empezar}) {
            b.setFont(Tema.letra(Font.BOLD, 20f));
            add(b);
        }
        aprobar.alHacerClic(() -> decidir(true));
        rechazar.alHacerClic(() -> decidir(false));
        empezar.alHacerClic(this::empezar);
        reloj = new Timer(1000, e -> {
            segundos--;
            if (segundos <= 0) {
                terminar();
            }
            repaint();
        });
        atajo("A", () -> decidir(true));
        atajo("R", () -> decidir(false));
        actualizarBotones();
    }

    private void atajo(String tecla, Runnable accion) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla.charAt(0) == 'A'
                ? KeyEvent.VK_A : KeyEvent.VK_R, 0), tecla);
        getActionMap().put(tecla, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isShowing()) {
                    accion.run();
                }
            }
        });
    }

    public void empezar() {
        juego.reiniciar();
        historial.clear();
        avisos.limpiarTodo();
        segundos = DURACION;
        jugando = true;
        terminado = false;
        if (!Animador.instantaneo) {
            reloj.start();
        }
        actualizarBotones();
        repaint();
    }

    /** Para las capturas: juega n decisiones correctas y una equivocada. */
    public void jugarDemo(int correctas) {
        empezar();
        for (int i = 0; i < correctas; i++) {
            decidir(!juego.actual().debeRechazarse());
        }
        decidir(juego.actual().debeRechazarse());
        segundos = 37;
        repaint();
    }

    private void decidir(boolean aprobo) {
        if (!jugando) {
            return;
        }
        Solicitud s = juego.actual();
        Decision d = juego.decidir(aprobo);
        historial.add(0, new Jugada(s, aprobo, d));
        ultimoDestello = System.currentTimeMillis();
        ultimoAcierto = d.acierto();
        avisos.mostrar(d.acierto() ? "✓ +" + d.puntos() + " puntos" : "✗ Pierdes una vida", d.explicacion(),
                d.acierto() ? new Color(0x7FD3A8) : new Color(0xF28B82));
        if (juego.terminado()) {
            terminar();
        }
        repaint();
    }

    private void terminar() {
        reloj.stop();
        jugando = false;
        terminado = true;
        actualizarBotones();
    }

    private void actualizarBotones() {
        aprobar.setVisible(jugando);
        rechazar.setVisible(jugando);
        empezar.setVisible(!jugando);
        empezar.setTexto(terminado ? "↺  Jugar otra vez" : "▶  Empezar  ·  60 segundos");
        doLayout();
    }

    private int anchoTarjeta() {
        return Math.min(760, (int) (getWidth() * 0.56));
    }

    @Override
    public void doLayout() {
        int x = 48;
        int w = anchoTarjeta();
        int yBotones = getHeight() - 116;
        int mitad = (w - 16) / 2;
        aprobar.setBounds(x, yBotones, mitad, 64);
        rechazar.setBounds(x + mitad + 16, yBotones, mitad, 64);
        int ancho = empezar.getPreferredSize().width + 40;
        empezar.setBounds(x + (w - ancho) / 2, getHeight() / 2 + 120, ancho, 64);
    }

    // ─────────────────────────── dibujo ───────────────────────────

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Tema.CELESTE_FONDO);
        g.fillRect(0, 0, w, h);
        Tema.tarjeta(g, 16, 12, w - 32, h - 32, 32, Tema.CREMA);

        int x = 48;
        int ancho = anchoTarjeta();
        g.setFont(Tema.letra(Font.BOLD, 30f));
        g.setColor(Tema.AZUL_NOCHE);
        g.drawString("Detective de chips", x, 66);
        g.setFont(Tema.letra(Font.PLAIN, 16f));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString("Atiendes la tienda. ¿Apruebas o rechazas el chip?", x, 92);

        dibujarMarcador(g, x, ancho);
        if (jugando) {
            dibujarSolicitud(g, x, 160, ancho, h - 160 - 136);
        } else if (terminado) {
            dibujarFinal(g, x, 160, ancho, h - 300);
        } else {
            dibujarPortada(g, x, 160, ancho, h - 300);
        }
        int xDer = x + ancho + 36;
        int anchoDer = w - xDer - 40;
        if (anchoDer > 260) {
            dibujarReglas(g, xDer, 40, anchoDer);
            dibujarHistorial(g, xDer, 380, anchoDer, h - 420);
        }
        avisos.dibujar(g, w - 40, h - 150, Math.min(420, w - x - ancho - 76));
        g.dispose();
    }

    private void dibujarMarcador(Graphics2D g, int x, int ancho) {
        int y = 112;
        // tiempo
        double fraccion = segundos / (double) DURACION;
        Tema.rectRedondo(g, x, y, ancho, 14, 14, Tema.GRIS_CLARO);
        Color color = segundos > 20 ? Tema.VERDE : segundos > 10 ? Tema.NARANJA : Tema.ROJO;
        Tema.rectRedondo(g, x, y, Math.max(14, ancho * fraccion), 14, 14, color);
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(Tema.AZUL_NOCHE);
        g.drawString(segundos + " s", x + ancho - 40, y - 8);
        // puntos, racha y vidas: en la fila del título, a la derecha
        int xv = x + ancho - 3 * 34;
        for (int i = 0; i < 3; i++) {
            g.setColor(i < juego.vidas() ? Tema.ROJO : Tema.GRIS);
            corazon(g, xv + i * 34, 44, 26);
        }
        g.setFont(Tema.letra(Font.BOLD, 26f));
        g.setColor(Tema.AZUL_NOCHE);
        String pts = juego.puntaje() + " pts";
        int anchoPts = g.getFontMetrics().stringWidth(pts);
        g.drawString(pts, xv - 24 - anchoPts, 66);
        if (juego.racha() >= 2) {
            Tema.pastilla(g, "racha x" + juego.racha(), xv - 24 - anchoPts - 110, 46, Tema.DORADO, Tema.AZUL_NOCHE,
                    Tema.letra(Font.BOLD, 13f));
        }
    }

    private void corazon(Graphics2D g, double x, double y, double t) {
        g.fill(new Ellipse2D.Double(x, y, t / 2 + 2, t / 2 + 2));
        g.fill(new Ellipse2D.Double(x + t / 2 - 2, y, t / 2 + 2, t / 2 + 2));
        java.awt.geom.Path2D p = new java.awt.geom.Path2D.Double();
        p.moveTo(x + 1, y + t / 3);
        p.lineTo(x + t, y + t / 3);
        p.lineTo(x + t / 2, y + t);
        p.closePath();
        g.fill(p);
    }

    private void dibujarSolicitud(Graphics2D g, int x, int y, int w, int h) {
        Solicitud s = juego.actual();
        Tema.tarjeta(g, x, y, w, h, 26, Tema.BLANCO);
        long t = System.currentTimeMillis() - ultimoDestello;
        if (t < 500 && !historial.isEmpty()) {
            Tema.borde(g, x, y, w, h, 26, ultimoAcierto ? Tema.VERDE : Tema.ROJO, (float) (8 * (1 - t / 500.0)));
            repaint();
        }
        g.setFont(Tema.letra(Font.BOLD, 14f));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString("SOLICITUD #" + s.numero() + "  ·  " + s.tienda() + "  ·  " + s.hora(), x + 30, y + 40);
        g.setFont(Tema.letra(Font.BOLD, 34f));
        g.setColor(Tema.AZUL_NOCHE);
        g.drawString(s.nombre(), x + 30, y + 88);
        g.setFont(Tema.letra(Font.PLAIN, 17f));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString("DNI " + s.dni() + "  quiere un chip nuevo", x + 30, y + 116);

        int fy = y + 150;
        int alto = Math.max(58, (h - 170) / 3 - 12);
        campo(g, x + 30, fy, w - 60, alto, "Huella en la tienda", s.huellaCoincide() ? "Coincide con el DNI" : "NO coincide",
                s.huellaCoincide(), "¿quien lo usará es el dueño en papel?  (u = f)");
        campo(g, x + 30, fy + alto + 12, w - 60, alto, "Documento", s.dniOriginal() ? "DNI original" : "Fotocopia",
                s.dniOriginal(), "una fotocopia no prueba quién eres");
        campo(g, x + 30, fy + 2 * (alto + 12), w - 60, alto, "Chips que ya tiene a su nombre",
                s.lineasActuales() + " de " + Solicitud.TOPE, s.lineasActuales() < Solicitud.TOPE,
                "|f⁻¹(" + s.nombre() + ")| < " + Solicitud.TOPE + " para poder sacar otro");
    }

    private void campo(Graphics2D g, int x, int y, int w, int alto, String titulo, String valor, boolean bien,
            String ayuda) {
        Tema.rectRedondo(g, x, y, w, alto, 18, bien ? new Color(0xF2F8F4) : Tema.ROJO_SUAVE);
        g.setFont(Tema.letra(Font.PLAIN, 14f));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString(titulo, x + 20, y + 24);
        g.setFont(Tema.letra(Font.BOLD, 21f));
        g.setColor(bien ? Tema.VERDE : Tema.ROJO_OSCURO);
        g.drawString(valor, x + 20, y + 52);
        if (alto >= 74) {
            g.setFont(Tema.letra(Font.ITALIC, 13f));
            g.setColor(Tema.TEXTO_SUAVE);
            g.drawString(ayuda, x + 20, y + 72);
        }
    }

    private void dibujarPortada(Graphics2D g, int x, int y, int w, int h) {
        Tema.tarjeta(g, x, y, w, h, 26, Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 30f));
        g.setColor(Tema.CREMA);
        g.drawString("¿Puedes detener a los extorsionadores?", x + 36, y + 70);
        g.setFont(Tema.letra(Font.PLAIN, 18f));
        g.setColor(new Color(0xC9D6EA));
        Tema.parrafo(g, "Llegan personas a pedir un chip. Algunas son extorsionadores con DNI ajeno. Tienes 60 "
                + "segundos y 3 vidas. Aprueba solo a quien cumple las 3 reglas de la derecha. Usa las teclas A y R "
                + "para ir más rápido.", x + 36, y + 100, w - 72, 1.35f);
    }

    private void dibujarFinal(Graphics2D g, int x, int y, int w, int h) {
        Tema.tarjeta(g, x, y, w, h, 26, Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 16f));
        g.setColor(Tema.DORADO);
        g.drawString(juego.vidas() <= 0 ? "SIN VIDAS" : "SE ACABÓ EL TIEMPO", x + 36, y + 52);
        g.setFont(Tema.letra(Font.BOLD, 56f));
        g.setColor(Tema.CREMA);
        g.drawString(juego.puntaje() + " puntos", x + 36, y + 118);
        g.setFont(Tema.letra(Font.PLAIN, 19f));
        g.setColor(new Color(0xC9D6EA));
        int total = juego.aciertos() + juego.errores();
        int precision = total == 0 ? 0 : Math.round(juego.aciertos() * 100f / total);
        g.drawString("Aciertos: " + juego.aciertos() + " de " + total + "  (" + precision + " %)", x + 36, y + 160);
        g.setColor(new Color(0x7FD3A8));
        g.drawString("Fraudes detenidos: " + juego.fraudesDetenidos(), x + 36, y + 192);
        g.setColor(new Color(0xF28B82));
        g.drawString("Chips irregulares que se colaron: " + juego.fraudesColados(), x + 36, y + 224);
    }

    private void dibujarReglas(Graphics2D g, int x, int y, int w) {
        Tema.tarjeta(g, x, y, w, 320, 24, Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 13f));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString("REGLAS · RECHAZA SI…", x + 22, y + 32);
        String[][] reglas = {
            {"La huella no coincide", "quien lo usa ≠ dueño en papel  (u ≠ f)"},
            {"Trae fotocopia del DNI", "sin prueba de identidad no hay contrato g"},
            {"Ya tiene 3 chips", "tope del palomar:  |f⁻¹(p)| ≤ 3"}
        };
        int yy = y + 66;
        for (int i = 0; i < reglas.length; i++) {
            g.setColor(Tema.ROJO);
            g.fill(new Ellipse2D.Double(x + 22, yy - 18, 30, 30));
            g.setColor(Tema.BLANCO);
            g.setFont(Tema.letra(Font.BOLD, 15f));
            Tema.textoCentrado(g, String.valueOf(i + 1), x + 37, yy - 3);
            g.setColor(Tema.AZUL_NOCHE);
            g.setFont(Tema.letra(Font.BOLD, 17f));
            g.drawString(reglas[i][0], x + 66, yy);
            g.setFont(Tema.letra(Font.PLAIN, 13.5f));
            g.setColor(Tema.AZUL_TEXTO);
            g.drawString(reglas[i][1], x + 66, yy + 22);
            yy += 76;
        }
        g.setFont(Tema.letra(Font.BOLD, 14f));
        g.setColor(Tema.VERDE);
        g.drawString("Si cumple las 3 → APRUEBA", x + 22, y + 296);
    }

    private void dibujarHistorial(Graphics2D g, int x, int y, int w, int h) {
        if (h < 120) {
            return;
        }
        Tema.tarjeta(g, x, y, w, h, 24, Tema.BLANCO);
        g.setFont(Tema.letra(Font.BOLD, 13f));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString("TUS ÚLTIMAS DECISIONES", x + 22, y + 32);
        if (historial.isEmpty()) {
            g.setFont(Tema.letra(Font.ITALIC, 15f));
            g.drawString("Aún no decides nada.", x + 22, y + 64);
            return;
        }
        int filas = Math.min(historial.size(), Math.max(1, (h - 50) / 34));
        for (int i = 0; i < filas; i++) {
            Jugada j = historial.get(i);
            int yy = y + 62 + i * 34;
            Tema.insignia(g, j.decision().acierto(), x + 34, yy - 6, 11);
            g.setFont(Tema.letra(Font.BOLD, 15f));
            g.setColor(Tema.AZUL_NOCHE);
            g.drawString("#" + j.solicitud().numero() + " " + j.solicitud().nombre(), x + 56, yy);
            g.setFont(Tema.letra(Font.PLAIN, 14f));
            g.setColor(Tema.TEXTO_SUAVE);
            g.drawString((j.aprobo() ? "aprobaste" : "rechazaste") + " · " + j.solicitud().motivo().toLowerCase(),
                    x + 150, yy);
        }
    }
}
