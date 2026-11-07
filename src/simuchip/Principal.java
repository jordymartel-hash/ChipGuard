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
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("SimuChip · Funciones y líneas móviles");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(new Aplicacion());
            ventana.setMinimumSize(new Dimension(1280, 820));
            ventana.setSize(1500, 920);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
