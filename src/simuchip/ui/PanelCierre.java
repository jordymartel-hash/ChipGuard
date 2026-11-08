package simuchip.ui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/** Capítulo 9: conclusiones, la pregunta final y las fuentes. */
public final class PanelCierre extends PanelEscenas {

    public PanelCierre() {
        super(List.of(
            new Escena("Lo que nos llevamos.", "Cuatro ideas para recordar.", true),
            new Escena("¿Ya revisaste tus líneas?",
                "Saca tu celular ahora: busca «Checa tus líneas» de OSIPTEL y pon tu DNI. Si aparece un chip que no es "
                    + "tuyo, repórtalo ahí mismo. Hoy lo haces a mano; con ChipGuard te avisaría solo."),
            new Escena("Fuentes.", "Datos y normas citados en la presentación.", true)
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        switch (i) {
            case 0 -> ideas(g, a, t);
            case 1 -> pregunta(g, a, t);
            default -> fuentes(g, a, t);
        }
    }

    private void ideas(Graphics2D g, Rectangle a, double t) {
        String[][] ideas = {
            {"El registro es una función.", "Cada chip apunta a un dueño; el problema es cuando no es quien lo usa."},
            {"No toda función debe ser inyectiva.", "Tener dos chips no es delito; el problema es quién los usa."},
            {"Biyectiva = trazable.", "Chip ↔ persona real: la policía llega al culpable, no a Ana."},
            {"La última palabra, en tu celular.", "ChipGuard no cambia la cerradura: te avisa y te deja decidir."}
        };
        int alto = Math.min(110, a.height / 4);
        for (int k = 0; k < ideas.length; k++) {
            double p = tramo(t, 0.2 + k * 0.35, 0.5);
            int al = (int) (255 * p);
            int y = a.y + k * alto;
            if (k > 0) {
                g.setColor(Tema.FILETE);
                g.fillRect(a.x, y, a.width, 1);
            }
            Iconos.dibujar(g, Iconos.Tipo.VISTO, a.x + 16, y + alto / 2.0, 26, Tema.conAlfa(Tema.VERDE, al));
            g.setFont(Tema.titulo(30f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, ideas[k][0], a.x + 56, y + alto / 2.0 - 4);
            g.setFont(Tema.letra(Font.PLAIN, 18f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.texto(g, ideas[k][1], a.x + 56, y + alto / 2.0 + 26);
        }
    }

    private void pregunta(Graphics2D g, Rectangle a, double t) {
        double p = tramo(t, 0.2, 0.9);
        float tam = (float) (60 + 70 * p);
        g.setFont(Tema.titulo(tam));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, "f⁻¹(tú) = ?", a.getCenterX(), a.getCenterY() - 20);
        double q = tramo(t, 1.2, 0.5);
        g.setFont(Tema.letra(Font.PLAIN, 20f));
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * q)));
        Tema.textoCentrado(g, "las líneas que el registro manda hacia tu DNI", a.getCenterX(), a.getCenterY() + 70);
    }

    private void fuentes(Graphics2D g, Rectangle a, double t) {
        String[] lista = {
            "Congreso de la República (2025). OSIPTEL bloquea más de 650 mil celulares y alerta crecimiento del uso delictivo de líneas móviles.",
            "DPL News (2025). Osiptel dispuso la baja de más de 102 mil líneas con registros inconsistentes.",
            "Canal N (2025). Osiptel detecta 200 mil usuarios con más de 10 líneas móviles.",
            "LP Derecho / La República (2025). Resolución 000116-2025-CD/OSIPTEL: medidas de seguridad en la contratación móvil.",
            "OSIPTEL. Cómo contratar un servicio móvil de manera segura; norma contra contrataciones no solicitadas.",
            "Infobae (2024). Intervienen galería en el Centro de Lima donde se vendían chips suplantados.",
            "Infobae (2025). Venta ilegal de chips bajo la mira: Osiptel explica plan para rastrear cada línea móvil.",
            "El Imparcial (2026). Perú y la validación con huellas dactilares (moldes de silicona).",
            "Rosen, K. H. (2019). Discrete Mathematics and Its Applications (8.ª ed.). McGraw-Hill."
        };
        int y = a.y;
        g.setFont(Tema.letra(Font.PLAIN, 16f));
        for (int k = 0; k < lista.length; k++) {
            double p = tramo(t, 0.1 + k * 0.08, 0.4);
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
            y += Tema.parrafo(g, lista[k], a.x, y, a.width, 1.3f) + 8;
        }
    }
}
