package runtime.org.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import runtime.org.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequest {
    private long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
