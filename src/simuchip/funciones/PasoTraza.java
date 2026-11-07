package simuchip.funciones;

/**
 * Un paso de la ejecución "en cámara lenta" de un método de Funciones.
 *
 * @param linea     renglón del código que se está ejecutando (empieza en 0)
 * @param foco      elemento que se está revisando (para iluminarlo en el dibujo), o null
 * @param variables valores de las variables en ese momento, como texto
 * @param mensaje   explicación en palabras para el público
 * @param tipo      NORMAL mientras avanza, OK o FALLA en el paso final
 */
public record PasoTraza(int linea, Object foco, String variables, String mensaje, Tipo tipo) {

    public enum Tipo { NORMAL, OK, FALLA }
}
