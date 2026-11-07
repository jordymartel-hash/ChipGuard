package simuchip.caso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import simuchip.funciones.Flecha;
import simuchip.funciones.Funciones;

/**
 * El caso real modelado con funciones.
 *
 *   L = líneas, P = personas (DNI), C = contratos con huella.
 *   f: L → P  registro      (lo que dice la operadora)
 *   u: L → P  uso real      (quién usa de verdad el chip)
 *   g: L → C  contrato      (solo existe con biometría)
 *
 * El problema: hay líneas con f(l) ≠ u(l).
 * La solución: que g sea BIYECTIVA y que la huella del contrato sea u(l).
 */
public final class Simulador {

    public static final int MAX_LINEAS = 16;
    public static final double PROBABILIDAD_FRAUDE = 0.4;
    private static final String[] NOMBRES = {"Ana", "Beto", "Carla", "Diego", "Elena", "Fabio"};

    private final long semilla;
    private Random azar;

    private final List<Persona> personas = new ArrayList<>();
    private final Persona extorsionador;

    private final List<Linea> lineas = new ArrayList<>();
    private final Map<Linea, Persona> registro = new LinkedHashMap<>();   // f
    private final Map<Linea, Persona> usoReal = new LinkedHashMap<>();    // u
    private final Map<Linea, Contrato> contratos = new LinkedHashMap<>(); // g
    private final List<Evento> eventos = new ArrayList<>();

    private boolean biometria;
    private boolean topeActivo;
    private final int tope = 3;
    private int bloqueados;
    private int rechazosTope;
    private int dia;
    private int siguienteLinea;
    private int siguienteContrato;

    public Simulador(long semilla) {
        this.semilla = semilla;
        for (int i = 0; i < NOMBRES.length; i++) {
            personas.add(new Persona("4" + (1234560 + i * 1111), NOMBRES[i], false));
        }
        extorsionador = new Persona("????????", "Extorsionador", true);
        personas.add(extorsionador);
        reiniciar();
    }

    // ─────────────────────────── acciones ───────────────────────────

    /** Una persona contrata una línea con su propio DNI. */
    public Resultado contratarLegal(Persona persona) {
        if (lineas.size() >= MAX_LINEAS) {
            return Resultado.LLENO;
        }
        if (topeActivo && lineasDe(persona).size() >= tope) {
            rechazosTope++;
            anotar(Evento.Tipo.TOPE, persona + " ya tiene " + tope + " líneas: el tope rechaza una más");
            return Resultado.RECHAZADA_TOPE;
        }
        Linea linea = crearLinea(persona, persona);
        anotar(Evento.Tipo.LEGAL, persona + " contrató " + linea + (biometria ? " con su huella" : ""));
        return Resultado.ACEPTADA;
    }

    /** El extorsionador intenta sacar una línea con el DNI de una víctima. */
    public Resultado contratarFraude(Persona victima) {
        if (lineas.size() >= MAX_LINEAS) {
            return Resultado.LLENO;
        }
        if (biometria) {
            bloqueados++;
            anotar(Evento.Tipo.BLOQUEO, "Huella distinta a la de " + victima + ": contratación fraudulenta bloqueada");
            return Resultado.RECHAZADA_BIOMETRIA;
        }
        if (topeActivo && lineasDe(victima).size() >= tope) {
            rechazosTope++;
            anotar(Evento.Tipo.TOPE, "Fraude con DNI de " + victima + " frenado por el tope de " + tope);
            return Resultado.RECHAZADA_TOPE;
        }
        Linea linea = crearLinea(victima, extorsionador);
        anotar(Evento.Tipo.FRAUDE, linea + " registrada a nombre de " + victima + ", pero la usa el extorsionador");
        return Resultado.ACEPTADA;
    }

    /** Avanza un día: alguien intenta contratar una línea (a veces es fraude). */
    public Resultado paso() {
        if (lineas.size() >= MAX_LINEAS) {
            anotar(Evento.Tipo.INFO, "Ya hay " + MAX_LINEAS + " líneas: reinicia para seguir simulando");
            return Resultado.LLENO;
        }
        dia++;
        Persona persona = personas.get(azar.nextInt(NOMBRES.length));
        if (azar.nextDouble() < PROBABILIDAD_FRAUDE) {
            return contratarFraude(persona);
        }
        return contratarLegal(persona);
    }

    /** Revisión biométrica de todas las líneas: baja a las que mienten y da contrato a las demás. */
    public int auditoria() {
        int bajas = 0;
        for (Linea linea : new ArrayList<>(lineas)) {
            if (!registro.get(linea).equals(usoReal.get(linea))) {
                lineas.remove(linea);
                registro.remove(linea);
                usoReal.remove(linea);
                contratos.remove(linea);
                bajas++;
            } else if (!contratos.containsKey(linea)) {
                contratos.put(linea, new Contrato(++siguienteContrato, usoReal.get(linea)));
            }
        }
        anotar(Evento.Tipo.AUDITORIA, "Auditoría biométrica: " + bajas + " líneas dadas de baja, "
                + lineas.size() + " con contrato");
        return bajas;
    }

