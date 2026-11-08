package simuchip.ui;

import static simuchip.ui.Personitas.ANA;
import static simuchip.ui.Personitas.POLICIA;
import static simuchip.ui.Personitas.VENDEDOR;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
                "Un servicio gratuito que protege tu DNI: nadie puede activar un chip a tu nombre sin tu permiso. "
                    + "Tiene tres partes.", true),
            new Escena("¿Para qué sirve?", "Cuatro cosas, todas desde tu celular.", true),
            new Escena("¿Quién lo usa?", "Tres actores, cada uno con su propia pantalla.", true),
            new Escena("La app, pantalla por pantalla.",
                "Te registras una vez y luego solo la abres cuando te llega una alerta.", true),
            new Escena("La web: para quien no tiene app.",
                "Desde cualquier navegador: pones tu DNI, recibes un código por SMS y ves tus chips.", true),
            new Escena("¿Qué cambia frente a lo que existe hoy?",
                "Hoy OSIPTEL ya tiene «Checa tus líneas», pero tienes que acordarte de revisar. ChipGuard te avisa "
                    + "antes de que el chip funcione.", true)
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
        for (int k = 0; k < 3; k++) {
            double p = tramo(t, 0.2 + k * 0.4, 0.5);
            int al = (int) (255 * p);
            int x = a.x + k * (w + sep);
            int y = a.y + (int) ((1 - p) * 24);
            Tema.sombra(g, x, y, w, h, 30, (int) (26 * p));
            Tema.rectRedondo(g, x, y, w, h, 30, Tema.conAlfa(Tema.BLANCO, al));
            double cx = x + w / 2.0;
            double cy = y + 120;
            switch (k) {
                case 0 -> miniCelular(g, cx, cy, 170, p);
                case 1 -> miniNavegador(g, cx, cy, 230, 150, p);
                default -> servidor(g, cx, cy, 130, 170, p);
            }
            g.setFont(Tema.titulo(28f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.textoCentrado(g, datos[k][0], cx, y + 240);
            g.setFont(Tema.letra(Font.BOLD, 15f));
            g.setColor(Tema.conAlfa(Tema.AZUL_TEXTO, al));
            Tema.textoCentrado(g, datos[k][1], cx, y + 270);
            g.setFont(Tema.letra(Font.PLAIN, 18f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.parrafo(g, datos[k][2], x + 30, y + 296, w - 60, 1.4f);
        }
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
        int h = Math.min(170, (a.height - sep) / 2);
        for (int k = 0; k < 4; k++) {
            double p = tramo(t, 0.2 + k * 0.35, 0.5);
            int al = (int) (255 * p);
            int x = a.x + (k % 2) * (w + sep);
            int y = a.y + (k / 2) * (h + sep);
            Tema.rectRedondo(g, x, y, w, h, 28, Tema.conAlfa(k % 3 == 0 ? Tema.AZUL_SUAVE : Tema.VERDE_SUAVE, al));
            g.setColor(Tema.conAlfa(Tema.BLANCO, al));
            g.fill(new Ellipse2D.Double(x + 30, y + h / 2.0 - 42, 84, 84));
            Iconos.dibujar(g, (Iconos.Tipo) datos[k][0], x + 72, y + h / 2.0, 42, Tema.conAlfa(Tema.AZUL, al));
            g.setFont(Tema.titulo(30f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, (String) datos[k][1], x + 140, y + h / 2.0 - 14);
            g.setFont(Tema.letra(Font.PLAIN, 18f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, al));
            Tema.parrafo(g, (String) datos[k][2], x + 140, (int) (y + h / 2.0), w - 170, 1.35f);
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
        double alto = Math.min(220, a.height * 0.5);
        for (int k = 0; k < 3; k++) {
            double p = tramo(t, 0.2 + k * 0.4, 0.5);
            int x = a.x + k * (w + sep);
            double cx = x + w / 2.0;
            Tema.rectRedondo(g, x, a.y + alto * 0.35, w, Math.min(a.height - alto * 0.35 - 10, alto * 0.65 + 200), 30,
                    Tema.conAlfa(Tema.AZUL_SUAVE, (int) (255 * p)));
            Personitas.persona(g, (Personitas.Look) datos[k][0], cx, a.y + alto + 30, alto, Cara.FELIZ, p);
            g.setFont(Tema.titulo(26f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * p)));
            Tema.textoCentrado(g, (String) datos[k][1], cx, a.y + alto + 80);
            g.setFont(Tema.letra(Font.PLAIN, 18f));
            g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * p)));
            Tema.parrafo(g, (String) datos[k][2], x + 30, (int) (a.y + alto + 104), w - 60, 1.4f);
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
        Personitas.huella(g, s.getCenterX(), s.y + 182, 54, Tema.AZUL, 1);
        g.setFont(Tema.letra(Font.PLAIN, 11f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.textoCentrado(g, "Pon tu dedo para verificar con RENIEC", s.getCenterX(), s.y + 228);
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
        Iconos.dibujar(g, Iconos.Tipo.ALERTA, s.getCenterX(), s.y + 36, 44, Tema.ROJO);
        g.setFont(Tema.titulo(17f));
        g.setColor(Tema.AZUL_NOCHE);
        Tema.textoCentrado(g, "¿Estás sacando", s.getCenterX(), s.y + 84);
        Tema.textoCentrado(g, "un chip?", s.getCenterX(), s.y + 106);
        g.setFont(Tema.letra(Font.PLAIN, 12f));
        g.setColor(Tema.TEXTO_SUAVE);
        Tema.parrafo(g, "Tienda «Chips al toque», Comas · hoy 3:12 p. m. · 2.º chip con tu DNI", s.x + 6, s.y + 126,
                s.width - 12, 1.35f);
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
        int w = (int) Math.min(a.width * 0.68, 980);
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
        String[][] filas = {{"987 111 222", "Operadora A", "Tienda Miraflores", "2023", "tuyo"},
            {"912 555 935", "Operadora B", "Chips al toque, Comas", "ayer", "?"}};
        int ty = cy + 110;
        String[] cab = {"Número", "Operadora", "Dónde se registró", "Cuándo", ""};
        double[] col = {0, 0.18, 0.36, 0.64, 0.76};
        int ancho = w - 80;
        g.setFont(Tema.letra(Font.BOLD, 13f));
        g.setColor(Tema.conAlfa(Tema.TEXTO_SUAVE, (int) (255 * q)));
        for (int c = 0; c < cab.length; c++) {
            Tema.texto(g, cab[c], cx + ancho * col[c], ty);
        }
        for (int f = 0; f < filas.length; f++) {
            int fy = ty + 16 + f * 56;
            boolean sospechoso = filas[f][4].equals("?");
            Tema.rectRedondo(g, cx - 10, fy, ancho + 20, 46, 12,
                    Tema.conAlfa(sospechoso ? Tema.ROJO_SUAVE : Tema.CELESTE_FONDO, (int) (255 * q)));
            g.setFont(Tema.letra(Font.PLAIN, 15f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, (int) (255 * q)));
            for (int c = 0; c < 4; c++) {
                Tema.texto(g, filas[f][c], cx + ancho * col[c], fy + 29);
            }
            if (sospechoso) {
                boton(g, (int) (cx + ancho * col[4]), fy + 6, 150, "No es mío · Bloquear", Tema.ROJO, Tema.BLANCO);
            } else {
                Tema.pastilla(g, "Es mío", (int) (cx + ancho * col[4]), fy + 9, Tema.VERDE_SUAVE, Tema.VERDE,
                        Tema.letra(Font.BOLD, 13f));
            }
        }
        // a la derecha, a quién le sirve
        int xd = x + w + 40;
        int anchoD = a.x + a.width - xd;
        if (anchoD > 200) {
            Personitas.persona(g, Personitas.VICTIMA, xd + anchoD / 2.0, a.y + 250, 200, Cara.FELIZ, tramo(t, 0.6, 0.4));
            g.setFont(Tema.titulo(22f));
            g.setColor(Tema.AZUL_NOCHE);
            Tema.parrafo(g, "Don Julio no tiene smartphone.", xd, a.y + 290, anchoD, 1.2f);
            g.setFont(Tema.letra(Font.PLAIN, 17f));
            g.setColor(Tema.TEXTO_SUAVE);
            Tema.parrafo(g, "Su nieta entra a la web, pone el DNI y el código que llega por SMS a su celular básico.",
                    xd, a.y + 350, anchoD, 1.4f);
        }
    }

    // ─────────────────────────── 5: comparación ───────────────────────────

    private void comparacion(Graphics2D g, Rectangle a, double t) {
        String[][] filas = {
            {"¿Cuándo te enteras?", "Cuando tú entras a revisar", "Al instante, con una alerta"},
            {"¿El chip ya funciona?", "Sí, ya puede extorsionar", "No: espera tu permiso"},
            {"¿Cómo lo bloqueas?", "Trámite con la operadora", "Un toque: «No fui yo»"},
            {"¿Y la tienda?", "Nadie la reporta", "Reporte automático a OSIPTEL"},
            {"¿Cuánto te cuesta?", "Gratis", "Gratis"}
        };
        int alto = Math.min(78, (a.height - 70) / filas.length);
        int c1 = a.x;
        int c2 = a.x + (int) (a.width * 0.3);
        int c3 = a.x + (int) (a.width * 0.65);
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
            g.setFont(Tema.titulo(21f));
            g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            Tema.texto(g, filas[f][0], c1, y + alto / 2.0 + 8);
            g.setFont(Tema.letra(Font.PLAIN, 19f));
            g.setColor(Tema.conAlfa(Tema.ROJO_OSCURO, f == 4 ? 0 : al));
            if (f == 4) {
                g.setColor(Tema.conAlfa(Tema.AZUL_NOCHE, al));
            }
            Tema.texto(g, filas[f][1], c2, y + alto / 2.0 + 7);
            g.setFont(Tema.letra(Font.BOLD, 19f));
            g.setColor(Tema.conAlfa(Tema.VERDE, al));
            Tema.texto(g, filas[f][2], c3, y + alto / 2.0 + 7);
        }
    }
}
