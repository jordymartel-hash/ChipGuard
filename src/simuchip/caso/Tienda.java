package simuchip.caso;

import java.util.ArrayList;
import java.util.List;

/**
 * El juego de la expo: el público atiende una tienda de chips y decide a quién venderle.
 * Sin ChipGuard, el fraude que no se ve (impostor con DNI ajeno, chip extra del vendedor) pasa.
 * Con ChipGuard, el dueño del DNI recibe la alerta en su celular y lo bloquea.
 */
public final class Tienda {

    /** Quien está en el mostrador, de quién es el DNI que entrega y si el vendedor saca un chip extra. */
    public record Cliente(String nombre, String dniDe, boolean vendedorSacaExtra) {

        public boolean esElDueno() {
            return nombre.equals(dniDe);
        }
    }

    public enum Evento { VENTA_OK, RECHAZO_BIEN, RECHAZO_MAL, FRAUDE_PASO, FRAUDE_BLOQUEADO, EXTRA_PASO, EXTRA_BLOQUEADO }

    private final List<Cliente> fila;
    private final boolean chipGuard;
    private int turno;
    private int vendidos;
    private int enManosCriminales;
    private int bloqueados;
    private int atrapados;
    private int perdidos;

    public Tienda(List<Cliente> fila, boolean chipGuard) {
        this.fila = List.copyOf(fila);
        this.chipGuard = chipGuard;
    }

    /** La misma fila para las dos rondas, así la comparación es justa. */
    public static List<Cliente> filaDelDia() {
        return List.of(
            new Cliente("Luis", "Luis", false),
            new Cliente("Encapuchado", "Pedro", false),
            new Cliente("Rosa", "Rosa", true),
            new Cliente("Carmen", "Carmen", false),
            new Cliente("Impostora", "Juana", false),
            new Cliente("Jorge", "Jorge", true)
        );
    }

    public Cliente actual() {
        return terminado() ? null : fila.get(turno);
    }

    public boolean terminado() {
        return turno >= fila.size();
    }

    /** El público decide vender o no al cliente del mostrador; devuelve lo que pasó, en orden. */
    public List<Evento> decidir(boolean vender) {
        if (terminado()) {
            throw new IllegalStateException("La fila ya terminó");
        }
        Cliente c = fila.get(turno++);
        List<Evento> eventos = new ArrayList<>();
        if (!vender) {
            if (c.esElDueno()) {
                perdidos++;
                eventos.add(Evento.RECHAZO_MAL);
            } else {
                atrapados++;
                eventos.add(Evento.RECHAZO_BIEN);
            }
            return eventos;
        }
        if (!c.esElDueno()) {
            eventos.add(fraude(Evento.FRAUDE_BLOQUEADO, Evento.FRAUDE_PASO));
            return eventos;
        }
        vendidos++;
        eventos.add(Evento.VENTA_OK);
        if (c.vendedorSacaExtra()) {
            eventos.add(fraude(Evento.EXTRA_BLOQUEADO, Evento.EXTRA_PASO));
        }
        return eventos;
    }

    private Evento fraude(Evento siBloquea, Evento siPasa) {
        if (chipGuard) {
            bloqueados++;
            return siBloquea;
        }
        enManosCriminales++;
        return siPasa;
    }

    public boolean chipGuard() { return chipGuard; }
    public int turno() { return turno; }
    public int total() { return fila.size(); }
    public int vendidos() { return vendidos; }
    public int enManosCriminales() { return enManosCriminales; }
    public int bloqueados() { return bloqueados; }
    public int atrapados() { return atrapados; }
    public int perdidos() { return perdidos; }
}
