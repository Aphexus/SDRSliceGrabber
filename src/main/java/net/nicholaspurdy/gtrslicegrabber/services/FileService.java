package net.nicholaspurdy.gtrslicegrabber.services;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipInputStream;

/**
 * Abstracts away apache common's FileUtils to make the SliceGrabberTasklet more unit testable.
 *
 * Also provides a container for unzip logic.
 */
@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    public void copyURLtoFile(URL url, File file) throws IOException {
        log.info("Downloading " + url.toString());
        FileUtils.copyURLToFile(url, file);
    }

    /**
     * This method should unzip the slice file, give the csv file the same name (but with .csv of course), and also
     * delete the old zip file after it has been unzipped if told to.
     */
    public File unzip(File zipFile, boolean zipShouldBeDeleted) throws IOException {

        byte[] buffer = new byte[1024];

        FileInputStream fis = new FileInputStream(zipFile.getPath());
        ZipInputStream zis = new ZipInputStream(fis);
        zis.getNextEntry();

        File csvFile = new File("/tmp/" + zipFile.getName().replace("zip","csv"));

        log.info("Unzipping " + zipFile.getAbsolutePath());

        FileOutputStream fos = new FileOutputStream(csvFile);

        int len;
        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }


        fos.close();
        zis.closeEntry();
        zis.close();
        fis.close();

        if (zipShouldBeDeleted)
            if (!zipFile.delete()) log.warn("Could not delete " + zipFile.getName());



        return csvFile;
    }

}
