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
            new Escena("Referencias.", "En formato APA (7.ª ed.). Los enlaces llevan a la fuente original.", true)
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
            "América TV. (s. f.). Así operaba banda criminal que vendía chips activados a nombre de terceras personas. https://www.americatv.com.pe/noticias/amp/actualidad/asi-operaba-banda-criminal-que-vendia-chips-activados-nombres-terceras-personas-n492832",
            "Bloomberg Línea. (s. f.). ¿Pueden clonar su huella digital y robar su dinero? Así operaban «Los Huelleros del Norte» en Perú. https://www.bloomberglinea.com/latinoamerica/peru/pueden-clonar-su-huella-digital-y-robar-su-dinero-asi-operaban-los-huelleros-del-norte-en-peru/",
            "Canal N. (2025). Osiptel detecta 200 mil usuarios con más de 10 líneas móviles. https://canaln.pe/actualidad/osiptel-detecta-200-mil-usuarios-mas-10-lineas-moviles-n488632",
            "Congreso de la República del Perú. (2025). OSIPTEL bloquea más de 650 mil celulares y alerta crecimiento del uso delictivo de líneas móviles. https://comunicaciones.congreso.gob.pe/damos-cuenta/osiptel-bloquea-mas-de-650-mil-celulares-y-alerta-crecimiento-del-uso-delictivo-de-lineas-moviles/",
            "DPL News. (2025). Perú: Osiptel dispuso la baja de más de 102 mil líneas con registros inconsistentes durante el estado de emergencia. https://dplnews.com/peru-osiptel-dispuso-baja-102-mil-lineas-registros-inconsistentes-estado-de-emergencia/",
            "DPL News. (s. f.). Perú: Osiptel refuerza acciones contra la venta ambulatoria de chips con operativo preventivo en Lima Norte. https://dplnews.com/peru-osiptel-refuerza-acciones-contra-venta-ambulatoria-chips-operativo-preventivo-lima-norte/",
            "El Peruano. (2025). ¿Puedes bloquear nuevas líneas a tu nombre? Osiptel habilita restricción contra contrataciones no autorizadas. https://elperuano.pe/noticia/288845-puedes-bloquear-nuevas-lineas-a-tu-nombre-osiptel-habilita-restriccion-contra-contrataciones-no-autorizadas",
            "El Peruano. (s. f.). Osiptel refuerza tu seguridad: detecta y reporta líneas móviles no contratadas en Checa tus líneas. https://elperuano.pe/noticia/288143-osiptel-refuerza-tu-seguridad-detecta-y-reporta-lineas-moviles-no-contratadas-en-checa-tus-lineas",
            "Organismo Supervisor de Inversión Privada en Telecomunicaciones. (2025). Resolución de Consejo Directivo N.° 000116-2025-CD/OSIPTEL. https://www.osiptel.gob.pe/media/xpxl0map/resol116-2025-cd.pdf",
            "Rosen, K. H. (2019). Discrete mathematics and its applications (8.ª ed.). McGraw-Hill Education."
        };
        int y = a.y;
        g.setFont(Tema.letra(Font.PLAIN, 15f));
        for (int k = 0; k < lista.length; k++) {
            double p = tramo(t, 0.1 + k * 0.08, 0.4);
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
            y += Tema.parrafo(g, lista[k], a.x + 28, y, a.width - 28, 1.22f) + 6;
        }
    }
}
