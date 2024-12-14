package runtime.org.shareit.request.service;

import runtime.org.shareit.request.dto.ItemRequestDto;
import runtime.org.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestServiceDao {
    ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoOut> getUserRequests(Long userId);

    List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoOut getRequestById(Long userId, Long requestId);
}
