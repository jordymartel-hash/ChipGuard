package simuchip.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 * ChipGuard: seis capítulos en orden, como una keynote. Barra superior mínima con la marca,
 * los capítulos en texto (un punto bajo el activo) y «Siguiente». Las flechas ← → también avanzan.
 */
public final class Aplicacion extends JPanel {

    private static final String[] CAPITULOS = {"El problema", "Cómo pasa", "Funciones", "Pruébalo", "El modelo",
        "Simulación", "ChipGuard", "Juego", "Cierre"};
    private static final int ALTO_BARRA = 60;
    private static final int TRANSICION_MS = 420;

    private final CardLayout tarjetas = new CardLayout();
    private final Escenario centro = new Escenario();
    private final Boton[] pestanas = new Boton[CAPITULOS.length];
    private final Boton siguiente = new Boton("Siguiente", Boton.Estilo.PRIMARIO).conIcono(Iconos.Tipo.SIGUIENTE);
    private final JComponent[] paneles;
    private final PanelAprende aprende = new PanelAprende();
    private final PanelSimula simula = new PanelSimula();
    private final PanelChipGuard chipGuard = new PanelChipGuard();
    private final PanelDetective detective = new PanelDetective();
    private final PanelCasos casos = new PanelCasos();
    private final PanelFunciones funciones = new PanelFunciones();
    private final PanelModelo modelo = new PanelModelo();
    private final PanelCierre cierre = new PanelCierre();
    private final Cronometro cronometro = new Cronometro();
    private int actual = -1;

    public Aplicacion() {
        super(new BorderLayout());
        setBackground(Tema.CELESTE_FONDO);
        paneles = new JComponent[] {new PanelProblema(() -> irA(1)), casos, funciones, aprende, modelo, simula,
            chipGuard, detective, cierre};
        add(new Barra(), BorderLayout.NORTH);
        for (int i = 0; i < paneles.length; i++) {
            centro.add(paneles[i], CAPITULOS[i]);
        }
        add(centro, BorderLayout.CENTER);
        // como en una keynote: primero las escenas del capítulo, luego el capítulo siguiente
        atajo(KeyEvent.VK_RIGHT, "siguiente", () -> {
            if (!(paneles[actual] instanceof Capitulo c) || !c.avanzar()) {
                irA(actual + 1);
            }
        });
        atajo(KeyEvent.VK_LEFT, "anterior", () -> {
            if (!(paneles[actual] instanceof Capitulo c) || !c.retroceder()) {
                irA(actual - 1);
            }
        });
        irA(0);
    }

    private void atajo(int tecla, String nombre, Runnable accion) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla, 0), nombre);
        getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accion.run();
            }
        });
    }

    public void irA(int indice) {
        if (indice < 0 || indice >= CAPITULOS.length || indice == actual) {
            return;
        }
        actual = indice;
        tarjetas.show(centro, CAPITULOS[indice]);
        for (int i = 0; i < pestanas.length; i++) {
            pestanas[i].setEncendido(i == indice);
        }
        siguiente.setVisible(indice < CAPITULOS.length - 1);
        if (indice < CAPITULOS.length - 1) {
            siguiente.setTexto(CAPITULOS[indice + 1]);
        }
        if (paneles[indice] instanceof Capitulo c) {
            c.alMostrar();
        }
        centro.entrar();
        revalidate();
        repaint();
    }

    public int cantidadCapitulos() {
        return CAPITULOS.length;
    }

    public PanelAprende aprende() {
        return aprende;
    }

    public PanelSimula simula() {
        return simula;
    }

    public PanelChipGuard chipGuard() {
        return chipGuard;
    }

    public PanelDetective detective() {
        return detective;
    }

    public PanelCasos casos() {
        return casos;
    }

    public PanelFunciones funciones() {
        return funciones;
    }

    public PanelModelo modelo() {
        return modelo;
    }

    public PanelCierre cierre() {
        return cierre;
    }

    /** Contenedor de capítulos con la transición: fundido desde blanco y subida de 18 px. */
    private final class Escenario extends JPanel {

        private long inicio;
        private final Timer reloj = new Timer(16, e -> {
            repaint();
            if (avance() >= 1) {
                ((Timer) e.getSource()).stop();
            }
        });

        Escenario() {
            super(tarjetas);
            setBackground(Tema.CELESTE_FONDO);
        }

        void entrar() {
            inicio = System.currentTimeMillis();
            if (!Animador.instantaneo) {
                reloj.restart();
            }
        }

        private double avance() {
            if (Animador.instantaneo) {
                return 1;
            }
            double t = Math.min(1, (System.currentTimeMillis() - inicio) / (double) TRANSICION_MS);
            return 1 - Math.pow(1 - t, 4);
        }

        @Override
        public void paint(Graphics graphics) {
            double p = avance();
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(0, (1 - p) * 18);
            super.paint(g);
            g.dispose();
            if (p < 1) {
                Graphics2D v = (Graphics2D) graphics.create();
                v.setColor(Tema.conAlfa(Tema.BLANCO, (int) (255 * (1 - p))));
                v.fillRect(0, 0, getWidth(), getHeight());
                v.dispose();
            }
        }
    }

    /** Barra superior: marca a la izquierda, capítulos al centro, «Siguiente» a la derecha. */
    private final class Barra extends JComponent {

        Barra() {
            setLayout(null);
            setPreferredSize(new Dimension(1200, ALTO_BARRA));
            for (int i = 0; i < CAPITULOS.length; i++) {
                int indice = i;
                pestanas[i] = new Boton(CAPITULOS[i], Boton.Estilo.PESTANA).alHacerClic(() -> irA(indice));
                add(pestanas[i]);
            }
            siguiente.setFont(Tema.medio(14f));
            siguiente.alHacerClic(() -> irA(actual + 1));
            add(siguiente);
            add(cronometro);
        }

        @Override
        public void doLayout() {
            int total = 0;
            for (Boton b : pestanas) {
                total += b.getPreferredSize().width;
            }
            int x = (getWidth() - total) / 2;
            for (Boton b : pestanas) {
                int bw = b.getPreferredSize().width;
                b.setBounds(x, 6, bw, ALTO_BARRA - 8);
                x += bw;
            }
            Dimension d = siguiente.getPreferredSize();
            int xs = getWidth() - 28 - Math.max(d.width, 130);
            siguiente.setBounds(getWidth() - 28 - d.width, (ALTO_BARRA - 36) / 2, d.width, 36);
            Dimension c = cronometro.getPreferredSize();
            cronometro.setBounds(xs - 12 - c.width, (ALTO_BARRA - c.height) / 2, c.width, c.height);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Tema.suavizar(g);
            g.setColor(Tema.BLANCO);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Tema.FILETE);
            g.fillRect(0, getHeight() - 1, getWidth(), 1);
            Iconos.dibujar(g, Iconos.Tipo.ESCUDO, 42, ALTO_BARRA / 2.0, 26, Tema.AZUL);
            Iconos.dibujar(g, Iconos.Tipo.VISTO, 42, ALTO_BARRA / 2.0 + 1, 12, Tema.AZUL);
            g.setFont(Tema.titulo(20f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.texto(g, "ChipGuard", 64, ALTO_BARRA / 2.0 + 7);
            g.dispose();
        }
    }
}
