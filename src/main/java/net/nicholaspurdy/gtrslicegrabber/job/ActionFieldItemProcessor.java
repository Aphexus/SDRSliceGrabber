package net.nicholaspurdy.gtrslicegrabber.job;

import net.nicholaspurdy.gtrslicegrabber.model.SliceFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        if ("CANCEL".equals(sliceFileItem.getACTION())) {
            log.info("Got a cancel.");
        }
        else if ("CORRECT".equals(sliceFileItem.getACTION())) {
            log.info("Got a correct.");
        }

        return sliceFileItem;
    }
}
