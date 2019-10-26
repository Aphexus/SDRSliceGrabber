package net.nicholaspurdy.gtrslicegrabber.job.steps.step1;

import net.nicholaspurdy.gtrslicegrabber.utils.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * The responsibility of this bean is to download the file, save it some object storage (S3), and then write to some DB
 * that the file was successfully downloaded. The ID number of that file in the DB should be passed to the next step in
 * the job by way of the ChunkContext.
 */
@Component
public class SliceGrabberTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(SliceGrabberTasklet.class);
    private static final String LINK = "https://kgc0418-tdw-data2-0.s3.amazonaws.com/slices/CUMULATIVE_";

    private final S3Uploader s3Uploader;
    private final SliceFileDao sliceFileDao;
    private final FileService fileService;

    @Autowired
    public SliceGrabberTasklet(S3Uploader s3Uploader, SliceFileDao sliceFileDao, FileService fileService) {
        this.s3Uploader = s3Uploader;
        this.sliceFileDao = sliceFileDao;
        this.fileService = fileService;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
        String assetClass = jobParameters.getString("assetClassParam");
        String dateStr = jobParameters.getString("dateStrParam");

        String urlStr = LINK + assetClass + "_" + dateStr + ".zip";
        File zipFile = new File("/tmp/" + "CUMULATIVE_" + assetClass + "_" + dateStr + ".zip");

        try {

            fileService.copyURLtoFile(new URL(urlStr), zipFile);
            log.info("Zip file size for " + assetClass + " " + dateStr + ": " + FormatUtils.getSize(zipFile));

            try {
                s3Uploader.upload(zipFile);
            }
            catch (SdkException e) {
                log.warn("Exception occurred while trying to upload " + zipFile.getName(), e);
            }

            int id = sliceFileDao.insertFileMetadata(assetClass, dateStr, zipFile.getName());
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("fileId", id);

            File file = fileService.unzip(zipFile, true);
            log.info("CSV file size for " + assetClass + " " + dateStr + ": " + FormatUtils.getSize(file));
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("file", file);

        }
        catch (IOException e) {
            log.error("Exception occurred while trying to download " + urlStr, e);
            throw new UnexpectedJobExecutionException("IOException occurred.", e);
        }
        catch (DataAccessException e) {
            log.error("Exception occurred while trying to update DB.", e);
            throw new UnexpectedJobExecutionException("DataAccessException occurred.", e);
        }

        return RepeatStatus.FINISHED;
    }

}
