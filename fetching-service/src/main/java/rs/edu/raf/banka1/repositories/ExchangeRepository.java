package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.entities.Exchange;


@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    Exchange findByExchangeName(String exchangeName);

}
