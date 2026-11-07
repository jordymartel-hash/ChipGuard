package simuchip.funciones;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Explica en palabras POR QUÉ una relación cumple o no cada propiedad.
 * Usa la clase Funciones para decidir y solo agrega la explicación.
 */
public final class Diagnostico {

    /** Respuesta para la pantalla: ¿cumple? y la razón en una frase. */
    public record Veredicto(boolean cumple, String razon) {}

    private Diagnostico() {}

    public static <A, B> Veredicto funcion(List<A> dominio, List<Flecha<A, B>> flechas) {
        for (A a : dominio) {
            int salen = Funciones.salidas(a, flechas);
            if (salen == 0) {
                return new Veredicto(false, "Del «" + a + "» no sale ninguna flecha");
            }
            if (salen > 1) {
                return new Veredicto(false, "Del «" + a + "» salen " + salen + " flechas");
            }
        }
        return new Veredicto(true, "De cada elemento de A sale exactamente una flecha");
    }

    public static <A, B> Veredicto inyectiva(List<A> dominio, List<B> codominio, List<Flecha<A, B>> flechas) {
        if (!Funciones.esFuncion(set(dominio), flechas)) {
            return new Veredicto(false, "Primero tiene que ser función");
        }
        for (B b : codominio) {
            List<A> origenes = Funciones.preimagen(b, flechas);
            if (origenes.size() > 1) {
                return new Veredicto(false, "A «" + b + "» le llegan " + origenes.size()
                        + " flechas (de " + unir(origenes) + ")");
            }
        }
        return new Veredicto(true, "Nadie en B recibe más de una flecha");
    }

    public static <A, B> Veredicto sobreyectiva(List<A> dominio, List<B> codominio, List<Flecha<A, B>> flechas) {
        if (!Funciones.esFuncion(set(dominio), flechas)) {
            return new Veredicto(false, "Primero tiene que ser función");
        }
        for (B b : codominio) {
            if (Funciones.llegadas(b, set(dominio), flechas) == 0) {
                return new Veredicto(false, "«" + b + "» se quedó sin flecha");
            }
        }
        return new Veredicto(true, "Todos en B reciben al menos una flecha");
    }

    public static <A, B> Veredicto biyectiva(List<A> dominio, List<B> codominio, List<Flecha<A, B>> flechas) {
        if (!Funciones.esFuncion(set(dominio), flechas)) {
            return new Veredicto(false, "Primero tiene que ser función");
        }
        boolean iny = Funciones.esInyectiva(set(dominio), set(codominio), flechas);
        boolean sob = Funciones.esSobreyectiva(set(dominio), set(codominio), flechas);
        if (iny && sob) {
            return new Veredicto(true, "Parejas perfectas: existe la inversa f⁻¹");
        }
        if (!iny && !sob) {
            return new Veredicto(false, "No es inyectiva ni sobreyectiva");
        }
        return new Veredicto(false, iny ? "Le falta ser sobreyectiva" : "Le falta ser inyectiva");
    }

    private static <T> Set<T> set(List<T> lista) {
        return new HashSet<>(lista);
    }

    private static <T> String unir(List<T> elementos) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < elementos.size(); i++) {
            if (i > 0) {
                texto.append(i == elementos.size() - 1 ? " y " : ", ");
            }
            texto.append(elementos.get(i));
        }
        return texto.toString();
    }
}
