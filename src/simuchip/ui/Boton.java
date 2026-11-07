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

/** Botón en forma de pastilla, dibujado a mano. Puede ser normal o interruptor (on/off). */
public final class Boton extends JComponent {

    public enum Estilo { PRIMARIO, SECUNDARIO, PELIGRO, EXITO, PESTANA }

    private String texto;
    private final Estilo estilo;
    private final boolean interruptor;
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
        setFont(Tema.letra(Font.BOLD, 15f));
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

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int extra = interruptor ? 52 : 0;
        return new Dimension(fm.stringWidth(texto) + 36 + extra, 42);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        Color fondo;
        Color letra;
        switch (estilo) {
            case PRIMARIO -> {
                fondo = Tema.AZUL_NOCHE;
                letra = Tema.CREMA;
            }
            case EXITO -> {
                fondo = Tema.VERDE;
                letra = Tema.BLANCO;
            }
            case PELIGRO -> {
                fondo = Tema.ROJO;
                letra = Tema.BLANCO;
            }
            case PESTANA -> {
                fondo = encendido ? Tema.DORADO : Tema.conAlfa(Tema.CREMA, 0);
                letra = encendido ? Tema.AZUL_NOCHE : Tema.CREMA;
            }
            default -> {
                fondo = interruptor && encendido ? Tema.VERDE : Tema.BLANCO;
                letra = interruptor && encendido ? Tema.BLANCO : Tema.AZUL_NOCHE;
            }
        }
        if (encima && !presionado) {
            fondo = estilo == Estilo.PESTANA && !encendido ? Tema.conAlfa(Tema.CREMA, 30) : fondo.brighter();
        }
        int dy = presionado ? 2 : 0;
        if (estilo != Estilo.PESTANA) {
            Tema.rectRedondo(g, 0, 3, w - 1, h - 4, h - 4, Tema.conAlfa(Tema.AZUL_NOCHE, 40));
        }
        Tema.rectRedondo(g, 0, dy, w - 1, h - 4, h - 4, fondo);
        if (estilo == Estilo.SECUNDARIO && !(interruptor && encendido)) {
            Tema.borde(g, 0.5, dy + 0.5, w - 2, h - 5, h - 5, Tema.GRIS, 1.5f);
        }
        g.setFont(getFont());
        g.setColor(letra);
        int textoX = interruptor ? 18 : (w - g.getFontMetrics().stringWidth(texto)) / 2;
        int base = dy + (h - 4 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2;
        g.drawString(texto, textoX, base);
        if (interruptor) {
            dibujarInterruptor(g, w - 52, dy + (h - 4) / 2 - 11);
        }
        g.dispose();
    }

    private void dibujarInterruptor(Graphics2D g, int x, int y) {
        Tema.rectRedondo(g, x, y, 38, 22, 22, encendido ? Tema.conAlfa(Tema.BLANCO, 90) : Tema.GRIS);
        g.setColor(encendido ? Tema.BLANCO : Tema.BLANCO);
        double cx = encendido ? x + 27 : x + 11;
        g.fill(new Ellipse2D.Double(cx - 8, y + 3, 16, 16));
    }
}
