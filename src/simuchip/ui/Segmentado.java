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

/** Control segmentado estilo macOS: un riel gris y el segmento elegido en blanco con sombra. */
public final class Segmentado extends JComponent {

    private final String[] opciones;
    private int elegido = -1;
    private IntConsumer alElegir = i -> {};

    public Segmentado(String... opciones) {
        this.opciones = opciones;
        setFont(Tema.medio(14f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int i = indiceEn(e.getX());
                if (i >= 0) {
                    elegido = i;
                    repaint();
                    alElegir.accept(i);
                }
            }
        });
    }

    public Segmentado alElegir(IntConsumer accion) {
        this.alElegir = accion;
        return this;
    }

    /** -1 deja todo sin elegir (por ejemplo, cuando el usuario dibuja a mano). */
    public void setElegido(int i) {
        elegido = i;
        repaint();
    }

    private int[] anchos() {
        FontMetrics fm = getFontMetrics(getFont());
        int[] a = new int[opciones.length];
        for (int i = 0; i < opciones.length; i++) {
            a[i] = fm.stringWidth(opciones[i]) + 30;
        }
        return a;
    }

    private int indiceEn(int x) {
        int acumulado = 3;
        int[] a = anchos();
        for (int i = 0; i < a.length; i++) {
            if (x >= acumulado && x < acumulado + a[i]) {
                return i;
            }
            acumulado += a[i];
        }
        return -1;
    }

    @Override
    public Dimension getPreferredSize() {
        int total = 6;
        for (int a : anchos()) {
            total += a;
        }
        return new Dimension(total, 36);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int h = getHeight();
        Tema.rectRedondo(g, 0, 0, getWidth(), h, h, Tema.GRIS_CLARO);
        int[] a = anchos();
        int x = 3;
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < opciones.length; i++) {
            if (i == elegido) {
                Tema.sombra(g, x, 3, a[i], h - 6, h - 6, 14);
                Tema.rectRedondo(g, x, 3, a[i], h - 6, h - 6, Tema.BLANCO);
            } else if (i > 0 && i != elegido + 1) {
                g.setColor(Tema.conAlfa(Tema.GRIS, 140));
                g.fillRect(x, 10, 1, h - 20);
            }
            g.setColor(i == elegido ? Tema.AZUL_NOCHE : Tema.TEXTO_SUAVE);
            g.setFont(i == elegido ? Tema.letra(java.awt.Font.BOLD, getFont().getSize2D()) : getFont());
            fm = g.getFontMetrics();
            Tema.texto(g, opciones[i], x + (a[i] - fm.stringWidth(opciones[i])) / 2.0,
                    (h + fm.getAscent() - fm.getDescent()) / 2.0);
            x += a[i];
        }
        g.dispose();
    }
}
