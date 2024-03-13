package rs.edu.raf.banka1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.banka1.model.ExchangeModel;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeModel, Long> {

}
