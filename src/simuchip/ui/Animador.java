package simuchip.ui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Lleva la cuenta de cuándo "nació" cada cosa dibujada (una flecha, un nodo)
 * para animarla creciendo. Repinta el componente a ~60 cuadros por segundo
 * solo mientras haya algo animándose.
 */
public final class Animador {

    /** En las capturas para el informe se dibuja todo terminado. */
    public static boolean instantaneo = false;

    private final Map<Object, Long> nacimientos = new HashMap<>();
    private final int duracionMs;
    private final Timer reloj;
    private long ultimoNacimiento;

    public Animador(JComponent componente, int duracionMs) {
        this.duracionMs = duracionMs;
        this.reloj = new Timer(16, e -> {
            componente.repaint();
            if (System.currentTimeMillis() - ultimoNacimiento > duracionMs + 50) {
                ((Timer) e.getSource()).stop();
            }
        });
    }

    /** Marca que "algo" acaba de aparecer (si ya existía, no lo reinicia). */
    public void nacer(Object algo) {
        if (!nacimientos.containsKey(algo)) {
            long ahora = System.currentTimeMillis();
            nacimientos.put(algo, ahora);
            ultimoNacimiento = ahora;
            if (!instantaneo) {
                reloj.start();
            }
        }
    }

    public void olvidar(Object algo) {
        nacimientos.remove(algo);
    }

    public void olvidarTodo() {
        nacimientos.clear();
    }

    /** Avance de 0 a 1 con frenado suave al final. */
    public double avance(Object algo) {
        if (instantaneo) {
            return 1;
        }
        Long inicio = nacimientos.get(algo);
        if (inicio == null) {
            return 1;
        }
        double t = Math.min(1.0, (System.currentTimeMillis() - inicio) / (double) duracionMs);
        return 1 - Math.pow(1 - t, 3);
    }

    /** Un pulso que va de 0 a 1 y vuelve (para resaltar algo nuevo). */
    public double pulso(Object algo) {
        double a = avance(algo);
        return a >= 1 ? 0 : Math.sin(a * Math.PI);
    }
}
