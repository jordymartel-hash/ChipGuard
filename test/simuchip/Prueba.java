package simuchip;

/** Mini corredor de pruebas sin librerías: cuenta aciertos y fallos. */
public final class Prueba {
    private static int ok = 0;
    private static int fallos = 0;

    private Prueba() {}

    public static void verificar(boolean condicion, String nombre) {
        if (condicion) {
            ok++;
            System.out.println("  ✔ " + nombre);
        } else {
            fallos++;
            System.out.println("  ✘ FALLA: " + nombre);
        }
    }

    public static void igual(Object esperado, Object real, String nombre) {
        boolean iguales = esperado == null ? real == null : esperado.equals(real);
        verificar(iguales, nombre + (iguales ? "" : "  (esperado: " + esperado + ", real: " + real + ")"));
    }

    public static void titulo(String texto) {
        System.out.println("\n▶ " + texto);
    }

    /** Imprime el resumen y termina con código 1 si algo falló. */
    public static void terminar() {
        System.out.println("\nResultado: " + ok + " pruebas OK, " + fallos + " fallos.");
        if (fallos > 0) {
            System.exit(1);
        }
    }
}
