package runtime.org.shareit.item.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import runtime.org.shareit.item.dto.ItemDto;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.item.service.ItemServiceDao;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    @Autowired
    private ItemServiceDao itemService;

    @PostMapping
    public ItemDtoOut add(@RequestHeader(USER_HEADER) Long userId,
                       @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut update(@RequestHeader(USER_HEADER) Long userId,
                             @RequestBody ItemDto itemDto,
                             @PathVariable Long itemId) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut findById(@RequestHeader(USER_HEADER) Long userId,
                               @PathVariable("itemId") Long itemId) {
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoOut> findAll(@RequestHeader(USER_HEADER) Long userId,
                                 @RequestParam(value = "from", defaultValue = "0") Integer from,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return itemService.findAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoOut> searchItems(@RequestHeader(USER_HEADER) Long userId,
                                        @RequestParam(name = "text") String text,
                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemService.search(userId, text);
    }
}