    /** Lo que haría la policía al investigar una línea. */
    public Rastreo rastrear(Linea linea) {
        return new Rastreo(linea, registro.get(linea), usoReal.get(linea), contratos.get(linea));
    }

    public void reiniciar() {
        azar = new Random(semilla);
        lineas.clear();
        registro.clear();
        usoReal.clear();
        contratos.clear();
        eventos.clear();
        biometria = false;
        topeActivo = false;
        bloqueados = 0;
        rechazosTope = 0;
        dia = 0;
        siguienteLinea = 0;
        siguienteContrato = 0;
    }

    // ─────────────────────────── funciones f, u, g ───────────────────────────

    public List<Flecha<Linea, Persona>> flechasRegistro() {
        return flechas(registro);
    }

    public List<Flecha<Linea, Persona>> flechasUso() {
        return flechas(usoReal);
    }

    public List<Flecha<Linea, Contrato>> flechasContrato() {
        return flechas(contratos);
    }

    /** f⁻¹(persona): las líneas registradas a su nombre. Usa la preimagen de Funciones. */
    public List<Linea> lineasDe(Persona persona) {
        return Funciones.preimagen(persona, flechasRegistro());
    }

    // ─────────────────────────── métricas ───────────────────────────

    /** Líneas donde el registro miente: f(l) ≠ u(l). */
    public List<Linea> inconsistentes() {
        List<Linea> resultado = new ArrayList<>();
        for (Linea linea : lineas) {
            if (!registro.get(linea).equals(usoReal.get(linea))) {
                resultado.add(linea);
            }
        }
        return resultado;
    }

    /** Porcentaje de líneas donde registro = realidad. */
    public double coincidencia() {
        if (lineas.isEmpty()) {
            return 100.0;
        }
        return (lineas.size() - inconsistentes().size()) * 100.0 / lineas.size();
    }

    public List<Persona> personasSobreTope() {
        List<Persona> resultado = new ArrayList<>();
        for (Persona persona : personas) {
            if (lineasDe(persona).size() > tope) {
                resultado.add(persona);
            }
        }
        return resultado;
    }

    // ─────────────────────────── consultas ───────────────────────────

    public Persona persona(String nombre) {
        for (Persona persona : personas) {
            if (persona.nombre().equals(nombre)) {
                return persona;
            }
        }
        throw new IllegalArgumentException("No existe la persona " + nombre);
    }

    public List<Persona> personas() {
        return Collections.unmodifiableList(personas);
    }

    public List<Linea> lineas() {
        return Collections.unmodifiableList(lineas);
    }

    public Set<Contrato> contratos() {
        return new HashSet<>(contratos.values());
    }

    public Persona titular(Linea linea) {
        return registro.get(linea);
    }

    public Persona usuarioReal(Linea linea) {
        return usoReal.get(linea);
    }

    public Contrato contrato(Linea linea) {
        return contratos.get(linea);
    }

    public Persona extorsionador() {
        return extorsionador;
    }

    public List<Evento> eventos() {
        return Collections.unmodifiableList(eventos);
    }

    public boolean biometria() {
        return biometria;
    }

    public void setBiometria(boolean activa) {
        biometria = activa;
        anotar(Evento.Tipo.INFO, "Validación biométrica " + (activa ? "ACTIVADA" : "desactivada"));
    }

    public boolean topeActivo() {
        return topeActivo;
    }

    public void setTopeActivo(boolean activo) {
        topeActivo = activo;
        anotar(Evento.Tipo.INFO, "Tope de " + tope + " líneas por persona " + (activo ? "ACTIVADO" : "desactivado"));
    }

    public int tope() {
        return tope;
    }

    public int bloqueados() {
        return bloqueados;
    }

    public int rechazosTope() {
        return rechazosTope;
    }

    public int dia() {
        return dia;
    }

    // ─────────────────────────── ayudantes ───────────────────────────

    private Linea crearLinea(Persona titular, Persona usuario) {
        siguienteLinea++;
        Linea linea = new Linea(siguienteLinea, "987 654 " + (100 + siguienteLinea));
        lineas.add(linea);
        registro.put(linea, titular);
        usoReal.put(linea, usuario);
        if (biometria) {
            contratos.put(linea, new Contrato(++siguienteContrato, usuario));
        }
        return linea;
    }

    private void anotar(Evento.Tipo tipo, String texto) {
        eventos.add(new Evento(dia, tipo, texto));
    }

    private static <A, B> List<Flecha<A, B>> flechas(Map<A, B> mapa) {
        List<Flecha<A, B>> resultado = new ArrayList<>();
        for (Map.Entry<A, B> par : mapa.entrySet()) {
            resultado.add(new Flecha<>(par.getKey(), par.getValue()));
        }
        return resultado;
    }
}
