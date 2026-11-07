package simuchip.caso;

/**
 * Resultado de rastrear una línea (lo que haría la policía).
 * Sin contrato, el sistema solo conoce f(línea) = titular.
 * Con contrato biométrico, g⁻¹ lleva a la huella de quien la usa.
 */
public record Rastreo(Linea linea, Persona titular, Persona usuarioReal, Contrato contrato) {

    /** A quién señala el sistema. */
    public Persona personaSenalada() {
        return contrato != null ? contrato.huella() : titular;
    }

    /** ¿El sistema señala a quien de verdad usa la línea? */
    public boolean culpableEncontrado() {
        return personaSenalada().equals(usuarioReal);
    }
}
