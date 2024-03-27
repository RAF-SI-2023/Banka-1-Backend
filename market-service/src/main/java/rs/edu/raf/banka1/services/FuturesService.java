package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ListingFuture;

import java.util.List;

public interface FuturesService {
    List<ListingFuture> fetchNFutures(int n);

    int addAllFutures(List<ListingFuture> futures);

    int addFuture(ListingFuture future);
}
