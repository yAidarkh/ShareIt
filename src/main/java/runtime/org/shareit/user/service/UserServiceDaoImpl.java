package runtime.org.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import runtime.org.shareit.exceptions.EmailAlreadyExistException;
import runtime.org.shareit.exceptions.EmailIsNullException;
import runtime.org.shareit.exceptions.UserNotFoundException;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.model.User;
import runtime.org.shareit.user.mapper.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceDaoImpl implements UserServiceDao {
    private static final Map<Long, User> users = new HashMap<Long, User>();

    private static long idCounter = users.size()+1;

    @Override
    public UserDto add(UserDto userDto) {
        if (null == userDto.getEmail()) {
            throw new EmailIsNullException("email is null");
        }
        if (emailExists(userDto.getEmail())) {
            throw new EmailAlreadyExistException("Email already exists");
        }
        User user = UserMapper.toUser(userDto);
        user.setId(generateId());
        users.put(user.getId(), user);
        userDto.setId(user.getId());
        return userDto;
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = UserMapper.toUser(findById(id));
        String name = userDto.getName();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        String email = userDto.getEmail();
        if (email != null && !email.isBlank()) {
            if (email.equals(user.getEmail())) {
                user.setEmail(email);
            }else if (emailExists(email)) {
                throw new EmailAlreadyExistException("Email already exists");
            }
            user.setEmail(email);
        }
        user.setId(id);
        users.put(id, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public UserDto findById(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователя с " + id + " не существует");
        }
        User user = users.get(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public static long generateId() {
        return idCounter++;
    }

    public boolean emailExists(String email) {
        List<String> emails = users.values().stream().map(User::getEmail).collect(Collectors.toList());
        if (emails.contains(email)) {
            return true;
        }
        return false;
    }
}
