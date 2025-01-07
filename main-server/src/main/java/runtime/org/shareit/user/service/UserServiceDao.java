package runtime.org.shareit.user.service;

import runtime.org.shareit.user.dto.UserDto;

import java.util.List;

public interface UserServiceDao {
    UserDto add(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    UserDto findById(Long id);

    void delete(Long id);

    List<UserDto> findAll();
}
