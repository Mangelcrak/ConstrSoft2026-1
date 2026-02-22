package app.domain.models;

import java.sql.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Order {

private long id;            // este será el número de orden (máx 6 dígitos)
private Patient patient;
private User doctor;
private Date date;
private OrderItem[] orderItems;

// metodo para obtener el número de orden (id)
public long getOrderNumber() {
return id;
}
}

