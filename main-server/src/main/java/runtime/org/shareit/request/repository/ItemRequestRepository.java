package runtime.org.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import runtime.org.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(Long userId);

    List<ItemRequest> findAllByRequester_IdNotOrderByCreatedDesc(Long userId, Pageable pageable);
}
