package app.domain.ports;

import app.domain.models.Order;

public interface OrderPort {
boolean existsByOrderNumber(long orderNumber);
Order save(Order order);
}


