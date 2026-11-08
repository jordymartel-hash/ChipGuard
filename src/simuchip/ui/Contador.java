package simuchip.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.IntConsumer;
import javax.swing.JComponent;

/** Contador compacto «A  −  4  +» en una sola píldora gris, como los steppers de macOS. */
public final class Contador extends JComponent {

    private final String etiqueta;
    private int valor;
    private IntConsumer alCambiar = d -> {};

    public Contador(String etiqueta, int valor) {
        this.etiqueta = etiqueta;
        this.valor = valor;
        setFont(Tema.medio(14f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int w = getWidth();
                if (e.getX() > w - 38) {
                    alCambiar.accept(1);
                } else if (e.getX() > w - 110 && e.getX() < w - 74) {
                    alCambiar.accept(-1);
                }
            }
        });
    }

    /** Recibe −1 o +1; quien lo usa decide el nuevo valor y lo devuelve con setValor. */
    public Contador alCambiar(IntConsumer accion) {
        this.alCambiar = accion;
        return this;
    }

    public void setValor(int v) {
        valor = v;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getFontMetrics(getFont()).stringWidth(etiqueta) + 132, 36);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        Tema.rectRedondo(g, 0, 0, w, h, h, Tema.GRIS_CLARO);
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        int base = (h + fm.getAscent() - fm.getDescent()) / 2;
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, etiqueta, 16, base);
        // −   valor   +
        Tema.rectRedondo(g, w - 110, 4, 32, h - 8, h - 8, Tema.BLANCO);
        Tema.rectRedondo(g, w - 36, 4, 32, h - 8, h - 8, Tema.BLANCO);
        g.setColor(Tema.AZUL_NOCHE);
        g.fillRect(w - 100, h / 2 - 1, 12, 2);
        g.fillRect(w - 26, h / 2 - 1, 12, 2);
        g.fillRect(w - 21, h / 2 - 6, 2, 12);
        g.setFont(Tema.letra(java.awt.Font.BOLD, 15f));
        fm = g.getFontMetrics();
        String v = String.valueOf(valor);
        Tema.texto(g, v, w - 57 - fm.stringWidth(v) / 2.0, base);
        g.dispose();
    }
}
