package net.nicholaspurdy.sdrslicegrabber.services;

import net.nicholaspurdy.sdrslicegrabber.job.steps.step1.FileService;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class TestFileService {

    private static File unzippedFile;

    @Test
    public void testUnzip() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File zipFile =  new File(classLoader.getResource("testfiles/CUMULATIVE_RATES_2019_03_20.zip").getFile());


        FileService fileService = new FileService();

        unzippedFile = fileService.unzip(zipFile, false);

        assertTrue("CUMULATIVE_RATES_2019_03_20.csv".equals(unzippedFile.getName()));

    }

    @AfterClass
    public static void tearDown() {
        unzippedFile.delete();
    }

}
