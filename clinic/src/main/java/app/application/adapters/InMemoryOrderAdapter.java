package app.application.adapters;

import java.util.HashMap;
import java.util.Map;

import app.domain.models.Order;
import app.domain.ports.OrderPort;

public class InMemoryOrderAdapter implements OrderPort {

    private final Map<Long, Order> storage = new HashMap<>();

    @Override
    public boolean existsByOrderNumber(long orderNumber) {
        return storage.containsKey(orderNumber);
    }

    @Override
    public Order save(Order order) {
        storage.put(order.getId(), order);
        return order;
    }
}


