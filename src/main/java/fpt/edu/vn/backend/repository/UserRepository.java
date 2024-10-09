package fpt.edu.vn.backend.repository;

import fpt.edu.vn.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findUserByEmail(String email);

    boolean existsByEmail(String email);


    @Query("SELECT u FROM User u WHERE LOWER(u.gender) = :gender AND u.id <> :userId")
    Page<User> findUsersByGenderAndExcludeCurrentUser(@Param("gender") String gender, @Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE LOWER(u.gender) = :gender AND u.id <> :userId AND u.dateOfBirth BETWEEN :startAge AND :endAge")
    Page<User> findUsersByGenderAndAgeRangeAndExcludeCurrentUser(@Param("gender") String gender,
                                                                 @Param("userId") Integer userId,
                                                                 @Param("startAge") LocalDate startAge,
                                                                 @Param("endAge") LocalDate endAge,
                                                                 Pageable pageable);

}