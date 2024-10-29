package runtime.org.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
        return userDto;
    }

    public User toUser(UserDto userDto) {
        User user = new User(
                userDto.getName(),
                userDto.getEmail()
        );
        return user;
    }
}
