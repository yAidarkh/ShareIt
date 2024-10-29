package runtime.org.shareit.user.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class User {
    private long id;
    private String name;
    @Email
    private String email;


    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
