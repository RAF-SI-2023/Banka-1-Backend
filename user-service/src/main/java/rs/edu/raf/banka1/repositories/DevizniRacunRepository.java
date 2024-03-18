package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.DevizniRacun;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevizniRacunRepository extends JpaRepository<DevizniRacun, String> {

    List<DevizniRacun> findDevizniRacunByOwnerId(String ownerId);

    Optional<DevizniRacun> findDevizniRacunByAccountNumber(String accountNumber);
}
