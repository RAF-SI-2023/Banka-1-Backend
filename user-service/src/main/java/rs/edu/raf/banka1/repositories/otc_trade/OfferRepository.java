package rs.edu.raf.banka1.repositories.otc_trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.banka1.model.offer.Offer;
import rs.edu.raf.banka1.model.offer.OfferStatus;

import java.util.List;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Offer findByIdBank(Long id);
    List<Offer> findAllByOfferStatus(OfferStatus offerStatus);

}
