package simuchip;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import simuchip.ui.Animador;
import simuchip.ui.Aplicacion;
import simuchip.ui.PanelTienda;

/**
 * Genera capturas PNG de la app sin abrir ventanas (para el informe y la PPT).
 * Uso: java -Djava.awt.headless=true -cp out simuchip.Capturas carpeta-destino
 */
public final class Capturas {

    private static final int ANCHO = Integer.getInteger("ancho", 1600);
    private static final int ALTO = Integer.getInteger("alto", 900);

    private Capturas() {}

    public static void main(String[] args) throws IOException {
        File carpeta = new File(args.length > 0 ? args[0] : "capturas");
        carpeta.mkdirs();
        Animador.instantaneo = true;
        Aplicacion app = new Aplicacion();

        app.irA(0);
        for (int e = 0; e < app.historia().cantidadEscenas(); e++) {
            app.historia().irAEscena(e);
            guardar(app, carpeta, "1" + (char) ('a' + e) + "-historia.png");
        }
        app.irA(1);
        for (int e = 0; e < app.matematica().cantidadEscenas(); e++) {
            app.matematica().irAEscena(e);
            guardar(app, carpeta, "2" + (char) ('a' + e) + "-matematica.png");
        }
        app.irA(2);
        for (int e = 0; e < 5; e++) {
            app.chipGuard().irAEscena(e);
            guardar(app, carpeta, "3" + (char) ('a' + e) + "-chipguard.png");
        }
        app.irA(3);
        PanelTienda t = app.tienda();
        guardar(app, carpeta, "4a-tienda-portada.png");
        t.continuar();
        guardar(app, carpeta, "4b-tienda-cliente.png");
        t.decidir(true);
        t.continuar();
        guardar(app, carpeta, "4c-tienda-encapuchado.png");
        t.decidir(true);
        guardar(app, carpeta, "4d-tienda-fraude.png");
        t.continuar();
        t.decidir(true);
        guardar(app, carpeta, "4e-tienda-extra.png");
        for (int k = 0; k < 3; k++) {
            t.continuar();
            t.decidir(k != 1);
        }
        t.continuar();
        guardar(app, carpeta, "4f-tienda-resumen.png");
        t.continuar();
        t.continuar();
        t.decidir(true);
        t.continuar();
        t.decidir(true);
        guardar(app, carpeta, "4g-tienda-bloqueado.png");
        for (int k = 0; k < 4; k++) {
            t.continuar();
            t.decidir(true);
        }
        t.continuar();
        guardar(app, carpeta, "4h-tienda-final.png");
        app.irA(4);
        for (int e = 0; e < app.cierre().cantidadEscenas(); e++) {
            app.cierre().irAEscena(e);
            guardar(app, carpeta, "5" + (char) ('a' + e) + "-cierre.png");
        }
        System.out.println("Capturas guardadas en " + carpeta.getAbsolutePath());
        System.exit(0);
    }

    private static void guardar(Aplicacion app, File carpeta, String nombre) throws IOException {
        app.setSize(ANCHO, ALTO);
        acomodar(app);
        BufferedImage imagen = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        app.printAll(g);
        g.dispose();
        ImageIO.write(imagen, "png", new File(carpeta, nombre));
    }

    /** Sin ventana, Swing no acomoda solo los componentes: se hace a mano. */
    private static void acomodar(Component c) {
        if (c instanceof Container contenedor) {
            contenedor.doLayout();
            for (Component hijo : contenedor.getComponents()) {
                acomodar(hijo);
            }
        }
    }
}
