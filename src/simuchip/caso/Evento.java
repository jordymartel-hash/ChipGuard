package simuchip.caso;

/** Una línea de la bitácora de la simulación. */
public record Evento(int dia, Tipo tipo, String texto) {

    public enum Tipo {
        LEGAL,
        FRAUDE,
        BLOQUEO,
        TOPE,
        AUDITORIA,
        INFO
    }
}
