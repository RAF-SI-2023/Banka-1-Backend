package rs.edu.raf.banka1.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u "
            + "where u.active = true "
            + "and (:email IS NULL or u.email like concat('%', concat(:email, '%'))) "
            + "and (:firstName IS NULL or u.firstName like concat('%', concat(:firstName, '%'))) "
            + "and (:lastName IS NULL or u.lastName like concat('%', concat(:lastName, '%'))) "
            + "and (:position IS NULL or :position = 'All' "
            + "or u.position like concat('%', concat(:position, '%')))")
    Optional<List<User>> searchUsersByEmailAndFirstNameAndLastNameAndPosition(@Param("email") String email,
                                                                  @Param("firstName") String firstName,
                                                                  @Param("lastName") String lastName,
                                                                  @Param("position") String position);
    Optional<User> findByEmail(String username);
    Optional<User> findByActivationToken(String activationToken);

    Optional<User> findByResetPasswordToken(String token);

    @Transactional
    @Modifying
    @Query("update User u set u.active = false where u.userId = :userId")
    void deactivateUser(@Param("userId") Long userId);
}

