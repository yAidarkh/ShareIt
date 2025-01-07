package runtime.org.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import runtime.org.shareit.booking.dto.BookingDto;
import runtime.org.shareit.booking.dto.BookingDtoOut;
import runtime.org.shareit.booking.service.BookingServiceDao;
import runtime.org.shareit.item.dto.CommentDto;
import runtime.org.shareit.item.dto.CommentDtoOut;
import runtime.org.shareit.item.dto.ItemDto;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.item.service.ItemServiceDao;
import runtime.org.shareit.request.dto.ItemRequestDto;
import runtime.org.shareit.request.service.ItemRequestServiceDao;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.service.UserServiceDao;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceDaoIT {
    @Autowired
    private ItemServiceDao itemService;

    @Autowired
    private UserServiceDao userService;

    @Autowired
    private ItemRequestServiceDao requestService;

    @Autowired
    private BookingServiceDao bookingService;
    private final UserDto userDto1 = UserDto.builder()
            .name("name1")
            .email("email1@email.com")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("name2")
            .email("email2@email.com")
            .build();

    private final ItemDto itemDto1 = ItemDto.builder()
            .name("item1 name")
            .description("item1 description")
            .available(true)
            .build();

    private final ItemDto itemDto2 = ItemDto.builder()
            .name("item2 name")
            .description("item2 description")
            .available(true)
            .build();

    private final ItemDto itemDtoRequest = ItemDto.builder()
            .name("itemDtoRequest name")
            .description("itemDtoRequest description")
            .available(true)
            .requestId(1L)
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("request description")
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusSeconds(1L))
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .text("comment text")
            .build();

    @Test
    @SneakyThrows
    void addCommentItem() {
        UserDto addedUser1 = userService.add(userDto1);
        UserDto addedUser2 = userService.add(userDto2);
        ItemDtoOut addedItem = itemService.add(addedUser2.getId(), itemDto2);
        BookingDtoOut bookingDtoOut = bookingService.add(addedUser1.getId(), bookingDto);

        bookingService.update(addedUser2.getId(), bookingDtoOut.getId(), true);
        Thread.sleep(2000);
        CommentDtoOut addedComment = itemService.createComment(addedUser1.getId(), commentDto, addedItem.getId());

        assertEquals(1L, addedComment.getId());
        assertEquals("comment text", addedComment.getText());
    }

    @Test
    void addNewItem() {
        UserDto addedUser = userService.add(userDto1);
        ItemDtoOut addedItem = itemService.add(addedUser.getId(), itemDto1);

        assertEquals(1L, addedItem.getId());
        assertEquals("item1 name", addedItem.getName());
    }

    @Test
    void addRequestItem() {
        UserDto addedUser = userService.add(userDto1);
        requestService.add(addedUser.getId(), requestDto);

        ItemDtoOut addedItemRequest = itemService.add(addedUser.getId(), itemDtoRequest);

        assertEquals(1L, addedItemRequest.getRequestId());
        assertEquals("itemDtoRequest name", addedItemRequest.getName());
    }

    @Test
    void getItemByIdWhenItemIdIsNotValid() {
        Long itemId = 3L;

        Assertions
                .assertThrows(RuntimeException.class,
                        () -> itemService.findItemById(userDto1.getId(), itemId));
    }
}
