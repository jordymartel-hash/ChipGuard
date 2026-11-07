package simuchip;

import static simuchip.Prueba.*;

import java.util.List;
import simuchip.funciones.Diagnostico;
import simuchip.funciones.Diagnostico.Veredicto;
import simuchip.funciones.Flecha;

/** Pruebas de las explicaciones en palabras que ve el público. */
public final class PruebasDiagnostico {

    static final List<Integer> A = List.of(1, 2, 3);
    static final List<Character> B = List.of('a', 'b', 'c');

    static Flecha<Integer, Character> f(int x, char y) {
        return new Flecha<>(x, y);
    }

    public static void main(String[] args) {
        titulo("Función");
        Veredicto v = Diagnostico.funcion(A, List.of(f(1, 'a'), f(1, 'b'), f(2, 'c')));
        verificar(!v.cumple(), "dos flechas desde 1 → no cumple");
        igual("Del «1» salen 2 flechas", v.razon(), "explica el primer problema");
        v = Diagnostico.funcion(A, List.of(f(1, 'a'), f(2, 'b')));
        igual("Del «3» no sale ninguna flecha", v.razon(), "explica al que le falta flecha");
        v = Diagnostico.funcion(A, List.of(f(1, 'a'), f(2, 'a'), f(3, 'b')));
        verificar(v.cumple(), "una flecha por elemento → cumple");

        titulo("Inyectiva");
        v = Diagnostico.inyectiva(A, B, List.of(f(1, 'a'), f(2, 'a'), f(3, 'b')));
        igual("A «a» le llegan 2 flechas (de 1 y 2)", v.razon(), "señala quién comparte");
        v = Diagnostico.inyectiva(A, B, List.of(f(1, 'a')));
        igual("Primero tiene que ser función", v.razon(), "si no es función lo dice");
        verificar(Diagnostico.inyectiva(A, B, List.of(f(1, 'a'), f(2, 'b'), f(3, 'c'))).cumple(), "parejas → cumple");

        titulo("Sobreyectiva");
        v = Diagnostico.sobreyectiva(A, B, List.of(f(1, 'a'), f(2, 'a'), f(3, 'b')));
        igual("«c» se quedó sin flecha", v.razon(), "señala al que quedó solo");

        titulo("Biyectiva");
        v = Diagnostico.biyectiva(A, B, List.of(f(1, 'a'), f(2, 'b'), f(3, 'c')));
        verificar(v.cumple(), "1→a, 2→b, 3→c → cumple");
        igual("Parejas perfectas: existe la inversa f⁻¹", v.razon(), "menciona la inversa");
        v = Diagnostico.biyectiva(A, B, List.of(f(1, 'a'), f(2, 'a'), f(3, 'b')));
        igual("No es inyectiva ni sobreyectiva", v.razon(), "cuando fallan las dos");
        v = Diagnostico.biyectiva(A, List.of('a', 'b', 'c', 'd'), List.of(f(1, 'a'), f(2, 'b'), f(3, 'c')));
        igual("Le falta ser sobreyectiva", v.razon(), "cuando solo falla sobreyectiva");

        terminar();
    }
}
