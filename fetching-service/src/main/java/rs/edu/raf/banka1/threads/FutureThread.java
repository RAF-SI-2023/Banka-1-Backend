package rs.edu.raf.banka1.threads;

import rs.edu.raf.banka1.model.ListingFuture;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.services.FuturesService;
import rs.edu.raf.banka1.services.ListingStockService;

import java.util.List;

public class FutureThread implements Runnable {
    private final FuturesService futuresService;
    private final ListingStockService listingStockService;

    public FutureThread(FuturesService futuresService, ListingStockService listingStockService) {
        this.futuresService = futuresService;
        this.listingStockService = listingStockService;
    }

    @Override
    public void run() {
        List<ListingFuture> futures = futuresService.fetchNFutures(10);
        futuresService.addAllFutures(futures);
        List<ListingHistory> histories = futuresService.fetchNFutureHistories(futures, 20);
        listingStockService.addAllListingsToHistory(histories);
    }
}
