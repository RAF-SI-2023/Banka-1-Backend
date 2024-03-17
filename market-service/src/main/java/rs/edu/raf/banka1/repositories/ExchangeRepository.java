package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.entities.Exchange;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}
