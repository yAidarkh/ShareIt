package runtime.org.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import runtime.org.shareit.request.model.ItemRequest;
import runtime.org.shareit.user.model.User;

@Getter
@Setter
@AllArgsConstructor
public class Item {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private User owner;
    @NotBlank
    private Boolean available;
    private ItemRequest request;


    public Item(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

}
