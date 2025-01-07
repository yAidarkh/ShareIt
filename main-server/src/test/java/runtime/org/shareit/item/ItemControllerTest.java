package runtime.org.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import runtime.org.shareit.item.controller.ItemController;
import runtime.org.shareit.item.dto.CommentDto;
import runtime.org.shareit.item.dto.CommentDtoOut;
import runtime.org.shareit.item.dto.ItemDto;
import runtime.org.shareit.item.dto.ItemDtoOut;
import runtime.org.shareit.item.mapper.ItemMapper;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.item.service.ItemServiceDao;
import runtime.org.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static runtime.org.shareit.item.controller.ItemController.USER_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemServiceDao itemService;


    private final User user = User.builder()
            .id(1L)
            .name("username")
            .email("my@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("description")
            .owner(user)
            .build();


    @Test
    @SneakyThrows
    void createItemWhenItemIsValid() {
        Long userId = 0L;
        ItemDto itemDtoToCreate = ItemDto.builder()
                .description("some item description")
                .name("some item name")
                .available(true)
                .build();

        when(itemService.add(userId, itemDtoToCreate)).thenReturn(ItemMapper.toItemDtoOut(ItemMapper.toItem(itemDtoToCreate)));

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }


    @Test
    @SneakyThrows
    void updateWhenItemIsValidShouldReturnStatusIsOk() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemDto itemDtoToCreate = ItemDto.builder()
                .description("some item description")
                .name("some item name")
                .available(true)
                .build();

        when(itemService.update(userId, itemId, itemDtoToCreate)).thenReturn(ItemMapper.toItemDtoOut(ItemMapper.toItem(itemDtoToCreate)));

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    @SneakyThrows
    void getShouldReturnStatusOk() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemDtoOut itemDtoToCreate = ItemDtoOut.builder()
                .id(itemId)
                .description("")
                .name("")
                .available(true)
                .build();

        when(itemService.findItemById(userId, itemId)).thenReturn(itemDtoToCreate);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoToCreate), result);
    }

    @Test
    @SneakyThrows
    void getAllShouldReturnStatusOk() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        List<ItemDtoOut> itemsDtoToExpect = List.of(ItemDtoOut.builder()
                .name("some item name")
                .description("some item description")
                .available(true)
                .build());

        when(itemService.findAll(userId, from, size)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items", from, size)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }

    @Test
    @SneakyThrows
    void searchItemsShouldReturnStatusOk() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        String text = "find";
        List<ItemDtoOut> itemsDtoToExpect = List.of(ItemDtoOut.builder()
                .name("some item name")
                .description("some item description")
                .available(true)
                .build());

        when(itemService.search(userId, text, from, size)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search", from, size)
                        .header(USER_HEADER, userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }


    @Test
    @SneakyThrows
    void createCommentWhenCommentIsValidShouldReturnStatusIsOk() {
        ItemDtoOut itemDtoOut = itemService.add(user.getId(), ItemMapper.toItemDto(item));
        CommentDto commentToAdd = CommentDto.builder()
                .text("some comment")
                .build();
        CommentDtoOut commentDtoOut = CommentDtoOut.builder()
                .id(1L)
                .itemId(item.getId())
                .text(commentToAdd.getText())
                .build();
        when(itemService.createComment(user.getId(), commentToAdd, item.getId())).thenReturn(commentDtoOut);

        String result = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(commentToAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDtoOut), result);
    }
}
