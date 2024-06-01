package rs.edu.raf.banka1.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.OrderType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    List<MarketOrder> findByStatusAndUpdatedAtLessThanEqual(OrderStatus status, Instant updatedAt);

    @Transactional
    @Modifying
    @Query("UPDATE MarketOrder m SET m.updatedAt = ?1 WHERE m.id = ?2")
    void updateUpdatedAtById(Instant updatedAt, Long id);

    @org.springframework.transaction.annotation.Transactional
    @Modifying
    @Query("update MarketOrder m set m.status = ?1 where m.id = ?2")
    int cancelOrder(OrderStatus status, Long id);

    @Query("SELECT mo FROM MarketOrder mo WHERE mo.id = :id")
    Optional<MarketOrder> fetchById(Long id);

    List<MarketOrder> getAllByOwner(User owner);

    @Query("SELECT mo FROM MarketOrder mo WHERE mo.listingId = :listingId AND mo.listingType = :listingType AND mo.owner = :owner AND mo.orderType = :orderType AND mo.status = :status AND mo.currentAmount < mo.contractSize ORDER BY mo.timestamp")
    Optional<List<MarketOrder>> getAllBuyOrders(
            Long listingId,
            ListingType listingType,
            User owner,
            OrderType orderType,
            OrderStatus status);
}
