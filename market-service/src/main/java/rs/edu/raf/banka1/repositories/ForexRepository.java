package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.Forex;

import java.util.Optional;

@Repository
public interface ForexRepository extends JpaRepository<Forex, Long> {
    boolean existsBySymbol(String symbol);
}
