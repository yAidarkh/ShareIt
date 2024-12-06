package runtime.org.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import runtime.org.shareit.booking.dto.BookingDtoOut;
import runtime.org.shareit.booking.mapper.BookingMapper;
import runtime.org.shareit.booking.model.Booking;
import runtime.org.shareit.booking.model.BookingStatus;
import runtime.org.shareit.booking.repository.BookingRepository;
import runtime.org.shareit.exceptions.*;
import runtime.org.shareit.item.dto.CommentDto;
import runtime.org.shareit.item.dto.CommentDtoOut;
import runtime.org.shareit.item.dto.ItemDto;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.item.mapper.CommentMapper;
import runtime.org.shareit.item.mapper.ItemMapper;
import runtime.org.shareit.item.model.Comment;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.item.repository.CommentRepository;
import runtime.org.shareit.item.repository.ItemRepository;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.mapper.UserMapper;
import runtime.org.shareit.user.model.User;
import runtime.org.shareit.user.service.UserServiceDao;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceDaoImpl implements ItemServiceDao {
    private final UserServiceDao userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDtoOut add(Long userId, ItemDto itemDto) {
        UserDto user = userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner((UserMapper.toUser(user)));
        return ItemMapper.toItemDtoOut(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDtoOut update(Long userId, Long itemId, ItemDto itemDto) {
        UserDto user = userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с " + itemId + " не существует"));

        if (!UserMapper.toUser(user).equals(item.getOwner())) {
            throw new NotFoundException("Пользователь с id = " + userId +
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

        return ItemMapper.toItemDtoOut(item);
    }

    @Override
    @Transactional
    public ItemDtoOut findItemById(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id = " + itemId));

        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
        itemDtoOut.setComments(getAllItemComments(itemId));

        if (!item.getOwner().getId().equals(userId)) {
            return itemDtoOut;
        }

        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(item, BookingStatus.APPROVED);
        List<BookingDtoOut> bookingDTOList = bookings.stream()
                .map(BookingMapper::toBookingOut)
                .collect(toList());

        itemDtoOut.setLastBooking(getLastBooking(bookingDTOList, LocalDateTime.now()));
        itemDtoOut.setNextBooking(getNextBooking(bookingDTOList, LocalDateTime.now()));

        return itemDtoOut;
    }

    @Override
    @Transactional
    public List<ItemDtoOut> findAll(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> itemList = itemRepository.findAllByOwnerIdOrderById(userId, pageable);
        List<Long> idList = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, List<CommentDtoOut>> comments = commentRepository.findAllByItemIdIn(idList).stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(groupingBy(CommentDtoOut::getItemId, toList()));

        Map<Long, List<BookingDtoOut>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(itemList,
                        BookingStatus.APPROVED).stream()
                .map(BookingMapper::toBookingOut)
                .collect(groupingBy(BookingDtoOut::getItemId, toList()));

        return itemList
                .stream()
                .map(item -> ItemMapper.toItemDtoOut(item, getLastBooking(bookings.get(item.getId()),
                                LocalDateTime.now()), comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())))
                .collect(toList());
    }

    @Override
    @Transactional
    public List<ItemDtoOut> search(Long userId, String text, Integer from, Integer size) {
        userService.findById(userId);
        Pageable pageable = PageRequest.of(from / size, size);

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> itemList = itemRepository.search(text, pageable);
        return itemList.stream()
                .map(ItemMapper::toItemDtoOut)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = UserMapper.toUser(userService.findById(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("У пользователя с id = " + userId + " не " +
                        "существует вещи с id = " + itemId));
        List<Booking> userBookings = bookingRepository.findAllByUserBookings(userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("У пользователя с id   " + userId + " должно быть хотя бы одно бронирование предмета с id " + itemId);
        }

        return CommentMapper.toCommentDtoOut(commentRepository.save(CommentMapper.toComment(commentDto, item, user)));
    }

    public List<CommentDtoOut> getAllItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList());
    }

    private BookingDtoOut getLastBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(bookingDTO -> !bookingDTO.getStart().isAfter(time))
                .reduce((booking1, booking2) -> booking1.getStart().isAfter(booking2.getStart()) ? booking1 : booking2)
                .orElse(null);
    }

    private BookingDtoOut getNextBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(bookingDTO -> bookingDTO.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }
}
