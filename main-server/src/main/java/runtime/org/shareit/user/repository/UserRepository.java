package runtime.org.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import runtime.org.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findUserByEmail(String email);
}
