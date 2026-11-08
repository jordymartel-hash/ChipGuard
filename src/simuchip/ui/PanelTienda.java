package simuchip.ui;

import static simuchip.ui.Personitas.VENDEDOR;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.Timer;
import simuchip.caso.Tienda;
import simuchip.caso.Tienda.Cliente;
import simuchip.caso.Tienda.Evento;
import simuchip.ui.Personitas.Accesorio;
import simuchip.ui.Personitas.Cara;
import simuchip.ui.Personitas.Look;

/**
 * Capítulo 4, el juego: el público atiende la tienda. Compara la cara del cliente con la foto del DNI y decide.
 * Ronda 1 sin ChipGuard, ronda 2 con ChipGuard, con la misma fila. Al final, el marcador de las dos rondas.
 */
public final class PanelTienda extends JComponent implements Capitulo {

    private enum Fase { PORTADA, ATENDIENDO, RESULTADO, RESUMEN, FINAL }

    private static final Map<String, Look> LOOKS = Map.of(
        "Luis", PanelMatematica.LUIS,
        "Rosa", PanelMatematica.ROSA,
        "Pedro", PanelMatematica.PEDRO,
        "Encapuchado", new Look("Encapuchado", new Color(0x3A3A44), Personitas.PIEL_3, Personitas.PELO_NEGRO,
            Accesorio.CAPUCHA),
        "Carmen", new Look("Carmen", new Color(0xF28C28), Personitas.PIEL_1, Personitas.PELO_CASTANO, Accesorio.MONO),
        "Juana", new Look("Juana", new Color(0x1FA2A0), Personitas.PIEL_2, Personitas.PELO_CASTANO, Accesorio.MONO),
        "Impostora", new Look("Impostora", new Color(0x1FA2A0), Personitas.PIEL_3, Personitas.PELO_NEGRO,
            Accesorio.CORTO),
        "Jorge", new Look("Jorge", new Color(0x8D6E63), Personitas.PIEL_3, Personitas.PELO_NEGRO, Accesorio.CORTO)
    );

    private Tienda tienda;
    private int criminalesRonda1;
    private int bloqueadosRonda2;
    private Fase fase = Fase.PORTADA;
    private List<Evento> eventos = List.of();
    private Cliente atendido;
    private long inicio = System.currentTimeMillis();
    private final Boton vender = new Boton("Vender", Boton.Estilo.EXITO).conIcono(Iconos.Tipo.VISTO);
    private final Boton noVender = new Boton("No vender", Boton.Estilo.PELIGRO).conIcono(Iconos.Tipo.EQUIS);
    private final Boton continuar = new Boton("Empezar", Boton.Estilo.PRIMARIO).conIcono(Iconos.Tipo.SIGUIENTE);
    private final Timer reloj = new Timer(16, e -> {
        if (isShowing()) {
            repaint();
        } else {
            ((Timer) e.getSource()).stop();
        }
    });

    public PanelTienda() {
        setLayout(null);
        for (Boton b : new Boton[] {vender, noVender, continuar}) {
            b.setFont(Tema.medio(18f));
            add(b);
        }
        vender.alHacerClic(() -> decidir(true));
        noVender.alHacerClic(() -> decidir(false));
        continuar.alHacerClic(this::continuar);
        empezarRonda(1);
    }

    // ─────────────────────────── control del juego ───────────────────────────

    public void empezarRonda(int ronda) {
        tienda = new Tienda(Tienda.filaDelDia(), ronda == 2);
        cambiar(Fase.PORTADA);
    }

    public void decidir(boolean venderle) {
        if (fase != Fase.ATENDIENDO) {
            return;
        }
        atendido = tienda.actual();
        eventos = tienda.decidir(venderle);
        cambiar(Fase.RESULTADO);
    }

    /** El botón azul (o la flecha →): avanza a lo que toque según la fase. */
    public void continuar() {
        switch (fase) {
            case PORTADA -> cambiar(Fase.ATENDIENDO);
            case RESULTADO -> {
                if (!tienda.terminado()) {
                    cambiar(Fase.ATENDIENDO);
                } else if (!tienda.chipGuard()) {
                    criminalesRonda1 = tienda.enManosCriminales();
                    cambiar(Fase.RESUMEN);
                } else {
                    bloqueadosRonda2 = tienda.bloqueados();
                    cambiar(Fase.FINAL);
                }
            }
            case RESUMEN -> empezarRonda(2);
            case FINAL -> empezarRonda(1);
            default -> { }
        }
    }

