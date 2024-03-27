package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingForex;
import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.ListingStock;

import java.util.List;
import java.util.Optional;

public interface FuturesService {
    List<ListingFuture> fetchNFutures(int n);

    List<ListingHistory> fetchNFutureHistories(List<ListingFuture> listingFutures, int n);

    int addAllFutures(List<ListingFuture> futures);

    int addFuture(ListingFuture future);

    List<ListingFuture> getAllFutures();
    Optional<ListingFuture> findById(Long id);

    List<ListingHistory> getListingHistoriesByTimestamp(Long id, Integer from, Integer to);

}
