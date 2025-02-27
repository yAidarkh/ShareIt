package runtime.org.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import runtime.org.shareit.item.model.Comment;
import runtime.org.shareit.item.model.Item;
import runtime.org.shareit.item.repository.CommentRepository;
import runtime.org.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("name")
            .email("email@email.com")
            .build();
    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final Comment comment = Comment.builder()
            .item(item)
            .author(user)
            .created(LocalDateTime.now())
            .text("comment")
            .build();


    @BeforeEach
    public void init() {
        testEntityManager.persist(user);
        testEntityManager.persist(item);
        testEntityManager.flush();
        commentRepository.save(comment);
    }

    @AfterEach
    public void deleteAll() {
        commentRepository.deleteAll();
    }

    @Test
    void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(1L);

        assertEquals(comments.size(), 1);
        assertEquals(comments.get(0).getText(), "comment");
    }
}
