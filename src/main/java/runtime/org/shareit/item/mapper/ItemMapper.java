package runtime.org.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import runtime.org.shareit.item.dto.ItemDto;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public ItemDtoOut toItemDtoOut(Item item) {
        ItemDtoOut dto = new ItemDtoOut(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null? item.getRequest().getId() : null,
                item.getOwner().getId()
        );
        return dto;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null? item.getRequest().getId() : null,
                item.getOwner().getId()
                );
        return itemDto;
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
                );
    }

    public Item toItem(ItemDtoOut itemDtoOut) {
        return new Item(
                itemDtoOut.getId(),
                itemDtoOut.getName(),
                itemDtoOut.getDescription(),
                itemDtoOut.getAvailable()
        );
    }
}
