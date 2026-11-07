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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import simuchip.funciones.Diagnostico;
import simuchip.funciones.Diagnostico.Veredicto;
import simuchip.funciones.Flecha;
import simuchip.funciones.Funciones;
import simuchip.funciones.PasoTraza;
import simuchip.funciones.Traza;

/**
 * Pestaña 1: el laboratorio. Se dibujan flechas entre A = {1, 2, 3…} y B = {a, b, c…}
 * y la app dice en vivo si es función, inyectiva, sobreyectiva o biyectiva.
 * Con «Ejecutar paso a paso» el código corre línea por línea, como un depurador.
 */
public final class PanelAprende extends JPanel {

    private static final int RADIO = 32;
    private static final String[] TITULOS = {"Función", "Inyectiva", "Sobreyectiva", "Biyectiva"};
    private static final String[] PREGUNTAS = {
        "¿De cada elemento de A sale exactamente 1 flecha?",
        "¿Nadie de B recibe 2 o más flechas?",
        "¿Nadie de B se queda sin flecha?",
        "¿Es inyectiva y sobreyectiva a la vez?"
    };
    private static final String[][] CODIGO = {
        {
            "boolean esFuncion(A, flechas) {",
            "  for (a : A)",
            "    if (salidas(a) != 1)",
            "      return false;   // 0 o 2 flechas",
            "  return true;",
            "}"
        },
        {
            "boolean esInyectiva(A, B, flechas) {",
            "  if (!esFuncion(A, flechas)) return false;",
            "  for (b : B)",
            "    if (llegadas(b) > 1)",
            "      return false;   // comparten b",
            "  return true;",
            "}"
        },
        {
            "boolean esSobreyectiva(A, B, flechas) {",
            "  if (!esFuncion(A, flechas)) return false;",
            "  for (b : B)",
            "    if (llegadas(b) == 0)",
            "      return false;   // b quedó solo",
            "  return true;",
            "}"
        },
        {
            "boolean esBiyectiva(A, B, flechas) {",
            "  return esInyectiva(A, B, flechas)",
            "      && esSobreyectiva(A, B, flechas);",
            "}"
        }
    };

    private final List<Integer> dominio = new ArrayList<>();
    private final List<Character> codominio = new ArrayList<>();
    private final List<Flecha<Integer, Character>> flechas = new ArrayList<>();
    private Integer seleccionado;
    private int tarjeta = 1;
    private boolean verInversa;

    // ejecución paso a paso
    private List<PasoTraza> traza;
    private int paso = -1;
    private final Timer ejecutor;

    private final Lienzo lienzo = new Lienzo();
    private final Lateral lateral = new Lateral();
    private final Animador animador;
    private final Avisos avisos;
    private final JLabel etiquetaTamanos = new JLabel();
    private Boton botonInversa;

    public PanelAprende() {
        super(new BorderLayout());
        setBackground(Tema.CELESTE_FONDO);
        animador = new Animador(lienzo, 420);
        avisos = new Avisos(lienzo);
        ejecutor = new Timer(1100, e -> avanzar());
        add(crearBarra(), BorderLayout.NORTH);
        add(lienzo, BorderLayout.CENTER);
        add(lateral, BorderLayout.EAST);
        cargar(4, 3, new int[][] {{1, 'a'}, {2, 'a'}, {3, 'b'}, {4, 'c'}});
    }

    // ─────────────────────────── barra de botones ───────────────────────────

