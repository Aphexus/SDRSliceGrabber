package net.nicholaspurdy.gtrslicegrabber.job.steps.shared;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@JobScope
@Getter
@Setter
public class CancelsAndCorrectsContainer {

    private final Map<Long,Long> cancellations;
    private final Map<Long,Long> corrections;

    public CancelsAndCorrectsContainer() {
        cancellations = new HashMap<>();
        corrections = new HashMap<>();
    }

}
