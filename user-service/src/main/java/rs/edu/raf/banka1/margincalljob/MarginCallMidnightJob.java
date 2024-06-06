package rs.edu.raf.banka1.margincalljob;

import lombok.RequiredArgsConstructor;
import rs.edu.raf.banka1.model.MarginAccount;
import rs.edu.raf.banka1.services.MarginAccountService;

@RequiredArgsConstructor
public class MarginCallMidnightJob implements Runnable {
    private final MarginAccount account;
    private final MarginAccountService marginAccountService;
    @Override
    public void run() {
        marginAccountService.triggerMarginCallAutomaticLiquidation(account);
    }
}
