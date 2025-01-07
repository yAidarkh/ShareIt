package runtime.org.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import runtime.org.shareit.request.dto.ItemRequestDto;
import runtime.org.shareit.request.dto.ItemRequestDtoOut;
import runtime.org.shareit.request.service.ItemRequestServiceDao;
import runtime.org.shareit.user.dto.UserDto;
import runtime.org.shareit.user.service.UserServiceDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceDaoIT {
    @Autowired
    private ItemRequestServiceDao requestService;

    @Autowired
    private UserServiceDao userService;

    private final UserDto userDto = UserDto.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("request description")
            .build();

    @Test
    void addNewRequest() {
        UserDto addedUser = userService.add(userDto);
        requestService.add(addedUser.getId(), requestDto);

        List<ItemRequestDtoOut> actualRequests = requestService.getUserRequests(addedUser.getId());

        assertEquals("request description", actualRequests.get(0).getDescription());
    }

    @Test
    void getRequestByIdWhenRequestIdIsNotValidShouldThrowObjectNotFoundException() {
        Long requestId = 2L;

        Assertions
                .assertThrows(RuntimeException.class,
                        () -> requestService.getRequestById(userDto.getId(), requestId));
    }
}
