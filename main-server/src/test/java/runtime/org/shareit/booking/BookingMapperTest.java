package runtime.org.shareit.booking;

import org.junit.jupiter.api.Test;
import runtime.org.shareit.booking.dto.BookingItemDto;
import runtime.org.shareit.booking.mapper.BookingMapper;
import runtime.org.shareit.booking.model.Booking;
import runtime.org.shareit.booking.model.BookingStatus;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(User.builder().id(1L).name("name").email("email@email.com").build())
            .item(new Item())
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusMinutes(10))
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void toBookingItemDto() {
        BookingItemDto actualBookingItemDto = BookingMapper.toBookingItemDto(booking);

        assertEquals(1L, actualBookingItemDto.getId());
        assertEquals(1L, actualBookingItemDto.getBookerId());
    }
}
