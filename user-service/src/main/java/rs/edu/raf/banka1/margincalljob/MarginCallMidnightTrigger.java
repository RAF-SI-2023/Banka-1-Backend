package rs.edu.raf.banka1.margincalljob;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MarginCallMidnightTrigger implements Trigger {
    @Override
    public Instant nextExecution(TriggerContext triggerContext) {
        if(triggerContext.lastActualExecution() == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        return midnight.toInstant(ZoneOffset.UTC);
    }
}
