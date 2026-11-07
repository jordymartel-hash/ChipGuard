package simuchip.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Timer;

/** Notificaciones emergentes ("toasts") que aparecen, se quedan unos segundos y se desvanecen. */
public final class Avisos {

    private record Aviso(String titulo, String texto, Color color, long nacio) {}

    private static final int DURACION_MS = 3600;
    private final int maximo;

    private final List<Aviso> lista = new ArrayList<>();
    private final Timer reloj;

    public Avisos(JComponent componente) {
        this(componente, 3);
    }

    public Avisos(JComponent componente, int maximo) {
        this.maximo = maximo;
        reloj = new Timer(30, e -> {
            componente.repaint();
            limpiar();
            if (lista.isEmpty()) {
                ((Timer) e.getSource()).stop();
            }
        });
    }

    public void mostrar(String titulo, String texto, Color color) {
        lista.add(new Aviso(titulo, texto, color, System.currentTimeMillis()));
        while (lista.size() > maximo) {
            lista.remove(0);
        }
        if (!Animador.instantaneo) {
            reloj.start();
        }
    }

    public void limpiarTodo() {
        lista.clear();
    }

    private void limpiar() {
        if (Animador.instantaneo) {
            return;
        }
        long ahora = System.currentTimeMillis();
        Iterator<Aviso> it = lista.iterator();
        while (it.hasNext()) {
            if (ahora - it.next().nacio() > DURACION_MS) {
                it.remove();
            }
        }
    }

    /** Dibuja los avisos apilados, alineados a la derecha en xDerecha, desde y hacia abajo. */
    public void dibujar(Graphics2D g, int xDerecha, int y, int ancho) {
        long ahora = System.currentTimeMillis();
        int yy = y;
        for (Aviso a : new ArrayList<>(lista)) {
            double edad = Animador.instantaneo ? 1000 : ahora - a.nacio();
            double entrada = Math.min(1, edad / 260.0);
            double salida = Animador.instantaneo ? 1 : Math.min(1, (DURACION_MS - edad) / 400.0);
            double alfa = Math.max(0, Math.min(entrada, salida));
            int desliz = (int) ((1 - entrada) * 40);

            g.setFont(Tema.letra(Font.PLAIN, 14f));
            FontMetrics fm = g.getFontMetrics();
            List<String> lineas = Tema.partir(a.texto(), fm, ancho - 66);
            int alto = 44 + lineas.size() * 19;
            int x = xDerecha - ancho + desliz;
            g.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, (float) alfa));
            Tema.tarjeta(g, x, yy, ancho, alto, 18, Tema.AZUL_NOCHE);
            Tema.rectRedondo(g, x, yy, 8, alto, 8, a.color());
            g.setColor(a.color());
            g.fill(new Ellipse2D.Double(x + 20, yy + 16, 14, 14));
            g.setFont(Tema.letra(Font.BOLD, 15f));
            g.setColor(Tema.CREMA);
            g.drawString(a.titulo(), x + 44, yy + 28);
            g.setFont(Tema.letra(Font.PLAIN, 14f));
            g.setColor(new Color(0xC9D6EA));
            int ly = yy + 50;
            for (String l : lineas) {
                g.drawString(l, x + 44, ly);
                ly += 19;
            }
            g.setComposite(java.awt.AlphaComposite.SrcOver);
            yy += alto + 10;
        }
    }
}
