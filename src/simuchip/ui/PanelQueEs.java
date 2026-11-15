package simuchip.ui;

import static simuchip.ui.Personitas.ANA;
import static simuchip.ui.Personitas.POLICIA;
import static simuchip.ui.Personitas.VENDEDOR;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import simuchip.ui.Personitas.Cara;

/**
 * Capítulo: qué es ChipGuard como producto. Qué es (app + web + servidor), para qué sirve, quién lo usa,
 * la app pantalla por pantalla, la versión web y en qué se diferencia de lo que existe hoy.
 */
public final class PanelQueEs extends PanelEscenas {

    public PanelQueEs() {
        super(List.of(
            new Escena("¿Qué es ChipGuard?",
                "Es nuestra propuesta: un servicio gratuito que protege tu DNI. La regla es simple: nadie puede activar "
                    + "un chip a tu nombre sin tu permiso. Para lograrlo tiene tres partes, una para cada tipo de usuario.", true),
            new Escena("¿Para qué sirve?", "Cuatro cosas, todas desde tu celular.", true),
            new Escena("¿Quién lo usa?", "Tres actores, cada uno con su propia pantalla.", true),
            new Escena("La app, pantalla por pantalla.",
                "Te registras una vez y luego solo la abres cuando te llega una alerta.", true),
            new Escena("La web: para quien no tiene app.",
                "Desde cualquier navegador: pones tu DNI, recibes un código por SMS y ves tus chips.", true),
            new Escena("¿Qué cambia frente a lo que existe hoy?",
                "OSIPTEL ya tiene «Checa tus líneas» y permite restringir nuevas líneas a tu nombre. Es un gran paso, "
                    + "pero tú tienes que ir a revisar. ChipGuard une todo en tu celular y te pregunta ANTES de que el chip "
                    + "funcione.", true)
        ));
    }

    @Override
    protected void dibujarFigura(Graphics2D g, int i, Rectangle a, double t) {
        switch (i) {
            case 0 -> partes(g, a, t);
            case 1 -> usos(g, a, t);
            case 2 -> actores(g, a, t);
            case 3 -> pantallas(g, a, t);
            case 4 -> web(g, a, t);
            default -> comparacion(g, a, t);
        }
    }

    // ─────────────────────────── 0: tres partes ───────────────────────────

    private void partes(Graphics2D g, Rectangle a, double t) {
        String[][] datos = {
            {"App móvil", "Android y iPhone", "Para ti: te avisa al instante y apruebas o bloqueas con tu huella."},
            {"Página web", "chipguard.pe", "Para quien no tiene smartphone: consulta y bloquea con DNI + código SMS."},
            {"Servidor central", "conectado a las operadoras", "El guardián: ninguna operadora activa un chip sin "
                + "su visto bueno."}
        };
        int sep = 30;
        int w = (a.width - 2 * sep) / 3;
        int h = Math.min(a.height - 10, 430);
        Font fTit = Tema.titulo(28f);
        Font fSub = Tema.letra(Font.BOLD, 15f);
        Font fDes = Tema.letra(Font.PLAIN, 18f);
        FontMetrics mt = g.getFontMetrics(fTit);
        FontMetrics ms = g.getFontMetrics(fSub);
        FontMetrics md = g.getFontMetrics(fDes);
        int lh = Math.round(md.getHeight() * 1.25f);
        // el texto manda: la tarjeta más llena define cuánto alto le queda al dibujo de arriba
        int lineas = 0;
        for (String[] d : datos) {
            lineas = Math.max(lineas, Tema.partir(d[2], md, w - 48).size());
        }
        int textoH = mt.getHeight() + ms.getHeight() + 8 + (lineas - 1) * lh + md.getHeight();
        int dibujoH = Math.min(200, h - textoH - 38);
        double esc = Math.max(0.3, (dibujoH - 10) / 170.0);
        h = Math.min(h, 38 + dibujoH + textoH);
        for (int k = 0; k < 3; k++) {
            double p = tramo(t, 0.2 + k * 0.4, 0.5);
            int al = (int) (255 * p);
            int x = a.x + k * (w + sep);
            int y = a.y + (int) ((1 - p) * 24);
            Tema.sombra(g, x, y, w, h, 30, (int) (26 * p));
            Tema.rectRedondo(g, x, y, w, h, 30, Tema.conAlfa(Tema.BLANCO, al));
            double cx = x + w / 2.0;
            double cy = y + 12 + dibujoH / 2.0;
            AffineTransform antes = g.getTransform();
            g.translate(cx, cy);
            g.scale(Math.min(1, esc), Math.min(1, esc));
            g.translate(-cx, -cy);
            switch (k) {
                case 0 -> miniCelular(g, cx, cy, 170, p);
                case 1 -> miniNavegador(g, cx, cy, 230, 150, p);
                default -> servidor(g, cx, cy, 130, 170, p);
            }
            g.setTransform(antes);
            int ty = y + 12 + dibujoH + 8;
            g.setFont(fTit);
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.textoCentrado(g, datos[k][0], cx, ty + mt.getHeight() / 2.0);
            ty += mt.getHeight();
            g.setFont(fSub);
            g.setColor(Tema.conAlfa(Tema.AZUL_TEXTO, al));
            Tema.textoCentrado(g, datos[k][1], cx, ty + ms.getHeight() / 2.0);
            ty += ms.getHeight() + 8;
            g.setFont(fDes);
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.parrafo(g, datos[k][2], x + 24, ty, w - 48, 1.25f);
        }
    }

