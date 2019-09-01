package net.nicholaspurdy.gtrslicegrabber.job;

import net.nicholaspurdy.gtrslicegrabber.model.AssetClass;
import net.nicholaspurdy.gtrslicegrabber.model.SliceFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActionFieldItemProcessor implements ItemProcessor<SliceFileItem,SliceFileItem> {

    private static final Logger log = LoggerFactory.getLogger(ActionFieldItemProcessor.class);

    private final JdbcTemplate jdbc;

    @Autowired
    public ActionFieldItemProcessor(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public SliceFileItem process(SliceFileItem sliceFileItem) throws Exception {

        int i = 0;

        if ("CANCEL".equals(sliceFileItem.getACTION())) {
            i = jdbc.update("UPDATE " + AssetClass.convertAbbrv(sliceFileItem.getASSET_CLASS()) +
                    " SET CANCELED_BY = ? WHERE DISSEMINATION_ID = ?",
                    sliceFileItem.getDISSEMINATION_ID(), sliceFileItem.getORIGINAL_DISSEMINATION_ID());
        }
        else if ("CORRECT".equals(sliceFileItem.getACTION())) {
            i = jdbc.update("UPDATE " + AssetClass.convertAbbrv(sliceFileItem.getASSET_CLASS()) +
                            " SET CORRECTED_BY = ? WHERE DISSEMINATION_ID = ?",
                    sliceFileItem.getDISSEMINATION_ID(), sliceFileItem.getORIGINAL_DISSEMINATION_ID());
        }
        else {
            sliceFileItem.setPROCESSED(true);
            return sliceFileItem;
        }

        //if (i == 0) log.warn("CANCEL/CORRECT was not applied.");

        if (i > 1) throw new UnexpectedJobExecutionException("CANCEL/CORRECT was applied more than once.");

        sliceFileItem.setPROCESSED(i == 1);

        return sliceFileItem;
    }
}
