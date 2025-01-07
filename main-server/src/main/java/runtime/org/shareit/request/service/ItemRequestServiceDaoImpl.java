package runtime.org.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import runtime.org.shareit.exceptions.NotFoundException;
import runtime.org.shareit.request.dto.ItemRequestDto;
import runtime.org.shareit.request.dto.ItemRequestDtoOut;
import runtime.org.shareit.request.mapper.ItemRequestMapper;
import runtime.org.shareit.request.model.ItemRequest;
import runtime.org.shareit.request.repository.ItemRequestRepository;
import runtime.org.shareit.user.mapper.UserMapper;
import runtime.org.shareit.user.model.User;
import runtime.org.shareit.user.service.UserServiceDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceDaoImpl implements ItemRequestServiceDao {
    private final ItemRequestRepository requestRepository;
    private final UserServiceDao userService;

    @Override
    @Transactional
    public ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        ItemRequest request = ItemRequestMapper.toRequest(user, itemRequestDto);
        request.setRequester(user);
        return ItemRequestMapper.toRequestDtoOut(requestRepository.save(request));
    }

    @Override
    @Transactional
    public List<ItemRequestDtoOut> getUserRequests(Long userId) {
        UserMapper.toUser(userService.findById(userId));
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterId(userId);
        return itemRequestList.stream()
                .map(ItemRequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size) {
        UserMapper.toUser(userService.findById(userId));
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequester_IdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size));
        return itemRequestList.stream()
                .map(ItemRequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestDtoOut getRequestById(Long userId, Long requestId) {
        userService.findById(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() ->  new NotFoundException("Запрос с id: " + requestId + " не был найден."));
        return ItemRequestMapper.toRequestDtoOut(request);
    }
}
