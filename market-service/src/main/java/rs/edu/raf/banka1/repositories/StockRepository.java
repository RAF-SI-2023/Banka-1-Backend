package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.ExampleModel;
import rs.edu.raf.banka1.model.entities.ListingStock;

import java.util.Optional;

public interface StockRepository extends JpaRepository<ListingStock,String> {

}
