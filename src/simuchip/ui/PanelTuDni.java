package simuchip.ui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Apertura: un voluntario escribe su nombre, le llegan chips que no compró y él decide cuáles bloquear.
 * De esa misma escena nacen la función (cada chip apunta a un DNI) y el palomar (3 cajones, 5 chips).
 */
public final class PanelTuDni extends PanelEscenas {

    private static final int CHIPS = 5;
    private static final int CAJONES = 3;
    private static final String[] NUMERO = {"…412", "…087", "…935", "…260", "…771"};

    private final JTextField campo = new JTextField();
    private final boolean[] bloqueado = new boolean[CHIPS];
    private final double[][] centro = new double[CHIPS][2];
    private double radio;
    private int escena;

    public PanelTuDni() {
        super(List.of(
            new Escena("Necesito un voluntario.",
                "Escribe tu nombre y presiona Enter. Desde ahora, este DNI es tuyo."),
            new Escena("Llegaron 5 chips a tu nombre.",
                "Una tienda los registró con tu DNI. A ti no te llegó ningún aviso: ni un SMS, ni un correo."),
            new Escena("¿Cuáles compraste tú? Tócalos.",
                "Haz clic en los chips que NO son tuyos para bloquearlos. Hoy, en la vida real, no puedes hacer "
                    + "esto: ni siquiera te enteras."),
            new Escena("Eso que ves es una función.",
                "El registro manda cada chip a UN solo DNI: nunca a cero, nunca a dos. Eso es una función. "
                    + "El problema no es la función: es que apunta a ti y el chip lo usa otra persona."),
            new Escena("Solo 3 cajones por DNI.",
                "Si la ley le diera a cada DNI 3 cajones para chips, ¿qué pasa con 5? Dos no caben. Más cosas que "
                    + "cajones: algo se queda fuera. Eso es el principio del palomar.")
        ));
        campo.setFont(Tema.titulo(24f));
        campo.setForeground(Tema.AZUL_NOCHE);
        campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Tema.AZUL, 2, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        campo.addActionListener(e -> avanzar());
        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { repaint(); }
            public void removeUpdate(DocumentEvent e) { repaint(); }
            public void changedUpdate(DocumentEvent e) { repaint(); }
        });
        add(campo);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (escena != 2) {
                    return;
                }
                for (int k = 0; k < CHIPS; k++) {
                    if (Math.hypot(e.getX() - centro[k][0], e.getY() - centro[k][1]) < radio) {
                        bloqueado[k] = !bloqueado[k];
                        repaint();
                    }
                }
            }
        });
    }

    public String nombre() {
        String n = campo.getText().trim();
        return n.isEmpty() ? "Tu nombre" : n;
    }

    public void ponerNombre(String n) {
        campo.setText(n);
    }

    @Override
    public void irAEscena(int i) {
        super.irAEscena(i);
        escena = Math.max(0, Math.min(cantidadEscenas() - 1, i));
        campo.setVisible(escena == 0);
        if (escena == 0) {
            SwingUtilities.invokeLater(campo::requestFocusInWindow);
        }
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        double cx = a.getCenterX();
        double dw = Math.min(360, a.width * 0.55);
        double dh = dw * 0.63;
        double dy = a.y + 10;
        PanelProblema.dni(g, cx - dw / 2, dy, dw, dh, nombre(), 1);

        double cw = Math.min(64, a.width / 11.0);
        radio = cw;
        double paso = Math.min(cw * 2.2, a.width / (double) CHIPS);
        double filaY = dy + dh + 120;
        for (int k = 0; k < CHIPS; k++) {
            centro[k][0] = cx + (k - 2) * paso;
            centro[k][1] = filaY;
        }

        switch (i) {
            case 0 -> {
                int w = (int) Math.min(380, a.width * 0.7);
                int x = (int) (cx - w / 2.0);
                int y = (int) (dy + dh + 40);
                if (campo.getX() != x || campo.getY() != y || campo.getWidth() != w) {
                    campo.setBounds(x, y, w, 54);
                }
            }
            case 1 -> {
                int llegados = 0;
                for (int k = 0; k < CHIPS; k++) {
                    double p = tramo(t, 0.3 + k * 0.45, 0.6);
                    if (p <= 0) {
                        continue;
                    }
                    double x0 = k < 2 ? a.x - 40 : a.x + a.width + 40;
                    double y0 = a.y + a.height;
                    PanelProblema.chip(g, x0 + (centro[k][0] - x0) * p, y0 + (filaY - y0) * p, cw, p,
                            tramo(t, 0.9 + k * 0.45, 0.3), NUMERO[k]);
                    if (p >= 1) {
                        llegados++;
                    }
                }
                contador(g, cx, filaY + cw * 1.3 + 60, "Chips a nombre de " + nombre(), llegados);
            }
            case 2 -> {
                int n = 0;
                for (int k = 0; k < CHIPS; k++) {
                    chipFila(g, k, cw, bloqueado[k]);
                    n += bloqueado[k] ? 1 : 0;
                }
                contador(g, cx, filaY + cw * 1.3 + 60, "Bloqueados por " + nombre(), n);
            }
            case 3 -> {
                for (int k = 0; k < CHIPS; k++) {
                    chipFila(g, k, cw, false);
                    Tema.flecha(g, centro[k][0], filaY - cw * 0.75, cx + (k - 2) * dw * 0.12, dy + dh + 6,
                            Tema.conAlfa(Tema.AZUL, 220), 2.4f, false, tramo(t, 0.3 + k * 0.25, 0.5));
                }
                double p = tramo(t, 1.8, 0.5);
                g.setFont(Tema.codigo(24f));
                g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
                Tema.textoCentrado(g, "f(chip) = " + nombre(), cx, filaY + cw * 1.3 + 60);
                g.setFont(Tema.letra(Font.PLAIN, 17f));
                g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * p)));
                Tema.textoCentrado(g, "5 chips, 5 flechas, 1 DNI: cada chip tiene exactamente un dueño en papel",
                        cx, filaY + cw * 1.3 + 96);
            }
            default -> palomar(g, a, cx, dy + dh, cw, t);
        }
    }

    private void chipFila(Graphics2D g, int k, double cw, boolean bloq) {
        PanelProblema.chip(g, centro[k][0], centro[k][1], cw, 1, bloq ? 0 : 1, bloq ? "Bloqueado" : NUMERO[k]);
        if (bloq) {
            Iconos.dibujar(g, Iconos.Tipo.EQUIS, centro[k][0], centro[k][1], cw * 0.9, Tema.ROJO);
        }
    }

    private void contador(Graphics2D g, double cx, double y, String rotulo, int n) {
        g.setFont(Tema.letra(Font.PLAIN, 16f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, rotulo, cx, y);
        g.setFont(Tema.titulo(46f));
        g.setColor(n > 0 ? Tema.ROJO : Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, String.valueOf(n), cx, y + 42);
    }

    // ── escena 4: 3 cajones bajo el DNI; los chips 4 y 5 rebotan ──
    private void palomar(Graphics2D g, Rectangle a, double cx, double bajoDni, double cw, double t) {
        double bw = cw * 1.7;
        double bh = cw * 2.1;
        double yCaj = bajoDni + 40;
        double yFila = a.y + a.height - cw * 1.3 - 30;
        int llenos = 0;
        for (int c = 0; c < CAJONES; c++) {
            double x = cx + (c - 1) * bw * 1.25 - bw / 2;
            boolean lleno = t > 0.4 + c * 0.6 + 0.5;
            llenos += lleno ? 1 : 0;
            Tema.rectRedondo(g, x, yCaj, bw, bh, 18, lleno ? Tema.AZUL_SUAVE : Tema.GRIS_CLARO);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(Tema.AZUL_TEXTO);
            Tema.textoCentrado(g, "cajón " + (c + 1), x + bw / 2, yCaj + bh + 22);
        }
        for (int k = 0; k < CHIPS; k++) {
            double x0 = cx + (k - 2) * cw * 2.2;
            double ida = tramo(t, 0.4 + k * 0.6, 0.5);
            double x;
            double y;
            double rojo = 0;
            if (k < CAJONES) {
                x = x0 + (cx + (k - 1) * bw * 1.25 - x0) * ida;
                y = yFila + (yCaj + bh / 2 - yFila) * ida;
            } else {
                // sube hasta los cajones, choca y vuelve a su sitio
                double vuelta = tramo(t, 0.4 + k * 0.6 + 0.5, 0.45);
                double alto = yCaj + bh + cw;
                y = yFila + (alto - yFila) * ida - (alto - yFila) * vuelta;
                x = x0;
                rojo = vuelta > 0 ? 1 : 0;
            }
            PanelProblema.chip(g, x, y, cw, 1, rojo, rojo > 0 ? "No cabe" : NUMERO[k]);
        }
        double fin = tramo(t, 0.4 + 4 * 0.6 + 1, 0.5);
        g.setFont(Tema.letra(Font.BOLD, 18f));
        g.setColor(Tema.conAlfa(Tema.ROJO_OSCURO, (int) (255 * fin)));
        Tema.textoCentrado(g, "5 chips, 3 cajones: 2 se quedan fuera. Ese es el palomar.", cx, a.y + a.height + 4);
        g.setFont(Tema.letra(Font.PLAIN, 16f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, "Cajones llenos: " + llenos + " de " + CAJONES, cx, yCaj - 16);
    }
}
