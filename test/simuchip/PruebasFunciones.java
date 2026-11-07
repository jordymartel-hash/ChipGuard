package simuchip;

import static simuchip.Prueba.*;

import java.util.List;
import java.util.Set;
import simuchip.funciones.Flecha;
import simuchip.funciones.Funciones;

/** Pruebas de la clase Funciones con los mismos dibujos de la PPT. */
public final class PruebasFunciones {

    static final Set<Integer> A3 = Set.of(1, 2, 3);
    static final Set<Integer> A4 = Set.of(1, 2, 3, 4);
    static final Set<Character> B3 = Set.of('a', 'b', 'c');
    static final Set<Character> B4 = Set.of('a', 'b', 'c', 'd');

    static Flecha<Integer, Character> f(int x, char y) {
        return new Flecha<>(x, y);
    }

    public static void main(String[] args) {
        titulo("Contar flechas");
        List<Flecha<Integer, Character>> dibujo = List.of(f(1, 'a'), f(2, 'a'), f(3, 'b'));
        igual(1, Funciones.salidas(1, dibujo), "del 1 sale una flecha");
        igual(2, Funciones.llegadas('a', dibujo), "a la 'a' le llegan dos flechas");
        igual(0, Funciones.llegadas('c', dibujo), "a la 'c' no le llega ninguna");

        titulo("¿Es función?");
        verificar(Funciones.esFuncion(A3, dibujo), "cada elemento de A tiene una flecha → es función");
        verificar(!Funciones.esFuncion(A3, List.of(f(1, 'a'), f(1, 'b'), f(2, 'c'))),
                "el 1 tiene dos flechas y el 3 ninguna → NO es función");
        verificar(!Funciones.esFuncion(A3, List.of(f(1, 'a'), f(2, 'b'))), "al 3 le falta su flecha → NO es función");
        verificar(Funciones.esFuncion(Set.of(), List.of()), "dominio vacío → función vacía (caso borde)");

        titulo("Inyectiva (nadie comparte)");
        List<Flecha<Integer, Character>> iny = List.of(f(1, 'a'), f(2, 'b'), f(3, 'c'));
        verificar(Funciones.esInyectiva(A3, B4, iny), "1→a, 2→b, 3→c con 'd' sola → inyectiva");
        verificar(!Funciones.esInyectiva(A3, B3, dibujo), "a la 'a' le llegan dos → NO inyectiva");
        verificar(!Funciones.esInyectiva(A3, B3, List.of(f(1, 'a'))), "si no es función, tampoco es inyectiva");

        titulo("Sobreyectiva (nadie se queda solo)");
        List<Flecha<Integer, Character>> sob = List.of(f(1, 'a'), f(2, 'a'), f(3, 'b'), f(4, 'c'));
        verificar(Funciones.esSobreyectiva(A4, B3, sob), "a, b y c reciben flecha → sobreyectiva");
        verificar(!Funciones.esInyectiva(A4, B3, sob), "pero como la 'a' recibe dos, NO es inyectiva");
        verificar(!Funciones.esSobreyectiva(A3, B4, iny), "la 'd' se queda sola → NO sobreyectiva");

        titulo("Biyectiva (parejas perfectas)");
        verificar(Funciones.esBiyectiva(A3, B3, iny), "1→a, 2→b, 3→c → biyectiva");
        verificar(!Funciones.esBiyectiva(A3, B4, iny), "sobra la 'd' → NO biyectiva");
        verificar(!Funciones.esBiyectiva(A4, B3, sob), "comparten la 'a' → NO biyectiva");

        titulo("Preimagen e inversa");
        igual(List.of(1, 2), Funciones.preimagen('a', dibujo), "f⁻¹(a) = {1, 2}");
        igual('b', Funciones.imagen(3, dibujo), "f(3) = b");
        igual(null, Funciones.imagen(9, dibujo), "f(9) no existe → null");
        List<Flecha<Character, Integer>> inv = Funciones.inversa(iny);
        igual(List.of(new Flecha<>('a', 1), new Flecha<>('b', 2), new Flecha<>('c', 3)), inv,
                "la inversa da vuelta cada flecha");
        verificar(Funciones.esBiyectiva(B3, A3, inv), "la inversa de una biyectiva también es biyectiva");

        titulo("Nombre del tipo");
        igual("Biyectiva", Funciones.tipo(A3, B3, iny), "tipo biyectiva");
        igual("Inyectiva", Funciones.tipo(A3, B4, iny), "tipo inyectiva");
        igual("Sobreyectiva", Funciones.tipo(A4, B3, sob), "tipo sobreyectiva");
        igual("Función (ninguna de las tres)", Funciones.tipo(A3, B3, dibujo), "tipo ninguna");
        igual("No es función", Funciones.tipo(A3, B3, List.of(f(1, 'a'))), "tipo no es función");

        titulo("Casos 'parece pero no es'");
        verificar(!Funciones.esFuncion(A3, List.of(f(1, 'a'), f(2, 'b'), f(3, 'c'), f(3, 'c'))),
                "flecha repetida 3→c dos veces cuenta como dos salidas → NO es función");
        verificar(!Funciones.esSobreyectiva(A3, B3, List.of(f(1, 'a'), f(2, 'b'), f(7, 'c'))),
                "a la 'c' solo le llega una flecha desde un 7 que no está en A → NO cuenta, 'c' sigue sola");
        verificar(!Funciones.esInyectiva(A3, B3, List.of(f(1, 'a'), f(2, 'b'), f(3, 'b'))),
                "3 flechas para 3 elementos parece biyectiva, pero 'b' recibe dos → NO inyectiva");
        verificar(!Funciones.esFuncion(A3, List.of(f(1, 'a'), f(2, 'b'), f(7, 'c'))),
                "flecha que sale de un 7 que no está en A no cuenta para el 3 → NO es función");

        terminar();
    }
}
