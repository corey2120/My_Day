package com.example.myday

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CalendarContract
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class DeviceCalendarSync(private val context: Context) {
    
    companion object {
        private const val TAG = "DeviceCalendarSync"
    }
    
    fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    suspend fun syncCalendarEvents(startDate: Date, endDate: Date): List<Task> {
        return withContext(Dispatchers.IO) {
            if (!hasCalendarPermission()) {
                Log.e(TAG, "Calendar permission not granted")
                return@withContext emptyList()
            }
            
            try {
                val tasks = mutableListOf<Task>()
                val contentResolver: ContentResolver = context.contentResolver
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                
                // Query calendar events
                val projection = arrayOf(
                    CalendarContract.Events._ID,
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DTEND,
                    CalendarContract.Events.CALENDAR_DISPLAY_NAME,
                    CalendarContract.Events.ALL_DAY
                )
                
                val selection = "(${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?)"
                val selectionArgs = arrayOf(
                    startDate.time.toString(),
                    endDate.time.toString()
                )
                
                val cursor: Cursor? = contentResolver.query(
                    CalendarContract.Events.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    "${CalendarContract.Events.DTSTART} ASC"
                )
                
                cursor?.use {
                    val idIndex = it.getColumnIndex(CalendarContract.Events._ID)
                    val titleIndex = it.getColumnIndex(CalendarContract.Events.TITLE)
                    val startIndex = it.getColumnIndex(CalendarContract.Events.DTSTART)
                    val calendarIndex = it.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME)
                    val allDayIndex = it.getColumnIndex(CalendarContract.Events.ALL_DAY)
                    
                    while (it.moveToNext()) {
                        val eventId = it.getLong(idIndex)
                        val title = it.getString(titleIndex) ?: "Untitled Event"
                        val startMillis = it.getLong(startIndex)
                        val calendarName = it.getString(calendarIndex) ?: "Calendar"
                        val allDay = it.getInt(allDayIndex) == 1
                        
                        val eventDate = Date(startMillis)
                        val dateTimeString = if (allDay) {
                            // For all-day events, just use the date
                            SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.getDefault()).format(eventDate)
                        } else {
                            dateFormat.format(eventDate)
                        }
                        
                        val task = Task(
                            id = UUID.randomUUID().toString(),
                            description = "$title (from $calendarName)",
                            dateTime = dateTimeString,
                            isCompleted = false,
                            listId = "device_calendar"
                        )
                        tasks.add(task)
                        Log.d(TAG, "Synced event: ${task.description} at ${task.dateTime}")
                    }
                }
                
                Log.d(TAG, "Successfully synced ${tasks.size} calendar events")
                tasks
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing calendar events", e)
                emptyList()
            }
        }
    }
    
    suspend fun getAllUpcomingEvents(daysAhead: Int = 30): List<Task> {
        val calendar = Calendar.getInstance()
        val startDate = calendar.time
        
        calendar.add(Calendar.DAY_OF_YEAR, daysAhead)
        val endDate = calendar.time
        
        return syncCalendarEvents(startDate, endDate)
    }
    
    suspend fun getAvailableCalendars(): List<CalendarInfo> {
        return withContext(Dispatchers.IO) {
            if (!hasCalendarPermission()) {
                return@withContext emptyList()
            }
            
            try {
                val calendars = mutableListOf<CalendarInfo>()
                val contentResolver: ContentResolver = context.contentResolver
                
                val projection = arrayOf(
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                    CalendarContract.Calendars.ACCOUNT_NAME,
                    CalendarContract.Calendars.CALENDAR_COLOR
                )
                
                val cursor: Cursor? = contentResolver.query(
                    CalendarContract.Calendars.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
                )
                
                cursor?.use {
                    val idIndex = it.getColumnIndex(CalendarContract.Calendars._ID)
                    val nameIndex = it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                    val accountIndex = it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
                    
                    while (it.moveToNext()) {
                        val id = it.getLong(idIndex)
                        val name = it.getString(nameIndex)
                        val account = it.getString(accountIndex)
                        
                        calendars.add(CalendarInfo(id, name, account))
                    }
                }
                
                calendars
            } catch (e: Exception) {
                Log.e(TAG, "Error getting calendars", e)
                emptyList()
            }
        }
    }
}

data class CalendarInfo(
    val id: Long,
    val name: String,
    val accountName: String
)
