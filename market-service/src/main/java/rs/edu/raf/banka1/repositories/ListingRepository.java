package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ListingModel;

import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<ListingModel, Long> {
    Optional<ListingModel> findByTicker(String ticker);
}
