package simuchip.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import simuchip.caso.Contrato;
import simuchip.caso.Evento;
import simuchip.caso.Linea;
import simuchip.caso.Persona;
import simuchip.caso.Rastreo;
import simuchip.caso.Resultado;
import simuchip.caso.Simulador;
import simuchip.funciones.Diagnostico;
import simuchip.funciones.Diagnostico.Veredicto;
import simuchip.funciones.Flecha;

/**
 * Pestaña 2: el caso real. Se simulan contrataciones de líneas (algunas con DNI robado)
 * y se ve cómo las funciones f (registro), u (uso real) y g (contrato) cambian.
 */
public final class PanelSimula extends JPanel {

    /** Semilla elegida para que la demo sea realista: 12 días → 3 fraudes y alguien sobre el tope. */
    private final Simulador sim = new Simulador(19);
    private final Lienzo lienzo = new Lienzo();
    private final Lateral lateral = new Lateral();
    private final Animador animador;
    private final Timer auto;
    private Linea seleccionada;

    private Boton botonAuto;
    private Boton botonBiometria;
    private Boton botonTope;

    public PanelSimula() {
        super(new BorderLayout());
        setBackground(Tema.CELESTE_FONDO);
        animador = new Animador(lienzo, 650);
        auto = new Timer(900, e -> {
            if (sim.paso() == Resultado.LLENO) {
                detenerAuto();
            }
            refrescar();
        });
        add(crearBarra(), BorderLayout.NORTH);
        add(lienzo, BorderLayout.CENTER);
        add(lateral, BorderLayout.EAST);
    }

