package com.example.taskmanagement.shared;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String getFileExtension( Uri uri , ContentResolver contentResolver ){
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public static String getDaysUntilStartDate( String startDate , String endDate){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            Date today = new Date();

//            Calendar calendarStart = Calendar.getInstance();
//            calendarStart.setTime(start);
//            calendarStart.set(Calendar.HOUR_OF_DAY, 0);
//            calendarStart.set(Calendar.MINUTE, 0);
//            calendarStart.set(Calendar.SECOND, 0);
//            calendarStart.set(Calendar.MILLISECOND, 0);
//
//            Calendar calendarToday = Calendar.getInstance();
//            calendarToday.setTime(today);
//            calendarToday.set(Calendar.HOUR_OF_DAY, 0);
//            calendarToday.set(Calendar.MINUTE, 0);
//            calendarToday.set(Calendar.SECOND, 0);
//            calendarToday.set(Calendar.MILLISECOND, 0);

            long differenceInMillis = start.getTime() - today.getTime();
            long differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24);

            if ( differenceInDays >= 30 ) {
                long months = differenceInDays / 30;
                return "Starts in " + months + " months";
            } else if ( differenceInDays > 1) {
                return "Starts in " + differenceInDays + " days";
            } else if (differenceInDays == 1) {
                return "Starts tomorrow";
            } else if (differenceInDays == 0) {
                return "Starts today";
            } else {

                long diffInDaysToEnd = end.getTime() - today.getTime() ;
                if (diffInDaysToEnd >= 0) {
                    // L'événement n'est pas encore terminé
                    return "Commenced";
                } else {
                    // L'événement est déjà terminé
                    return "Ended";
                }


            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Date invalide";
        }
    }
}
