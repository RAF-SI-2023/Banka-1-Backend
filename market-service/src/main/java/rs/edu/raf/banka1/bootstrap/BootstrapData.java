package rs.edu.raf.banka1.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import rs.edu.raf.banka1.model.listing.MyStock;
import rs.edu.raf.banka1.repositories.otc.MyStockRepository;

import java.util.List;

@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final MyStockRepository myStockRepository;

    @Override
    public void run(String... args) throws Exception {
        Logger.info("All data loaded...");
        if (myStockRepository.count() == 0) {
            MyStock stok1 = new MyStock();
            stok1.setTicker("STK1");
            stok1.setAmount(100);
            stok1.setCurrencyMark("RSD");
            stok1.setPrivateAmount(50);
            stok1.setPublicAmount(50);
            stok1.setCompanyId(1L);
            stok1.setUserId(null);
            stok1.setMinimumPrice(500.0);

            MyStock stok2 = new MyStock();
            stok2.setTicker("STK2");
            stok2.setAmount(100);
            stok2.setCurrencyMark("RSD");
            stok2.setPrivateAmount(50);
            stok2.setPublicAmount(50);
            stok2.setCompanyId(1L);
            stok2.setUserId(null);
            stok2.setMinimumPrice(1500.0);

            MyStock stok3 = new MyStock();
            stok3.setTicker("STK3");
            stok3.setAmount(100);
            stok3.setCurrencyMark("RSD");
            stok3.setPrivateAmount(50);
            stok3.setPublicAmount(50);
            stok3.setCompanyId(1L);
            stok3.setUserId(null);
            stok3.setMinimumPrice(200.0);

            myStockRepository.saveAll(List.of(stok1, stok2, stok3));
        }
    }
}
