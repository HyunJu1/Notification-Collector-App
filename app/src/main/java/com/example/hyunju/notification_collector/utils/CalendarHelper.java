package com.example.hyunju.notification_collector.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.CalendarContract;

public class CalendarHelper {
    public static long pushAppointmentsToCalender(ContentResolver cr, String title, String description, String place, long startDate) {
        ContentValues eventValues = new ContentValues();

        eventValues.put("calendar_id", 3);
        eventValues.put("title", title);
        eventValues.put("description", description);
        if (!place.equals("")) {
            eventValues.put("eventLocation", place);
        }

        eventValues.put("dtstart", startDate);
        eventValues.put("dtend", startDate + 500);

        eventValues.put("eventTimezone", "UTC/GMT +2:00");
        eventValues.put("hasAlarm", 1);

        Uri eventUri = cr.insert(
                CalendarContract.Events.CONTENT_URI, eventValues
        );
        long newEventID = Long.parseLong(eventUri.getLastPathSegment());

        return newEventID;
    }
}
