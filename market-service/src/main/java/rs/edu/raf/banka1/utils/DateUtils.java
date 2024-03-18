package rs.edu.raf.banka1.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {
    // Method to compare two Date objects by day
    public static boolean datesOnSameDay(Date date1, Date date2) {

        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year1 = localDate1.getYear();
        int day1 = localDate1.getDayOfYear();

        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year2 = localDate2.getYear();
        int day2 = localDate2.getDayOfYear();

        return year1 == year2 && day1 == day2;
    }
}
