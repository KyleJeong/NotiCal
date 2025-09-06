package com.young2000.notical.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.young2000.notical.R
import com.young2000.notical.data.EventInfo
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    
    private var events = listOf<EventInfo>()
    
    fun updateEvents(events: List<EventInfo>) {
        this.events = events
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }
    
    override fun getItemCount(): Int = events.size
    
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.eventTitleTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.eventTimeTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.eventLocationTextView)
        
        fun bind(event: EventInfo) {
            titleTextView.text = event.title
            
            val dateFormat = SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault())
            val startTime = dateFormat.format(Date(event.startTime))
            val endTime = dateFormat.format(Date(event.endTime))
            timeTextView.text = "$startTime - $endTime"
            locationTextView.text = event.location.ifEmpty { "위치 정보 없음" }
        }
    }
}
