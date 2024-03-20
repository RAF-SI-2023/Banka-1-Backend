package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.entities.Inflation;

import java.util.List;

@Repository
public interface InflationRepository extends JpaRepository<Inflation, Long> {

    List<Inflation> findAllByCurrencyId(Long currencyId);

    List<Inflation> findAllByCurrencyIdAndYear(Long currencyId, Integer year);
}
