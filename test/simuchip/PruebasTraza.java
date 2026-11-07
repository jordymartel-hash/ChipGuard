package simuchip;

import static simuchip.Prueba.*;

import java.util.List;
import simuchip.funciones.Flecha;
import simuchip.funciones.PasoTraza;
import simuchip.funciones.Traza;

/** Pruebas de la ejecución paso a paso que se ve en pantalla. */
public final class PruebasTraza {

    static final List<Integer> A = List.of(1, 2, 3);
    static final List<Character> B = List.of('a', 'b', 'c');

    static Flecha<Integer, Character> f(int x, char y) {
        return new Flecha<>(x, y);
    }

    static PasoTraza ultimo(List<PasoTraza> pasos) {
        return pasos.get(pasos.size() - 1);
    }

    public static void main(String[] args) {
        List<Flecha<Integer, Character>> biy = List.of(f(1, 'a'), f(2, 'b'), f(3, 'c'));
        List<Flecha<Integer, Character>> comparte = List.of(f(1, 'a'), f(2, 'a'), f(3, 'b'));
        List<Flecha<Integer, Character>> dosDesde1 = List.of(f(1, 'a'), f(1, 'b'), f(2, 'c'));

        titulo("esFuncion paso a paso");
        List<PasoTraza> t = Traza.esFuncion(A, biy);
        igual(PasoTraza.Tipo.OK, ultimo(t).tipo(), "termina en OK si es función");
        igual(4, ultimo(t).linea(), "termina en la línea 'return true'");
        igual(7, t.size(), "2 pasos por cada elemento (3) + el final = 7");
        igual(1, t.get(0).foco(), "el primer paso mira al «1»");
        t = Traza.esFuncion(A, dosDesde1);
        igual(PasoTraza.Tipo.FALLA, ultimo(t).tipo(), "termina en FALLA si el 1 tiene 2 flechas");
        igual(3, ultimo(t).linea(), "se detiene en 'return false'");
        igual(1, ultimo(t).foco(), "y señala al «1»");
        verificar(ultimo(t).variables().contains("salidas(a) = 2"), "muestra la variable salidas(a) = 2");

        titulo("esInyectiva paso a paso");
        t = Traza.esInyectiva(A, B, comparte);
        igual(PasoTraza.Tipo.FALLA, ultimo(t).tipo(), "a la 'a' le llegan 2 → FALLA");
        igual(4, ultimo(t).linea(), "se detiene en 'return false' del for");
        igual('a', ultimo(t).foco(), "y señala a la «a»");
        t = Traza.esInyectiva(A, B, List.of(f(1, 'a')));
        igual(1, ultimo(t).linea(), "si no es función se detiene en la primera comprobación");
        igual(PasoTraza.Tipo.OK, ultimo(Traza.esInyectiva(A, B, biy)).tipo(), "parejas → OK");

        titulo("esSobreyectiva paso a paso");
        t = Traza.esSobreyectiva(A, B, comparte);
        igual('c', ultimo(t).foco(), "la «c» se quedó sola");
        verificar(ultimo(t).variables().contains("llegadas(b) = 0"), "muestra llegadas(b) = 0");

        titulo("esBiyectiva paso a paso");
        t = Traza.esBiyectiva(A, B, comparte);
        igual(1, ultimo(t).linea(), "corto circuito: si falla la inyectiva, ni mira la sobreyectiva");
        igual(PasoTraza.Tipo.FALLA, ultimo(t).tipo(), "termina en FALLA");
        t = Traza.esBiyectiva(A, B, biy);
        igual(2, ultimo(t).linea(), "si todo va bien llega a la segunda condición");
        igual(PasoTraza.Tipo.OK, ultimo(t).tipo(), "y termina en OK");

        titulo("Cada paso tiene un mensaje para el público");
        for (PasoTraza p : Traza.esInyectiva(A, B, comparte)) {
            verificar(!p.mensaje().isBlank(), "paso en línea " + p.linea() + " tiene mensaje");
        }

        terminar();
    }
}
