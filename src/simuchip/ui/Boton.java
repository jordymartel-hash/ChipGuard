package simuchip.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;

/**
 * Botón estilo Apple: píldora azul (principal), gris (secundaria), texto rojo (destructiva),
 * verde (aprobar), interruptor tipo iOS o pestaña de texto con un punto bajo la activa.
 */
public final class Boton extends JComponent {

    public enum Estilo { PRIMARIO, SECUNDARIO, PELIGRO, EXITO, PESTANA }

    private String texto;
    private final Estilo estilo;
    private final boolean interruptor;
    private Iconos.Tipo icono;
    private boolean encendido;
    private boolean encima;
    private boolean presionado;
    private Runnable accion = () -> {};

    public Boton(String texto, Estilo estilo) {
        this(texto, estilo, false);
    }

    public Boton(String texto, Estilo estilo, boolean interruptor) {
        this.texto = texto;
        this.estilo = estilo;
        this.interruptor = interruptor;
        setFont(estilo == Estilo.PESTANA ? Tema.medio(15f) : Tema.medio(14.5f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                presionado = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                presionado = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (presionado && encima) {
                    if (interruptor) {
                        encendido = !encendido;
                    }
                    accion.run();
                }
                presionado = false;
                repaint();
            }
        });
    }

    public Boton alHacerClic(Runnable accion) {
        this.accion = accion;
        return this;
    }

    public Boton conIcono(Iconos.Tipo tipo) {
        this.icono = tipo;
        revalidate();
        repaint();
        return this;
    }

    public boolean encendido() {
        return encendido;
    }

    public void setEncendido(boolean valor) {
        encendido = valor;
        repaint();
    }

    public void setTexto(String nuevo) {
        texto = nuevo;
        revalidate();
        repaint();
    }

    private int tamIcono() {
        return Math.round(getFont().getSize2D() * 0.95f);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int extra = interruptor ? 58 : 0;
        int conIcono = icono != null ? tamIcono() + (texto.isEmpty() ? 0 : 8) : 0;
        int relleno = estilo == Estilo.PESTANA ? 20 : 36;
        int alto = Math.max(38, Math.round(getFont().getSize2D() * 2.5f));
        return new Dimension(fm.stringWidth(texto) + relleno + extra + conIcono, alto);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();

        if (estilo == Estilo.PESTANA) {
            Color c = encendido ? Tema.AZUL_NOCHE : encima ? Tema.AZUL_NOCHE : Tema.TEXTO_SUAVE;
            g.setFont(encendido ? Tema.letra(Font.BOLD, getFont().getSize2D()) : getFont());
            fm = g.getFontMetrics();
            g.setColor(c);
            int base = (h + fm.getAscent() - fm.getDescent()) / 2 - 2;
            Tema.texto(g, texto, (w - fm.stringWidth(texto)) / 2.0, base);
            if (encendido) {
                g.setColor(Tema.AZUL);
                g.fill(new Ellipse2D.Double(w / 2.0 - 2.5, h - 8, 5, 5));
            }
            g.dispose();
            return;
        }

        Color fondo;
        Color letra;
        switch (estilo) {
            case PRIMARIO -> {
                fondo = Tema.AZUL;
                letra = Tema.BLANCO;
            }
            case EXITO -> {
                fondo = Tema.VERDE;
                letra = Tema.BLANCO;
            }
            case PELIGRO -> {
                fondo = Tema.GRIS_CLARO;
                letra = Tema.ROJO_OSCURO;
            }
            default -> {
                fondo = Tema.GRIS_CLARO;
                letra = Tema.AZUL_NOCHE;
            }
        }
        if (presionado) {
            fondo = mezclar(fondo, Color.BLACK, 0.14);
        } else if (encima) {
            fondo = mezclar(fondo, Color.BLACK, 0.06);
        }
        Tema.rectRedondo(g, 0, 0, w, h, h, fondo);

        int x = interruptor ? 18 : 0;
        int anchoContenido = fm.stringWidth(texto) + (icono != null ? tamIcono() + (texto.isEmpty() ? 0 : 8) : 0);
        if (!interruptor) {
            x = (w - anchoContenido) / 2;
        }
        int cy = h / 2;
        if (icono != null) {
            Iconos.dibujar(g, icono, x + tamIcono() / 2.0, cy, tamIcono(), letra);
            x += tamIcono() + 8;
        }
        g.setColor(letra);
        Tema.texto(g, texto, x, (h + fm.getAscent() - fm.getDescent()) / 2.0);
        if (interruptor) {
            dibujarInterruptor(g, w - 54, cy - 13);
        }
        g.dispose();
    }

    /** Interruptor tipo iOS: verde cuando está encendido. */
    private void dibujarInterruptor(Graphics2D g, int x, int y) {
        Tema.rectRedondo(g, x, y, 44, 26, 26, encendido ? new Color(0x34C759) : Tema.GRIS);
        double cx = encendido ? x + 31 : x + 13;
        g.setColor(new Color(0, 0, 0, 30));
        g.fill(new Ellipse2D.Double(cx - 11, y + 3, 22, 22));
        g.setColor(Tema.BLANCO);
        g.fill(new Ellipse2D.Double(cx - 11, y + 2, 22, 22));
    }

    private static Color mezclar(Color a, Color b, double t) {
        return new Color((int) (a.getRed() * (1 - t) + b.getRed() * t), (int) (a.getGreen() * (1 - t) + b.getGreen() * t),
                (int) (a.getBlue() * (1 - t) + b.getBlue() * t));
    }
}
