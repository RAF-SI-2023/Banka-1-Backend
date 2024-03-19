package rs.edu.raf.banka1.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ListingStock;


@Repository
public interface StockRepository extends JpaRepository<ListingStock,String> {
    ListingStock findByTicker(String ticker);

}
