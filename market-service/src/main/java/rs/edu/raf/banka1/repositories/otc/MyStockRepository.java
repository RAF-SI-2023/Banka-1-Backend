package zews.otc_testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zews.otc_testing.entity.listing.MyStock;

import java.util.List;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface MyStockRepository extends JpaRepository<MyStock, Long> {
    MyStock findByTickerAndCompanyId(String ticker, Long companyId);

    MyStock findByTicker(String ticker);

    List<MyStock> findAllByCompanyIdAndPublicAmountGreaterThan(Long companyId, Integer publicAmount);
}
