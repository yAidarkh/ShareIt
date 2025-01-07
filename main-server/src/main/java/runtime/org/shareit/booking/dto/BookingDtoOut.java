package runtime.org.shareit.booking.dto;

import lombok.*;
import runtime.org.shareit.booking.model.BookingStatus;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoOut {
    private Long id;
    private ItemDtoOut item;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private BookingStatus status;

    public Long getItemId() {
        return item.getId();
    }

    public long getBookerId() {
        return booker.getId();
    }
}
