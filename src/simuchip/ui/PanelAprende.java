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
import simuchip.funciones.Diagnostico;
import simuchip.funciones.Diagnostico.Veredicto;
import simuchip.funciones.Flecha;
import simuchip.funciones.Funciones;

/**
 * Pestaña 1: el laboratorio. Se dibujan flechas entre A = {1, 2, 3…} y B = {a, b, c…}
 * y la app dice en vivo si es función, inyectiva, sobreyectiva o biyectiva,
 * marcando la línea de código que lo decidió.
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
    private int tarjeta = 0;
    private boolean verInversa;

    private final Lienzo lienzo = new Lienzo();
    private final Lateral lateral = new Lateral();
    private final Animador animador;
    private final JLabel etiquetaTamanos = new JLabel();
    private Boton botonInversa;

    public PanelAprende() {
        super(new BorderLayout());
        setBackground(Tema.CELESTE_FONDO);
        animador = new Animador(lienzo, 420);
        add(crearBarra(), BorderLayout.NORTH);
        add(lienzo, BorderLayout.CENTER);
        add(lateral, BorderLayout.EAST);
        cargar(3, 3, new int[][] {{1, 'a'}, {2, 'b'}, {3, 'c'}});
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
            refrescar();
        }));
        barra.add(espacio(4));
        barra.add(etiquetaTamanos);
        barra.add(new Boton("A −", Boton.Estilo.SECUNDARIO).alHacerClic(() -> cambiarTamano(-1, 0)));
        barra.add(new Boton("A +", Boton.Estilo.SECUNDARIO).alHacerClic(() -> cambiarTamano(1, 0)));
        barra.add(new Boton("B −", Boton.Estilo.SECUNDARIO).alHacerClic(() -> cambiarTamano(0, -1)));
        barra.add(new Boton("B +", Boton.Estilo.SECUNDARIO).alHacerClic(() -> cambiarTamano(0, 1)));
        barra.add(espacio(4));
        Boton inversa = new Boton("Ver inversa f⁻¹", Boton.Estilo.SECUNDARIO, true);
        botonInversa = inversa;
        inversa.alHacerClic(() -> {
            verInversa = inversa.encendido();
            animador.olvidarTodo();
            for (Flecha<Integer, Character> f : flechas) {
                animador.nacer("inv" + f);
            }
            refrescar();
        });
        barra.add(inversa);
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
        refrescar();
    }

    public void setVerInversa(boolean valor) {
        verInversa = valor;
        botonInversa.setEncendido(valor);
        refrescar();
    }

    public void setTarjeta(int indice) {
        tarjeta = indice;
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
        refrescar();
    }

    private void refrescar() {
        etiquetaTamanos.setText("|A| = " + dominio.size() + "   |B| = " + codominio.size());
        etiquetaTamanos.setFont(Tema.letra(Font.BOLD, 15f));
        etiquetaTamanos.setForeground(Tema.AZUL_NOCHE);
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
            altoOvalo = Math.min(getHeight() - 200, 96 * maximo + 110);
            cy = 150 + altoOvalo / 2;
        }

        private Point2D posA(int i) {
            return pos(cxA, i, dominio.size());
        }

        private Point2D posB(int i) {
            return pos(cxB, i, codominio.size());
        }

        private Point2D pos(double cx, int i, int n) {
            double util = altoOvalo - 90;
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

            // instrucción
            g.setFont(Tema.letra(Font.PLAIN, 16f));
            g.setColor(Tema.TEXTO_SUAVE);
            String ayuda = seleccionado == null
                    ? "Haz clic en un número de A y luego en una letra de B para dibujar (o borrar) una flecha"
                    : "Ahora elige a dónde va la flecha del «" + seleccionado + "»  (clic en una letra de B)";
            Tema.textoCentrado(g, ayuda, w / 2.0, 40);

            // resultado grande
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
            Tema.rectRedondo(g, w / 2.0 - anchoChip / 2.0, 58, anchoChip, 44, 44, color);
            g.setColor(Tema.BLANCO);
            Tema.textoCentrado(g, chip, w / 2.0, 80);

            // óvalos y títulos
            Tema.ovalo(g, cxA, cy, 210, altoOvalo, Tema.NARANJA_SUAVE, Tema.NARANJA);
            Tema.ovalo(g, cxB, cy, 210, altoOvalo, Tema.AZUL_SUAVE, Tema.AZUL);
            g.setFont(Tema.letra(Font.BOLD, 18f));
            g.setColor(Tema.NARANJA_TEXTO);
            Tema.textoCentrado(g, "A  ·  dominio", cxA, cy - altoOvalo / 2 - 18);
            g.setColor(Tema.AZUL_TEXTO);
            Tema.textoCentrado(g, "B  ·  codominio", cxB, cy - altoOvalo / 2 - 18);

            dibujarFlechas(g);
            dibujarNodos(g);
            dibujarPie(g);
            g.dispose();
        }

        private void dibujarFlechas(Graphics2D g) {
            boolean biyectiva = Funciones.esBiyectiva(setA(), setB(), flechas);
            for (Flecha<Integer, Character> f : flechas) {
                Point2D a = posA(dominio.indexOf(f.desde()));
                Point2D b = posB(codominio.indexOf(f.hasta()));
                boolean malSale = Funciones.salidas(f.desde(), flechas) != 1;
                double dy = verInversa && biyectiva ? -9 : 0;
                Tema.flecha(g, a.getX() + RADIO + 4, a.getY() + dy, b.getX() - RADIO - 6, b.getY() + dy,
                        malSale ? Tema.ROJO : Tema.AZUL_NOCHE, malSale ? 4.5f : 3.5f, false, animador.avance(f));
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
            for (int i = 0; i < dominio.size(); i++) {
                Integer a = dominio.get(i);
                int salen = Funciones.salidas(a, flechas);
                Point2D p = posA(i);
                boolean elegido = a.equals(seleccionado);
                if (elegido) {
                    g.setColor(Tema.conAlfa(Tema.DORADO, 120));
                    g.fill(new Ellipse2D.Double(p.getX() - RADIO - 12, p.getY() - RADIO - 12, (RADIO + 12) * 2,
                            (RADIO + 12) * 2));
                }
                nodo(g, p, String.valueOf(a), Tema.NARANJA, Tema.AZUL_NOCHE, salen != 1 ? Tema.ROJO : null, false);
                insigniaNumero(g, p, salen, salen != 1, "sale");
            }
            for (int i = 0; i < codominio.size(); i++) {
                Character b = codominio.get(i);
                int llegan = Funciones.llegadas(b, setA(), flechas);
                Point2D p = posB(i);
                Color anillo = llegan > 1 ? Tema.ROJO : null;
                nodo(g, p, String.valueOf(b), llegan == 0 ? Tema.GRIS : Tema.AZUL,
                        llegan == 0 ? Tema.AZUL_NOCHE : Tema.BLANCO, anillo, llegan == 0);
                insigniaNumero(g, p, llegan, llegan != 1, "llega");
            }
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

        private void insigniaNumero(Graphics2D g, Point2D p, int n, boolean alerta, String verbo) {
            double x = p.getX() + RADIO - 6;
            double y = p.getY() - RADIO - 8;
            g.setColor(alerta ? Tema.ROJO : Tema.AZUL_NOCHE);
            g.fill(new Ellipse2D.Double(x, y, 26, 26));
            g.setColor(Tema.BLANCO);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            Tema.textoCentrado(g, String.valueOf(n), x + 13, y + 13);
        }

        private void dibujarPie(Graphics2D g) {
            int y = getHeight() - 52;
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            g.setColor(Tema.TEXTO_SUAVE);
            String texto = "El circulito con número = cuántas flechas SALEN (en A) o LLEGAN (en B). Rojo = problema.";
            Tema.textoCentrado(g, texto, getWidth() / 2.0, y);
            if (verInversa && !Funciones.esBiyectiva(setA(), setB(), flechas)) {
                g.setFont(Tema.letra(Font.BOLD, 16f));
                String aviso = "No existe la inversa f⁻¹: " + veredicto(3).razon().toLowerCase();
                int ancho = g.getFontMetrics().stringWidth(aviso) + 40;
                Tema.rectRedondo(g, getWidth() / 2.0 - ancho / 2.0, y - 58, ancho, 38, 38, Tema.ROJO_SUAVE);
                g.setColor(Tema.ROJO_OSCURO);
                Tema.textoCentrado(g, aviso, getWidth() / 2.0, y - 39);
            }
        }
    }

    // ─────────────────────────── panel derecho ───────────────────────────

    private final class Lateral extends JComponent {

        private static final int ANCHO = 500;
        private static final int ALTO_TARJETA = 90;
        private static final int Y_TARJETAS = 62;

        Lateral() {
            setPreferredSize(new Dimension(ANCHO, 600));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int i = (e.getY() - Y_TARJETAS) / (ALTO_TARJETA + 10);
                    if (i >= 0 && i < 4 && e.getY() >= Y_TARJETAS) {
                        tarjeta = i;
                        repaint();
                    }
                }
            });
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Tema.suavizar(g);
            int x = 8;
            int w = getWidth() - 24;
            g.setFont(Tema.letra(Font.BOLD, 20f));
            g.setColor(Tema.AZUL_NOCHE);
            g.drawString("¿Qué tipo de función es?", x + 4, 40);

            for (int i = 0; i < 4; i++) {
                Veredicto v = veredicto(i);
                int y = Y_TARJETAS + i * (ALTO_TARJETA + 10);
                Tema.tarjeta(g, x, y, w, ALTO_TARJETA, 22, Tema.BLANCO);
                if (i == tarjeta) {
                    Tema.borde(g, x, y, w, ALTO_TARJETA, 22, Tema.DORADO, 3.5f);
                }
                Tema.insignia(g, v.cumple(), x + 34, y + ALTO_TARJETA / 2, 18);
                g.setFont(Tema.letra(Font.BOLD, 18f));
                g.setColor(Tema.AZUL_NOCHE);
                g.drawString(TITULOS[i] + (v.cumple() ? ":  SÍ" : ":  NO"), x + 66, y + 26);
                g.setFont(Tema.letra(Font.PLAIN, 13.5f));
                g.setColor(Tema.TEXTO_SUAVE);
                g.drawString(PREGUNTAS[i], x + 66, y + 47);
                g.setFont(Tema.letra(Font.BOLD, 14.5f));
                g.setColor(v.cumple() ? Tema.VERDE : Tema.ROJO_OSCURO);
                g.drawString("→ " + v.razon(), x + 66, y + 72);
            }
            dibujarCodigo(g, x, Y_TARJETAS + 4 * (ALTO_TARJETA + 10) + 6, w);
            g.dispose();
        }

        private void dibujarCodigo(Graphics2D g, int x, int y, int w) {
            String[] lineas = CODIGO[tarjeta];
            int altoLinea = 23;
            int alto = 56 + lineas.length * altoLinea + 16;
            Tema.tarjeta(g, x, y, w, alto, 22, Tema.AZUL_NOCHE);
            g.setFont(Tema.letra(Font.BOLD, 14f));
            g.setColor(Tema.DORADO);
            g.drawString("Así lo decide el código  ·  Funciones.java", x + 20, y + 32);
            Set<Integer> marcadas = lineasMarcadas();
            boolean ok = veredicto(tarjeta).cumple();
            g.setFont(Tema.codigo(14.5f));
            for (int i = 0; i < lineas.length; i++) {
                int yy = y + 56 + i * altoLinea;
                if (marcadas.contains(i)) {
                    Tema.rectRedondo(g, x + 10, yy - 2, w - 20, altoLinea, 10,
                            ok ? Tema.conAlfa(Tema.VERDE, 200) : Tema.conAlfa(Tema.ROJO, 210));
                }
                g.setColor(marcadas.contains(i) ? Tema.BLANCO : new Color(0xC9D6EA));
                g.drawString(lineas[i], x + 22, yy + 17);
            }
        }

        /** Qué renglón del código "decidió" el resultado de la tarjeta elegida. */
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