    private JComponent crearBarra() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(4, 14, 0, 14));
        barra.add(etiqueta("Ejemplos:"));
        barra.add(new Boton("No es función", Boton.Estilo.SECUNDARIO)
                .alHacerClic(() -> cargar(3, 3, new int[][] {{1, 'a'}, {1, 'b'}, {2, 'c'}})));
        barra.add(new Boton("Inyectiva", Boton.Estilo.SECUNDARIO)
                .alHacerClic(() -> cargar(3, 4, new int[][] {{1, 'a'}, {2, 'b'}, {3, 'c'}})));
        barra.add(new Boton("Sobreyectiva", Boton.Estilo.SECUNDARIO)
                .alHacerClic(() -> cargar(4, 3, new int[][] {{1, 'a'}, {2, 'a'}, {3, 'b'}, {4, 'c'}})));
        barra.add(new Boton("Biyectiva", Boton.Estilo.SECUNDARIO)
                .alHacerClic(() -> cargar(3, 3, new int[][] {{1, 'a'}, {2, 'b'}, {3, 'c'}})));
        barra.add(new Boton("Ninguna", Boton.Estilo.SECUNDARIO)
                .alHacerClic(() -> cargar(3, 3, new int[][] {{1, 'a'}, {2, 'a'}, {3, 'b'}})));
        barra.add(new Boton("Borrar", Boton.Estilo.PELIGRO).alHacerClic(() -> {
            flechas.clear();
            seleccionado = null;
            detenerEjecucion();
            refrescar();
        }));
        barra.add(espacio(4));
        barra.add(etiquetaTamanos);
        barra.add(new Boton("A −", Boton.Estilo.SECUNDARIO).alHacerClic(() -> cambiarTamano(-1, 0)));
        barra.add(new Boton("A +", Boton.Estilo.SECUNDARIO).alHacerClic(() -> cambiarTamano(1, 0)));
        barra.add(new Boton("B −", Boton.Estilo.SECUNDARIO).alHacerClic(() -> cambiarTamano(0, -1)));
        barra.add(new Boton("B +", Boton.Estilo.SECUNDARIO).alHacerClic(() -> cambiarTamano(0, 1)));
        barra.add(espacio(4));
        botonInversa = new Boton("Ver inversa f⁻¹", Boton.Estilo.SECUNDARIO, true);
        botonInversa.alHacerClic(() -> {
            verInversa = botonInversa.encendido();
            for (Flecha<Integer, Character> f : flechas) {
                animador.olvidar("inv" + f);
                animador.nacer("inv" + f);
            }
            if (verInversa && !Funciones.esBiyectiva(setA(), setB(), flechas)) {
                avisos.mostrar("No hay inversa", veredicto(3).razon(), Tema.ROJO);
            }
            refrescar();
        });
        barra.add(botonInversa);
        return barra;
    }

    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.letra(Font.BOLD, 15f));
        l.setForeground(Tema.TEXTO_SUAVE);
        return l;
    }

    private static JComponent espacio(int ancho) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(ancho, 10));
        return p;
    }

    // ─────────────────────────── estado ───────────────────────────

    /** Carga un ejemplo: tamaños de A y B y sus flechas. */
    public void cargar(int nA, int nB, int[][] pares) {
        ajustarConjuntos(nA, nB);
        flechas.clear();
        animador.olvidarTodo();
        for (int[] par : pares) {
            Flecha<Integer, Character> f = new Flecha<>(par[0], (char) par[1]);
            flechas.add(f);
            animador.nacer(f);
            animador.nacer("inv" + f);
        }
        seleccionado = null;
        detenerEjecucion();
        refrescar();
    }

    public void setVerInversa(boolean valor) {
        verInversa = valor;
        botonInversa.setEncendido(valor);
        refrescar();
    }

    public void setTarjeta(int indice) {
        tarjeta = indice;
        detenerEjecucion();
        refrescar();
    }

    /** Para las capturas: ejecuta hasta el paso indicado (o el final si es mayor). */
    public void ejecutarHasta(int indice) {
        traza = generarTraza();
        paso = Math.min(indice, traza.size() - 1);
        if (paso == traza.size() - 1) {
            anunciarSiTermina();
        }
        refrescar();
    }

    private void ajustarConjuntos(int nA, int nB) {
        dominio.clear();
        codominio.clear();
        for (int i = 1; i <= nA; i++) {
            dominio.add(i);
        }
        for (int i = 0; i < nB; i++) {
            codominio.add((char) ('a' + i));
        }
    }

    private void cambiarTamano(int dA, int dB) {
        int nA = Math.max(1, Math.min(5, dominio.size() + dA));
        int nB = Math.max(1, Math.min(5, codominio.size() + dB));
        ajustarConjuntos(nA, nB);
        flechas.removeIf(f -> !dominio.contains(f.desde()) || !codominio.contains(f.hasta()));
        if (seleccionado != null && !dominio.contains(seleccionado)) {
            seleccionado = null;
        }
        detenerEjecucion();
        refrescar();
    }

    private void alternarFlecha(Integer a, Character b) {
        Flecha<Integer, Character> f = new Flecha<>(a, b);
        if (flechas.remove(f)) {
            animador.olvidar(f);
        } else {
            flechas.add(f);
            animador.nacer(f);
            animador.nacer("inv" + f);
        }
        detenerEjecucion();
        refrescar();
    }

    private void refrescar() {
        etiquetaTamanos.setText("|A| = " + dominio.size() + "   |B| = " + codominio.size());
        etiquetaTamanos.setFont(Tema.letra(Font.BOLD, 15f));
        etiquetaTamanos.setForeground(Tema.AZUL_NOCHE);
        lateral.actualizarBotones();
        lienzo.repaint();
        lateral.repaint();
    }

    private Set<Integer> setA() {
        return new HashSet<>(dominio);
    }

    private Set<Character> setB() {
        return new HashSet<>(codominio);
    }

    private Veredicto veredicto(int indice) {
        return switch (indice) {
            case 0 -> Diagnostico.funcion(dominio, flechas);
            case 1 -> Diagnostico.inyectiva(dominio, codominio, flechas);
            case 2 -> Diagnostico.sobreyectiva(dominio, codominio, flechas);
            default -> Diagnostico.biyectiva(dominio, codominio, flechas);
        };
    }

    // ─────────────────────────── ejecución paso a paso ───────────────────────────

    private List<PasoTraza> generarTraza() {
        return switch (tarjeta) {
            case 0 -> Traza.esFuncion(dominio, flechas);
            case 1 -> Traza.esInyectiva(dominio, codominio, flechas);
            case 2 -> Traza.esSobreyectiva(dominio, codominio, flechas);
            default -> Traza.esBiyectiva(dominio, codominio, flechas);
        };
    }

    private boolean ejecutando() {
        return traza != null && paso >= 0;
    }

    private PasoTraza pasoActual() {
        return ejecutando() ? traza.get(paso) : null;
    }

    /** Botón ▶: arranca (o pausa) la ejecución automática. */
    private void reproducir() {
        if (ejecutor.isRunning()) {
            ejecutor.stop();
        } else {
            if (!ejecutando() || paso >= traza.size() - 1) {
                traza = generarTraza();
                paso = 0;
                avisos.limpiarTodo();
            }
            ejecutor.start();
        }
        refrescar();
    }

    /** Botón Paso: avanza un solo renglón. */
    private void unPaso() {
        ejecutor.stop();
        if (!ejecutando() || paso >= traza.size() - 1) {
            traza = generarTraza();
            paso = 0;
            avisos.limpiarTodo();
        } else {
            paso++;
        }
        anunciarSiTermina();
        refrescar();
    }

    private void avanzar() {
        if (!ejecutando()) {
            ejecutor.stop();
            return;
        }
        if (paso < traza.size() - 1) {
            paso++;
            anunciarSiTermina();
        }
        if (paso >= traza.size() - 1) {
            ejecutor.stop();
        }
        refrescar();
    }

    private void anunciarSiTermina() {
        PasoTraza p = traza.get(paso);
        if (paso == traza.size() - 1) {
            boolean ok = p.tipo() == PasoTraza.Tipo.OK;
            avisos.mostrar(TITULOS[tarjeta] + (ok ? ": SÍ" : ": NO"), p.mensaje(),
                    ok ? Tema.VERDE_CLARO : Tema.ROJO_CLARO);
        }
    }

    private void detenerEjecucion() {
        ejecutor.stop();
        traza = null;
        paso = -1;
    }

    // ─────────────────────────── el diagrama ───────────────────────────

    private final class Lienzo extends JComponent {

        private double cxA;
        private double cxB;
        private double cy;
        private double altoOvalo;

        Lienzo() {
            MouseAdapter raton = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    clic(e.getPoint());
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    boolean sobreNodo = nodoA(e.getPoint()) != null || nodoB(e.getPoint()) != null;
                    setCursor(Cursor.getPredefinedCursor(sobreNodo ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                }
            };
            addMouseListener(raton);
            addMouseMotionListener(raton);
        }

        private void medir() {
            cxA = getWidth() * 0.27;
            cxB = getWidth() * 0.73;
            int maximo = Math.max(dominio.size(), codominio.size());
            altoOvalo = Math.max(220, Math.min(getHeight() - 250, 96 * maximo + 110));
            cy = 140 + altoOvalo / 2;
        }

        private Point2D posA(int i) {
            return pos(cxA, i, dominio.size());
        }

        private Point2D posB(int i) {
            return pos(cxB, i, codominio.size());
        }

        private Point2D pos(double cx, int i, int n) {
            double util = altoOvalo - 80;
            double paso = util / n;
            return new Point2D.Double(cx, cy - util / 2 + paso * (i + 0.5));
        }

        private Integer nodoA(Point p) {
            medir();
            for (int i = 0; i < dominio.size(); i++) {
                if (posA(i).distance(p) <= RADIO + 6) {
                    return dominio.get(i);
                }
            }
            return null;
        }

        private Character nodoB(Point p) {
            medir();
            for (int i = 0; i < codominio.size(); i++) {
                if (posB(i).distance(p) <= RADIO + 6) {
                    return codominio.get(i);
                }
            }
            return null;
        }

        private void clic(Point p) {
            Integer a = nodoA(p);
            Character b = nodoB(p);
            if (a != null) {
                seleccionado = a.equals(seleccionado) ? null : a;
                refrescar();
            } else if (b != null && seleccionado != null) {
                alternarFlecha(seleccionado, b);
            } else {
                seleccionado = null;
                refrescar();
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Tema.suavizar(g);
            medir();
            int w = getWidth();
            Tema.tarjeta(g, 16, 8, w - 24, getHeight() - 24, 32, Tema.CREMA);

            g.setFont(Tema.letra(Font.PLAIN, 16f));
            g.setColor(Tema.TEXTO_SUAVE);
            String ayuda = seleccionado == null
                    ? "Clic en un número de A y luego en una letra de B para dibujar (o borrar) una flecha"
                    : "Ahora elige a dónde va la flecha del «" + seleccionado + "»  (clic en una letra de B)";
            Tema.textoCentrado(g, ayuda, w / 2.0, 40);

            String tipo = Funciones.tipo(setA(), setB(), flechas);
            Color color = switch (tipo) {
                case "Biyectiva" -> Tema.VERDE;
                case "No es función" -> Tema.ROJO;
                case "Función (ninguna de las tres)" -> Tema.TEXTO_SUAVE;
                default -> Tema.AZUL_TEXTO;
            };
            g.setFont(Tema.letra(Font.BOLD, 22f));
            String chip = "Resultado: " + tipo;
            int anchoChip = g.getFontMetrics().stringWidth(chip) + 44;
            Tema.rectRedondo(g, w / 2.0 - anchoChip / 2.0, 56, anchoChip, 44, 44, color);
            g.setColor(Tema.BLANCO);
            Tema.textoCentrado(g, chip, w / 2.0, 78);

            Tema.ovalo(g, cxA, cy, 210, altoOvalo, Tema.NARANJA_SUAVE, Tema.NARANJA);
            Tema.ovalo(g, cxB, cy, 210, altoOvalo, Tema.AZUL_SUAVE, Tema.AZUL);
            g.setFont(Tema.letra(Font.BOLD, 18f));
            g.setColor(Tema.NARANJA_TEXTO);
            Tema.textoCentrado(g, "A  ·  dominio", cxA, cy - altoOvalo / 2 - 16);
            g.setColor(Tema.AZUL_TEXTO);
            Tema.textoCentrado(g, "B  ·  codominio", cxB, cy - altoOvalo / 2 - 16);

            dibujarFlechas(g);
            dibujarNodos(g);
            dibujarPie(g);
            avisos.dibujar(g, w - 30, 120, 330);
            g.dispose();
        }

        private void dibujarFlechas(Graphics2D g) {
            boolean biyectiva = Funciones.esBiyectiva(setA(), setB(), flechas);
            Object foco = pasoActual() != null ? pasoActual().foco() : null;
            for (Flecha<Integer, Character> f : flechas) {
                Point2D a = posA(dominio.indexOf(f.desde()));
                Point2D b = posB(codominio.indexOf(f.hasta()));
                boolean malSale = Funciones.salidas(f.desde(), flechas) != 1;
                boolean enFoco = foco != null && (foco.equals(f.desde()) || foco.equals(f.hasta()));
                boolean apagada = foco != null && !enFoco;
                double dy = verInversa && biyectiva ? -9 : 0;
                Color c = malSale ? Tema.ROJO : enFoco ? Tema.NARANJA_TEXTO : Tema.AZUL_NOCHE;
                Tema.flecha(g, a.getX() + RADIO + 4, a.getY() + dy, b.getX() - RADIO - 6, b.getY() + dy,
                        Tema.conAlfa(c, apagada ? 70 : 255), enFoco ? 5.5f : malSale ? 4.5f : 3.5f, false,
                        animador.avance(f));
            }
            if (verInversa && biyectiva) {
                for (Flecha<Character, Integer> f : Funciones.inversa(flechas)) {
                    Point2D b = posB(codominio.indexOf(f.desde()));
                    Point2D a = posA(dominio.indexOf(f.hasta()));
                    Tema.flecha(g, b.getX() - RADIO - 4, b.getY() + 9, a.getX() + RADIO + 6, a.getY() + 9,
                            Tema.NARANJA_TEXTO, 3.5f, true, animador.avance("inv" + new Flecha<>(f.hasta(), f.desde())));
                }
            }
        }

        private void dibujarNodos(Graphics2D g) {
            Object foco = pasoActual() != null ? pasoActual().foco() : null;
            for (int i = 0; i < dominio.size(); i++) {
                Integer a = dominio.get(i);
                int salen = Funciones.salidas(a, flechas);
                Point2D p = posA(i);
                if (a.equals(seleccionado) || a.equals(foco)) {
                    halo(g, p);
                }
                nodo(g, p, String.valueOf(a), Tema.NARANJA, Tema.AZUL_NOCHE, salen != 1 ? Tema.ROJO : null, false);
                insigniaNumero(g, p, salen, salen != 1);
            }
            for (int i = 0; i < codominio.size(); i++) {
                Character b = codominio.get(i);
                int llegan = Funciones.llegadas(b, setA(), flechas);
                Point2D p = posB(i);
                if (b.equals(foco)) {
                    halo(g, p);
                }
                Color anillo = llegan > 1 ? Tema.ROJO : null;
                nodo(g, p, String.valueOf(b), llegan == 0 ? Tema.GRIS : Tema.AZUL,
                        llegan == 0 ? Tema.AZUL_NOCHE : Tema.BLANCO, anillo, llegan == 0);
                insigniaNumero(g, p, llegan, llegan != 1);
            }
        }

        private void halo(Graphics2D g, Point2D p) {
            g.setColor(Tema.conAlfa(Tema.DORADO, 140));
            g.fill(new Ellipse2D.Double(p.getX() - RADIO - 14, p.getY() - RADIO - 14, (RADIO + 14) * 2,
                    (RADIO + 14) * 2));
        }

        private void nodo(Graphics2D g, Point2D p, String texto, Color relleno, Color letra, Color anillo,
                boolean punteado) {
            double x = p.getX() - RADIO;
            double y = p.getY() - RADIO;
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, 35));
            g.fill(new Ellipse2D.Double(x, y + 4, RADIO * 2, RADIO * 2));
            g.setColor(relleno);
            g.fill(new Ellipse2D.Double(x, y, RADIO * 2, RADIO * 2));
            if (anillo != null || punteado) {
                g.setStroke(punteado
                        ? new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                                new float[] {6f, 6f}, 0f)
                        : new BasicStroke(5f));
                g.setColor(anillo != null ? anillo : Tema.TEXTO_SUAVE);
                g.draw(new Ellipse2D.Double(x - 6, y - 6, RADIO * 2 + 12, RADIO * 2 + 12));
                g.setStroke(new BasicStroke(1f));
            }
            g.setFont(Tema.letra(Font.BOLD, 26f));
            g.setColor(letra);
            Tema.textoCentrado(g, texto, p.getX(), p.getY());
        }

        private void insigniaNumero(Graphics2D g, Point2D p, int n, boolean alerta) {
            double x = p.getX() + RADIO - 6;
            double y = p.getY() - RADIO - 8;
            g.setColor(alerta ? Tema.ROJO : Tema.AZUL_NOCHE);
            g.fill(new Ellipse2D.Double(x, y, 26, 26));
            g.setColor(Tema.BLANCO);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            Tema.textoCentrado(g, String.valueOf(n), x + 13, y + 13);
        }

        private void dibujarPie(Graphics2D g) {
            int w = getWidth();
            PasoTraza p = pasoActual();
            if (p != null) {
                // narrador: qué está haciendo el código ahora mismo
                int ancho = Math.min(760, w - 80);
                int x = (w - ancho) / 2;
                int y = getHeight() - 104;
                Color borde = switch (p.tipo()) {
                    case OK -> Tema.VERDE;
                    case FALLA -> Tema.ROJO;
                    default -> Tema.DORADO;
                };
                Tema.tarjeta(g, x, y, ancho, 70, 20, Tema.AZUL_NOCHE);
                Tema.rectRedondo(g, x, y, 10, 70, 10, borde);
                g.setFont(Tema.letra(Font.BOLD, 13f));
                g.setColor(Tema.DORADO);
                Tema.texto(g, "PASO " + (paso + 1) + " DE " + traza.size(), x + 26, y + 24);
                g.setFont(Tema.letra(Font.BOLD, 18f));
                g.setColor(Tema.CREMA);
                Tema.texto(g, p.mensaje(), x + 26, y + 52);
                return;
            }
            int y = getHeight() - 52;
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.textoCentrado(g, "Circulito con número = cuántas flechas SALEN (en A) o LLEGAN (en B). Rojo = problema.",
                    w / 2.0, y);
            if (verInversa && !Funciones.esBiyectiva(setA(), setB(), flechas)) {
                g.setFont(Tema.letra(Font.BOLD, 16f));
                String aviso = "No existe la inversa f⁻¹: " + veredicto(3).razon().toLowerCase();
                int ancho = g.getFontMetrics().stringWidth(aviso) + 40;
                Tema.rectRedondo(g, w / 2.0 - ancho / 2.0, y - 58, ancho, 38, 38, Tema.ROJO_SUAVE);
                g.setColor(Tema.ROJO_OSCURO);
                Tema.textoCentrado(g, aviso, w / 2.0, y - 39);
            }
        }
    }

    // ─────────────────────────── panel derecho ───────────────────────────

    private final class Lateral extends JComponent {

        private static final int Y_TARJETAS = 52;
        private static final int ALTO_LINEA = 23;
        private final Boton reproducir = new Boton("Ejecutar paso a paso", Boton.Estilo.PRIMARIO).conIcono(Iconos.Tipo.PLAY);
        private final Boton unPaso = new Boton("Paso", Boton.Estilo.SECUNDARIO).conIcono(Iconos.Tipo.SIGUIENTE);

        Lateral() {
            setLayout(null);
            setPreferredSize(new Dimension(500, 600));
            reproducir.setFont(Tema.letra(Font.BOLD, 14f));
            unPaso.setFont(Tema.letra(Font.BOLD, 14f));
            reproducir.alHacerClic(PanelAprende.this::reproducir);
            unPaso.alHacerClic(PanelAprende.this::unPaso);
            add(reproducir);
            add(unPaso);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int alto = altoTarjeta();
                    int i = (e.getY() - Y_TARJETAS) / (alto + 8);
                    if (i >= 0 && i < 4 && e.getY() >= Y_TARJETAS && e.getY() < yCodigo()) {
                        setTarjeta(i);
                    }
                }
            });
        }

        private int altoCodigo() {
            return 52 + 7 * ALTO_LINEA + 52;
        }

        /** Las tarjetas se encogen si la ventana es baja, para que el código siempre se vea. */
        private int altoTarjeta() {
            int disponible = getHeight() - Y_TARJETAS - altoCodigo() - 20;
            return Math.max(62, Math.min(90, disponible / 4 - 8));
        }

        private int yCodigo() {
            return Y_TARJETAS + 4 * (altoTarjeta() + 8) + 4;
        }

        @Override
        public void doLayout() {
            int w = getWidth() - 24;
            int y = yCodigo();
            int anchoPaso = unPaso.getPreferredSize().width;
            int anchoRep = reproducir.getPreferredSize().width;
            unPaso.setBounds(8 + w - anchoPaso - 12, y + 10, anchoPaso, 40);
            reproducir.setBounds(8 + w - anchoPaso - anchoRep - 20, y + 10, anchoRep, 40);
        }

        void actualizarBotones() {
            reproducir.setTexto(ejecutor.isRunning() ? "Pausa" : "Ejecutar paso a paso");
            reproducir.conIcono(ejecutor.isRunning() ? Iconos.Tipo.PAUSA : Iconos.Tipo.PLAY);
            doLayout();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Tema.suavizar(g);
            int x = 8;
            int w = getWidth() - 24;
            int alto = altoTarjeta();
            boolean compacto = alto < 84;
            g.setFont(Tema.letra(Font.BOLD, 19f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.texto(g, "¿Qué tipo de función es?  (clic para elegir)", x + 4, 34);

            for (int i = 0; i < 4; i++) {
                Veredicto v = veredicto(i);
                int y = Y_TARJETAS + i * (alto + 8);
                Tema.tarjeta(g, x, y, w, alto, 20, Tema.BLANCO);
                if (i == tarjeta) {
                    Tema.borde(g, x, y, w, alto, 20, Tema.DORADO, 3.5f);
                }
                Tema.insignia(g, v.cumple(), x + 32, y + alto / 2, 17);
                g.setFont(Tema.letra(Font.BOLD, 17f));
                g.setColor(Tema.AZUL_NOCHE);
                Tema.texto(g, TITULOS[i] + (v.cumple() ? ":  SÍ" : ":  NO"), x + 62, y + 25);
                if (!compacto) {
                    g.setFont(Tema.letra(Font.PLAIN, 13.5f));
                    g.setColor(Tema.TEXTO_SUAVE);
                    Tema.texto(g, PREGUNTAS[i], x + 62, y + 46);
                }
                g.setFont(Tema.letra(Font.BOLD, 14f));
                g.setColor(v.cumple() ? Tema.VERDE : Tema.ROJO_OSCURO);
                Tema.texto(g, "→ " + v.razon(), x + 62, y + (compacto ? 50 : 70));
            }
            dibujarCodigo(g, x, yCodigo(), w);
            g.dispose();
        }

        private void dibujarCodigo(Graphics2D g, int x, int y, int w) {
            String[] lineas = CODIGO[tarjeta];
            int alto = altoCodigo();
            Tema.tarjeta(g, x, y, w, alto, 22, Tema.AZUL_NOCHE);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(Tema.DORADO);
            Tema.texto(g, "Funciones.java", x + 20, y + 35);

            PasoTraza p = pasoActual();
            Set<Integer> marcadas = p != null ? Set.of(p.linea()) : lineasMarcadas();
            Color marca;
            if (p != null) {
                marca = switch (p.tipo()) {
                    case OK -> Tema.conAlfa(Tema.VERDE, 220);
                    case FALLA -> Tema.conAlfa(Tema.ROJO, 220);
                    default -> Tema.conAlfa(Tema.DORADO, 200);
                };
            } else {
                marca = veredicto(tarjeta).cumple() ? Tema.conAlfa(Tema.VERDE, 200) : Tema.conAlfa(Tema.ROJO, 210);
            }
            g.setFont(Tema.codigo(14.5f));
            int y0 = y + 62;
            for (int i = 0; i < lineas.length; i++) {
                int yy = y0 + i * ALTO_LINEA;
                boolean marcada = marcadas.contains(i);
                if (marcada) {
                    Tema.rectRedondo(g, x + 10, yy - 2, w - 20, ALTO_LINEA, 10, marca);
                    if (p != null) {
                        g.setColor(Tema.AZUL_NOCHE);
                        Iconos.dibujar(g, Iconos.Tipo.PLAY, x + 19, yy + 10, 12, Tema.AZUL_NOCHE);
                    }
                }
                g.setColor(marcada ? (p != null && p.tipo() == PasoTraza.Tipo.NORMAL ? Tema.AZUL_NOCHE : Tema.BLANCO)
                        : Tema.TEXTO_OSCURO_SUAVE);
                Tema.texto(g, lineas[i], x + 30, yy + 15);
            }
            // fila de variables
            int yv = y + alto - 40;
            Tema.rectRedondo(g, x + 10, yv, w - 20, 30, 10, Tema.AZUL_NOCHE_2);
            g.setFont(Tema.letra(Font.BOLD, 13f));
            g.setColor(Tema.DORADO);
            Tema.texto(g, "VARIABLES", x + 22, yv + 20);
            g.setFont(Tema.codigo(14f));
            g.setColor(Tema.CREMA);
            String vars = p == null ? "pulsa Ejecutar para ver el código correr"
                    : p.variables().isEmpty() ? "—" : p.variables();
            Tema.texto(g, vars, x + 112, yv + 20);
        }

        /** Sin ejecución: qué renglón "decidió" el resultado. */
        private Set<Integer> lineasMarcadas() {
            Set<Integer> r = new HashSet<>();
            boolean esFuncion = Funciones.esFuncion(setA(), flechas);
            switch (tarjeta) {
                case 0 -> r.add(esFuncion ? 4 : 3);
                case 1, 2 -> {
                    if (!esFuncion) {
                        r.add(1);
                    } else {
                        r.add(veredicto(tarjeta).cumple() ? 5 : 4);
                    }
                }
                default -> {
                    boolean iny = Funciones.esInyectiva(setA(), setB(), flechas);
                    boolean sob = Funciones.esSobreyectiva(setA(), setB(), flechas);
                    if (iny && sob) {
                        r.add(1);
                        r.add(2);
                    } else {
                        r.add(iny ? 2 : 1);
                    }
                }
            }
            return r;
        }
    }
}
