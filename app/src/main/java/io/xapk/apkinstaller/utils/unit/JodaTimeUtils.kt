package io.xapk.apkinstaller.utils.unit

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

object JodaTimeUtils {
    const val FORMAT_DATE = "yyyy-MM-dd"
    const val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm"
    const val FORMAT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_DATE_TIME_SECOND_GMT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val YEAR = (365 * 24 * 60 * 60).toLong()
    private const val MONTH = (30 * 24 * 60 * 60).toLong()
    private const val DAY = (24 * 60 * 60).toLong()
    private const val HOUR = (60 * 60).toLong()
    private const val MINUTE: Long = 60

    fun dateToString(data: Date, formatType: String): String {
        return DateTime(data).toString(formatType)
    }

    fun dateToString(data: DateTime, formatType: String): String {
        return DateTime(data).toString(formatType)
    }

    fun longToString(currentTime: Long, formatType: String): String {
        return DateTime(currentTime).toString(formatType)
    }

    fun stringToDate(strTime: String, formatType: String): DateTime {
        return DateTime.parse(strTime, DateTimeFormat.forPattern(formatType))
    }

    fun longToDate(currentTime: Long): Date {
        return DateTime(currentTime).toDate()
    }

    fun formatDataToShortDateInfo(data: Date) = dateToString(data, FORMAT_DATE_TIME_SECOND)
}