package runtime.org.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import runtime.org.shareit.exceptions.*;
import runtime.org.shareit.item.dto.ItemDto;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.item.mapper.ItemMapper;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.user.mapper.UserMapper;
import runtime.org.shareit.user.model.User;
import runtime.org.shareit.user.service.UserServiceDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceDaoImpl implements ItemServiceDao {
    private static final Map<Long, Item> items = new HashMap<>();
    private final UserServiceDao userService;

    private static long idCounter = items.size()+1;

    @Override
    public ItemDtoOut add(Long userId, ItemDto itemDto) {
        if (userService.findById(userId) == null) {
            throw new UserNotFoundException("User with id:{"+userId+"} not found");
        }
        if (itemDto.getAvailable() == null) {
            throw new AvailableIsNullExceptions("Available is null");
        }
        if (itemDto.getName().isBlank()) {
            throw new NameIsNullException("Name is null");
        }
        if (itemDto.getDescription() == null) {
            throw new DescriptionIsNullException("Description is null");
        }
        User user = UserMapper.toUser(userService.findById(userId));
        user.setId(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item.setId(generateId());
        items.put(item.getId(), item);
        return ItemMapper.toItemDtoOut(item);
    }

    @Override
    public ItemDtoOut update(Long userId, Long itemId, ItemDto itemDto) {
        ItemDtoOut itemDtoOut = findItemById(userId,itemId);
        long ownerId = itemDtoOut.getOwnerId();
        User itemOwner = UserMapper.toUser(userService.findById(ownerId));
        itemOwner.setId(ownerId);
        Item item = ItemMapper.toItem(itemDtoOut);
        item.setOwner(itemOwner);
        if (!userId.equals(item.getOwner().getId())) {
            throw new UserNotOwnsItemException("Пользователь с id = " + userId +
                    " не является собственником вещи id = " + itemId);
        }
        Boolean isAvailable = itemDto.getAvailable();
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
        }
        String description = itemDto.getDescription();
        if (description != null && !description.isBlank()) {
            item.setDescription(description);
        }
        String name = itemDto.getName();
        if (name != null && !name.isBlank()) {
            item.setName(name);
        }
        items.put(item.getId(), item);
        return ItemMapper.toItemDtoOut(item);
    }

    @Override
    public ItemDtoOut findItemById(Long userId, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещи с " + itemId + " не существует");
        }
        return ItemMapper.toItemDtoOut(items.get(itemId));
    }

    @Override
    public List<ItemDtoOut> findAll(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDtoOut> search(Long userId, String text) {
        String textLower = text.toLowerCase();
        return items.values()
                .stream().filter(item -> item.getDescription() !=null && item.getDescription().toLowerCase().contains(textLower) && !text.isBlank() && !text.isEmpty() && item.getAvailable())
                .map(ItemMapper::toItemDtoOut)
                .collect(Collectors.toList());
    }

    public static long generateId() {
        return idCounter++;
    }
}
