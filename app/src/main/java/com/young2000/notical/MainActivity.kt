package com.young2000.notical

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.young2000.notical.adapter.CalendarAdapter
import com.young2000.notical.adapter.EventAdapter
import com.young2000.notical.data.CalendarInfo
import com.young2000.notical.data.EventInfo
import com.young2000.notical.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var viewModel: MainViewModel
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var eventAdapter: EventAdapter
    private lateinit var notificationTimeSpinner: Spinner
    private lateinit var soundCheckBox: CheckBox
    private lateinit var vibrationCheckBox: CheckBox
    private lateinit var startServiceButton: Button
    
    private val PERMISSION_REQUEST_CODE = 1001
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupViewModel()
        setupRecyclerViews()
        setupSpinner()
        setupClickListeners()
        
        checkPermissions()
    }
    
    private fun initViews() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        eventRecyclerView = findViewById(R.id.eventRecyclerView)
        notificationTimeSpinner = findViewById(R.id.notificationTimeSpinner)
        soundCheckBox = findViewById(R.id.soundCheckBox)
        vibrationCheckBox = findViewById(R.id.vibrationCheckBox)
        startServiceButton = findViewById(R.id.startServiceButton)
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        viewModel.calendars.observe(this) { calendars ->
            calendarAdapter.updateCalendars(calendars)
        }
        
        viewModel.events.observe(this) { events ->
            eventAdapter.updateEvents(events)
        }
    }
    
    private fun setupRecyclerViews() {
        calendarAdapter = CalendarAdapter { calendar ->
            viewModel.selectCalendar(calendar)
            loadEventsForCalendar(calendar.id)
        }
        
        eventAdapter = EventAdapter()
        
        calendarRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = calendarAdapter
        }
        
        eventRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = eventAdapter
        }
    }
    
    private fun setupSpinner() {
        val timeOptions = arrayOf("5분", "10분", "15분", "30분")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        notificationTimeSpinner.adapter = adapter
    }
    
    private fun setupClickListeners() {
        startServiceButton.setOnClickListener {
            if (viewModel.selectedCalendar.value != null) {
                val notificationTime = getNotificationTimeInMinutes()
                val soundEnabled = soundCheckBox.isChecked
                val vibrationEnabled = vibrationCheckBox.isChecked
                
                viewModel.startNotificationService(notificationTime, soundEnabled, vibrationEnabled)
                Toast.makeText(this, "알림 서비스가 시작되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "달력을 먼저 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.POST_NOTIFICATIONS
        )
        
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            loadCalendars()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                loadCalendars()
            } else {
                Toast.makeText(this, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadCalendars() {
        val calendars = mutableListOf<CalendarInfo>()
        
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )
        
        val cursor: Cursor? = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                val name = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.NAME))
                val accountName = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME))
                val accountType = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE))
                val displayName = it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                
                calendars.add(CalendarInfo(id, name ?: "", accountName ?: "", accountType ?: "", displayName ?: ""))
            }
        }
        
        viewModel.updateCalendars(calendars)
    }
    
    private fun loadEventsForCalendar(calendarId: Long) {
        val events = mutableListOf<EventInfo>()
        
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION
        )
        
        val selection = " = ? AND  > ?"
        val selectionArgs = arrayOf(calendarId.toString(), System.currentTimeMillis().toString())
        
        val cursor: Cursor? = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            " ASC"
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events._ID))
                val title = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.TITLE))
                val startTime = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART))
                val endTime = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Events.DTEND))
                val description = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION))
                val location = it.getString(it.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION))
                
                events.add(EventInfo(id, title ?: "", startTime, endTime, description ?: "", location ?: ""))
            }
        }
        
        viewModel.updateEvents(events)
    }
    
    private fun getNotificationTimeInMinutes(): Int {
        return when (notificationTimeSpinner.selectedItemPosition) {
            0 -> 5
            1 -> 10
            2 -> 15
            3 -> 30
            else -> 5
        }
    }
}
