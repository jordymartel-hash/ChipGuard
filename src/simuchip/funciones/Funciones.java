package simuchip.funciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * El corazón del proyecto: las definiciones de matemática discreta escritas en Java.
 *
 * Todo se reduce a CONTAR FLECHAS:
 *   - salidas(a):  cuántas flechas salen de un elemento de A.
 *   - llegadas(b): cuántas flechas llegan a un elemento de B.
 *
 * Con esas dos cuentas se responden las cuatro preguntas:
 *   Función      → de cada elemento de A sale exactamente 1 flecha.
 *   Inyectiva    → además, a ningún elemento de B le llegan 2 o más.
 *   Sobreyectiva → además, a ningún elemento de B le llegan 0.
 *   Biyectiva    → inyectiva y sobreyectiva a la vez.
 */
public final class Funciones {

    private Funciones() {}

    /** ¿Cuántas flechas salen de "a"? */
    public static <A, B> int salidas(A a, List<Flecha<A, B>> flechas) {
        int cuenta = 0;
        for (Flecha<A, B> flecha : flechas) {
            if (flecha.desde().equals(a)) {
                cuenta++;
            }
        }
        return cuenta;
    }

    /** ¿Cuántas flechas llegan a "b"? (solo cuentan las que salen de A) */
    public static <A, B> int llegadas(B b, Set<A> dominio, List<Flecha<A, B>> flechas) {
        int cuenta = 0;
        for (Flecha<A, B> flecha : flechas) {
            if (flecha.hasta().equals(b) && dominio.contains(flecha.desde())) {
                cuenta++;
            }
        }
        return cuenta;
    }

    /** Versión corta: cuenta todas las flechas que llegan a "b". */
    public static <A, B> int llegadas(B b, List<Flecha<A, B>> flechas) {
        int cuenta = 0;
        for (Flecha<A, B> flecha : flechas) {
            if (flecha.hasta().equals(b)) {
                cuenta++;
            }
        }
        return cuenta;
    }

    /** FUNCIÓN: de cada elemento de A sale exactamente UNA flecha. */
    public static <A, B> boolean esFuncion(Set<A> dominio, List<Flecha<A, B>> flechas) {
        for (A a : dominio) {
            if (salidas(a, flechas) != 1) {
                return false;          // 0 flechas o 2 flechas: no es función
            }
        }
        return true;
    }

    /** INYECTIVA: nadie de B recibe 2 o más flechas. */
    public static <A, B> boolean esInyectiva(Set<A> dominio, Set<B> codominio, List<Flecha<A, B>> flechas) {
        if (!esFuncion(dominio, flechas)) {
            return false;
        }
        for (B b : codominio) {
            if (llegadas(b, dominio, flechas) > 1) {
                return false;          // dos elementos de A comparten a "b"
            }
        }
        return true;
    }

    /** SOBREYECTIVA: nadie de B se queda sin flecha. */
    public static <A, B> boolean esSobreyectiva(Set<A> dominio, Set<B> codominio, List<Flecha<A, B>> flechas) {
        if (!esFuncion(dominio, flechas)) {
            return false;
        }
        for (B b : codominio) {
            if (llegadas(b, dominio, flechas) == 0) {
                return false;          // "b" se quedó solo
            }
        }
        return true;
    }

    /** BIYECTIVA: inyectiva y sobreyectiva → parejas perfectas. */
    public static <A, B> boolean esBiyectiva(Set<A> dominio, Set<B> codominio, List<Flecha<A, B>> flechas) {
        return esInyectiva(dominio, codominio, flechas) && esSobreyectiva(dominio, codominio, flechas);
    }

    /** f(a): a dónde apunta la flecha de "a" (null si no tiene). */
    public static <A, B> B imagen(A a, List<Flecha<A, B>> flechas) {
        for (Flecha<A, B> flecha : flechas) {
            if (flecha.desde().equals(a)) {
                return flecha.hasta();
            }
        }
        return null;
    }

    /** f⁻¹(b): todos los elementos de A cuya flecha llega a "b". */
    public static <A, B> List<A> preimagen(B b, List<Flecha<A, B>> flechas) {
        List<A> resultado = new ArrayList<>();
        for (Flecha<A, B> flecha : flechas) {
            if (flecha.hasta().equals(b)) {
                resultado.add(flecha.desde());
            }
        }
        return resultado;
    }

    /** La inversa: da vuelta cada flecha (solo tiene sentido si es biyectiva). */
    public static <A, B> List<Flecha<B, A>> inversa(List<Flecha<A, B>> flechas) {
        List<Flecha<B, A>> resultado = new ArrayList<>();
        for (Flecha<A, B> flecha : flechas) {
            resultado.add(new Flecha<>(flecha.hasta(), flecha.desde()));
        }
        return resultado;
    }

    /** Nombre del tipo, para mostrarlo en pantalla. */
    public static <A, B> String tipo(Set<A> dominio, Set<B> codominio, List<Flecha<A, B>> flechas) {
        if (!esFuncion(dominio, flechas)) {
            return "No es función";
        }
        boolean iny = esInyectiva(dominio, codominio, flechas);
        boolean sob = esSobreyectiva(dominio, codominio, flechas);
        if (iny && sob) {
            return "Biyectiva";
        }
        if (iny) {
            return "Inyectiva";
        }
        if (sob) {
            return "Sobreyectiva";
        }
        return "Función (ninguna de las tres)";
    }
}
