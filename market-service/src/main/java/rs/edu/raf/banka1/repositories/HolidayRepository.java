package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.entities.Holiday;

import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    @Query("select h from Holiday h where h.country.id = :countryId")
    Optional<List<Holiday>> findByCountryId(@Param("countryId") Long countryId);

}
