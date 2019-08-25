package net.nicholaspurdy.gtrslicegrabber.job;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class CustomBeanWrapperFieldSetMapper<T> extends BeanWrapperFieldSetMapper<T> {

    @Override
    protected void initBinder(DataBinder binder) {

        DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (!StringUtils.isEmpty(text)) {
                    setValue(LocalDate.parse(text, localDateFormatter));
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() throws IllegalArgumentException {
                Object date = getValue();
                if (date != null) {
                    return localDateFormatter.format((LocalDate) date);
                } else {
                    return "";
                }
            }
        });

        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (!StringUtils.isEmpty(text)) {
                    setValue(LocalDateTime.parse(text, localDateTimeFormatter));
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() throws IllegalArgumentException {
                Object date = getValue();
                if (date != null) {
                    return localDateTimeFormatter.format((LocalDateTime) date);
                } else {
                    return "";
                }
            }
        });

    } // end initBinder

}

