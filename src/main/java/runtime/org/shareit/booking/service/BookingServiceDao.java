package runtime.org.shareit.booking.service;

import runtime.org.shareit.booking.dto.BookingDto;
import runtime.org.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingServiceDao {
    BookingDtoOut add(Long userId, BookingDto bookingDto);

    BookingDtoOut update(Long userId, Long bookingId, Boolean approved);

    BookingDtoOut findBookingByUserId(Long userId, Long bookingId);

    List<BookingDtoOut> findAll(Long userId, String state, Integer from, Integer size);

    List<BookingDtoOut> findAllOwner(Long userId, String state, Integer from, Integer size);
}
