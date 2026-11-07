package simuchip.caso;

/**
 * Una solicitud de chip que llega a la tienda (para el juego Detective).
 * Se debe RECHAZAR si: la huella no coincide, el DNI es fotocopia o ya tiene 3 líneas (tope).
 */
public record Solicitud(int numero, String nombre, String dni, String tienda, String hora, int lineasActuales,
        boolean huellaCoincide, boolean dniOriginal) {

    public static final int TOPE = 3;

    public boolean debeRechazarse() {
        return !huellaCoincide || !dniOriginal || lineasActuales >= TOPE;
    }

    public String motivo() {
        if (!huellaCoincide) {
            return "La huella no coincide con el DNI";
        }
        if (!dniOriginal) {
            return "Presentó una fotocopia del DNI";
        }
        if (lineasActuales >= TOPE) {
            return "Ya tiene " + lineasActuales + " líneas: llegó al tope de " + TOPE;
        }
        return "Todo en orden: es la dueña o el dueño real";
    }
}
