package simuchip;

import static simuchip.Prueba.*;

import simuchip.caso.Detective;
import simuchip.caso.Detective.Decision;
import simuchip.caso.Solicitud;

/** Pruebas del juego Detective: reglas, puntaje y vidas. */
public final class PruebasDetective {

    public static void main(String[] args) {
        titulo("Reglas de una solicitud");
        Solicitud buena = new Solicitud(1, "Ana", "41234560", "Tienda", "10:00", 1, true, true);
        verificar(!buena.debeRechazarse(), "huella OK, DNI original y 1 línea → se aprueba");
        igual("Todo en orden: es la dueña o el dueño real", buena.motivo(), "motivo de la buena");
        Solicitud huella = new Solicitud(2, "Beto", "4", "T", "3:10", 0, false, true);
        verificar(huella.debeRechazarse(), "huella que no coincide → rechazar");
        igual("La huella no coincide con el DNI", huella.motivo(), "motivo huella");
        Solicitud copia = new Solicitud(3, "Carla", "4", "T", "9:00", 0, true, false);
        igual("Presentó una fotocopia del DNI", copia.motivo(), "motivo fotocopia");
        Solicitud tope = new Solicitud(4, "Diego", "4", "T", "9:00", 3, true, true);
        verificar(tope.debeRechazarse(), "ya tiene 3 líneas → rechazar por tope");
        verificar(tope.motivo().contains("tope"), "el motivo menciona el tope");
        verificar(!new Solicitud(5, "E", "4", "T", "9:00", 2, true, true).debeRechazarse(),
                "2 líneas todavía está bajo el tope (parece pero no es)");

        titulo("Motivo dentro de una frase");
        igual("la huella no coincide con el DNI", huella.motivoEnFrase(), "solo la inicial va en minúscula, «DNI» se queda");
        igual("presentó una fotocopia del DNI", copia.motivoEnFrase(), "no escribe «dni» en minúsculas");

        titulo("Puntaje y vidas");
        Detective d = new Detective(7);
        Solicitud s = d.actual();
        Decision ok = d.decidir(!s.debeRechazarse());
        verificar(ok.acierto(), "decidir lo correcto es acierto");
        igual(10, d.puntaje(), "acierto suma 10");
        igual(3, d.vidas(), "las vidas no bajan al acertar");
        verificar(d.actual().numero() == s.numero() + 1, "después de decidir llega la siguiente solicitud");
        Solicitud s2 = d.actual();
        Decision mal = d.decidir(s2.debeRechazarse());
        verificar(!mal.acierto(), "decidir al revés es error");
        igual(2, d.vidas(), "un error quita una vida");
        verificar(!mal.explicacion().isBlank(), "el error trae explicación");
        d.decidir(d.actual().debeRechazarse());
        d.decidir(d.actual().debeRechazarse());
        verificar(d.terminado(), "con 3 errores se acaba el juego");
        igual(0, d.vidas(), "vidas en 0");

        titulo("Racha");
        Detective r = new Detective(11);
        for (int i = 0; i < 3; i++) {
            r.decidir(!r.actual().debeRechazarse());
        }
        igual(35, r.puntaje(), "3 aciertos seguidos: 10 + 10 + 15 (bono de racha)");

        titulo("Generador equilibrado");
        Detective g = new Detective(3);
        int malas = 0;
        for (int i = 0; i < 200; i++) {
            if (g.generar().debeRechazarse()) {
                malas++;
            }
        }
        verificar(malas > 70 && malas < 130, "entre 35 % y 65 % de solicitudes son para rechazar (" + malas + "/200)");

        titulo("Reinicio");
        d.reiniciar();
        igual(3, d.vidas(), "reiniciar devuelve las 3 vidas");
        igual(0, d.puntaje(), "y el puntaje a 0");
        verificar(!d.terminado(), "y el juego vuelve a empezar");

        terminar();
    }
}
