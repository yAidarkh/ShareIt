package runtime.org.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import runtime.org.shareit.request.dto.ItemRequestDto;
import runtime.org.shareit.request.dto.ItemRequestDtoOut;
import runtime.org.shareit.request.service.ItemRequestServiceDao;

import java.util.List;

import static runtime.org.shareit.item.controller.ItemController.USER_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestServiceDao requestService;

    @PostMapping
    public ItemRequestDtoOut add(@RequestHeader(USER_HEADER) Long userId,
                                 @RequestBody ItemRequestDto requestDto) {
        return requestService.add(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getUserRequests(@RequestHeader(USER_HEADER) Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut get(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
