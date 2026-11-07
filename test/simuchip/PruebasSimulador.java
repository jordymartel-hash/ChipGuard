package simuchip;

import static simuchip.Prueba.*;

import java.util.HashSet;
import simuchip.caso.Linea;
import simuchip.caso.Persona;
import simuchip.caso.Rastreo;
import simuchip.caso.Resultado;
import simuchip.caso.Simulador;
import simuchip.funciones.Funciones;

/** Pruebas del caso real: líneas, titulares, fraude, biometría y tope. */
public final class PruebasSimulador {

    public static void main(String[] args) {
        titulo("Contratación legal");
        Simulador s = new Simulador(7);
        Persona ana = s.persona("Ana");
        igual(Resultado.ACEPTADA, s.contratarLegal(ana), "Ana contrata una línea");
        Linea l1 = s.lineas().get(0);
        igual(ana, s.titular(l1), "f(línea) = Ana (registro)");
        igual(ana, s.usuarioReal(l1), "u(línea) = Ana (uso real)");
        igual(100.0, s.coincidencia(), "registro y realidad coinciden 100 %");

        titulo("Fraude con DNI ajeno");
        igual(Resultado.ACEPTADA, s.contratarFraude(ana), "el extorsionador saca una línea con el DNI de Ana");
        Linea l2 = s.lineas().get(1);
        igual(ana, s.titular(l2), "el registro dice que es de Ana");
        igual(s.extorsionador(), s.usuarioReal(l2), "pero la usa el extorsionador");
        igual(1, s.inconsistentes().size(), "1 línea con f ≠ u");
        igual(50.0, s.coincidencia(), "coincidencia baja a 50 %");
        Rastreo r = s.rastrear(l2);
        verificar(!r.culpableEncontrado(), "rastrear sin biometría NO encuentra al culpable");
        igual(ana, r.personaSenalada(), "y señala a la víctima (Ana)");
        verificar(Funciones.esFuncion(new HashSet<>(s.lineas()), s.flechasRegistro()),
                "el registro sigue siendo función aunque mienta");
        verificar(!Funciones.esInyectiva(new HashSet<>(s.lineas()), new HashSet<>(s.personas()), s.flechasRegistro()),
                "Ana tiene 2 líneas → el registro NO es inyectiva (y eso es normal)");

        titulo("Biometría");
        s.setBiometria(true);
        igual(Resultado.RECHAZADA_BIOMETRIA, s.contratarFraude(ana), "con biometría la huella no coincide → rechazada");
        igual(1, s.bloqueados(), "1 intento bloqueado");
        igual(2, s.lineas().size(), "no se creó ninguna línea nueva");
        igual(Resultado.ACEPTADA, s.contratarLegal(s.persona("Beto")), "Beto contrata con su huella");
        Linea l3 = s.lineas().get(2);
        verificar(s.contrato(l3) != null, "la línea de Beto nace con contrato biométrico");

        titulo("Tope de líneas por persona (palomar)");
        Simulador t = new Simulador(1);
        t.setTopeActivo(true);
        Persona carla = t.persona("Carla");
        for (int i = 0; i < t.tope(); i++) {
            t.contratarLegal(carla);
        }
        igual(Resultado.RECHAZADA_TOPE, t.contratarLegal(carla), "Carla ya tiene k líneas → la siguiente se rechaza");
        igual(Resultado.RECHAZADA_TOPE, t.contratarFraude(carla), "un fraude con su DNI también choca con el tope");
        igual(t.tope(), t.lineasDe(carla).size(), "|f⁻¹(Carla)| = k");
        verificar(t.personasSobreTope().isEmpty(), "con tope nadie lo supera");

        titulo("Auditoría biométrica → biyección");
        int bajas = s.auditoria();
        igual(1, bajas, "se da de baja la línea fraudulenta");
        igual(100.0, s.coincidencia(), "ahora registro = realidad");
        verificar(Funciones.esBiyectiva(new HashSet<>(s.lineas()), s.contratos(), s.flechasContrato()),
                "g: Líneas → Contratos es BIYECTIVA");
        Rastreo r2 = s.rastrear(s.lineas().get(0));
        verificar(r2.culpableEncontrado(), "con contrato, g⁻¹ lleva a la persona real");

        titulo("Simulación automática");
        Simulador x = new Simulador(42);
        Simulador y = new Simulador(42);
        for (int i = 0; i < 6; i++) {
            x.paso();
            y.paso();
        }
        igual(x.lineas().size(), y.lineas().size(), "misma semilla → mismo resultado (reproducible)");
        verificar(x.lineas().size() > 0, "después de 6 días hay líneas");
        verificar(!x.eventos().isEmpty(), "cada día deja un evento en la bitácora");

        titulo("Capacidad del dibujo");
        Simulador lleno = new Simulador(3);
        Persona diego = lleno.persona("Diego");
        for (int i = 0; i < Simulador.MAX_LINEAS; i++) {
            lleno.contratarLegal(diego);
        }
        igual(Resultado.LLENO, lleno.contratarLegal(diego), "pasado el máximo no entra otra línea");

        titulo("Reiniciar");
        s.reiniciar();
        igual(0, s.lineas().size(), "reiniciar deja todo en cero");
        igual(0, s.bloqueados(), "y el contador de bloqueos también");

        terminar();
    }
}
