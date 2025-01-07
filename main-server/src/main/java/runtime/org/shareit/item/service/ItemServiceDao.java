package runtime.org.shareit.item.service;

import runtime.org.shareit.item.dto.CommentDto;
import runtime.org.shareit.item.dto.CommentDtoOut;
import runtime.org.shareit.item.dto.ItemDto;
import runtime.org.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemServiceDao {
    ItemDtoOut add(Long userId, ItemDto itemDto);

    ItemDtoOut update(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoOut findItemById(Long userId, Long itemId);

    List<ItemDtoOut> findAll(Long userId, Integer from, Integer size);

    List<ItemDtoOut> search(Long userId, String text, Integer from, Integer size);

    CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId);
}