    private void cambiar(Fase nueva) {
        fase = nueva;
        inicio = System.currentTimeMillis();
        vender.setVisible(fase == Fase.ATENDIENDO);
        noVender.setVisible(fase == Fase.ATENDIENDO);
        continuar.setVisible(fase != Fase.ATENDIENDO);
        continuar.setTexto(switch (fase) {
            case PORTADA -> tienda.chipGuard() ? "Abrir la tienda con ChipGuard" : "Abrir la tienda";
            case RESULTADO -> tienda.terminado() ? "Ver cómo nos fue" : "Siguiente cliente";
            case RESUMEN -> "Ronda 2: con ChipGuard";
            default -> "Jugar otra vez";
        });
        if (!Animador.instantaneo) {
            reloj.restart();
        }
        doLayout();
        repaint();
    }

    @Override
    public void alMostrar() {
        if (fase == Fase.FINAL) {
            empezarRonda(1);
        }
        cambiar(fase);
    }

    @Override
    public boolean avanzar() {
        if (fase == Fase.FINAL) {
            return false;
        }
        if (fase != Fase.ATENDIENDO) {
            continuar();
        }
        return true;
    }

    private double segundos() {
        return Animador.instantaneo ? 99 : (System.currentTimeMillis() - inicio) / 1000.0;
    }

    private double tramo(double desde, double dura) {
        return Diagrama.tramo(segundos(), desde, dura);
    }

    // ─────────────────────────── disposición ───────────────────────────

    private int xPanel() {
        return (int) (getWidth() * 0.64);
    }

    @Override
    public void doLayout() {
        int x = xPanel();
        int y = getHeight() - 96;
        int wv = vender.getPreferredSize().width + 20;
        vender.setBounds(x, y, wv, 54);
        noVender.setBounds(x + wv + 14, y, noVender.getPreferredSize().width + 20, 54);
        Dimension d = continuar.getPreferredSize();
        if (fase == Fase.PORTADA || fase == Fase.RESUMEN || fase == Fase.FINAL) {
            continuar.setBounds((getWidth() - d.width - 20) / 2, getHeight() - 120, d.width + 20, 54);
        } else {
            continuar.setBounds(x, y, d.width + 20, 54);
        }
    }

