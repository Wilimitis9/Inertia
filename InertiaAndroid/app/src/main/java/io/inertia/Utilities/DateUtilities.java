package io.inertia.Utilities;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Luke Wilimitis on 10/26/16.
 */
public class DateUtilities {

    public static Date addIntervalAmount(Date date, int calendarInterval, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendarInterval, amount);
        return calendar.getTime();
    }
}