    /** Párrafo con cada línea centrada en cx. Devuelve la altura usada. */
    private static int parrafoCentrado(Graphics2D g, String texto, double cx, double y, int ancho, float interlineado) {
        FontMetrics fm = g.getFontMetrics();
        int alto = Math.round(fm.getHeight() * interlineado);
        List<String> lineas = Tema.partir(texto, fm, ancho);
        double yy = y + fm.getAscent();
        for (String linea : lineas) {
            Tema.texto(g, linea, cx - fm.stringWidth(linea) / 2.0, yy);
            yy += alto;
        }
        return lineas.size() * alto;
    }

    private static void miniCelular(Graphics2D g, double cx, double cy, double alto, double p) {
        int al = (int) (255 * p);
        double w = alto * 0.5;
        double x = cx - w / 2;
        double y = cy - alto / 2;
        g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
        g.fill(new RoundRectangle2D.Double(x, y, w, alto, 22, 22));
        g.setColor(Tema.conAlfa(Tema.BLANCO, al));
        g.fill(new RoundRectangle2D.Double(x + 5, y + 5, w - 10, alto - 10, 18, 18));
        Iconos.dibujar(g, Iconos.Tipo.ESCUDO, cx, cy - 18, 40, Tema.conAlfa(Tema.AZUL, al));
        Iconos.dibujar(g, Iconos.Tipo.VISTO, cx, cy - 16, 16, Tema.conAlfa(Tema.AZUL, al));
        Tema.rectRedondo(g, x + 14, cy + 22, w - 28, 18, 9, Tema.conAlfa(Tema.VERDE, al));
        Tema.rectRedondo(g, x + 14, cy + 46, w - 28, 18, 9, Tema.conAlfa(Tema.GRIS_CLARO, al));
    }

    private static void miniNavegador(Graphics2D g, double cx, double cy, double w, double h, double p) {
        int al = (int) (255 * p);
        double x = cx - w / 2;
        double y = cy - h / 2;
        Tema.rectRedondo(g, x, y, w, h, 14, Tema.conAlfa(Tema.GRIS_CLARO, al));
        Tema.rectRedondo(g, x + 4, y + 26, w - 8, h - 30, 10, Tema.conAlfa(Tema.BLANCO, al));
        Color[] luces = {Tema.ROJO, Tema.DORADO, Tema.VERDE_CLARO};
        for (int k = 0; k < 3; k++) {
            g.setColor(Tema.conAlfa(luces[k], al));
            g.fill(new Ellipse2D.Double(x + 10 + k * 14, y + 8, 9, 9));
        }
        Tema.rectRedondo(g, x + 58, y + 6, w - 70, 14, 7, Tema.conAlfa(Tema.BLANCO, al));
        g.setFont(Tema.letra(Font.PLAIN, 10f));
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
        Tema.texto(g, "chipguard.pe", x + 66, y + 17);
        Tema.rectRedondo(g, x + 20, y + 44, w * 0.5, 12, 6, Tema.conAlfa(Tema.AZUL_NOCHE, al));
        Tema.rectRedondo(g, x + 20, y + 68, w - 40, 22, 8, Tema.conAlfa(Tema.GRIS_CLARO, al));
        Tema.rectRedondo(g, x + 20, y + 98, w * 0.38, 22, 11, Tema.conAlfa(Tema.AZUL, al));
    }

