package runtime.org.shareit.exceptions.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    String error;

    String description;
}
