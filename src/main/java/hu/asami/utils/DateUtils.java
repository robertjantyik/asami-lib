package hu.asami.utils;

import lombok.experimental.UtilityClass;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.Date;

@UtilityClass
public class DateUtils {
    public static int getWeek(Date day){
        ZoneId zoneId = ZoneId.of("Europe/Budapest");
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(day.toInstant(), zoneId);
        return zonedDateTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }
}
