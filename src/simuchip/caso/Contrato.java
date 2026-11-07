package simuchip.caso;

/** Contrato validado con biometría: guarda la huella de quien de verdad usa la línea. */
public record Contrato(int id, Persona huella) {

    @Override
    public String toString() {
        return "C" + id;
    }
}
