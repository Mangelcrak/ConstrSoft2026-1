package app.domain.models;

import app.domain.Exceptions.BusinessException;
import app.domain.ports.OrderPort;

import java.util.HashSet;
import java.util.Set;


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
        long orderNumber = order.getId();

        if (orderNumber <= 0) {
            throw new BusinessException("El número de orden debe ser mayor a 0.");
        }

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
        OrderItem[] items = order.getOrderItems();

        if (items == null || items.length == 0) {
            throw new BusinessException("La orden debe tener al menos un ítem.");
        }

        boolean hasMedicalSupport = false;
        boolean hasMedicine = false;
        boolean hasProcedure = false;

        // Regla correcta: no repetir el mismo Item en la orden
        Set<Long> uniqueItemIds = new HashSet<>();

        for (OrderItem oi : items) {
            if (oi == null) {
                throw new BusinessException("La orden contiene un ítem nulo.");
            }

            ItemType type = oi.getItemType();
            if (type == null) {
                throw new BusinessException("Cada ítem debe tener un tipo (ItemType).");
            }

            if (type == ItemType.MEDICALSUPPORT) hasMedicalSupport = true;
            if (type == ItemType.MEDICINE) hasMedicine = true;
            if (type == ItemType.PROCEDURE) hasProcedure = true;
            
            Item item = (Item) oi.getItem();
            if (item == null) {
                throw new BusinessException("Cada OrderItem debe tener un Item asociado.");
            }

            long itemId = item.getId();
            if (itemId <= 0) {
                throw new BusinessException("El Item debe tener un id válido.");
            }

            if (!uniqueItemIds.add(itemId)) {
                throw new BusinessException(
                    "El ítem '" + item.getValue() + "' está repetido en la orden."
                );
            }
        }

        // Regla del PDF: examen NO puede ir con medicamentos ni procedimientos
        if (hasMedicalSupport && (hasMedicine || hasProcedure)) {
            throw new BusinessException(
                "Si la orden tiene ayuda diagnóstica, no puede incluir medicamentos ni procedimientos."
            );
        }
    }
}

