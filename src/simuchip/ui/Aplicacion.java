package simuchip.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** Todo el contenido de la app: cabecera con pestañas + las tres pantallas. */
public final class Aplicacion extends JPanel {

    private static final String[] NOMBRES = {"Inicio", "1 · Aprende", "2 · Simula"};

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel centro = new JPanel(tarjetas);
    private final Boton[] pestanas = new Boton[NOMBRES.length];
    private final PanelAprende aprende = new PanelAprende();
    private final PanelSimula simula = new PanelSimula();

    public Aplicacion() {
        super(new BorderLayout());
        add(new Cabecera(), BorderLayout.NORTH);
        centro.add(new PanelInicio(() -> irA(1), () -> irA(2)), NOMBRES[0]);
        centro.add(aprende, NOMBRES[1]);
        centro.add(simula, NOMBRES[2]);
        add(centro, BorderLayout.CENTER);
        irA(0);
    }

    public void irA(int indice) {
        tarjetas.show(centro, NOMBRES[indice]);
        for (int i = 0; i < pestanas.length; i++) {
            pestanas[i].setEncendido(i == indice);
        }
    }

    public PanelAprende aprende() {
        return aprende;
    }

    public PanelSimula simula() {
        return simula;
    }

    private final class Cabecera extends JComponent {

        Cabecera() {
            setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 15));
            setPreferredSize(new Dimension(1200, 74));
            for (int i = 0; i < NOMBRES.length; i++) {
                int indice = i;
                pestanas[i] = new Boton(NOMBRES[i], Boton.Estilo.PESTANA).alHacerClic(() -> irA(indice));
                pestanas[i].setFont(Tema.letra(Font.BOLD, 16f));
                add(pestanas[i]);
            }
            JPanel margen = new JPanel();
            margen.setOpaque(false);
            margen.setPreferredSize(new Dimension(12, 10));
            add(margen);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Tema.suavizar(g);
            g.setColor(Tema.AZUL_NOCHE);
            g.fillRect(0, 0, getWidth(), getHeight());
            // logo: dos conjuntos unidos por una flecha
            g.setColor(Tema.DORADO);
            g.fill(new Ellipse2D.Double(28, 22, 22, 30));
            g.setColor(new java.awt.Color(0x7FB0F0));
            g.fill(new Ellipse2D.Double(66, 22, 22, 30));
            Tema.flecha(g, 44, 37, 70, 37, Tema.CREMA, 2.5f, false, 1);
            g.setFont(Tema.letra(Font.BOLD, 26f));
            g.setColor(Tema.CREMA);
            g.drawString("SimuChip", 104, 46);
            int ancho = g.getFontMetrics().stringWidth("SimuChip");
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            g.setColor(new java.awt.Color(0xC9D6EA));
            g.drawString("Funciones discretas aplicadas a las líneas móviles", 104 + ancho + 18, 45);
            g.dispose();
        }
    }
}
