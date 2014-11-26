
package com.android.lib.core.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class DateTimeUtil {
    /**
     * Tính toán khoảng cách ngày giữa ngày hiện tại và ngày truyền vào (day between)
     * 
     * @param date
     *            dd MMM yyyy
     * @param locale
     * @param format
     * @return days number
     */
    public static long calculatorDaysAgo(String date, Locale locale, String format) {
        Calendar today = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
        try {
            cal.setTime(formatter.parse(date));
        } catch (ParseException e) {
            DebugLog.e("ParseException");
            return 0;
        }
        return (today.getTimeInMillis() - cal.getTimeInMillis()) / (24 * 60 * 60 * 1000);
    }

    public static String getCurrenDateString() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setTimeZone(cal.getTimeZone());
        return dateFormat.format(cal.getTime());
    }

    public static Calendar getCalendarFromDateString(String dateString, String format, Locale locale) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(formatter.parse(dateString));
        } catch (ParseException e) {
            DebugLog.e("ParseException");
            return null;
        }
        return cal;
    }

    /**
     * @param cal
     * @param locale
     * @return
     */
    public static String format(Calendar cal, String format, Locale locale) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
        return formatter.format(cal.getTime());
    }
}
