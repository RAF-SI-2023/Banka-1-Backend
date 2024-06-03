package rs.edu.raf.banka1.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class MarginSummaryScheduler {
    @Scheduled(fixedRate = 3600*24*1000) //Once per day
    void checkMarginCalls() {

    }
}
