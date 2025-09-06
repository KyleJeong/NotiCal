package com.young2000.notical.data

data class EventInfo(
    val id: Long,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val description: String,
    val location: String
)
