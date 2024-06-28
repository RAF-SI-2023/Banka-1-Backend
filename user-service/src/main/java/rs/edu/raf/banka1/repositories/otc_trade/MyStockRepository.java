package rs.edu.raf.banka1.repositories.otc_trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.banka1.model.listing.MyStock;

import java.util.List;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface MyStockRepository extends JpaRepository<MyStock, Long> {
    MyStock findByTickerAndCompanyId(String ticker, Long companyId);

    MyStock findByTicker(String ticker);

    List<MyStock> findAllByCompanyIdAndPublicAmountGreaterThan(Long companyId, Integer publicAmount);
}
