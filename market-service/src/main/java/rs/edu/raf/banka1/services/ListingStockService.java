package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.entities.ListingStock;
import java.util.List;

public interface ListingStockService {
    public void initializeStock();
    public void getStockData(String symbol);
}
