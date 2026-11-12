import java.util.*;
import simuchip.caso.Tienda;
import simuchip.funciones.*;

/** Datos reales para el informe: traza, tiempos y escenarios de la tienda. Lo llama generar-docx.js. */
public class Experimento {
    public static void main(String[] args) {
        // Traza de esInyectiva: sobreventa de un bus (Rosa y Jorge comparten A2).
        List<String> a = List.of("Luis", "Rosa", "Jorge"), b = List.of("A1", "A2", "A3", "A4");
        List<Flecha<String, String>> f = List.of(new Flecha<>("Luis", "A1"), new Flecha<>("Rosa", "A2"), new Flecha<>("Jorge", "A2"));
        for (PasoTraza p : Traza.esInyectiva(a, b, f)) {
            System.out.println("TRAZA|" + p.linea() + "|" + p.variables() + "|" + p.mensaje());
        }

        // Tiempo de esBiyectiva sobre la identidad de tamaño n (mejor de 5, con calentamiento).
        medir(4000);
        for (int n : new int[] {1000, 2000, 4000, 8000, 16000}) {
            System.out.printf(Locale.ROOT, "TIEMPO|%d|%.1f%n", n, medir(n));
        }

        // Tres estrategias de quien atiende, sin y con ChipGuard.
        boolean[][] estrategias = {
            {true, false, true, true, false, true},   // perfecta: rechaza solo a los impostores
            {true, true, true, false, false, true},   // la de la prueba: un impostor pasa y una dueña se rechaza
            {true, true, true, true, true, true},     // vender a todos
        };
        String[] nombres = {"Decidir todo bien", "Con dos errores", "Vender a todos"};
        for (int s = 0; s < estrategias.length; s++) {
            for (boolean cg : new boolean[] {false, true}) {
                Tienda t = new Tienda(Tienda.filaDelDia(), cg);
                for (boolean v : estrategias[s]) {
                    t.decidir(v);
                }
                System.out.printf("ESC|%s|%s|%d|%d|%d|%d%n", nombres[s], cg ? "Sí" : "No",
                    t.enManosCriminales(), t.bloqueados(), t.vendidos(), t.perdidos());
            }
        }
    }

    private static double medir(int n) {
        Set<Integer> dom = new HashSet<>(), cod = new HashSet<>();
        List<Flecha<Integer, Integer>> fl = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            dom.add(i);
            cod.add(i);
            fl.add(new Flecha<>(i, i));
        }
        long mejor = Long.MAX_VALUE;
        for (int r = 0; r < 5; r++) {
            long t = System.nanoTime();
            if (!Funciones.esBiyectiva(dom, cod, fl)) throw new AssertionError("la identidad debe ser biyectiva");
            mejor = Math.min(mejor, System.nanoTime() - t);
        }
        return mejor / 1e6;
    }
}
