package simuchip;

import static simuchip.Prueba.*;

import java.util.List;
import simuchip.caso.Tienda;
import simuchip.caso.Tienda.Evento;

/** Pruebas del juego de la tienda: lo que pasa sin y con ChipGuard. */
public final class PruebasTienda {

    public static void main(String[] args) {
        titulo("Sin ChipGuard");
        Tienda sin = new Tienda(Tienda.filaDelDia(), false);
        igual(List.of(Evento.VENTA_OK), sin.decidir(true), "Luis es el dueño: venta normal");
        igual(List.of(Evento.FRAUDE_PASO), sin.decidir(true), "vender al encapuchado con DNI de Pedro: fraude pasa");
        igual(List.of(Evento.VENTA_OK, Evento.EXTRA_PASO), sin.decidir(true), "Rosa compra, el vendedor saca un extra");
        igual(List.of(Evento.RECHAZO_MAL), sin.decidir(false), "rechazar a Carmen, la dueña: cliente perdido");
        igual(List.of(Evento.RECHAZO_BIEN), sin.decidir(false), "rechazar a la impostora: atrapada");
        sin.decidir(true);
        verificar(sin.terminado(), "la fila termina tras 6 clientes");
        igual(3, sin.enManosCriminales(), "3 chips terminan en manos criminales (1 venta al impostor + 2 extras)");
        igual(1, sin.atrapados(), "1 impostora atrapada");
        igual(1, sin.perdidos(), "1 cliente honesto perdido");

        Tienda perfecta = new Tienda(Tienda.filaDelDia(), false);
        for (boolean vender : new boolean[] {true, false, true, true, false, true}) {
            perfecta.decidir(vender);
        }
        igual(2, perfecta.enManosCriminales(), "decidiendo TODO bien, los 2 chips extra igual llegan a criminales");

        titulo("Con ChipGuard");
        Tienda con = new Tienda(Tienda.filaDelDia(), true);
        for (int i = 0; i < 6; i++) {
            con.decidir(true);
        }
        igual(0, con.enManosCriminales(), "vendiendo a todos, ningún chip llega a criminales");
        igual(4, con.bloqueados(), "2 impostores + 2 extras bloqueados por los dueños");
        igual(4, con.vendidos(), "los 4 dueños reales sí reciben su chip");

        titulo("Bordes");
        try {
            con.decidir(true);
            verificar(false, "decidir con la fila vacía lanza error");
        } catch (IllegalStateException e) {
            verificar(true, "decidir con la fila vacía lanza error");
        }
        verificar(new Tienda.Cliente("Ana", "Ana", false).esElDueno(), "Ana con su DNI es la dueña");
        verificar(!new Tienda.Cliente("Encapuchado", "Pedro", false).esElDueno(), "DNI ajeno no es el dueño");
        terminar();
    }
}
