package com.young2000.notical.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.young2000.notical.R
import com.young2000.notical.data.CalendarInfo

class CalendarAdapter(
    private val onCalendarClick: (CalendarInfo) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    
    private var calendars = listOf<CalendarInfo>()
    
    fun updateCalendars(calendars: List<CalendarInfo>) {
        this.calendars = calendars
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar, parent, false)
        return CalendarViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(calendars[position])
    }
    
    override fun getItemCount(): Int = calendars.size
    
    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.calendarNameTextView)
        private val accountTextView: TextView = itemView.findViewById(R.id.calendarAccountTextView)
        
        fun bind(calendar: CalendarInfo) {
            nameTextView.text = calendar.displayName.ifEmpty { calendar.name }
            accountTextView.text = calendar.accountName
            
            itemView.setOnClickListener {
                onCalendarClick(calendar)
            }
        }
    }
}
