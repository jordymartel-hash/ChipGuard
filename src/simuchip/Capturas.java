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
        guardar(app, carpeta, "01-problema.png");

        app.irA(1);
        for (int e = 0; e < app.casos().cantidadEscenas(); e++) {
            app.casos().irAEscena(e);
            guardar(app, carpeta, "02" + (char) ('a' + e) + "-casos.png");
        }
        app.irA(2);
        for (int e = 0; e < app.funciones().cantidadEscenas(); e++) {
            app.funciones().irAEscena(e);
            guardar(app, carpeta, "03" + (char) ('a' + e) + "-funciones.png");
        }

        app.irA(3);
        app.aprende().setTarjeta(1);
        app.aprende().cargar(4, 3, new int[][] {{1, 'a'}, {2, 'a'}, {3, 'b'}, {4, 'c'}});
        guardar(app, carpeta, "04a-pruebalo.png");
        app.aprende().ejecutarHasta(2);
        guardar(app, carpeta, "04b-pruebalo-paso-a-paso.png");

        app.irA(4);
        for (int e = 0; e < app.modelo().cantidadEscenas(); e++) {
            app.modelo().irAEscena(e);
            guardar(app, carpeta, "05" + (char) ('a' + e) + "-modelo.png");
        }

        app.irA(5);
        app.simula().simularDias(12);
        guardar(app, carpeta, "06a-simula-problema.png");
        app.simula().rastrearEjemplo();
        guardar(app, carpeta, "06b-simula-rastreo-falla.png");
        app.simula().activarTope(true);
        app.simula().activarBiometria(true);
        app.simula().simularDias(4);
        app.simula().auditar();
        app.simula().rastrearEjemplo();
        guardar(app, carpeta, "06c-simula-solucion.png");

        app.irA(6);
        app.chipGuard().irAEscena(1);
        guardar(app, carpeta, "07a-chipguard-sin-proteccion.png");
        app.chipGuard().irAEscena(4);
        guardar(app, carpeta, "07b-chipguard-alerta.png");
        app.chipGuard().irAEscena(6);
        guardar(app, carpeta, "07c-chipguard-mapa.png");

        app.irA(7);
        guardar(app, carpeta, "08a-juego-portada.png");
        app.detective().jugarDemo(4);
        guardar(app, carpeta, "08b-juego-jugando.png");

        app.irA(8);
        for (int e = 0; e < app.cierre().cantidadEscenas(); e++) {
            app.cierre().irAEscena(e);
            guardar(app, carpeta, "09" + (char) ('a' + e) + "-cierre.png");
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
