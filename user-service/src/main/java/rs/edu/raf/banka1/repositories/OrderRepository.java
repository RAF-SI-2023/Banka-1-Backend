package rs.edu.raf.banka1.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;

import java.util.List;

public interface OrderRepository extends JpaRepository<MarketOrder, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE MarketOrder mo SET mo.status = :orderStatus, mo.processedNumber = mo.contractSize WHERE mo.id = :orderId")
    void finishOrder(final Long orderId, final OrderStatus orderStatus);

    @Transactional
    @Modifying
    @Query("UPDATE MarketOrder mo SET mo.status = :orderStatus WHERE mo.id = :orderId")
    void changeStatus(final Long orderId, final OrderStatus orderStatus);

    @Transactional
    @Modifying
    @Query("UPDATE MarketOrder mo SET mo.processedNumber = :processedNum WHERE mo.id = :orderId")
    void changeProcessedNumber(final Long orderId, final Long processedNum);



    List<MarketOrder> findByIsTradingTrue();



}
