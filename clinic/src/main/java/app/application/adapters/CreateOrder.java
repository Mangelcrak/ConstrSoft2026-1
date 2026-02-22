
package app.application.adapters;

import java.util.HashSet;
import java.util.Set;

import app.domain.Exceptions.BusinessException;
import app.domain.models.ItemType;
import app.domain.models.Order;
import app.domain.models.OrderItem;
import app.domain.ports.OrderPort;

public class CreateOrder {

    private final OrderPort orderPort;

    public CreateOrder(OrderPort orderPort) {
        this.orderPort = orderPort;
    }

    public Order create(Order order) {
        validateOrderNumber(order);
        validateUniqueOrderNumber(order);
        validateBusinessRules(order);

        return orderPort.save(order);
    }

    private void validateOrderNumber(Order order) {
        long orderNumber = order.getId(); // si tu Order usa otro campo, cámbialo aquí

        if (orderNumber <= 0) {
            throw new BusinessException("El número de orden debe ser mayor a 0.");
        }

        // máximo 6 dígitos -> 0 a 999999 (pero sin 0)
        if (orderNumber > 999999) {
            throw new BusinessException("El número de orden no puede tener más de 6 dígitos.");
        }
    }

    private void validateUniqueOrderNumber(Order order) {
        long orderNumber = order.getId();

        if (orderPort.existsByOrderNumber(orderNumber)) {
            throw new BusinessException("Ya existe una orden con el número: " + orderNumber);
        }
    }

    private void validateBusinessRules(Order order) {
        OrderItem[] items = order.getOrderItems(); // ajusta el getter si se llama distinto

        if (items == null || items.length == 0) {
            throw new BusinessException("La orden debe tener al menos un ítem.");
        }

        boolean hasMedicalSupport = false;
        boolean hasMedicine = false;
        boolean hasProcedure = false;

        // Regla: no repetir ítems dentro de la misma orden
        // Si tu OrderItem tiene "id" como número de ítem, usamos eso.
        // Si tienes otro campo, cámbialo en uniqueKey.
        Set<String> unique = new HashSet<>();

        for (OrderItem oi : items) {
            if (oi == null) continue;

            // Ajusta según tu modelo:
            // - si OrderItem tiene getItemType()
            // - o si apunta a Item y el tipo está en Item
            ItemType type = oi.getItemType();  // <-- ajusta esto si tu modelo es diferente

            if (type == ItemType.MEDICALSUPPORT) hasMedicalSupport = true;
            if (type == ItemType.MEDICINE) hasMedicine = true;
            if (type == ItemType.PROCEDURE) hasProcedure = true;

            // Unicidad orden_ítem: usa el identificador del ítem.
            // Si tienes oi.getId() úsalo; si tienes oi.getItem().getId() usa ese.
            // Aquí dejo ejemplo con oi.getId():
            String uniqueKey = String.valueOf(oi.getId());  // <-- ajusta aquí

            if (!unique.add(uniqueKey)) {
                throw new BusinessException("Ítem repetido en la orden: " + uniqueKey);
            }
        }

        // Regla: si hay examen, no puede haber meds/procedimientos
        if (hasMedicalSupport && (hasMedicine || hasProcedure)) {
            throw new BusinessException("Si la orden tiene ayuda diagnóstica, no puede incluir medicamentos ni procedimientos.");
        }
    }
}

