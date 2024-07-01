package rs.edu.raf.banka1.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.MarginAccount;
import rs.edu.raf.banka1.model.MarginTransaction;
import rs.edu.raf.banka1.services.MarginAccountService;
import rs.edu.raf.banka1.services.MarginTransactionService;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MarginSummaryScheduler {

    private final MarketService marketService;
    private final MarginAccountService marginAccountService;
    private final MarginTransactionService marginTransactionService;


    /*
    Should check maintenance margins and set margin call to true if needed
     */
    @Scheduled(fixedRate = 3600*24*1000) //Once per day
//    @Scheduled(fixedRate = 60*1000)
    void checkMarginCalls() {
        List<MarginAccount> accounts = marginAccountService.getAllMarginAccountEntities();

        for (MarginAccount account : accounts) {
            Map<ListingBaseDto, Double> positions = marginTransactionService.getAllMarginPositions(account);

            double capitalWorth = 0d;

            for(ListingBaseDto listingBaseDto : positions.keySet()) {
                capitalWorth += positions.get(listingBaseDto) * listingBaseDto.getPrice() * 100;
            }

            double equity = capitalWorth - account.getLoanValue();
            double maintenanceMargin = capitalWorth * Constants.MAINTENANCE_MARGIN_RATE;

            if(equity < maintenanceMargin) {
                //Trigger margin call
                //Schedule new event to trigger liquidation after midnight
                marginAccountService.triggerMarginCall(account);
            }

            //Set new equity
            //Set new maintenance margin
            marginAccountService.updateOnMarginSummary(account, equity, maintenanceMargin);
        }
    }
}
