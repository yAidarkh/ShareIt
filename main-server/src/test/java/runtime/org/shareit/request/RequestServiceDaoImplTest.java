package runtime.org.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import runtime.org.shareit.exceptions.NotFoundException;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.request.dto.ItemRequestDto;
import runtime.org.shareit.request.dto.ItemRequestDtoOut;
import runtime.org.shareit.request.mapper.ItemRequestMapper;
import runtime.org.shareit.request.model.ItemRequest;
import runtime.org.shareit.request.repository.ItemRequestRepository;
import runtime.org.shareit.request.service.ItemRequestServiceDao;
import runtime.org.shareit.request.service.ItemRequestServiceDaoImpl;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.model.User;
import runtime.org.shareit.user.service.UserServiceDao;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceDaoImplTest {
    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserServiceDao userService;

    @InjectMocks
    private ItemRequestServiceDaoImpl requestService;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("request description")
            .items(List.of(item))
            .build();

    @Test
    void addNewRequest() {
        ItemRequestDto requestDto = ItemRequestMapper.toRequestDto(request);
        ItemRequestDtoOut expectedRequestDto = ItemRequestMapper.toRequestDtoOut(request);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDtoOut actualRequestDto = requestService.add(user.getId(), requestDto);

        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void getUserRequests() {
        List<ItemRequestDtoOut> expectedRequestsDto = List.of(ItemRequestMapper.toRequestDtoOut(request));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findAllByRequesterId(userDto.getId())).thenReturn(List.of(request));

        List<ItemRequestDtoOut> actualRequestsDto = requestService.getUserRequests(userDto.getId());

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDtoOut> expectedRequestsDto = List.of(ItemRequestMapper.toRequestDtoOut(request));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findAllByRequester_IdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(request));

        List<ItemRequestDtoOut> actualRequestsDto = requestService.getAllRequests(userDto.getId(), 0, 10);

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }

    @Test
    void getRequestById() {
        ItemRequestDtoOut expectedRequestDto = ItemRequestMapper.toRequestDtoOut(request);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        ItemRequestDtoOut actualRequestDto = requestService.getRequestById(userDto.getId(), request.getId());

        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void getRequestByIdWhenRequestIdIsNotValidShouldThrowObjectNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        NotFoundException requestNotFoundException = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(userDto.getId(), request.getId()));

        assertEquals(requestNotFoundException.getMessage(), String.format("Запрос с id: %s" +
                " не был найден.", request.getId()));
    }
}
