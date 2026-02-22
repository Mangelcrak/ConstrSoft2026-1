package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OrderItem {
    private long id;
    private Item item;      // ← este Item es el del mismo package
    private ItemType itemType;
}

