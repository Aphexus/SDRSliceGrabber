package net.nicholaspurdy.sdrslicegrabber.job.steps.step2;

import net.nicholaspurdy.sdrslicegrabber.model.SliceFileItem;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

public class SanityCheckItemProcessor implements ItemProcessor<SliceFileItem,SliceFileItem> {


    @Override
    public SliceFileItem process(SliceFileItem sliceFileItem) throws Exception {

        if (sliceFileItem.getDISSEMINATION_ID() == null)
            throw new UnexpectedJobExecutionException("No DISSEMINATION_ID detected.");

        if (StringUtils.isEmpty(sliceFileItem.getASSET_CLASS()))
            throw new UnexpectedJobExecutionException("No ASSET_CLASS detected.");

        if (StringUtils.isEmpty(sliceFileItem.getACTION()))
            throw new UnexpectedJobExecutionException("No ACTION detected.");

        if(sliceFileItem.getORIGINAL_DISSEMINATION_ID() != null
                && sliceFileItem.getORIGINAL_DISSEMINATION_ID() >= sliceFileItem.getDISSEMINATION_ID())
            throw new UnexpectedJobExecutionException("ORIGINAL_DISSEMINATION_ID is greater than or equal to DISSEMINATION_ID");


        return sliceFileItem;
    }

}
