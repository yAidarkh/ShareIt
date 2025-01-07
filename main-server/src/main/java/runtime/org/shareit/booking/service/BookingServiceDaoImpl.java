package runtime.org.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import runtime.org.shareit.booking.dto.BookingDto;
import runtime.org.shareit.booking.dto.BookingDtoOut;
import runtime.org.shareit.booking.mapper.BookingMapper;
import runtime.org.shareit.booking.model.Booking;
import runtime.org.shareit.booking.model.BookingState;
import runtime.org.shareit.booking.model.BookingStatus;
import runtime.org.shareit.booking.repository.BookingRepository;
import runtime.org.shareit.exceptions.NotFoundException;
import runtime.org.shareit.exceptions.ValidationException;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.item.repository.ItemRepository;
import runtime.org.shareit.user.mapper.UserMapper;
import runtime.org.shareit.user.model.User;
import runtime.org.shareit.user.service.UserServiceDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceDaoImpl implements BookingServiceDao {
    private final UserServiceDao userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDtoOut add(Long userId, BookingDto bookingDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        Optional<Item> itemById = itemRepository.findById(bookingDto.getItemId());

        if (itemById.isEmpty()) {
            throw new NotFoundException("Вещь не найдена.");
        }

        Item item = itemById.get();
        bookingValidation(bookingDto, user, item);
        Booking booking = BookingMapper.toBooking(user, item, bookingDto);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateBookingDetailsOwner(userId, bookingId);
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut findBookingByUserId(Long userId, Long bookingId) {
        Booking booking = validateBookingDetailsAuthor(userId, bookingId);
        return BookingMapper.toBookingOut(booking);
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAll(Long userId, String state, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        userService.findById(userId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(userId, pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    @Transactional
    public List<BookingDtoOut> findAllOwner(Long userId, String state, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        userService.findById(userId);
        switch (validState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByOwnerId(userId, pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(userId, LocalDateTime.now(), pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(userId, pageable).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void bookingValidation(BookingDto bookingDto, User user, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования.");
        }

        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Вещь не найдена.");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Дата окончания не может быть раньше или равна дате начала");
        }
    }

    private Booking validateBookingDetailsOwner(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронь не найдена."));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является владельцем.");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Бронь не cо статусом WAITING.");
        }
        return booking;
    }

    private Booking validateBookingDetailsAuthor(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронь не найдена."));

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не владелец и не автор бронирования.");
        }
        return booking;
    }


    private BookingState validState(String bookingState) {
        try {
            BookingState state = BookingState.valueOf(bookingState);
            return state;
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + bookingState);
        }
    }
}
