package simuchip;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import simuchip.ui.Aplicacion;

/** Punto de entrada: abre la ventana de SimuChip. */
public final class Principal {

    private Principal() {}

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        ajustarEscalaEnLinux();
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("ChipGuard");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(new Aplicacion());
            ventana.setMinimumSize(new Dimension(1200, 700));
            // Casi toda la pantalla, pero dejando libre abajo el espacio del dock (en Linux flota encima).
            java.awt.Rectangle util = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getMaximumWindowBounds();
            boolean linux = System.getProperty("os.name", "").toLowerCase().contains("linux");
            int ancho = Math.min(util.width - 20, 1600);
            int alto = Math.min(util.height - (linux ? 120 : 20), 980);
            ventana.setBounds(util.x + (util.width - ancho) / 2, util.y + (linux ? 36 : 10), ancho, alto);
            ventana.setVisible(true);
        });
    }

    /**
     * En Linux, Java no detecta solo el escalado de pantallas de alta resolución y todo se ve diminuto.
     * Si el usuario no fijó -Dsun.java2d.uiScale, se calcula con el ancho de la pantalla (base 1920 px).
     * Debe correr ANTES de crear cualquier ventana. En Windows y macOS Java ya escala solo.
     */
    private static void ajustarEscalaEnLinux() {
        if (!System.getProperty("os.name", "").toLowerCase().contains("linux")
                || System.getProperty("sun.java2d.uiScale") != null) {
            return;
        }
        try {
            Process p = new ProcessBuilder("xrandr", "--current").redirectErrorStream(true).start();
            String salida = new String(p.getInputStream().readAllBytes());
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("current (\\d+) x (\\d+)").matcher(salida);
            if (m.find()) {
                int ancho = Integer.parseInt(m.group(1));
                if (ancho > 2200) {
                    // En Linux Java solo acepta escalas ENTERAS (1.5 se ignora; comprobado): 3072 px → 2.
                    long escala = Math.max(1, Math.min(3, Math.round(ancho / 1920.0)));
                    System.setProperty("sun.java2d.uiScale", String.valueOf(escala));
                }
            }
        } catch (Exception e) {
            // sin xrandr: se queda la escala por defecto
        }
    }
}
