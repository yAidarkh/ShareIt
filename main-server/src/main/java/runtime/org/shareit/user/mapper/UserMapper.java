package runtime.org.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
