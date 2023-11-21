package xyz.needpainkiller.core.helper;

import xyz.needpainkiller.core.lib.exceptions.error.BusinessException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static xyz.needpainkiller.core.lib.exceptions.CommonErrorCode.DATETIME_PARSE_FAILED;

@UtilityClass
@Slf4j
public class TimeHelper {
    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    public static final Locale LOCALE = Locale.KOREA;
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone(TimeHelper.ZONE_ID);
    public static final DateTimeFormatter DEF_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DEF_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter DEF_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Long DAY_MILLISECOND = 86400000L;


    public static Timestamp now() {
//        ZonedDateTime zonedDateTime = ZonedDateTime.now(getContextZoneId());
//        return Timestamp.from(zonedDateTime.toInstant());
        return Timestamp.from(Instant.now());
    }

    public static LocalDate nowLocalDate() {
        return LocalDate.now(ZONE_ID);
    }

    public static LocalTime nowLocalTime() {
        return LocalTime.now(ZONE_ID);
    }

    public static String nowLocalDateString() {
        return DEF_DATE_FORMAT.format(LocalDate.now(ZONE_ID));
    }

    public static String localDateString(LocalDate localDate) {
        return DEF_DATE_FORMAT.format(localDate);
    }

    public static LocalDateTime nowLocalDateTime() {
        return LocalDateTime.now(ZONE_ID);
    }

    public static long nowEpochDay() {
        return nowLocalDate().toEpochDay();
    }

    public static long epochDay(LocalDate localDate) {
        return localDate.toEpochDay();
    }

    public static Timestamp fromDateStringToTimestamp(String source) {
        LocalDate localDate = LocalDate.parse(source, DEF_DATE_FORMAT);
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    public static Timestamp fromDateTimeStringToTimestamp(String source) {
        LocalDateTime localDateTime = LocalDateTime.from(DEF_DATE_TIME_FORMAT.parse(source));
        return Timestamp.valueOf(localDateTime);
    }


    public static Timestamp fromLocalDateToTimestamp(LocalDate source) {
        return Timestamp.valueOf(source.atStartOfDay());
    }

    public static Timestamp fromLocalDateTimeToTimestamp(LocalDateTime source) {
        return Timestamp.valueOf(source);
    }


    public static String fromTimestampToString(Timestamp source) {
        return DEF_DATE_TIME_FORMAT.format(source.toLocalDateTime());
    }

    public static String fromTimestampToDateString(Timestamp source) {
        return DEF_DATE_FORMAT.format(source.toLocalDateTime());
    }

    public static LocalDate fromStringToLocalDate(String source) throws BusinessException {
        try {
            return LocalDate.parse(source, DEF_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(DATETIME_PARSE_FAILED, e.getMessage());
        }
    }

    public static LocalDateTime fromStringToLocalDateTime(String source) {
        return LocalDateTime.from(DEF_DATE_TIME_FORMAT.parse(source));
    }


    public static LocalTime fromStringToLocalTime(String source) {
        return LocalTime.from(DEF_TIME_FORMAT.parse(source));
    }

    public static LocalDate fromTimestampToLocalDate(Timestamp source) {
        return source.toLocalDateTime().toLocalDate();
    }

    public static LocalDateTime fromTimestampToLocalDateTime(Timestamp source) {
        return source.toLocalDateTime();
    }

    public static Date fromLocalDateTimeToDate(LocalDateTime source) {
        return Date.from(source.atZone(ZONE_ID).toInstant());
    }

    public static String fromDateToString(Date source) {
        return SIMPLE_DATE_TIME_FORMAT.format(source);
    }

    public static GregorianCalendar fromLocalDateToCalendar(LocalDate source) {
        return GregorianCalendar.from(source.atStartOfDay(ZONE_ID));
    }

    public static Integer dateToMonotonicInt(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        return (year * 10000) + (month * 100) + day;
    }

    public LocalDateTime concat(LocalDate localDate, LocalTime localTime) {
        return LocalDateTime.of(localDate, localTime);
    }

    public Timestamp concatToTimestamp(LocalDate localDate, LocalTime localTime) {
        return Timestamp.valueOf(LocalDateTime.of(localDate, localTime));
    }

    public long calcTurnaroundTimeSecond(long before, long after) {
        return TimeUnit.MILLISECONDS.toSeconds(calcTurnaroundTimeMillisecond(before, after));
    }

    private long calcTurnaroundTimeMillisecond(long before, long after) {
        long diff = after - before;
        if (diff < 0) diff = -diff;
        return diff;
    }

    public static long parseRunningTime(Timestamp start, Timestamp end) {
        return calcTurnaroundTimeMillisecond(start.getTime(), end.getTime());
    }

    public static String parseRunningTimeString(long millisecond) {
        if (millisecond > DAY_MILLISECOND) {
            return DurationFormatUtils.formatDuration(millisecond, "d일 HH:mm:ss", true);
        } else {
            return DurationFormatUtils.formatDuration(millisecond, "HH:mm:ss", true);
        }
    }


    public static long getEpochWeeks(LocalDate localDate) {
        LocalDate epoch = LocalDate.ofEpochDay(0);
        return ChronoUnit.WEEKS.between(epoch, localDate);
    }
}