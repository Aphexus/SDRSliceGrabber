package net.nicholaspurdy.gtrslicegrabber.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.ExceptionHandler;

public class TaskletExceptionHandler implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(TaskletExceptionHandler.class);

    @Override
    public void handleException(RepeatContext repeatContext, Throwable throwable) throws Throwable {

        // TODO: option to retry tasklet by not rethrowing the exception, ex. the file may not be there yet.
        throw throwable;
    }
}
