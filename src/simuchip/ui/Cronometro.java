package simuchip.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Cronómetro del presentador: clic para empezar o pausar, clic derecho para reiniciar.
 * Se pone naranja a los 12 minutos y rojo a los 15, el límite de la exposición.
 */
public final class Cronometro extends JComponent {

    private long acumulado;
    private long desde = -1;
    private final Timer reloj = new Timer(500, e -> repaint());

    public Cronometro() {
        setFont(Tema.codigo(14f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Clic: empezar o pausar · clic derecho: reiniciar");
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    acumulado = 0;
                    desde = desde >= 0 ? System.currentTimeMillis() : -1;
                } else if (desde >= 0) {
                    acumulado += System.currentTimeMillis() - desde;
                    desde = -1;
                    reloj.stop();
                } else {
                    desde = System.currentTimeMillis();
                    reloj.start();
                }
                repaint();
            }
        });
    }

    private long milis() {
        return acumulado + (desde >= 0 ? System.currentTimeMillis() - desde : 0);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(92, 34);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        long s = milis() / 1000;
        Color c = s >= 15 * 60 ? Tema.ROJO : s >= 12 * 60 ? Tema.NARANJA_TEXTO : Tema.AZUL_NOCHE;
        int h = getHeight();
        Tema.rectRedondo(g, 0, 0, getWidth(), h, h, Tema.GRIS_CLARO);
        g.setColor(desde >= 0 ? new Color(0x34C759) : Tema.GRIS);
        g.fill(new Ellipse2D.Double(14, h / 2.0 - 4, 8, 8));
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        g.setColor(c);
        String texto = String.format("%d:%02d", s / 60, s % 60);
        Tema.texto(g, texto, 30, (h + fm.getAscent() - fm.getDescent()) / 2.0);
        g.dispose();
    }
}