    private JComponent crearBarra() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(4, 14, 0, 14));
        barra.add(new Boton("▶  Simular 1 día", Boton.Estilo.PRIMARIO).alHacerClic(() -> {
            sim.paso();
            refrescar();
        }));
        botonAuto = new Boton("Automático", Boton.Estilo.SECUNDARIO, true);
        botonAuto.alHacerClic(() -> {
            if (botonAuto.encendido()) {
                auto.start();
            } else {
                auto.stop();
            }
        });
        barra.add(botonAuto);
        barra.add(espacio(18));
        botonBiometria = new Boton("Validación biométrica", Boton.Estilo.SECUNDARIO, true);
        botonBiometria.alHacerClic(() -> {
            sim.setBiometria(botonBiometria.encendido());
            refrescar();
        });
        barra.add(botonBiometria);
        botonTope = new Boton("Tope k = 3", Boton.Estilo.SECUNDARIO, true);
        botonTope.alHacerClic(() -> {
            sim.setTopeActivo(botonTope.encendido());
            refrescar();
        });
        barra.add(botonTope);
        barra.add(new Boton("Auditoría biométrica", Boton.Estilo.SECUNDARIO).alHacerClic(() -> {
            sim.auditoria();
            refrescar();
        }));
        barra.add(espacio(18));
        barra.add(new Boton("Reiniciar", Boton.Estilo.PELIGRO).alHacerClic(this::reiniciar));
        return barra;
    }

    private static JComponent espacio(int ancho) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(ancho, 10));
        return p;
    }

    // ─────────────────────────── acciones (también para las capturas) ───────────────────────────

    public void simularDias(int dias) {
        for (int i = 0; i < dias; i++) {
            sim.paso();
        }
        refrescar();
    }

    public void activarBiometria(boolean valor) {
        sim.setBiometria(valor);
        botonBiometria.setEncendido(valor);
        refrescar();
    }

    public void activarTope(boolean valor) {
        sim.setTopeActivo(valor);
        botonTope.setEncendido(valor);
        refrescar();
    }

    public void auditar() {
        sim.auditoria();
        refrescar();
    }

    /** Selecciona la primera línea fraudulenta (o la primera línea) para mostrar el rastreo. */
    public void rastrearEjemplo() {
        List<Linea> malas = sim.inconsistentes();
        seleccionada = !malas.isEmpty() ? malas.get(0) : sim.lineas().isEmpty() ? null : sim.lineas().get(0);
        refrescar();
    }

    public void reiniciar() {
        detenerAuto();
        sim.reiniciar();
        botonBiometria.setEncendido(false);
        botonTope.setEncendido(false);
        seleccionada = null;
        animador.olvidarTodo();
        refrescar();
    }

    private void detenerAuto() {
        auto.stop();
        if (botonAuto != null) {
            botonAuto.setEncendido(false);
        }
    }

    private void refrescar() {
        for (Linea linea : sim.lineas()) {
            animador.nacer(linea);
        }
        if (seleccionada != null && !sim.lineas().contains(seleccionada)) {
            seleccionada = null;
        }
        lienzo.repaint();
        lateral.repaint();
    }

    // ─────────────────────────── el diagrama ───────────────────────────

    private final class Lienzo extends JComponent {

        private double cxL;
        private double cxP;
        private double arriba;
        private double util;

        Lienzo() {
            MouseAdapter raton = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    Linea l = lineaEn(e.getPoint());
                    seleccionada = l != null && !l.equals(seleccionada) ? l : null;
                    refrescar();
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(
                            lineaEn(e.getPoint()) != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                }
            };
            addMouseListener(raton);
            addMouseMotionListener(raton);
        }

        private void medir() {
            cxL = getWidth() * 0.27;
            cxP = getWidth() * 0.72;
            arriba = 128;
            util = getHeight() - arriba - 205;
        }

        private Point2D posLinea(int i) {
            int n = Math.max(sim.lineas().size(), 8);
            double paso = util / n;
            return new Point2D.Double(cxL, arriba + 20 + paso * (i + 0.5));
        }

        private Point2D posPersona(int i) {
            int n = sim.personas().size();
            double paso = util / n;
            double extra = sim.personas().get(i).esExtorsionador() ? 14 : 0;
            return new Point2D.Double(cxP, arriba + 20 + paso * (i + 0.5) + extra);
        }

        /** Con muchas líneas las pastillas se achican para no encimarse. */
        private double altoPastilla() {
            double paso = util / Math.max(sim.lineas().size(), 8);
            return Math.min(30, paso - 5);
        }

        private Linea lineaEn(Point p) {
            medir();
            for (int i = 0; i < sim.lineas().size(); i++) {
                Point2D c = posLinea(i);
                if (Math.abs(p.x - c.getX()) <= 44 && Math.abs(p.y - c.getY()) <= altoPastilla() / 2 + 2) {
                    return sim.lineas().get(i);
                }
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Tema.suavizar(g);
            medir();
            int w = getWidth();
            int h = getHeight();
            Tema.tarjeta(g, 16, 8, w - 24, h - 24, 32, Tema.CREMA);

            dibujarLeyenda(g, w);

            double altoOvalo = util + 50;
            double cy = arriba + 20 + util / 2;
            Tema.ovalo(g, cxL, cy, 190, altoOvalo, Tema.NARANJA_SUAVE, Tema.NARANJA);
            Tema.ovalo(g, cxP, cy, 260, altoOvalo, Tema.AZUL_SUAVE, Tema.AZUL);
            g.setFont(Tema.letra(Font.BOLD, 17f));
            g.setColor(Tema.NARANJA_TEXTO);
            Tema.textoCentrado(g, "L = Líneas (chips)", cxL, arriba - 22);
            g.setColor(Tema.AZUL_TEXTO);
            Tema.textoCentrado(g, "P = Personas (DNI)", cxP, arriba - 22);

            dibujarFlechas(g);
            dibujarLineas(g);
            dibujarPersonas(g);

            if (sim.lineas().isEmpty()) {
                g.setFont(Tema.letra(Font.BOLD, 20f));
                g.setColor(Tema.TEXTO_SUAVE);
                Tema.textoCentrado(g, "Pulsa «Simular 1 día» para empezar", (cxL + cxP) / 2, cy);
            }
            if (seleccionada != null) {
                dibujarRastreo(g, w, h);
            } else {
                dibujarBitacora(g, w, h);
            }
            g.dispose();
        }

        private void dibujarLeyenda(Graphics2D g, int w) {
            int y = 46;
            int x = 48;
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            Tema.flecha(g, x, y, x + 46, y, Tema.AZUL_NOCHE, 3f, false, 1);
            g.setColor(Tema.AZUL_NOCHE);
            g.drawString("registro f", x + 58, y + 5);
            x += 170;
            Tema.flecha(g, x, y, x + 46, y, Tema.ROJO, 3f, true, 1);
            g.setColor(Tema.ROJO_OSCURO);
            g.drawString("uso real u", x + 58, y + 5);
            x += 170;
            g.setColor(Tema.VERDE);
            g.fill(new Ellipse2D.Double(x, y - 8, 16, 16));
            g.drawString("contrato con huella", x + 26, y + 5);
            x += 190;
            g.setColor(Tema.AZUL);
            g.fill(new Ellipse2D.Double(x, y - 13, 26, 26));
            g.setColor(Tema.BLANCO);
            g.setFont(Tema.letra(Font.BOLD, 13f));
            Tema.textoCentrado(g, "n", x + 13, y);
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            g.setColor(Tema.AZUL_TEXTO);
            g.drawString("= |f⁻¹(persona)|", x + 34, y + 5);

            g.setFont(Tema.letra(Font.BOLD, 16f));
            String dia = "Día " + sim.dia();
            int ancho = g.getFontMetrics().stringWidth(dia) + 30;
            Tema.rectRedondo(g, w - 48 - ancho, y - 17, ancho, 34, 34, Tema.AZUL_NOCHE);
            g.setColor(Tema.CREMA);
            Tema.textoCentrado(g, dia, w - 48 - ancho / 2.0, y);
            g.setFont(Tema.letra(Font.PLAIN, 14f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.textoCentrado(g, "Clic en una línea para rastrearla", (cxL + cxP) / 2, arriba - 22);
        }

        private void dibujarFlechas(Graphics2D g) {
            List<Linea> lineas = sim.lineas();
            for (int i = 0; i < lineas.size(); i++) {
                Linea l = lineas.get(i);
                Point2D a = posLinea(i);
                Point2D b = posPersona(sim.personas().indexOf(sim.titular(l)));
                boolean apagada = seleccionada != null && !l.equals(seleccionada);
                boolean elegida = l.equals(seleccionada);
                double av = animador.avance(l);
                Tema.flecha(g, a.getX() + 46, a.getY(), b.getX() - 92, b.getY(),
                        Tema.conAlfa(Tema.AZUL_NOCHE, apagada ? 45 : 210), elegida ? 4.5f : 2.6f, false, av);
                Persona usuario = sim.usuarioReal(l);
                if (!usuario.equals(sim.titular(l))) {
                    Point2D c = posPersona(sim.personas().indexOf(usuario));
                    Tema.flecha(g, a.getX() + 46, a.getY() + 4, c.getX() - 92, c.getY(),
                            Tema.conAlfa(Tema.ROJO, apagada ? 50 : 230), elegida ? 4.5f : 3f, true, av);
                }
            }
        }

        private void dibujarLineas(Graphics2D g) {
            List<Linea> lineas = sim.lineas();
            List<Linea> malas = sim.inconsistentes();
            for (int i = 0; i < lineas.size(); i++) {
                Linea l = lineas.get(i);
                Point2D p = posLinea(i);
                double pulso = animador.pulso(l);
                double escala = 0.7 + 0.3 * animador.avance(l) + 0.12 * pulso;
                double w = 88 * escala;
                double h = altoPastilla() * escala;
                double x = p.getX() - w / 2;
                double y = p.getY() - h / 2;
                if (l.equals(seleccionada)) {
                    Tema.rectRedondo(g, x - 7, y - 7, w + 14, h + 14, h + 14, Tema.conAlfa(Tema.DORADO, 160));
                }
                Tema.rectRedondo(g, x, y + 3, w, h, h, Tema.conAlfa(Tema.AZUL_NOCHE, 35));
                Tema.rectRedondo(g, x, y, w, h, h, malas.contains(l) ? Tema.ROJO_SUAVE : Tema.NARANJA);
                if (malas.contains(l)) {
                    Tema.borde(g, x, y, w, h, h, Tema.ROJO, 3f);
                }
                g.setFont(Tema.letra(Font.BOLD, altoPastilla() >= 26 ? 15f : 13f));
                g.setColor(malas.contains(l) ? Tema.ROJO_OSCURO : Tema.AZUL_NOCHE);
                Tema.textoCentrado(g, l.nombreCorto(), p.getX() + (sim.contrato(l) != null ? 8 : 0), p.getY());
                if (sim.contrato(l) != null) {
                    g.setColor(Tema.VERDE);
                    g.fill(new Ellipse2D.Double(x + 8, p.getY() - 7, 14, 14));
                }
            }
        }

        private void dibujarPersonas(Graphics2D g) {
            List<Persona> personas = sim.personas();
            for (int i = 0; i < personas.size(); i++) {
                Persona p = personas.get(i);
                Point2D c = posPersona(i);
                int cuantas = sim.lineasDe(p).size();
                boolean sobreTope = cuantas > sim.tope();
                double w = 176;
                double h = 44;
                double x = c.getX() - w / 2;
                double y = c.getY() - h / 2;
                if (p.esExtorsionador()) {
                    g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                            new float[] {5f, 5f}, 0f));
                    g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, 90));
                    g.draw(new Line2D.Double(c.getX() - 90, y - 14, c.getX() + 90, y - 14));
                    g.setStroke(new BasicStroke(1f));
                }
                Tema.rectRedondo(g, x, y + 3, w, h, h, Tema.conAlfa(Tema.AZUL_NOCHE, 35));
                Tema.rectRedondo(g, x, y, w, h, h, p.esExtorsionador() ? Tema.ROJO : Tema.AZUL);
                g.setFont(Tema.letra(Font.BOLD, 16f));
                g.setColor(Tema.BLANCO);
                g.drawString(p.nombre(), (float) x + 18, (float) (c.getY() + 6));
                if (!p.esExtorsionador()) {
                    String texto = String.valueOf(cuantas);
                    Color fondo = sobreTope ? Tema.ROJO : Tema.BLANCO;
                    g.setColor(fondo);
                    g.fill(new Ellipse2D.Double(x + w - 42, y + 7, 30, 30));
                    g.setFont(Tema.letra(Font.BOLD, 15f));
                    g.setColor(sobreTope ? Tema.BLANCO : Tema.AZUL_NOCHE);
                    Tema.textoCentrado(g, texto, x + w - 27, y + 22);
                }
            }
        }

        private void dibujarBitacora(Graphics2D g, int w, int h) {
            int x = 48;
            int y = h - 150;
            int ancho = w - 96;
            Tema.rectRedondo(g, x, y, ancho, 116, 20, Tema.conAlfa(Tema.AZUL_NOCHE, 14));
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(Tema.TEXTO_SUAVE);
            g.drawString("BITÁCORA", x + 18, y + 24);
            List<Evento> eventos = sim.eventos();
            int desde = Math.max(0, eventos.size() - 3);
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            for (int i = desde; i < eventos.size(); i++) {
                Evento e = eventos.get(i);
                int yy = y + 50 + (i - desde) * 26;
                g.setColor(colorEvento(e.tipo()));
                g.fill(new Ellipse2D.Double(x + 20, yy - 10, 11, 11));
                g.setColor(Tema.AZUL_NOCHE);
                g.drawString("Día " + e.dia() + "  ·  " + e.texto(), x + 42, yy);
            }
            if (eventos.isEmpty()) {
                g.setColor(Tema.TEXTO_SUAVE);
                g.drawString("Aquí aparecerá lo que pasa cada día.", x + 20, y + 52);
            }
        }

        private Color colorEvento(Evento.Tipo tipo) {
            return switch (tipo) {
                case LEGAL -> Tema.AZUL;
                case FRAUDE -> Tema.ROJO;
                case BLOQUEO, AUDITORIA -> Tema.VERDE;
                case TOPE -> Tema.NARANJA;
                default -> Tema.GRIS;
            };
        }

        private void dibujarRastreo(Graphics2D g, int w, int h) {
            Rastreo r = sim.rastrear(seleccionada);
            int ancho = Math.min(760, w - 96);
            int x = (w - ancho) / 2;
            int y = h - 162;
            Tema.tarjeta(g, x, y, ancho, 128, 22, Tema.AZUL_NOCHE);
            g.setFont(Tema.letra(Font.BOLD, 17f));
            g.setColor(Tema.DORADO);
            g.drawString("Rastreo policial de " + seleccionada.nombreCorto() + "  ·  " + seleccionada.numero(),
                    x + 22, y + 30);
            g.setFont(Tema.letra(Font.PLAIN, 16f));
            g.setColor(Tema.CREMA);
            g.drawString("Registro  f(" + seleccionada + ") = " + r.titular(), x + 22, y + 60);
            g.drawString("Uso real  u(" + seleccionada + ") = " + r.usuarioReal(), x + ancho / 2, y + 60);
            Contrato c = r.contrato();
            String veredicto;
            if (c != null) {
                veredicto = "Contrato " + c + " con huella: g⁻¹ lleva a " + c.huella() + ". Persona real confirmada.";
            } else if (!r.culpableEncontrado()) {
                veredicto = "Sin contrato: la policía llega a " + r.titular() + ", que es inocente.";
            } else {
                veredicto = "Coincide, pero sin contrato con huella no hay prueba de quién la usa.";
            }
            Tema.insignia(g, r.culpableEncontrado() && c != null, x + 34, y + 98, 13);
            g.setFont(Tema.letra(Font.BOLD, 16f));
            g.setColor(r.culpableEncontrado() ? new Color(0x7FD3A8) : new Color(0xF28B82));
            g.drawString(veredicto, x + 58, y + 104);
        }
    }

    // ─────────────────────────── panel derecho ───────────────────────────

    private final class Lateral extends JComponent {

        Lateral() {
            setPreferredSize(new Dimension(500, 600));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Tema.suavizar(g);
            int x = 8;
            int w = getWidth() - 24;
            int mitad = (w - 12) / 2;

            double coincide = sim.coincidencia();
            kpi(g, x, 8, mitad, "Líneas activas", sim.lineas().size() + " / " + Simulador.MAX_LINEAS, Tema.AZUL_NOCHE);
            kpi(g, x + mitad + 12, 8, mitad, "Registro = realidad", Math.round(coincide) + " %",
                    coincide >= 99.9 ? Tema.VERDE : coincide >= 75 ? Tema.NARANJA_TEXTO : Tema.ROJO);
            kpi(g, x, 98, mitad, "Fraudes bloqueados", String.valueOf(sim.bloqueados()), Tema.VERDE);
            kpi(g, x + mitad + 12, 98, mitad, "Frenados por el tope", String.valueOf(sim.rechazosTope()),
                    Tema.NARANJA_TEXTO);

            g.setFont(Tema.letra(Font.BOLD, 19f));
            g.setColor(Tema.AZUL_NOCHE);
            g.drawString("Análisis con funciones", x + 4, 214);

            List<Linea> lineas = sim.lineas();
            List<Persona> personas = sim.personas();
            int y = 230;
            Veredicto f1 = Diagnostico.funcion(lineas, sim.flechasRegistro());
            y = analisis(g, x, y, w, "f: L → P es función", "Cada chip tiene un solo titular", f1.cumple(),
                    lineas.isEmpty() ? "Aún no hay líneas" : f1.razon());

            Veredicto f2 = Diagnostico.inyectiva(lineas, personas, sim.flechasRegistro());
            y = analisis(g, x, y, w, "f es inyectiva", "No es delito: alguien puede tener 2 chips", f2.cumple(),
                    f2.razon().replace("flechas", "líneas"));

            List<Linea> malas = sim.inconsistentes();
            y = analisis(g, x, y, w, "f = u  (el registro dice la verdad)", "Aquí está el verdadero problema",
                    malas.isEmpty(), malas.isEmpty() ? "Todas las líneas coinciden" : "Mienten: " + unir(malas));

            List<Persona> excedidos = sim.personasSobreTope();
            y = analisis(g, x, y, w, "|f⁻¹(p)| ≤ k  (tope de " + sim.tope() + ")", "Principio del palomar",
                    excedidos.isEmpty(), excedidos.isEmpty() ? "Nadie supera el tope"
                            : excedidos.get(0) + " tiene " + sim.lineasDe(excedidos.get(0)).size() + " líneas");

            analisis(g, x, y, w, "g: L → C es biyectiva", "Una línea ↔ un contrato con huella", gBiyectiva(),
                    razonG());
            g.dispose();
        }

        private boolean gBiyectiva() {
            if (sim.lineas().isEmpty()) {
                return false;
            }
            return Diagnostico.biyectiva(sim.lineas(), new ArrayList<>(sim.contratos()), sim.flechasContrato())
                    .cumple();
        }

        private String razonG() {
            if (sim.lineas().isEmpty()) {
                return "Aún no hay líneas";
            }
            List<Flecha<Linea, Contrato>> flechas = sim.flechasContrato();
            for (Linea l : sim.lineas()) {
                if (sim.contrato(l) == null) {
                    long sin = sim.lineas().stream().filter(x -> sim.contrato(x) == null).count();
                    return "«" + l + "» no tiene contrato (" + sin + " sin huella en total)";
                }
            }
            return Diagnostico.biyectiva(sim.lineas(), new ArrayList<>(sim.contratos()), flechas).razon()
                    .replace("f⁻¹", "g⁻¹");
        }

        private void kpi(Graphics2D g, int x, int y, int w, String titulo, String valor, Color color) {
            Tema.tarjeta(g, x, y, w, 80, 20, Tema.BLANCO);
            g.setFont(Tema.letra(Font.PLAIN, 14f));
            g.setColor(Tema.TEXTO_SUAVE);
            g.drawString(titulo, x + 18, y + 26);
            g.setFont(Tema.letra(Font.BOLD, 30f));
            g.setColor(color);
            g.drawString(valor, x + 18, y + 64);
        }

        private int analisis(Graphics2D g, int x, int y, int w, String titulo, String ayuda, boolean ok,
                String razon) {
            int alto = 82;
            Tema.tarjeta(g, x, y, w, alto, 20, Tema.BLANCO);
            Tema.insignia(g, ok, x + 30, y + alto / 2, 16);
            g.setFont(Tema.letra(Font.BOLD, 16.5f));
            g.setColor(Tema.AZUL_NOCHE);
            g.drawString(titulo, x + 58, y + 26);
            g.setFont(Tema.letra(Font.PLAIN, 13f));
            g.setColor(Tema.TEXTO_SUAVE);
            g.drawString(ayuda, x + 58, y + 46);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(ok ? Tema.VERDE : Tema.ROJO_OSCURO);
            g.drawString("→ " + razon, x + 58, y + 68);
            return y + alto + 9;
        }

        private String unir(List<Linea> lineas) {
            StringBuilder texto = new StringBuilder();
            for (int i = 0; i < lineas.size() && i < 6; i++) {
                texto.append(i > 0 ? ", " : "").append(lineas.get(i));
            }
            return lineas.size() > 6 ? texto + "…" : texto.toString();
        }
    }
}
