package net.nicholaspurdy.gtrslicegrabber.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@DataJdbcTest
public class TestSliceFileDao {

    @Autowired
    private DataSource dataSource;

    private SliceFileDao dao;

    @Test
    public void contextLoads() {

        SliceFileDao dao = new SliceFileDao(dataSource);

        int id1 = dao.insertFileMetadata("MyFileName.zip", null, null);
        assertEquals(1, id1);

        int id2 = dao.insertFileMetadata("MyFileName.zip", null, null);
        assertEquals(2, id2);

    }
}
