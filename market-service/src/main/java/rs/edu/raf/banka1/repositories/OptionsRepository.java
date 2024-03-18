package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.OptionsModel;

@Repository
public interface OptionsRepository extends JpaRepository<OptionsModel, Long> {

}
