package runtime.org.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.service.UserServiceDao;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServiceDao userService;

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,@Valid @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
