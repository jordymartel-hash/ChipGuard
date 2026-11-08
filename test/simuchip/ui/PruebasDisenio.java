package simuchip.ui;

import static simuchip.Prueba.*;

import java.awt.Rectangle;

/** La tienda de la historia no debe salirse por arriba del área de la figura (taparía el texto). */
public final class PruebasDisenio {

    public static void main(String[] args) {
        titulo("Tienda dentro del área");
        int[][] areas = {{1440, 330}, {1720, 300}, {2400, 380}, {900, 500}};
        for (int[] ar : areas) {
            Rectangle a = new Rectangle(80, 300, ar[0], ar[1]);
            double wt = PanelHistoria.anchoTienda(a, Math.min(420, a.width * 0.36));
            double piso = a.y + a.height - 24;
            double tope = piso - wt * 0.42 * 2.6 - 35; // toldo + letrero, como en Personitas.tienda
            verificar(tope >= a.y, "área " + ar[0] + "×" + ar[1] + ": la tienda empieza en y=" + (int) tope
                    + " ≥ " + a.y);
        }
        igual(420.0, PanelHistoria.anchoTienda(new Rectangle(0, 0, 1600, 700), 420), "con alto de sobra no se achica");
        terminar();
    }
}
