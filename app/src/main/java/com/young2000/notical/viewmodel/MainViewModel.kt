package com.young2000.notical.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.young2000.notical.data.CalendarInfo
import com.young2000.notical.data.EventInfo

class MainViewModel : ViewModel() {
    
    private val _calendars = MutableLiveData<List<CalendarInfo>>()
    val calendars: LiveData<List<CalendarInfo>> = _calendars
    
    private val _events = MutableLiveData<List<EventInfo>>()
    val events: LiveData<List<EventInfo>> = _events
    
    private val _selectedCalendar = MutableLiveData<CalendarInfo?>()
    val selectedCalendar: LiveData<CalendarInfo?> = _selectedCalendar
    
    fun updateCalendars(calendars: List<CalendarInfo>) {
        _calendars.value = calendars
    }
    
    fun updateEvents(events: List<EventInfo>) {
        _events.value = events
    }
    
    fun selectCalendar(calendar: CalendarInfo) {
        _selectedCalendar.value = calendar
    }
    
    fun startNotificationService(notificationTimeMinutes: Int, soundEnabled: Boolean, vibrationEnabled: Boolean) {
        // This will be implemented in the service
    }
}
