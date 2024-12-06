package runtime.org.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoOut {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
    private Long itemId;
}
