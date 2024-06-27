package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.StockProfit;

@Repository
public interface StockProfitRepository extends JpaRepository<StockProfit, Long> {
}
