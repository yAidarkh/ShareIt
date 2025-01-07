package runtime.org.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import runtime.org.shareit.booking.dto.BookingDto;
import runtime.org.shareit.booking.dto.BookingDtoOut;
import runtime.org.shareit.booking.dto.BookingItemDto;
import runtime.org.shareit.booking.model.Booking;
import runtime.org.shareit.booking.model.BookingStatus;
import runtime.org.shareit.item.mapper.ItemMapper;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.user.mapper.UserMapper;
import runtime.org.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(User user, Item item, BookingDto bookingDto) {
        return Booking.builder()
                .item(item)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingDtoOut toBookingOut(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .item(ItemMapper.toItemDtoOut(booking.getItem()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
