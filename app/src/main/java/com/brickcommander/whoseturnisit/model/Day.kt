package com.brickcommander.whoseturnisit.model

import java.time.DayOfWeek

enum class Day(val value: Int) {
    Sunday(8), Monday(1), Tuesday(2), Wednesday(3), Thursday(4), Friday(5), SaturdayMorning(6), SaturdayEvening(7), Default(9);

    companion object {
        fun fromKey(str: String): Day {
            for (day in entries) {
                if (day.name == str) {
                    return day
                }
            }
            return Default
        }
        fun convertToDay(dayOfWeek: DayOfWeek): Day {
            if(dayOfWeek == DayOfWeek.SUNDAY) return Sunday
            if(dayOfWeek == DayOfWeek.MONDAY) return Monday
            if(dayOfWeek == DayOfWeek.TUESDAY) return Tuesday
            if(dayOfWeek == DayOfWeek.WEDNESDAY) return Wednesday
            if(dayOfWeek == DayOfWeek.THURSDAY) return Thursday
            if(dayOfWeek == DayOfWeek.FRIDAY) return Friday
            if(dayOfWeek == DayOfWeek.SATURDAY) return SaturdayMorning
            return SaturdayEvening
        }
    }
}