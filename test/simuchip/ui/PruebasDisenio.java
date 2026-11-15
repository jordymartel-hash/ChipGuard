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

        titulo("Texto sin cuadraditos en Windows");
        java.awt.Font geist = Tema.letra(java.awt.Font.BOLD, 40f);
        for (String txt : new String[] {"f⁻¹(tú) = ?", "f⁻¹(Ana): los chips con su DNI", "existe la inversa f⁻¹"}) {
            java.text.AttributedCharacterIterator it = Tema.preparar(txt, geist).getIterator();
            boolean todo = true;
            for (char c = it.first(); c != java.text.CharacterIterator.DONE; c = it.next()) {
                java.awt.Font f = (java.awt.Font) it.getAttribute(java.awt.font.TextAttribute.FONT);
                todo &= f != null && f.getFamily().equals(geist.getFamily()) && f.canDisplay(c);
            }
            verificar(todo, "«" + txt + "» se dibuja entero con Geist");
        }
        titulo("Capítulo de objetivo y marco teórico");
        Aplicacion app = new Aplicacion();
        igual(8, app.cantidadCapitulos(), "la app tiene 8 capítulos");
        igual("Objetivos", app.nombreCapitulo(1), "va justo después de La historia, como en el informe");
        igual("La matemática", app.nombreCapitulo(2), "la matemática sigue después");
        igual(4, app.objetivos().cantidadEscenas(), "cuatro escenas: objetivo, específicos y marco teórico en dos partes");
        igual(3, PanelObjetivos.GENERAL.length, "el objetivo general se parte en sus tres verbos");
        igual(4, PanelObjetivos.ESPECIFICOS.length, "los 4 objetivos específicos del informe");
        igual(5, PanelObjetivos.CONCEPTOS.length, "cinco conceptos del marco teórico");

        titulo("Letra para proyector");
        verificar(Tema.letra(java.awt.Font.PLAIN, 14f).getSize2D() >= 17f, "el texto de 14 px se proyecta a 17 px o más");
        verificar(Tema.letra(java.awt.Font.PLAIN, 19f).getSize2D() >= 22.5f, "la bajada de 19 px pasa a 22.5 px o más");
        igual(58f, Tema.titulo(58f).getSize2D(), "los titulares grandes no cambian");
        verificar(Tema.medio(30f).getSize2D() > 30f && Tema.medio(30f).getSize2D() < 34f, "lo mediano crece poco");

        titulo("Barra: los 8 capítulos no chocan con la marca ni con el cronómetro");
        java.awt.Container barra = (java.awt.Container) app.getComponent(0);
        for (int ancho : new int[] {1920, 1600, 1366, 1280, 1024}) {
            barra.setSize(ancho, 60);
            barra.doLayout();
            java.awt.Component[] hijos = barra.getComponents();
            java.awt.Component crono = hijos[hijos.length - 1];
            if (ancho >= 1280) {
                verificar(crono.isVisible(), ancho + " px: el cronómetro del expositor sigue visible");
            }
            if (!crono.isVisible()) {
                crono = hijos[hijos.length - 2]; // sin cronómetro, el límite es «Siguiente»
            }
            int izq = Integer.MAX_VALUE;
            int der = 0;
            for (int k = 0; k < app.cantidadCapitulos(); k++) {
                izq = Math.min(izq, hijos[k].getX());
                der = Math.max(der, hijos[k].getX() + hijos[k].getWidth());
            }
            verificar(izq >= 180 && der <= crono.getX() - 8,
                    ancho + " px: pestañas de " + izq + " a " + der + ", cronómetro en " + crono.getX());
        }
        for (String[] c : PanelObjetivos.CONCEPTOS) {
            java.text.AttributedCharacterIterator it = Tema.preparar(c[1], geist).getIterator();
            boolean todo = true;
            for (char ch = it.first(); ch != java.text.CharacterIterator.DONE; ch = it.next()) {
                java.awt.Font f = (java.awt.Font) it.getAttribute(java.awt.font.TextAttribute.FONT);
                todo &= f != null && f.getFamily().equals(geist.getFamily()) && f.canDisplay(ch);
            }
            verificar(todo, "fórmula «" + c[1] + "» sin cuadraditos");
        }
        titulo("El cronómetro no se pierde en ningún capítulo a 1280 px (el botón cambia de largo)");
        for (int i = 0; i < app.cantidadCapitulos(); i++) {
            app.irA(i);
            barra.setSize(1280, 60);
            barra.doLayout();
            java.awt.Component[] hs = barra.getComponents();
            java.awt.Component cr = hs[hs.length - 1];
            int der = 0;
            for (int k = 0; k < app.cantidadCapitulos(); k++) {
                der = Math.max(der, hs[k].getX() + hs[k].getWidth());
            }
            verificar(cr.isVisible() && der <= cr.getX() - 8, "en «" + app.nombreCapitulo(i) + "»: pestañas hasta " + der
                    + ", cronómetro en " + cr.getX() + (cr.isVisible() ? "" : " (oculto)"));
        }
        terminar();
    }
}
