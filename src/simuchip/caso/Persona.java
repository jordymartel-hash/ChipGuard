package simuchip.caso;

/** Una persona con DNI. El extorsionador también es una persona del conjunto P. */
public record Persona(String dni, String nombre, boolean esExtorsionador) {

    @Override
    public String toString() {
        return nombre;
    }
}
