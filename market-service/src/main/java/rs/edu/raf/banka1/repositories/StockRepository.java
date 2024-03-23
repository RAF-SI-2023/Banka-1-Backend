package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.banka1.model.ListingStock;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<ListingStock,Long> {
    Optional<ListingStock> findByTicker(String ticker);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ListingStock " +
            "SET high = :high, " + "low = :low, price = :price, " +
            "volume = :volume, priceChange = :change, " + "lastRefresh = :refreshed " +
            "WHERE listingId = :listing_id")
    void updateFreshValuesStock(@Param("high") double high, @Param("low") double low, @Param("price") double price,
                                @Param("volume") int volume, @Param("change") double priceChange,
                                @Param("listing_id") Long id, @Param("refreshed") Integer refreshed);
}
