package rs.edu.raf.banka1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ListingStock;


@Repository
public interface StockRepository extends JpaRepository<ListingStock,String> {
    ListingStock findByTicker(String ticker);

   // @Query(value = "SELECT * FROM listing_stock LIMIT :max")
    //List<ListingStock> fetchStocksByUpperBound(@Param(value = "max") Integer max);

}