    private static void servidor(Graphics2D g, double cx, double cy, double w, double h, double p) {
        int al = (int) (255 * p);
        double x = cx - w / 2;
        double y = cy - h / 2;
        for (int k = 0; k < 3; k++) {
            double yy = y + k * (h / 3);
            Tema.rectRedondo(g, x, yy, w, h / 3 - 8, 12, Tema.conAlfa(Tema.AZUL_NOCHE, al));
            g.setColor(Tema.conAlfa(Tema.VERDE_CLARO, al));
            g.fill(new Ellipse2D.Double(x + 14, yy + (h / 3 - 8) / 2 - 5, 10, 10));
            for (int r = 0; r < 4; r++) {
                Tema.rectRedondo(g, x + 44 + r * 18, yy + (h / 3 - 8) / 2 - 8, 8, 16, 3,
                        Tema.conAlfa(Tema.TEXTO_OSCURO_SUAVE, al));
            }
        }
    }

    // ─────────────────────────── 1: para qué sirve ───────────────────────────

    private void usos(Graphics2D g, Rectangle a, double t) {
        Object[][] datos = {
            {Iconos.Tipo.ALERTA, "Te avisa", "Cada vez que alguien intenta sacar un chip con tu DNI, en cualquier tienda."},
            {Iconos.Tipo.HUELLA, "Te deja decidir", "Apruebas con tu huella en el celular, o tocas «No fui yo» y se bloquea."},
            {Iconos.Tipo.CHIP, "Te muestra tus chips", "Todas las líneas a tu nombre, con operadora, tienda y fecha."},
            {Iconos.Tipo.DOCUMENTO, "Denuncia por ti", "El intento bloqueado llega a OSIPTEL con la tienda y la hora."}
        };
        int sep = 24;
        int w = (a.width - sep) / 2;
        Font fTit = Tema.titulo(30f);
        Font fDes = Tema.letra(Font.PLAIN, 18f);
        FontMetrics mt = g.getFontMetrics(fTit);
        FontMetrics md = g.getFontMetrics(fDes);
        int lh = Math.round(md.getHeight() * 1.3f);
        int lineas = 0;
        for (Object[] d : datos) {
            lineas = Math.max(lineas, Tema.partir((String) d[2], md, w - 170).size());
        }
        int bloque = mt.getHeight() + lineas * lh; // título + descripción, centrados en la tarjeta
        int h = Math.min(Math.max(170, bloque + 36), (a.height - sep) / 2);
        for (int k = 0; k < 4; k++) {
            double p = tramo(t, 0.2 + k * 0.35, 0.5);
            int al = (int) (255 * p);
            int x = a.x + (k % 2) * (w + sep);
            int y = a.y + (k / 2) * (h + sep);
            Tema.rectRedondo(g, x, y, w, h, 28, Tema.conAlfa(k % 3 == 0 ? Tema.AZUL_SUAVE : Tema.VERDE_SUAVE, al));
            g.setColor(Tema.conAlfa(Tema.BLANCO, al));
            g.fill(new Ellipse2D.Double(x + 30, y + h / 2.0 - 42, 84, 84));
            Iconos.dibujar(g, (Iconos.Tipo) datos[k][0], x + 72, y + h / 2.0, 42, Tema.conAlfa(Tema.AZUL, al));
            int ty = y + (h - mt.getHeight() - Tema.partir((String) datos[k][2], md, w - 170).size() * lh) / 2;
            g.setFont(fTit);
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, (String) datos[k][1], x + 140, ty + mt.getAscent());
            g.setFont(fDes);
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.parrafo(g, (String) datos[k][2], x + 140, ty + mt.getHeight(), w - 170, 1.3f);
        }
    }

    // ─────────────────────────── 2: quién lo usa ───────────────────────────

    private void actores(Graphics2D g, Rectangle a, double t) {
        Object[][] datos = {
            {ANA, "El ciudadano", "Instala la app o entra a la web. Recibe alertas, aprueba y ve sus chips."},
            {VENDEDOR, "La tienda y la operadora", "Antes de activar, su sistema consulta a ChipGuard. No puede saltarse el paso."},
            {POLICIA, "OSIPTEL y la policía", "Reciben los reportes y ven el mapa de tiendas con más intentos bloqueados."}
        };
        int sep = 30;
        int w = (a.width - 2 * sep) / 3;
        Font fTit = Tema.titulo(26f);
        Font fDes = Tema.letra(Font.PLAIN, 18f);
        FontMetrics mt = g.getFontMetrics(fTit);
        FontMetrics md = g.getFontMetrics(fDes);
        // el texto más largo de las tres tarjetas fija cuánto alto le queda a la persona
        int titH = 0;
        int desH = 0;
        for (Object[] d : datos) {
            titH = Math.max(titH, Tema.partir((String) d[1], mt, w - 40).size() * mt.getHeight());
            desH = Math.max(desH, Tema.partir((String) d[2], md, w - 48).size() * Math.round(md.getHeight() * 1.3f));
        }
        int textoH = titH + 8 + desH;
        double alto = Math.max(110, Math.min(220, a.height - 10 - 24 - textoH - 30 - 30));
        int fondo = (int) Math.min(a.y + a.height - 10, a.y + alto + 60 + textoH + 24);
        for (int k = 0; k < 3; k++) {
            double p = tramo(t, 0.2 + k * 0.4, 0.5);
            int x = a.x + k * (w + sep);
            double cx = x + w / 2.0;
            int yc = (int) (a.y + alto * 0.35);
            Tema.rectRedondo(g, x, yc, w, fondo - yc, 30, Tema.conAlfa(Tema.AZUL_SUAVE, (int) (255 * p)));
            Personitas.persona(g, (Personitas.Look) datos[k][0], cx, a.y + alto + 30, alto, Cara.FELIZ, p);
            int ty = (int) (a.y + alto + 60);
            g.setFont(fTit);
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
            int propio = Tema.partir((String) datos[k][1], mt, w - 40).size() * mt.getHeight();
            parrafoCentrado(g, (String) datos[k][1], cx, ty + (titH - propio) / 2.0, w - 40, 1f);
            g.setFont(fDes);
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * p)));
            Tema.parrafo(g, (String) datos[k][2], x + 24, ty + titH + 8, w - 48, 1.3f);
        }
    }

    // ─────────────────────────── 3: pantallas de la app ───────────────────────────

    private void pantallas(Graphics2D g, Rectangle a, double t) {
        String[] titulo = {"1. Registro", "2. Mis chips", "3. Alerta", "4. Historial"};
        String[] pie = {"DNI + huella, una sola vez", "Tus líneas y el tope de 3", "Apruebas o bloqueas",
            "Todo queda anotado"};
        int sep = 28;
        int w = Math.min(250, (a.width - 3 * sep) / 4);
        int h = Math.min(a.height - 60, (int) (w * 2.05));
        int x0 = a.x + (a.width - (4 * w + 3 * sep)) / 2;
        for (int k = 0; k < 4; k++) {
            double p = tramo(t, 0.2 + k * 0.45, 0.5);
            if (p <= 0) {
                continue;
            }
            int x = x0 + k * (w + sep);
            int y = a.y + (int) ((1 - p) * 30);
            Rectangle s = marcoCelular(g, x, y, w, h);
            switch (k) {
                case 0 -> pantallaRegistro(g, s);
                case 1 -> pantallaChips(g, s);
                case 2 -> pantallaAlerta(g, s);
                default -> pantallaHistorial(g, s);
            }
            g.setFont(Tema.titulo(20f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.textoCentrado(g, titulo[k], x + w / 2.0, y + h + 26);
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.textoCentrado(g, pie[k], x + w / 2.0, y + h + 50);
        }
    }

    /** Dibuja el marco del celular y devuelve el rectángulo de la pantalla. */
    private static Rectangle marcoCelular(Graphics2D g, int x, int y, int w, int h) {
        Tema.sombra(g, x, y, w, h, 34, 30);
        Tema.rectRedondo(g, x, y, w, h, 34, Tema.AZUL_NOCHE);
        Rectangle s = new Rectangle(x + 7, y + 7, w - 14, h - 14);
        Tema.rectRedondo(g, s.x, s.y, s.width, s.height, 28, Tema.BLANCO);
        Tema.rectRedondo(g, x + w / 2.0 - 34, y + 14, 68, 16, 8, Tema.AZUL_NOCHE);
        g.setFont(Tema.letra(Font.BOLD, 11f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "9:41", s.x + 16, s.y + 20);
        // cabecera de la app
        Iconos.dibujar(g, Iconos.Tipo.ESCUDO, s.x + 24, s.y + 48, 20, Tema.AZUL);
        g.setFont(Tema.titulo(15f));
        Tema.texto(g, "ChipGuard", s.x + 40, s.y + 53);
        return new Rectangle(s.x + 14, s.y + 70, s.width - 28, s.height - 84);
    }

    private static void campo(Graphics2D g, int x, int y, int w, String rotulo, String valor) {
        g.setFont(Tema.letra(Font.PLAIN, 11f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, rotulo, x, y);
        Tema.rectRedondo(g, x, y + 6, w, 30, 10, Tema.GRIS_CLARO);
        g.setFont(Tema.letra(Font.BOLD, 13f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, valor, x + 10, y + 26);
    }

    private static void boton(Graphics2D g, int x, int y, int w, String texto, Color fondo, Color letra) {
        Tema.rectRedondo(g, x, y, w, 34, 17, fondo);
        g.setFont(Tema.letra(Font.BOLD, 13f));
        g.setColor(letra);
        Tema.textoCentrado(g, texto, x + w / 2.0, y + 17);
    }

    private static void pantallaRegistro(Graphics2D g, Rectangle s) {
        g.setFont(Tema.titulo(17f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.parrafo(g, "Protege tu DNI", s.x, s.y + 4, s.width, 1.2f);
        campo(g, s.x, s.y + 44, s.width, "Número de DNI", "4123 4560");
        campo(g, s.x, s.y + 94, s.width, "Celular", "987 111 222");
        // huella + aviso entre el último campo y el botón; el aviso se parte en líneas y la huella cede el alto
        String aviso = "Pon tu dedo para verificar con RENIEC";
        g.setFont(Tema.letra(Font.PLAIN, 11f));
        FontMetrics fm = g.getFontMetrics();
        int lh = Math.round(fm.getHeight() * 1.1f);
        int avisoH = Tema.partir(aviso, fm, s.width - 8).size() * lh;
        int arriba = s.y + 138;
        int abajo = s.y + s.height - 50;
        int huellaH = Math.max(0, Math.min(54, abajo - arriba - avisoH - 6));
        int y0 = arriba + (abajo - arriba - huellaH - 6 - avisoH) / 2;
        if (huellaH >= 20) {
            Personitas.huella(g, s.getCenterX(), y0 + huellaH / 2.0, huellaH, Tema.AZUL, 1);
        }
        g.setColor(Tema.TEXTO_SUAVE);
        parrafoCentrado(g, aviso, s.getCenterX(), y0 + huellaH + 6, s.width - 8, 1.1f);
        boton(g, s.x, s.y + s.height - 44, s.width, "Activar protección", Tema.AZUL, Tema.BLANCO);
    }

    private static void pantallaChips(Graphics2D g, Rectangle s) {
        g.setFont(Tema.titulo(17f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "Mis chips", s.x, s.y + 16);
        g.setFont(Tema.letra(Font.BOLD, 12f));
        g.setColor(Tema.VERDE);
        Tema.texto(g, "1 de 3 permitidos", s.x, s.y + 36);
        String[][] chips = {{"987 111 222", "Operadora A · 2023"}};
        int y = s.y + 52;
        for (String[] c : chips) {
            Tema.rectRedondo(g, s.x, y, s.width, 54, 14, Tema.VERDE_SUAVE);
            Iconos.dibujar(g, Iconos.Tipo.CHIP, s.x + 22, y + 27, 22, Tema.VERDE);
            g.setFont(Tema.letra(Font.BOLD, 13f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.texto(g, c[0], s.x + 42, y + 24);
            g.setFont(Tema.letra(Font.PLAIN, 11f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.texto(g, c[1], s.x + 42, y + 41);
            y += 62;
        }
        // interruptor: DNI cerrado para nuevas líneas
        Tema.rectRedondo(g, s.x, y + 10, s.width, 58, 14, Tema.GRIS_CLARO);
        g.setFont(Tema.letra(Font.BOLD, 12f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "DNI cerrado a", s.x + 12, y + 34);
        Tema.texto(g, "nuevas líneas", s.x + 12, y + 52);
        Tema.rectRedondo(g, s.x + s.width - 56, y + 26, 44, 26, 13, Tema.VERDE);
        g.setColor(Tema.BLANCO);
        g.fill(new Ellipse2D.Double(s.x + s.width - 34, y + 28, 22, 22));
        boton(g, s.x, s.y + s.height - 44, s.width, "Ver mapa de mis chips", Tema.AZUL_SUAVE, Tema.AZUL_TEXTO);
    }

    private static void pantallaAlerta(Graphics2D g, Rectangle s) {
        Tema.rectRedondo(g, s.x - 4, s.y - 4, s.width + 8, s.height + 8, 18, Tema.ROJO_SUAVE);
        String detalle = "Tienda «Chips al toque», Comas · hoy 3:12 p. m. · 2.º chip con tu DNI";
        g.setFont(Tema.letra(Font.PLAIN, 12f));
        FontMetrics fm = g.getFontMetrics();
        float inter = 1.25f;
        int detalleH = Tema.partir(detalle, fm, s.width - 12).size() * Math.round(fm.getHeight() * inter);
        // si el detalle no cabe sobre los botones, el ícono y la pregunta suben (el ícono se achica)
        int d = Math.max(0, Math.min(34, s.y + 126 + detalleH - (s.y + s.height - 92)));
        Iconos.dibujar(g, Iconos.Tipo.ALERTA, s.getCenterX(), s.y + 36 - d / 2.0, 44 - d * 0.6, Tema.ROJO);
        g.setFont(Tema.titulo(17f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, "¿Estás sacando", s.getCenterX(), s.y + 84 - d);
        Tema.textoCentrado(g, "un chip?", s.getCenterX(), s.y + 106 - d);
        g.setFont(fm.getFont());
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.parrafo(g, detalle, s.x + 6, s.y + 126 - d, s.width - 12, inter);
        boton(g, s.x, s.y + s.height - 86, s.width, "No fui yo · Bloquear", Tema.ROJO, Tema.BLANCO);
        boton(g, s.x, s.y + s.height - 44, s.width, "Sí, fui yo (huella)", Tema.VERDE_SUAVE, Tema.VERDE);
    }

    private static void pantallaHistorial(Graphics2D g, Rectangle s) {
        g.setFont(Tema.titulo(17f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "Historial", s.x, s.y + 16);
        String[][] filas = {
            {"15:12", "Intento bloqueado", "Chips al toque · Comas"},
            {"15:13", "Reporte a OSIPTEL", "N.º 2025-118734"},
            {"15:10", "Chip aprobado", "987 111 222"},
            {"2023", "Registro con RENIEC", "Huella verificada"}
        };
        Color[] color = {Tema.ROJO, Tema.AZUL, Tema.VERDE, Tema.TEXTO_SUAVE};
        int y = s.y + 44;
        for (int k = 0; k < filas.length; k++) {
            g.setColor(color[k]);
            g.fill(new Ellipse2D.Double(s.x + 2, y + 2, 10, 10));
            if (k < filas.length - 1) {
                g.setColor(Tema.FILETE);
                g.fillRect(s.x + 6, y + 14, 2, 44);
            }
            g.setFont(Tema.letra(Font.BOLD, 12f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.texto(g, filas[k][1], s.x + 22, y + 11);
            g.setFont(Tema.letra(Font.PLAIN, 11f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.texto(g, filas[k][0] + " · " + filas[k][2], s.x + 22, y + 28);
            y += 58;
        }
    }

    // ─────────────────────────── 4: la web ───────────────────────────

    private void web(Graphics2D g, Rectangle a, double t) {
        double p = tramo(t, 0.2, 0.6);
        String[][] filas = {{"987 111 222", "Operadora A", "Tienda Miraflores", "2023", "tuyo"},
            {"912 555 935", "Operadora B", "Chips al toque, Comas", "ayer", "?"}};
        String[] cab = {"Número", "Operadora", "Dónde se registró", "Cuándo", ""};
        String bloquear = "No es mío · Bloquear";
        Font fCab = Tema.letra(Font.BOLD, 13f);
        Font fCel = Tema.letra(Font.PLAIN, 15f);
        FontMetrics mCab = g.getFontMetrics(fCab);
        FontMetrics mCel = g.getFontMetrics(fCel);
        int anchoBoton = g.getFontMetrics(Tema.letra(Font.BOLD, 13f)).stringWidth(bloquear) + 28;
        // cada columna mide lo que su texto más largo; la tabla decide el ancho mínimo del navegador
        int sepCol = 20;
        int[] colX = new int[5];
        for (int c = 0; c < 4; c++) {
            int maxC = mCab.stringWidth(cab[c]);
            for (String[] f : filas) {
                maxC = Math.max(maxC, mCel.stringWidth(f[c]));
            }
            colX[c + 1] = colX[c] + maxC + sepCol;
        }
        int ancho0 = colX[4] + anchoBoton;
        int w = (int) Math.min(Math.max(a.width * 0.68, ancho0 + 80), 980);
        int h = Math.min(a.height - 10, 470);
        int x = a.x;
        int y = a.y + (int) ((1 - p) * 24);
        Tema.sombra(g, x, y, w, h, 20, 30);
        Tema.rectRedondo(g, x, y, w, h, 20, Tema.GRIS_CLARO);
        Tema.rectRedondo(g, x + 6, y + 44, w - 12, h - 50, 14, Tema.BLANCO);
        Color[] luces = {Tema.ROJO, Tema.DORADO, Tema.VERDE_CLARO};
        for (int k = 0; k < 3; k++) {
            g.setColor(luces[k]);
            g.fill(new Ellipse2D.Double(x + 18 + k * 22, y + 16, 13, 13));
        }
        Tema.rectRedondo(g, x + 110, y + 10, w - 140, 26, 13, Tema.BLANCO);
        g.setFont(Tema.letra(Font.PLAIN, 14f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "https://chipguard.pe/mis-chips", x + 128, y + 28);
        // contenido
        int cx = x + 40;
        int cy = y + 90;
        g.setFont(Tema.titulo(28f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "Consulta tus chips", cx, cy);
        g.setFont(Tema.letra(Font.PLAIN, 13f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "DNI", cx, cy + 36);
        Tema.rectRedondo(g, cx, cy + 44, 200, 36, 10, Tema.GRIS_CLARO);
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "Código SMS", cx + 220, cy + 36);
        Tema.rectRedondo(g, cx + 220, cy + 44, 140, 36, 10, Tema.GRIS_CLARO);
        g.setFont(Tema.letra(Font.BOLD, 15f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.texto(g, "41234560", cx + 12, cy + 68);
        Tema.texto(g, "• • • • • •", cx + 234, cy + 68);
        boton(g, cx + 380, cy + 45, 130, "Consultar", Tema.AZUL, Tema.BLANCO);
        // tabla de resultados
        double q = tramo(t, 0.9, 0.5);
        int ty = cy + 110;
        int ancho = w - 80;
        // el espacio que sobra se reparte entre las columnas
        int extra = Math.max(0, (ancho - ancho0) / 4);
        int[] xs = new int[5];
        for (int c = 0; c < 5; c++) {
            xs[c] = cx + colX[c] + c * extra;
        }
        g.setFont(fCab);
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * q)));
        for (int c = 0; c < cab.length; c++) {
            Tema.texto(g, cab[c], xs[c], ty);
        }
        for (int f = 0; f < filas.length; f++) {
            int fy = ty + 16 + f * 56;
            boolean sospechoso = filas[f][4].equals("?");
            Tema.rectRedondo(g, cx - 10, fy, ancho + 20, 46, 12,
                    Tema.conAlfa(sospechoso ? Tema.ROJO_SUAVE : Tema.CELESTE_FONDO, (int) (255 * q)));
            g.setFont(fCel);
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * q)));
            for (int c = 0; c < 4; c++) {
                Tema.texto(g, filas[f][c], xs[c], fy + 29);
            }
            if (sospechoso) {
                boton(g, xs[4], fy + 6, anchoBoton, bloquear, Tema.ROJO, Tema.BLANCO);
            } else {
                Tema.pastilla(g, "Es mío", xs[4], fy + 9, Tema.VERDE_SUAVE, Tema.VERDE, Tema.letra(Font.BOLD, 13f));
            }
        }
        // a la derecha, a quién le sirve
        int xd = x + w + 40;
        int anchoD = a.x + a.width - xd;
        if (anchoD > 200) {
            String titD = "Don Julio no tiene smartphone.";
            String desD = "Su nieta entra a la web, pone el DNI y el código que llega por SMS a su celular básico.";
            Font fTitD = Tema.titulo(22f);
            Font fDesD = Tema.letra(Font.PLAIN, 17f);
            FontMetrics mT = g.getFontMetrics(fTitD);
            FontMetrics mD = g.getFontMetrics(fDesD);
            int titH = Tema.partir(titD, mT, anchoD).size() * Math.round(mT.getHeight() * 1.15f);
            int desH = Tema.partir(desD, mD, anchoD).size() * Math.round(mD.getHeight() * 1.3f);
            // la persona se achica si el texto (ya partido en líneas) necesita el alto
            double altoP = Math.max(100, Math.min(200, a.height - 10 - titH - 12 - desH - 70));
            double pies = a.y + 30 + altoP;
            Personitas.persona(g, Personitas.VICTIMA, xd + anchoD / 2.0, pies, altoP, Cara.FELIZ, tramo(t, 0.6, 0.4));
            g.setFont(fTitD);
            g.setColor(Tema.AZUL_NOCHE);
            Tema.parrafo(g, titD, xd, (int) pies + 40, anchoD, 1.15f);
            g.setFont(fDesD);
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.parrafo(g, desD, xd, (int) pies + 40 + titH + 12, anchoD, 1.3f);
        }
    }

    // ─────────────────────────── 5: comparación ───────────────────────────

    private void comparacion(Graphics2D g, Rectangle a, double t) {
        String[][] filas = {
            {"¿Cómo ves tus chips?", "Entras a la web cuando te acuerdas", "Siempre en tu celular"},
            {"¿Cuándo te enteras?", "Cuando revisas, o por un SMS si llega", "Al instante, antes de activar"},
            {"¿El chip ya funciona?", "Sí: se activa y luego reclamas", "No: espera tu permiso"},
            {"¿Bloquear nuevas líneas?", "Sí, una restricción general", "Sí, y además chip por chip"},
            {"¿Y la tienda?", "Reclamo aparte", "Reporte automático a OSIPTEL"}
        };
        int alto = Math.min(78, (a.height - 70) / filas.length);
        // columnas medidas: cada una empieza después del texto más largo de la anterior
        int[] maxCol = new int[3];
        Font[] fCol = {Tema.titulo(21f), Tema.letra(Font.PLAIN, 19f), Tema.letra(Font.BOLD, 19f)};
        for (String[] f : filas) {
            for (int c = 0; c < 3; c++) {
                maxCol[c] = Math.max(maxCol[c], g.getFontMetrics(fCol[c]).stringWidth(f[c]));
            }
        }
        int c1 = a.x;
        int c2 = Math.max(a.x + (int) (a.width * 0.3), c1 + maxCol[0] + 24);
        int c3 = Math.max(a.x + (int) (a.width * 0.65), c2 + maxCol[1] + 32);
        c3 = Math.min(c3, a.x + a.width - maxCol[2] - 16);
        int anchoC2 = c3 - 32 - c2; // lo que no quepa aquí se parte en dos líneas
        g.setFont(Tema.titulo(24f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.texto(g, "Hoy: «Checa tus líneas»", c2, a.y + 30);
        g.setColor(Tema.VERDE);
        Iconos.dibujar(g, Iconos.Tipo.ESCUDO, c3 + 12, a.y + 22, 26, Tema.VERDE);
        Tema.texto(g, "Con ChipGuard", c3 + 34, a.y + 30);
        for (int f = 0; f < filas.length; f++) {
            double p = tramo(t, 0.3 + f * 0.3, 0.45);
            int al = (int) (255 * p);
            int y = a.y + 54 + f * alto;
            Tema.rectRedondo(g, c3 - 16, y + 4, a.x + a.width - c3 + 16, alto - 8, 16,
                    Tema.conAlfa(Tema.VERDE_SUAVE, al));
            g.setColor(Tema.FILETE);
            g.fillRect(c1, y + alto, a.width, 1);
            g.setFont(fCol[0]);
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, filas[f][0], c1, y + alto / 2.0 + 8);
            g.setFont(fCol[1]);
            g.setColor(Tema.conAlfa(Tema.ROJO_OSCURO, al));
            celda(g, filas[f][1], c2, y + alto / 2.0, anchoC2);
            g.setFont(fCol[2]);
            g.setColor(Tema.conAlfa(Tema.VERDE, al));
            Tema.texto(g, filas[f][2], c3, y + alto / 2.0 + 7);
        }
    }

    /** Texto centrado en yc; si es más ancho que la columna se parte en dos líneas parejas, con interlineado justo. */
    private static void celda(Graphics2D g, String s, int x, double yc, int ancho) {
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(s) <= ancho) {
            Tema.texto(g, s, x, yc + 7);
            return;
        }
        int corte = -1;
        int mejor = Integer.MAX_VALUE;
        for (int i = s.indexOf(' '); i > 0; i = s.indexOf(' ', i + 1)) {
            int m = Math.max(fm.stringWidth(s.substring(0, i)), fm.stringWidth(s.substring(i + 1)));
            if (m < mejor) {
                mejor = m;
                corte = i;
            }
        }
        if (corte < 0) {
            Tema.texto(g, s, x, yc + 7);
            return;
        }
        int lh = fm.getAscent() + fm.getDescent();
        double base = yc - lh + fm.getAscent();
        Tema.texto(g, s.substring(0, corte), x, base);
        Tema.texto(g, s.substring(corte + 1), x, base + lh);
    }
}