    // ─────────────────────────── dibujo ───────────────────────────

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Tema.suavizar(g);
        int w = getWidth();
        int h = getHeight();
        g.setColor(Tema.FONDO_PAGINA);
        g.fillRect(0, 0, w, h);
        switch (fase) {
            case PORTADA -> portada(g, w, h);
            case RESUMEN -> resumen(g, w, h);
            case FINAL -> finalJuego(g, w, h);
            default -> juego(g, w, h);
        }
        g.dispose();
    }

    private void cabecera(Graphics2D g, int w) {
        g.setFont(Tema.titulo(40f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "Atiende la tienda.", 60, 66);
        boolean cg = tienda.chipGuard();
        Tema.pastilla(g, cg ? "Ronda 2 · con ChipGuard" : "Ronda 1 · sin ChipGuard", 60, 84,
                cg ? Tema.VERDE : Tema.ROJO, Tema.BLANCO, Tema.letra(Font.BOLD, 15f));
        String[][] marcador = {
            {String.valueOf(tienda.vendidos()), "chips vendidos"},
            {String.valueOf(tienda.enManosCriminales()), "en manos de extorsionadores"},
            {String.valueOf(tienda.bloqueados()), "bloqueados por su dueño"}
        };
        Color[] colores = {Tema.AZUL_NOCHE, Tema.ROJO, Tema.VERDE};
        int x = w - 60 - 3 * 190;
        for (int k = 0; k < 3; k++) {
            g.setFont(Tema.titulo(44f));
            g.setColor(colores[k]);
            Tema.texto(g, marcador[k][0], x + k * 190, 70);
            g.setFont(Tema.letra(Font.PLAIN, 14f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.parrafo(g, marcador[k][1], x + k * 190, 82, 170, 1.2f);
        }
    }

    private void juego(Graphics2D g, int w, int h) {
        cabecera(g, w);
        double piso = h - 40;
        double alto = Math.min(260, h * 0.32);
        double wt = Math.min(360, w * 0.24);
        double xt = w * 0.3;
        // fila de los que esperan
        int quedan = tienda.total() - tienda.turno();
        List<Cliente> fila = new ArrayList<>(Tienda.filaDelDia().subList(tienda.turno(), tienda.total()));
        int desde = fase == Fase.ATENDIENDO ? 1 : 0;
        int visibles = Math.min(fila.size(), desde + 3);
        double paso = Math.min(alto * 0.5, (xt - 200) / 3.0);
        for (int k = visibles - 1; k >= desde; k--) {
            double x = xt - 160 - (k - desde + 1) * paso + paso * 0.4;
            Personitas.persona(g, LOOKS.get(fila.get(k).nombre()), x, piso - 20, alto * 0.7, Cara.SERIA, 0.55);
        }
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "Cliente " + Math.min(tienda.total(), tienda.turno() + (fase == Fase.ATENDIENDO ? 1 : 0))
                + " de " + tienda.total() + (quedan > 1 ? "  ·  en la fila: " + (quedan - desde) : ""), 60, 136);

        // vendedor y mostrador
        Personitas.persona(g, VENDEDOR, xt + wt * 0.62, piso - alto * 0.22, alto, Cara.FELIZ, 1);
        Personitas.tienda(g, xt, piso, wt, "Chips al toque");

        Cliente c = fase == Fase.ATENDIENDO ? tienda.actual() : atendido;
        if (c == null) {
            return;
        }
        Look look = LOOKS.get(c.nombre());
        double entra = fase == Fase.ATENDIENDO ? tramo(0, 0.7) : 1;
        double sale = fase == Fase.RESULTADO ? tramo(2.6, 1.0) : 0;
        double xc = xt - 70 - (1 - entra) * 160 - sale * 600;
        boolean malo = eventos.contains(Evento.FRAUDE_PASO) || eventos.contains(Evento.FRAUDE_BLOQUEADO);
        Cara cara = fase == Fase.RESULTADO && (eventos.contains(Evento.RECHAZO_MAL)
                || eventos.contains(Evento.FRAUDE_BLOQUEADO)) ? Cara.TRISTE
                : c.esElDueno() ? Cara.FELIZ : (fase == Fase.RESULTADO && malo ? Cara.MALA : Cara.SERIA);
        Personitas.persona(g, look, xc, piso, alto, cara, 1 - sale);
        if (fase == Fase.ATENDIENDO) {
            String dice = switch (c.nombre()) {
                case "Encapuchado" -> "Es mi DNI, jefe. Rapidito.";
                case "Impostora" -> "Hola, soy Juana. Un chip, porfa.";
                default -> "Hola, un chip por favor. Aquí mi DNI.";
            };
            Personitas.burbuja(g, dice, xc, piso - alto - 8, 300, Tema.BLANCO, Tema.AZUL_NOCHE, tramo(0.6, 0.3));
        }

        // panel derecho: el DNI que entregó y la decisión o el resultado
        int xp = xPanel();
        int anchoP = w - xp - 60;
        double dw = Math.min(400, anchoP);
        double dh = dw * 0.63;
        g.setFont(Tema.letra(Font.BOLD, 16f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "El DNI que te entrega:", xp, 176);
        Personitas.dni(g, xp, 190, dw, dh, c.dniDe() + " " + apellido(c.dniDe()), 1, LOOKS.get(c.dniDe()));
        int y = (int) (190 + dh + 34);
        if (fase == Fase.ATENDIENDO) {
            g.setFont(Tema.titulo(26f));
            g.setColor(Tema.AZUL_NOCHE);
            y += Tema.parrafo(g, "¿Es la misma persona de la foto? ¿Le vendes?", xp, y, anchoP, 1.2f);
        } else {
            resultado(g, c, xp, y, anchoP, piso, xt, wt);
        }
    }

    private static String apellido(String nombre) {
        return switch (nombre) {
            case "Luis" -> "Ramos";
            case "Pedro" -> "Soto";
            case "Rosa" -> "Huamán";
            case "Carmen" -> "Díaz";
            case "Juana" -> "Paredes";
            default -> "Quispe";
        };
    }

    private void resultado(Graphics2D g, Cliente c, int x, int y, int ancho, double piso, double xt, double wt) {
        for (int k = 0; k < eventos.size(); k++) {
            Evento e = eventos.get(k);
            double p = tramo(0.2 + k * 1.1, 0.4);
            if (p <= 0) {
                continue;
            }
            boolean bueno = e == Evento.VENTA_OK || e == Evento.RECHAZO_BIEN || e == Evento.FRAUDE_BLOQUEADO
                    || e == Evento.EXTRA_BLOQUEADO;
            Color fondo = e == Evento.RECHAZO_MAL ? Tema.NARANJA_SUAVE : bueno ? Tema.VERDE_SUAVE : Tema.ROJO_SUAVE;
            Color letra = e == Evento.RECHAZO_MAL ? Tema.NARANJA_TEXTO : bueno ? Tema.VERDE : Tema.ROJO_OSCURO;
            String[] texto = mensaje(e, c);
            int alto = 104;
            Tema.rectRedondo(g, x, y, ancho, alto, 20, Tema.conAlfa(fondo, (int) (255 * p)));
            g.setFont(Tema.titulo(22f));
            g.setColor(Tema.conAlfa(letra, (int) (255 * p)));
            Tema.texto(g, texto[0], x + 20, y + 36);
            g.setFont(Tema.letra(Font.PLAIN, 16f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
            Tema.parrafo(g, texto[1], x + 20, y + 48, ancho - (e.name().endsWith("BLOQUEADO") ? 130 : 40), 1.25f);
            if (e == Evento.FRAUDE_BLOQUEADO || e == Evento.EXTRA_BLOQUEADO) {
                Personitas.celular(g, x + ancho - 56, y + alto / 2.0, 92, "No fui yo", Tema.ROJO, p);
            }
            // el chip viaja: al cliente, al extorsionador o queda bloqueado
            double viaje = tramo(0.3 + k * 1.1, 0.9);
            double x0 = xt + wt * 0.4;
            double y0 = piso - wt * 0.42 - 30;
            boolean aCriminal = e == Evento.FRAUDE_PASO || e == Evento.EXTRA_PASO;
            if (e == Evento.VENTA_OK || aCriminal || e.name().endsWith("BLOQUEADO")) {
                double x1 = e == Evento.EXTRA_PASO ? xt + wt + 120 : xt - 60;
                double y1 = e == Evento.EXTRA_PASO ? piso - 40 : y0 + 40;
                Personitas.chip(g, x0 + (x1 - x0) * viaje, y0 + (y1 - y0) * viaje, 38, 1 - tramo(2.6, 0.6),
                        aCriminal ? 1 : 0, e.name().endsWith("BLOQUEADO") ? "Bloqueado" : aCriminal ? "a criminales"
                                : "OK");
            }
        }
    }

    private static String[] mensaje(Evento e, Cliente c) {
        return switch (e) {
            case VENTA_OK -> new String[] {"Venta correcta.", c.nombre() + " es el dueño real del DNI."};
            case RECHAZO_BIEN -> new String[] {"¡Bien visto!", "No era " + c.dniDe() + ": la cara no coincide con la foto."};
            case RECHAZO_MAL -> new String[] {"Uy, era la dueña.", c.nombre() + " sí era la de la foto. Perdiste un cliente."};
            case FRAUDE_PASO -> new String[] {"Le vendiste a un impostor.",
                "El chip quedó a nombre de " + c.dniDe() + ", que ni se enteró."};
            case FRAUDE_BLOQUEADO -> new String[] {c.dniDe() + " lo bloqueó desde su celular.",
                "ChipGuard le avisó al dueño del DNI y dijo «No fui yo»."};
            case EXTRA_PASO -> new String[] {"El vendedor sacó un chip extra.",
                "Con la 2.ª huella de " + c.nombre() + ". Desde el mostrador no se ve."};
            case EXTRA_BLOQUEADO -> new String[] {"Chip extra bloqueado.",
                c.nombre() + " recibió: «¿2 chips con tu DNI?» y bloqueó el segundo."};
        };
    }

    private void portada(Graphics2D g, int w, int h) {
        boolean cg = tienda.chipGuard();
        double p = tramo(0, 0.5);
        g.setFont(Tema.titulo(64f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
        Tema.textoCentrado(g, cg ? "Ronda 2: con ChipGuard" : "Hoy tú atiendes la tienda.", w / 2.0, h * 0.2);
        g.setFont(Tema.letra(Font.PLAIN, 22f));
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * p)));
        String texto = cg
            ? "Los mismos 6 clientes. Ahora, cada vez que alguien saca un chip con un DNI, al dueño le llega una alerta."
            : "Llegan 6 clientes. Mira su cara, mira la foto del DNI y decide: ¿le vendes o no? "
                + "Tu meta: que ningún chip termine en manos de un extorsionador.";
        Tema.parrafo(g, texto, (int) (w / 2.0 - 420), (int) (h * 0.2 + 40), 840, 1.4f);
        double piso = h * 0.78;
        List<Cliente> fila = Tienda.filaDelDia();
        double paso = Math.min(150, (w - 200) / (double) fila.size());
        for (int k = 0; k < fila.size(); k++) {
            double x = w / 2.0 + (k - (fila.size() - 1) / 2.0) * paso;
            Personitas.persona(g, LOOKS.get(fila.get(k).nombre()), x, piso, 170, Cara.FELIZ, tramo(0.3 + k * 0.12, 0.3));
        }
    }

    private void resumen(Graphics2D g, int w, int h) {
        double p = tramo(0, 0.5);
        g.setFont(Tema.titulo(150f));
        g.setColor(Tema.ROJO);
        Tema.textoCentrado(g, String.valueOf(Math.round(criminalesRonda1 * p)), w / 2.0, h * 0.34);
        g.setFont(Tema.titulo(40f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, criminalesRonda1 == 1 ? "chip terminó en manos de extorsionadores."
                : "chips terminaron en manos de extorsionadores.", w / 2.0, h * 0.34 + 120);
        g.setFont(Tema.letra(Font.PLAIN, 21f));
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * tramo(0.6, 0.5))));
        Tema.parrafo(g, "Y eso que revisaste caras. Dos chips los sacó el vendedor a escondidas con la segunda huella: "
                + "desde el mostrador es imposible verlo. Los dueños de esos DNI no se enteraron de nada.",
                (int) (w / 2.0 - 430), (int) (h * 0.34 + 160), 860, 1.4f);
        g.setFont(Tema.titulo(30f));
        g.setColor(Tema.conAlfa(Tema.VERDE, (int) (255 * tramo(1.4, 0.5))));
        Tema.textoCentrado(g, "¿Y si el dueño del DNI se enterara al instante?", w / 2.0, h * 0.78);
    }

    private void finalJuego(Graphics2D g, int w, int h) {
        g.setFont(Tema.titulo(52f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, "Los mismos clientes. Otro final.", w / 2.0, h * 0.16);
        double[] cx = {w * 0.3, w * 0.7};
        String[] titulo = {"Sin ChipGuard", "Con ChipGuard"};
        int[] valor = {criminalesRonda1, tienda.enManosCriminales()};
        for (int k = 0; k < 2; k++) {
            double p = tramo(0.3 + k * 0.6, 0.6);
            Color col = valor[k] > 0 ? Tema.ROJO : Tema.VERDE;
            Tema.rectRedondo(g, cx[k] - 230, h * 0.24, 460, h * 0.44, 36, k == 0 ? Tema.ROJO_SUAVE : Tema.VERDE_SUAVE);
            g.setFont(Tema.titulo(30f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.textoCentrado(g, titulo[k], cx[k], h * 0.24 + 56);
            g.setFont(Tema.titulo(140f));
            g.setColor(Tema.conAlfa(col, (int) (255 * p)));
            Tema.textoCentrado(g, String.valueOf(valor[k]), cx[k], h * 0.24 + 160);
            g.setFont(Tema.letra(Font.PLAIN, 19f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.textoCentrado(g, "chips en manos de extorsionadores", cx[k], h * 0.24 + 256);
            if (k == 1) {
                g.setFont(Tema.letra(Font.BOLD, 19f));
                g.setColor(Tema.conAlfa(Tema.VERDE, (int) (255 * p)));
                Tema.textoCentrado(g, bloqueadosRonda2 + " bloqueados por sus dueños desde el celular", cx[k],
                        h * 0.24 + 296);
            }
        }
        g.setFont(Tema.titulo(26f));
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * tramo(1.6, 0.5))));
        Tema.textoCentrado(g, "No lo arregló el vendedor ni tú: lo arregló el dueño del DNI, desde su celular.",
                w / 2.0, h * 0.76);
    }
}
