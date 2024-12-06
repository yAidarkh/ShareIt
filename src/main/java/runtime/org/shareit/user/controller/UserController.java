package runtime.org.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.service.UserServiceDao;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceDao userService;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на добавление пользователя {}", userDto);
        return userService.add(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя id = {}", userId);
        return userService.findById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Запрос на получение списка всех пользователей");
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя id = {}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Delete - запрос на удаление пользователя id = {}", userId);
        userService.delete(userId);
    }
}
