package rs.edu.raf.banka1.stocksimulation;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.Instant;

public class StockSimulationTrigger implements Trigger {
    @Override
    public Instant nextExecution(TriggerContext triggerContext) {
        return Instant.now().plusSeconds(5);
    }
}
