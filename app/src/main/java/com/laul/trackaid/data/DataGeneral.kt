package com.laul.trackaid.data

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class DataGeneral {

    companion object {
        /**
         * Calculates time ranges based on the duration to cover
         * @param duration number of days as Int
         * @return Time for now, start of current day, and start of entire range in millis
         */
        fun getTimes(   duration: Int): List<Long>{
            val cal = GregorianCalendar()
            val TimeNowInMilli = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 0) //anything 0 - 23
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val TimeEndInMilli =  cal.timeInMillis
            val TimeStartInMilli = TimeEndInMilli - ((duration-1)*24*60*60*1000)
            return listOf(TimeNowInMilli, TimeStartInMilli, TimeEndInMilli)
        }


        fun createTimes(duration: Int): List<Instant>{
            val now = ZonedDateTime.now()
            val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            val start = startOfDay.toInstant().minus(duration.toLong(), ChronoUnit.DAYS).atZone(
                ZoneId.systemDefault())

            return listOf(start.toInstant(), now.toInstant())
        }

        /**
         * Return date in specified format.
         * @param milliSeconds Date in milliseconds
         * @param dateFormat Date format
         * @return String representing date in specified format
         */
        fun getDate(milliSeconds: Long, dateFormat: String?): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }


    }
}