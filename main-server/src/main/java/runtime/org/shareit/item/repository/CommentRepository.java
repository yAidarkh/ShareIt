package runtime.org.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import runtime.org.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemIds);
}
