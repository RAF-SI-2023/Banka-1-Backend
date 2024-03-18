package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.entities.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
