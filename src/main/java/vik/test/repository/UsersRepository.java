package vik.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vik.test.models.User;

public interface UsersRepository extends JpaRepository<User, Long> {

    User findById(Integer id);
}
