package rs.edu.raf.banka1.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.banka1.model.OptionsModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionsRepository extends JpaRepository<OptionsModel, Long> {
    Optional<List<OptionsModel>> findByTicker(String ticker);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE options_model",nativeQuery = true)
    void truncateTable();

    @Query("SELECT o FROM OptionsModel o WHERE o.optionType ='CALL'")
    Optional<List<OptionsModel>> getAllCallsOptions();

    @Query("SELECT o FROM OptionsModel o WHERE o.optionType = 'CALL' AND o.listingId = :listingId")
    Optional<OptionsModel> getCallOptionById(@Param("listingId") Long listingId);

    @Query("SELECT o FROM OptionsModel o WHERE o.optionType ='PUT'")
    Optional<List<OptionsModel>> getAllPutsOptions();

    @Query("SELECT o FROM OptionsModel o WHERE o.optionType = 'PUT' AND o.listingId = :listingId")
    Optional<OptionsModel> getPutOptionById(@Param("listingId") Long listingId);

}
