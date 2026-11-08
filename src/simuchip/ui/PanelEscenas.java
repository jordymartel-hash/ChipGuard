package simuchip.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Capítulo hecho de escenas, como diapositivas animadas de una keynote: titular grande y texto a la
 * izquierda, la figura animada a la derecha (o debajo, si la escena la pide a todo el ancho).
 * Las flechas ← → recorren las escenas antes de cambiar de capítulo.
 */
public abstract class PanelEscenas extends JComponent implements Capitulo {

    /** Una escena: titular, texto y si la figura va a todo el ancho debajo del texto. */
    protected record Escena(String titulo, String texto, boolean anchoCompleto) {

        public Escena(String titulo, String texto) {
            this(titulo, texto, false);
        }
    }

    private final List<Escena> escenas;
    private int escena;
    private long inicio = System.currentTimeMillis();
    private final Boton anterior = new Boton("Anterior", Boton.Estilo.SECUNDARIO).conIcono(Iconos.Tipo.ANTERIOR);
    private final Boton siguiente = new Boton("Siguiente", Boton.Estilo.PRIMARIO).conIcono(Iconos.Tipo.SIGUIENTE);
    private final Timer reloj;

    protected PanelEscenas(List<Escena> escenas) {
        this.escenas = escenas;
        setLayout(null);
        anterior.setFont(Tema.medio(15f));
        siguiente.setFont(Tema.medio(15f));
        anterior.alHacerClic(this::retroceder);
        siguiente.alHacerClic(this::avanzar);
        add(anterior);
        add(siguiente);
        reloj = new Timer(16, e -> {
            if (isShowing()) {
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
    }

    /** Dibuja la figura de la escena i dentro de "area". t = segundos desde que empezó la escena. */
    protected abstract void dibujarFigura(Graphics2D g, int i, Rectangle area, double t);

    /** Para quien necesite dibujar algo propio debajo del texto (por ejemplo una fuente). */
    protected void dibujarExtra(Graphics2D g, int i, int x, int y, int ancho, double t) {}

    @Override
    public void alMostrar() {
        irAEscena(0);
    }

    @Override
    public boolean avanzar() {
        if (escena < escenas.size() - 1) {
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

    public void irAEscena(int i) {
        escena = Math.max(0, Math.min(escenas.size() - 1, i));
        inicio = System.currentTimeMillis();
        anterior.setVisible(escena > 0);
        siguiente.setVisible(escena < escenas.size() - 1);
        if (!Animador.instantaneo) {
            reloj.restart();
        }
        repaint();
    }

    protected double segundos() {
        return Animador.instantaneo ? 99 : (System.currentTimeMillis() - inicio) / 1000.0;
    }

    protected static double tramo(double t, double desde, double dura) {
        return Diagrama.tramo(t, desde, dura);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Tema.FONDO_PAGINA);
        g.fillRect(0, 0, w, h);

        Escena e = escenas.get(escena);
        double t = segundos();
        double entra = tramo(t, 0, 0.5);
        int x = 80;

        // progreso de escenas
        for (int i = 0; i < escenas.size(); i++) {
            Tema.rectRedondo(g, x + i * 26, 44, i == escena ? 22 : 16, 6, 6,
                    i == escena ? Tema.AZUL : i < escena ? Tema.conAlfa(Tema.AZUL, 90) : Tema.GRIS_CLARO);
        }

        int anchoTexto = e.anchoCompleto() ? w - 2 * x : (int) Math.min(560, w * 0.36);
        int dy = (int) ((1 - entra) * 14);
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * entra)));
        g.setFont(Tema.titulo(e.anchoCompleto() ? 46f : 50f));
        int y = 74 + dy;
        y += Tema.parrafo(g, e.titulo(), x, y, anchoTexto, 1.04f) + 14;
        g.setFont(Tema.letra(Font.PLAIN, 19f));
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * entra)));
        y += Tema.parrafo(g, e.texto(), x, y, e.anchoCompleto() ? Math.min(980, anchoTexto) : anchoTexto, 1.4f);
        dibujarExtra(g, escena, x, y + 16, anchoTexto, t);

        Rectangle area;
        if (e.anchoCompleto()) {
            area = new Rectangle(x, y + 30, w - 2 * x, h - y - 30 - 90);
            colocarBotones(x, h - 78);
        } else {
            int xf = x + anchoTexto + 60;
            area = new Rectangle(xf, 70, w - xf - 60, h - 150);
            colocarBotones(x, Math.min(h - 78, y + 34));
        }
        dibujarFigura(g, escena, area, t);
        g.dispose();
    }

    private void colocarBotones(int x, int y) {
        int wa = anterior.getPreferredSize().width;
        int ws = siguiente.getPreferredSize().width;
        int xs = anterior.isVisible() ? x + wa + 12 : x;
        if (anterior.getX() != x || anterior.getY() != y || siguiente.getX() != xs) {
            anterior.setBounds(x, y, wa, 46);
            siguiente.setBounds(xs, y, ws, 46);
        }
    }

    @Override
    public void doLayout() {
        colocarBotones(80, getHeight() - 78);
    }

    public int cantidadEscenas() {
        return escenas.size();
    }
}
