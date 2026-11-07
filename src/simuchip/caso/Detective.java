package simuchip.caso;

import java.util.Random;

/**
 * El juego Detective: llegan solicitudes de chips y el jugador decide aprobar o rechazar.
 * Acierto +10 (desde el 3.º acierto seguido, +15). Error: −1 vida. Con 0 vidas se acaba.
 */
public final class Detective {

    /** Resultado de una decisión del jugador. */
    public record Decision(boolean acierto, String explicacion, int puntos) {}

    private static final String[] NOMBRES = {"Ana", "Beto", "Carla", "Diego", "Elena", "Fabio", "Gina", "Hugo",
        "Iris", "Jorge", "Karen", "Luis"};
    private static final String[] TIENDAS = {"Tienda oficial · Jr. de la Unión", "Stand · Real Plaza",
        "Puesto ambulante · Av. Abancay", "Bodega · Comas", "Agente autorizado · Miraflores",
        "Kiosko · Mercado Central"};

    private final long semilla;
    private Random azar;
    private Solicitud actual;
    private int numero;
    private int puntaje;
    private int vidas;
    private int aciertos;
    private int errores;
    private int racha;
    private int fraudesDetenidos;
    private int fraudesColados;

    public Detective(long semilla) {
        this.semilla = semilla;
        reiniciar();
    }

    public void reiniciar() {
        azar = new Random(semilla);
        numero = 0;
        puntaje = 0;
        vidas = 3;
        aciertos = 0;
        errores = 0;
        racha = 0;
        fraudesDetenidos = 0;
        fraudesColados = 0;
        actual = generar();
    }

    /** Crea una solicitud nueva: más o menos la mitad deben rechazarse. */
    public Solicitud generar() {
        numero++;
        String nombre = NOMBRES[azar.nextInt(NOMBRES.length)];
        String dni = String.valueOf(40000000 + azar.nextInt(9999999));
        String tienda = TIENDAS[azar.nextInt(TIENDAS.length)];
        String hora = String.format("%d:%02d", 8 + azar.nextInt(14), azar.nextInt(60));
        boolean huella = true;
        boolean original = true;
        int lineas = azar.nextInt(3);
        if (azar.nextDouble() < 0.5) {
            switch (azar.nextInt(3)) {
                case 0 -> huella = false;
                case 1 -> original = false;
                default -> lineas = 3 + azar.nextInt(2);
            }
        }
        return new Solicitud(numero, nombre, dni, tienda, hora, lineas, huella, original);
    }

    /** El jugador decide: true = aprobar, false = rechazar. */
    public Decision decidir(boolean aprobar) {
        if (terminado()) {
            return new Decision(false, "El juego terminó", 0);
        }
        Solicitud s = actual;
        boolean correcto = aprobar != s.debeRechazarse();
        Decision decision;
        if (correcto) {
            racha++;
            aciertos++;
            int puntos = racha >= 3 ? 15 : 10;
            puntaje += puntos;
            if (s.debeRechazarse()) {
                fraudesDetenidos++;
            }
            decision = new Decision(true, (aprobar ? "Bien aprobada. " : "¡Bien detenido! ") + s.motivo(), puntos);
        } else {
            racha = 0;
            errores++;
            vidas--;
            if (aprobar) {
                fraudesColados++;
                decision = new Decision(false, "Se coló un chip irregular: " + s.motivo().toLowerCase(), 0);
            } else {
                decision = new Decision(false, "Rechazaste a alguien legítimo: " + s.motivo().toLowerCase(), 0);
            }
        }
        actual = generar();
        return decision;
    }

    public boolean terminado() {
        return vidas <= 0;
    }

    public Solicitud actual() {
        return actual;
    }

    public int puntaje() {
        return puntaje;
    }

    public int vidas() {
        return vidas;
    }

    public int aciertos() {
        return aciertos;
    }

    public int errores() {
        return errores;
    }

    public int racha() {
        return racha;
    }

    public int fraudesDetenidos() {
        return fraudesDetenidos;
    }

    public int fraudesColados() {
        return fraudesColados;
    }
}
