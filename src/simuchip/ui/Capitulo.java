package simuchip.ui;

/**
 * Un capítulo de la presentación. Las flechas ← → primero recorren sus escenas (como las
 * animaciones de una keynote) y solo cuando se acaban pasan al capítulo vecino.
 */
public interface Capitulo {

    default void alMostrar() {}

    /** Avanza una escena; false si ya estaba en la última. */
    default boolean avanzar() {
        return false;
    }

    /** Retrocede una escena; false si ya estaba en la primera. */
    default boolean retroceder() {
        return false;
    }
}
