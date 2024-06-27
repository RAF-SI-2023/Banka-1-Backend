package zews.otc_testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zews.otc_testing.entity.offer.Offer;
import zews.otc_testing.entity.offer.OfferStatus;

import java.util.List;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Offer findByIdBank(Long id);
    List<Offer> findAllByOfferStatus(OfferStatus offerStatus);

}
