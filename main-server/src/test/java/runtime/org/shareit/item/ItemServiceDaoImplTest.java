package runtime.org.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import runtime.org.shareit.booking.model.Booking;
import runtime.org.shareit.booking.model.BookingStatus;
import runtime.org.shareit.booking.repository.BookingRepository;
import runtime.org.shareit.exceptions.NotFoundException;
import runtime.org.shareit.exceptions.ValidationException;
import runtime.org.shareit.item.dto.CommentDtoOut;
import runtime.org.shareit.item.dto.ItemDto;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.item.mapper.CommentMapper;
import runtime.org.shareit.item.mapper.ItemMapper;
import runtime.org.shareit.item.model.Comment;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.item.repository.CommentRepository;
import runtime.org.shareit.item.repository.ItemRepository;
import runtime.org.shareit.item.service.ItemServiceDaoImpl;
import runtime.org.shareit.request.model.ItemRequest;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.mapper.UserMapper;
import runtime.org.shareit.user.model.User;
import runtime.org.shareit.user.service.UserServiceDao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceDaoImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceDao userService;

    @InjectMocks
    private ItemServiceDaoImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("email@email.com")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("username2")
            .email("email2@email.com")
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

    private final ItemDtoOut itemDto = ItemDtoOut.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .available(true)
            .comments(Collections.emptyList())
            .build();
    private final ItemDto itemDtoUpdate = ItemDto.builder()
            .build();

    private final Comment comment = Comment.builder()
            .id(1L)
            .text("comment")
            .created(LocalDateTime.now())
            .author(user)
            .item(item)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    private final Booking lastBooking = Booking.builder()
            .id(2L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(2L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    private final Booking pastBooking = Booking.builder()
            .id(3L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(10L))
            .end(LocalDateTime.now().minusDays(9L))
            .build();

    private final Booking nextBooking = Booking.builder()
            .id(4L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final Booking futureBooking = Booking.builder()
            .id(5L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(10L))
            .end(LocalDateTime.now().plusDays(20L))
            .build();

    @Test
    void addNewItemWhenInvoked() {
        Item itemSaveTest = Item.builder()
                .name("test item name")
                .description("test description")
                .available(true)
                .build();

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.save(itemSaveTest)).thenReturn(itemSaveTest);

        ItemDtoOut actualItemDto = itemService.add(userDto.getId(), ItemMapper.toItemDto(itemSaveTest));

        assertEquals(actualItemDto.getName(), "test item name");
        assertEquals(actualItemDto.getDescription(), "test description");
    }

    @Test
    void getItemById() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDtoOut actualItemDto = itemService.findItemById(user.getId(), item.getId());

        assertEquals(itemDto, actualItemDto);
    }


    @Test
    void updateItem() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(), null);
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user)
                .request(itemRequest)
                .build();

        when(userService.findById(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));

        ItemDtoOut savedItem = itemService.update(user.getId(), itemDto.getId(), ItemMapper.toItemDto(updatedItem));

        assertEquals("updated name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
    }

    @Test
    void updateItemWhenUserIsNotItemOwnerShouldThrowException() {
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user2)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(updatedItem));
        when(userService.findById(user.getId())).thenReturn(userDto);

        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.update(user.getId(), itemDto.getId(), ItemMapper.toItemDto(updatedItem)));

        assertEquals(itemNotFoundException.getMessage(), "Пользователь с id = " + user.getId() +
                " не является собственником вещи id = " + item.getId());
    }

    @Test
    void updateItemWhenItemIdIsNotValid() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.update(user.getId(), itemDto.getId(), ItemMapper.toItemDto(item)));
        assertEquals(itemNotFoundException.getMessage(), "Вещи с " + item.getId() + " не существует");
    }

    @Test
    void getAllComments() {
        List<CommentDtoOut> expectedCommentsDto = List.of(CommentMapper.toCommentDtoOut(comment));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        List<CommentDtoOut> actualComments = itemService.getAllItemComments(item.getId());

        assertEquals(actualComments.size(), 1);
        assertEquals(actualComments, expectedCommentsDto);
    }

    @Test
    void createComment() {
        CommentDtoOut expectedCommentDto = CommentMapper.toCommentDtoOut(comment);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoOut actualCommentDto = itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId());

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void createComment_whenItemIdIsNotValid_thenThrowObjectNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(itemNotFoundException.getMessage(), "У пользователя с id = " + user.getId() + " не " +
                "существует вещи с id = " + item.getId());
    }

    @Test
    void createCommentWhenUserHaveNotAnyBookingsShouldThrowValidationException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationException userBookingsNotFoundException = assertThrows(ValidationException.class,
                () -> itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(userBookingsNotFoundException.getMessage(), "У пользователя с id   " + user.getId() + " должно быть хотя бы одно бронирование предмета с id " + item.getId());
    }
}
