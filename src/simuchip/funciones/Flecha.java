package simuchip.funciones;

/**
 * Una flecha del diagrama sagital: va desde un elemento de A hasta uno de B.
 * Ejemplo: new Flecha<>(1, 'a') es la flecha 1 → a.
 */
public record Flecha<A, B>(A desde, B hasta) {

    @Override
    public String toString() {
        return desde + " → " + hasta;
    }
}
