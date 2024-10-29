package runtime.org.shareit.user.dto;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String name;
    @Email
    private String email;
}
