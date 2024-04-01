package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.MarketOrder;

public interface OrderRepository extends JpaRepository<MarketOrder, Long> {


}
