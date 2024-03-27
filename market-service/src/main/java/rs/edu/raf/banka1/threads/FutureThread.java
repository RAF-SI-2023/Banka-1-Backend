package rs.edu.raf.banka1.threads;

import org.springframework.beans.factory.annotation.Autowired;
import rs.edu.raf.banka1.services.FuturesService;
import rs.edu.raf.banka1.services.ListingStockService;
import rs.edu.raf.banka1.services.OptionsService;

public class FutureThread implements Runnable {
    private final FuturesService futuresService;
    private final ListingStockService listingStockService;

    public FutureThread(FuturesService futuresService, ListingStockService listingStockService) {
        this.futuresService = futuresService;
        this.listingStockService = listingStockService;
    }

    @Override
    public void run() {
        var futures = futuresService.fetchNFutures(10);
        futuresService.addAllFutures(futures);
        var histories = futuresService.fetchNFutureHistories(futures, 20);
        listingStockService.addAllListingsToHistory(histories);
    }
}
