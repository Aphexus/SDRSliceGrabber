package net.nicholaspurdy.sdrslicegrabber.job.steps.step4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class CorrectsAndCancelsDao {

    private final JdbcTemplate jdbc;

    @Autowired
    public CorrectsAndCancelsDao(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Transactional
    public int processCancel(Map.Entry<Long,Long> entry, String assetClass) {

        int i = jdbc.update("UPDATE " + assetClass + " SET CANCELED_BY = " + entry.getKey() +
                " WHERE DISSEMINATION_ID = " + entry.getValue());

        if (i == 1) jdbc.update("UPDATE " + assetClass + " SET PROCESSED = true WHERE DISSEMINATION_ID = " +
                entry.getKey());

        return i;

    }

    @Transactional
    public int processCorrect(Map.Entry<Long,Long> entry, String assetClass) {

        int i = jdbc.update("UPDATE " + assetClass + " SET CORRECTED_BY = " + entry.getKey() +
                " WHERE DISSEMINATION_ID = " + entry.getValue());

        if (i == 1) jdbc.update("UPDATE " + assetClass + " SET PROCESSED = true WHERE DISSEMINATION_ID = " +
                entry.getKey());

        return i;

    }

}
