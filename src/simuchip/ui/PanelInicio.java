package simuchip.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;

/** Pantalla de bienvenida: la pregunta, qué se puede hacer y botones para empezar. */
public final class PanelInicio extends JComponent {

    private final Animador animador;
    private final Boton aprender;
    private final Boton simular;

    public PanelInicio(Runnable irAprender, Runnable irSimular) {
        setLayout(null);
        animador = new Animador(this, 900);
        aprender = new Boton("1 · Aprende funciones  →", Boton.Estilo.PRIMARIO).alHacerClic(irAprender);
        simular = new Boton("2 · Simula el caso real  →", Boton.Estilo.SECUNDARIO).alHacerClic(irSimular);
        aprender.setFont(Tema.letra(Font.BOLD, 18f));
        simular.setFont(Tema.letra(Font.BOLD, 18f));
        add(aprender);
        add(simular);
        for (int i = 0; i < 3; i++) {
            animador.nacer("flecha" + i);
        }
    }

    @Override
    public void doLayout() {
        int x = 96;
        int y = getHeight() - 250;
        aprender.setBounds(x, y, aprender.getPreferredSize().width, 54);
        simular.setBounds(x + aprender.getPreferredSize().width + 18, y, simular.getPreferredSize().width, 54);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Tema.CELESTE_FONDO);
        g.fillRect(0, 0, w, h);
        Tema.tarjeta(g, 24, 16, w - 48, h - 48, 40, Tema.CREMA);

        int x = 96;
        g.setFont(Tema.letra(Font.BOLD, 17f));
        g.setColor(Tema.NARANJA_TEXTO);
        g.drawString("MATEMÁTICA DISCRETA  ·  FUNCIONES", x, 120);
        g.setFont(Tema.letra(Font.BOLD, 58f));
        g.setColor(Tema.AZUL_NOCHE);
        g.drawString("¿A quién le pertenece", x, 196);
        g.drawString("tu chip?", x, 266);
        g.setFont(Tema.letra(Font.PLAIN, 21f));
        g.setColor(Tema.TEXTO_SUAVE);
        int anchoTexto = Math.min(760, w / 2);
        int alto = Tema.parrafo(g, "En el Perú hay cientos de miles de líneas registradas a nombre de personas "
                + "que no las usan. Con funciones inyectivas, sobreyectivas y biyectivas vamos a entender "
                + "el problema y a simular la solución.", x, 300, anchoTexto, 1.25f);

        int yPasos = 300 + alto + 30;
        paso(g, x, yPasos, "1", "Aprende", "Dibuja flechas y mira cómo el código decide el tipo de función.");
        paso(g, x, yPasos + 52, "2", "Simula", "Contrataciones reales, fraude con DNI ajeno y rastreo policial.");
        paso(g, x, yPasos + 104, "3", "Resuelve", "Activa la biometría: g: Línea ↔ Contrato se vuelve biyectiva.");

        dibujarAdorno(g, w, h);
        g.dispose();
    }

    private void paso(Graphics2D g, int x, int y, String numero, String titulo, String texto) {
        g.setColor(Tema.AZUL_NOCHE);
        g.fill(new Ellipse2D.Double(x, y - 22, 34, 34));
        g.setColor(Tema.CREMA);
        g.setFont(Tema.letra(Font.BOLD, 17f));
        Tema.textoCentrado(g, numero, x + 17, y - 5);
        g.setColor(Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 19f));
        g.drawString(titulo, x + 50, y);
        int ancho = g.getFontMetrics().stringWidth(titulo);
        g.setFont(Tema.letra(Font.PLAIN, 18f));
        g.setColor(Tema.TEXTO_SUAVE);
        g.drawString("  " + texto, x + 50 + ancho, y);
    }

    /** Dibujo decorativo: un diagrama sagital biyectivo que se arma al abrir la app. */
    private void dibujarAdorno(Graphics2D g, int w, int h) {
        double cxA = w - 470;
        double cxB = w - 220;
        double cy = h / 2.0 - 40;
        if (cxA < w / 2.0 + 160) {
            return;   // pantalla angosta: sin adorno
        }
        Tema.ovalo(g, cxA, cy, 150, 380, Tema.NARANJA_SUAVE, Tema.NARANJA);
        Tema.ovalo(g, cxB, cy, 150, 380, Tema.AZUL_SUAVE, Tema.AZUL);
        String[] izq = {"L1", "L2", "L3"};
        String[] der = {"C1", "C2", "C3"};
        for (int i = 0; i < 3; i++) {
            double y = cy - 100 + i * 100;
            Tema.flecha(g, cxA + 36, y - 6, cxB - 38, y - 6, Tema.AZUL_NOCHE, 3.5f, false,
                    animador.avance("flecha" + i));
            Tema.flecha(g, cxB - 36, y + 8, cxA + 38, y + 8, Tema.NARANJA_TEXTO, 3f, true,
                    animador.avance("flecha" + i));
            circulo(g, cxA, y, izq[i], Tema.NARANJA, Tema.AZUL_NOCHE);
            circulo(g, cxB, y, der[i], Tema.VERDE, Tema.BLANCO);
        }
        g.setFont(Tema.letra(Font.BOLD, 16f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, "Línea  ↔  Contrato con huella", (cxA + cxB) / 2, cy + 230);
    }

    private void circulo(Graphics2D g, double cx, double cy, String texto, java.awt.Color fondo,
            java.awt.Color letra) {
        g.setColor(fondo);
        g.fill(new Ellipse2D.Double(cx - 30, cy - 30, 60, 60));
        g.setColor(letra);
        g.setFont(Tema.letra(Font.BOLD, 18f));
        Tema.textoCentrado(g, texto, cx, cy);
    }
}
