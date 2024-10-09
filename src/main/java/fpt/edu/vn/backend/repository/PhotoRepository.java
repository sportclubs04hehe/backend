package fpt.edu.vn.backend.repository;

import fpt.edu.vn.backend.entity.Photo;
import fpt.edu.vn.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    Photo findByUser(User user);
    boolean existsByUserAndIsMainTrue(User user);
}
