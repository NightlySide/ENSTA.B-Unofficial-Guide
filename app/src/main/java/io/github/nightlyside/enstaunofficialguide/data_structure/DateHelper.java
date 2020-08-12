package io.github.nightlyside.enstaunofficialguide.data_structure;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private Calendar calendar = Calendar.getInstance(Locale.FRANCE);

    public DateHelper() { }
    public DateHelper(Calendar calendar) { this.calendar = calendar; }
    public DateHelper(Date date) {
        setDate(date);
    }

    public int getYear() { return calendar.get(Calendar.YEAR); }
    public int getMonth() { return calendar.get(Calendar.MONTH); }
    public int getDay() { return calendar.get(Calendar.DAY_OF_MONTH); }
    public int getHour() { return calendar.get(Calendar.HOUR_OF_DAY); }
    public int getMinute() { return calendar.get(Calendar.MINUTE); }
    public String getStrHourMinute() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(calendar.getTime());
    }

    public void setYear(int year) { calendar.set(Calendar.YEAR, year); }
    public void setMonth(int month) { calendar.set(Calendar.MONTH, month); }
    public void setDay(int day) { calendar.set(Calendar.DAY_OF_MONTH, day); }
    public void setHour(int hour) { calendar.set(Calendar.HOUR_OF_DAY, hour); }
    public void setMinute(int minute) { calendar.set(Calendar.MINUTE, minute); }

    public void setDate(Date newDate) {calendar.setTime(newDate);}
    public Date getTime() { return calendar.getTime(); }
    public Calendar getCalendar() { return calendar; }

    public boolean isSameDayAs(DateHelper other) {
        return calendar.get(Calendar.DAY_OF_YEAR) == other.getCalendar().get(Calendar.DAY_OF_YEAR) &&
                calendar.get(Calendar.YEAR) == other.getCalendar().get(Calendar.YEAR);
    }

    public boolean isBefore(DateHelper other) {
        return calendar.before(other.getCalendar());
    }

    public boolean isAfter(DateHelper other) {
        return calendar.after(other.getCalendar());
    }

    static public String dateHelperToString(DateHelper helper) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy - HH'h'mm", Locale.FRANCE);
        return dateFormat.format(helper.getTime());
    }

    static public DateHelper stringToDateHelper(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy - HH'h'mm", Locale.FRANCE);
        try {
            Date newDate = dateFormat.parse(strDate);
            return new DateHelper(newDate);
        } catch (ParseException e) {
            Log.e("DateHelper", e.toString());
        }
        return null;
    }

    static public boolean eventInMonth(DateHelper startTime, DateHelper endTime, int year, int month) {
        return (startTime.getYear() == year && startTime.getMonth() == month - 1) || (endTime.getYear() == year && endTime.getMonth() == month - 1);
    }
}
