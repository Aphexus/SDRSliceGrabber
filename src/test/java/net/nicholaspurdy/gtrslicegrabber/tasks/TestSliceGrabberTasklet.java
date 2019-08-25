package net.nicholaspurdy.gtrslicegrabber.tasks;

import net.nicholaspurdy.gtrslicegrabber.dao.SliceFileDao;
import net.nicholaspurdy.gtrslicegrabber.services.FileService;
import net.nicholaspurdy.gtrslicegrabber.services.S3Uploader;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.test.context.junit4.SpringRunner;
import software.amazon.awssdk.core.exception.SdkException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * This class test the execute() method SliceGrabberTasklet for all the possible exceptions.
 */
@RunWith(SpringRunner.class)
public class TestSliceGrabberTasklet {

    @MockBean
    private S3Uploader s3Uploader;

    @MockBean
    private SliceFileDao sliceFileDao;

    @MockBean
    private FileService fileService;

    @Mock
    private StepContribution stepContribution;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    private static JobParameters jobParameters;

    @BeforeClass
    public static void setup() {
        Map<String,JobParameter> map = new HashMap<>();
        map.put("assetClassParam", new JobParameter("INVALID"));
        map.put("dateStrParam", new JobParameter("INVALID"));
        jobParameters = new JobParameters(map);
    }

    @Test
    public void testForIOException() {

        when(chunkContext.getStepContext().getStepExecution().getJobParameters()).thenReturn(jobParameters);

        SliceGrabberTasklet tasklet = new SliceGrabberTasklet(s3Uploader, sliceFileDao, new FileService());

        try {
            tasklet.execute(stepContribution, chunkContext);
        }
        catch (UnexpectedJobExecutionException e) {
            assertEquals(IOException.class, e.getCause().getClass());
        }

    }

    @Test
    public void testForSdkException() throws IOException {

        when(chunkContext.getStepContext().getStepExecution().getJobParameters()).thenReturn(jobParameters);
        doNothing().when(fileService).copyURLtoFile(any(URL.class), any(File.class));
        when(s3Uploader.upload(any(File.class))).thenThrow(SdkException.class);

        SliceGrabberTasklet tasklet = new SliceGrabberTasklet(s3Uploader, sliceFileDao, fileService);

        try {
            tasklet.execute(stepContribution, chunkContext);
        }
        catch (UnexpectedJobExecutionException e) {
            assertEquals(SdkException.class, e.getCause().getClass());
        }

    }

    @Test
    public void testForDataAccessException() throws IOException {

        when(chunkContext.getStepContext().getStepExecution().getJobParameters()).thenReturn(jobParameters);
        doNothing().when(fileService).copyURLtoFile(any(URL.class), any(File.class));
        when(s3Uploader.upload(any(File.class))).thenReturn(null);
        when(sliceFileDao.insertFileMetadata(anyString(), anyString(), anyString())).thenThrow(QueryTimeoutException.class);

        SliceGrabberTasklet tasklet = new SliceGrabberTasklet(s3Uploader, sliceFileDao, fileService);

        try {
            tasklet.execute(stepContribution, chunkContext);
        }
        catch (UnexpectedJobExecutionException e) {
            assertEquals(QueryTimeoutException.class, e.getCause().getClass());
        }

    }

    // TODO
    @Test
    public void testForFileMetadataId() throws IOException {
        // not really interested in testing anything further tbh
    }



}
