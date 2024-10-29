package runtime.org.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDtoOut {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private Boolean available;
    private Long requestId;
    private Long ownerId;

    public ItemDtoOut(Long id, String name, String description, Boolean available, Long requestId, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
        this.ownerId = ownerId;
    }
}
