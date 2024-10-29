package runtime.org.shareit.booking.model;

import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.user.model.User;

import java.time.LocalDate;

public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatus status;

    public Booking(LocalDate start, LocalDate end, Item item, User booker, BookingStatus status) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}
