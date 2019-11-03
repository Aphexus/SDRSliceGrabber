package net.nicholaspurdy.sdrslicegrabber.job.steps.step2;

import net.nicholaspurdy.sdrslicegrabber.job.steps.shared.CancelsAndCorrectsContainer;
import net.nicholaspurdy.sdrslicegrabber.model.SliceFileItem;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@JobScope
public class CancelOrCorrectItemProcessor implements ItemProcessor<SliceFileItem,SliceFileItem> {

    private final CancelsAndCorrectsContainer container;

    @Autowired
    public CancelOrCorrectItemProcessor(CancelsAndCorrectsContainer container) {
        this.container = container;
    }

    @Override
    public SliceFileItem process(SliceFileItem item) throws Exception {

        if ("CANCEL".equals(item.getACTION())) {
            container.getCancellations().put(item.getDISSEMINATION_ID(), item.getORIGINAL_DISSEMINATION_ID());
        }
        else if ("CORRECT".equals(item.getACTION())) {
            container.getCorrections().put(item.getDISSEMINATION_ID(), item.getORIGINAL_DISSEMINATION_ID());
        }

        return item;
    }
}
