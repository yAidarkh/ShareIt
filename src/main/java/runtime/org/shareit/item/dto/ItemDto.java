package runtime.org.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private Boolean available;
    private Long requestId;
    private Long ownerId;

    public ItemDto(String name, Boolean available, String description) {
        this.name = name;
        this.available = available;
        this.description = description;
    }
}
