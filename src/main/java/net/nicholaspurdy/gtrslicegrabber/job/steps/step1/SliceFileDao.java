package net.nicholaspurdy.gtrslicegrabber.job.steps.step1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SliceFileDao {

    private final Logger log = LoggerFactory.getLogger(SliceFileDao.class);

    private final SimpleJdbcInsert jdbc;

    public SliceFileDao(DataSource dataSource) {
        this.jdbc = new SimpleJdbcInsert(dataSource)
                .withTableName("files")
                .usingGeneratedKeyColumns("file_id");
    }

    public int insertFileMetadata(String assetClass, String dateStr, String fileName) {

        Map<String,Object> args = new HashMap<>();
        args.put("FILE_NAME", fileName);
        args.put("ASSET_CLASS", assetClass);
        args.put("DATE_STR", dateStr);
        args.put("UPLOAD_TIMESTAMP", LocalDateTime.now(Clock.systemUTC()));

        log.info("Inserting metadata for " + fileName + " into FILES table.");

        return jdbc.executeAndReturnKey(args).intValue();

    }
}
