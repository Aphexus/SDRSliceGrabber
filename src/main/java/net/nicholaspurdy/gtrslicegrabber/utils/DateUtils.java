package net.nicholaspurdy.gtrslicegrabber.utils;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy_MM_dd");
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy_MM_dd");
    public static final LocalDate TODAY = LocalDate.now(Clock.systemUTC());

    private DateUtils() { }

}
