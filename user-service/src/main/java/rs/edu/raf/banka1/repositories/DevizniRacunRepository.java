package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.DevizniRacun;
import rs.edu.raf.banka1.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevizniRacunRepository extends JpaRepository<DevizniRacun, Long> {

//    List<DevizniRacun> findDevizniRacunByOwnerId(Long ownerId);

//    List<DevizniRacun> findDevizniRacunByUser(User user);

    Optional<DevizniRacun> findDevizniRacunByAccountNumber(String accountNumber);
}
