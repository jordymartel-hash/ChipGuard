package simuchip.funciones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import simuchip.funciones.PasoTraza.Tipo;

/**
 * Ejecuta los métodos de Funciones "en cámara lenta": devuelve cada paso que da el código,
 * con el renglón, el elemento revisado, las variables y una explicación.
 * Los números de renglón coinciden con el código que se muestra en la pestaña Aprende.
 */
public final class Traza {

    private Traza() {}

    /** Renglones: 1 for (a : A) · 2 if (salidas(a) != 1) · 3 return false · 4 return true */
    public static <A, B> List<PasoTraza> esFuncion(List<A> dominio, List<Flecha<A, B>> flechas) {
        List<PasoTraza> pasos = new ArrayList<>();
        for (A a : dominio) {
            pasos.add(new PasoTraza(1, a, "a = " + a, "Tomo el «" + a + "» de A", Tipo.NORMAL));
            int salen = Funciones.salidas(a, flechas);
            String vars = "a = " + a + "   ·   salidas(a) = " + salen;
            if (salen != 1) {
                pasos.add(new PasoTraza(2, a, vars, "Del «" + a + "» salen " + salen + " flechas, no 1", Tipo.NORMAL));
                pasos.add(new PasoTraza(3, a, vars, "return false → NO es función", Tipo.FALLA));
                return pasos;
            }
            pasos.add(new PasoTraza(2, a, vars, "Del «" + a + "» sale 1 flecha: todo bien, sigo", Tipo.NORMAL));
        }
        pasos.add(new PasoTraza(4, null, "", "Todos tienen exactamente 1 flecha → ES función", Tipo.OK));
        return pasos;
    }

    /** Renglones: 1 if (!esFuncion) · 2 for (b : B) · 3 if (llegadas(b) > 1) · 4 return false · 5 return true */
    public static <A, B> List<PasoTraza> esInyectiva(List<A> dominio, List<B> codominio, List<Flecha<A, B>> flechas) {
        return recorrerB(dominio, codominio, flechas, true);
    }

    /** Mismos renglones que esInyectiva, pero la condición es llegadas(b) == 0. */
    public static <A, B> List<PasoTraza> esSobreyectiva(List<A> dominio, List<B> codominio,
            List<Flecha<A, B>> flechas) {
        return recorrerB(dominio, codominio, flechas, false);
    }

    /** Renglones: 1 return esInyectiva(...) · 2 && esSobreyectiva(...) */
    public static <A, B> List<PasoTraza> esBiyectiva(List<A> dominio, List<B> codominio, List<Flecha<A, B>> flechas) {
        List<PasoTraza> pasos = new ArrayList<>();
        Set<A> dom = new HashSet<>(dominio);
        Set<B> cod = new HashSet<>(codominio);
        boolean iny = Funciones.esInyectiva(dom, cod, flechas);
        if (!iny) {
            pasos.add(new PasoTraza(1, null, "esInyectiva = false",
                    "No es inyectiva → false. Java ni mira lo de la derecha (cortocircuito)", Tipo.FALLA));
            return pasos;
        }
        pasos.add(new PasoTraza(1, null, "esInyectiva = true", "Es inyectiva, ahora reviso la otra mitad", Tipo.NORMAL));
        boolean sob = Funciones.esSobreyectiva(dom, cod, flechas);
        pasos.add(new PasoTraza(2, null, "esInyectiva = true   ·   esSobreyectiva = " + sob,
                sob ? "Las dos son true → ES BIYECTIVA (tiene inversa)" : "No es sobreyectiva → NO biyectiva",
                sob ? Tipo.OK : Tipo.FALLA));
        return pasos;
    }

    private static <A, B> List<PasoTraza> recorrerB(List<A> dominio, List<B> codominio, List<Flecha<A, B>> flechas,
            boolean inyectiva) {
        List<PasoTraza> pasos = new ArrayList<>();
        Set<A> dom = new HashSet<>(dominio);
        if (!Funciones.esFuncion(dom, flechas)) {
            pasos.add(new PasoTraza(1, null, "esFuncion = false", "Ni siquiera es función → return false", Tipo.FALLA));
            return pasos;
        }
        pasos.add(new PasoTraza(1, null, "esFuncion = true", "Primero: ¿es función? Sí, sigo", Tipo.NORMAL));
        for (B b : codominio) {
            pasos.add(new PasoTraza(2, b, "b = " + b, "Tomo la «" + b + "» de B", Tipo.NORMAL));
            int llegan = Funciones.llegadas(b, dom, flechas);
            String vars = "b = " + b + "   ·   llegadas(b) = " + llegan;
            boolean falla = inyectiva ? llegan > 1 : llegan == 0;
            if (falla) {
                pasos.add(new PasoTraza(3, b, vars, "A la «" + b + "» le llegan " + llegan
                        + (inyectiva ? ": la comparten" : ": se quedó sola"), Tipo.NORMAL));
                pasos.add(new PasoTraza(4, b, vars, "return false → NO es " + (inyectiva ? "inyectiva" : "sobreyectiva"),
                        Tipo.FALLA));
                return pasos;
            }
            pasos.add(new PasoTraza(3, b, vars, "A la «" + b + "» le llegan " + llegan + ": está bien, sigo",
                    Tipo.NORMAL));
        }
        pasos.add(new PasoTraza(5, null, "", inyectiva ? "Nadie de B recibe 2 → ES inyectiva"
                : "Nadie de B quedó solo → ES sobreyectiva", Tipo.OK));
        return pasos;
    }
}
