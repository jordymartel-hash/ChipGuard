package simuchip.caso;

/** Una línea móvil (un chip). "L3" es su nombre corto para el dibujo. */
public record Linea(int id, String numero) {

    public String nombreCorto() {
        return "L" + id;
    }

    @Override
    public String toString() {
        return nombreCorto();
    }
}
