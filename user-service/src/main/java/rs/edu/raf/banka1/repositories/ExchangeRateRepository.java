package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.ExchangeRate;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate,Long> {
}
