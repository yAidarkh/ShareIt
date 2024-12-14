package runtime.org.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.item.mapper.ItemMapper;
import runtime.org.shareit.request.dto.ItemRequestDto;
import runtime.org.shareit.request.dto.ItemRequestDtoOut;
import runtime.org.shareit.request.model.ItemRequest;
import runtime.org.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toRequest(User user, ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public ItemRequestDto toRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .description(request.getDescription())
                .build();
    }

    public ItemRequestDtoOut toRequestDtoOut(ItemRequest request) {
        List<ItemDtoOut> itemsDtoOut = new ArrayList<>();
        if (!Objects.isNull(request.getItems())) {
            itemsDtoOut = request.getItems().stream()
                    .map(ItemMapper::toItemDtoOut)
                    .collect(Collectors.toList());
        }
        return ItemRequestDtoOut.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemsDtoOut)
                .build();
    }
}
