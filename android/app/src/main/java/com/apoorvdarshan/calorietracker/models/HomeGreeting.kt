package com.apoorvdarshan.calorietracker.models

import java.time.LocalDate
import java.time.DayOfWeek

data class HomeGreeting(val id: String, val chinese: String, val english: String)

object HomeGreetings {
    private val holidays = mapOf(
        "元旦" to listOf("新年好，今天也有小美好", "新的一年，按自己的节奏来", "愿新的一年，吃好也睡好"),
        "春节" to listOf("新春快乐，愿你自在又明亮", "新的一年，慢慢走也很好", "春节快乐，吃好，也休息好"),
        "除夕" to listOf("除夕到了，愿你温暖又安心", "岁末团圆，好好享用这一餐", "今晚，把美好留给自己"),
        "元宵" to listOf("元宵快乐，愿日子圆圆满满", "灯火可亲，今天也有小甜蜜", "元宵夜，愿你心里暖暖的"),
        "端午" to listOf("端午安康，愿今天清爽自在", "一叶粽香，愿你平安舒心", "端午到了，好好享用这一餐"),
        "七夕" to listOf("七夕快乐，也记得喜欢自己", "把温柔留一点给自己", "七夕到了，愿你被温柔以待"),
        "中秋" to listOf("中秋快乐，愿你心里有团圆", "月圆人安，慢慢享用这一餐", "中秋到了，给日子添一点甜"),
        "重阳" to listOf("今日重阳，愿你平安舒心", "重阳到了，给牵挂的人问个好", "愿这个秋日，多一点从容"),
        "劳动节" to listOf("劳动节快乐，辛苦的你歇一歇", "今天，也给自己一点闲暇", "愿你忙有所获，闲有所乐"),
        "国庆" to listOf("国庆快乐，今天也要自在一点", "金秋好时节，愿你吃好玩好", "国庆快乐，给自己一点好心情")
    )
    fun festival(date: LocalDate): String? = GreetingCalendar.lunarFestivals[date.toString()] ?: when (date.monthValue to date.dayOfMonth) {
        1 to 1 -> "元旦"
        5 to 1 -> "劳动节"
        10 to 1 -> "国庆"
        else -> null
    }
    fun candidates(date: LocalDate, hour: Int): List<HomeGreeting> {
        val festival = festival(date)
        if (festival != null) return holidays.getValue(festival).mapIndexed { i, text ->
            HomeGreeting("holiday:$festival:$i", text, listOf("Wishing you a lovely holiday", "A little joy for your day", "Take a moment for yourself")[i])
        }
        val term = GreetingCalendar.solarTerms[date.toString()]
        if (term != null) {
            val text = if (term == "清明") listOf("清明时节，愿牵挂都有安放", "今日清明，愿你平安从容", "把思念放在心里，也照顾好自己")
                else listOf("今日${term}，和新时节打个招呼", "${term}到了，愿今天轻松一点", "又到${term}，好好吃饭，好好生活")
            return text.mapIndexed { i, t -> HomeGreeting("term:$term:$i", t,
                listOf("A new season, a gentle hello", "A little ease for your day", "Welcome the season at your own pace")[i]) }
        }
        val timed = when (hour) {
            in 5..10 -> listOf("早上好，慢慢开启新一天", "早安，今天也有小美好")
            in 11..13 -> listOf("中午好，好好享用这一餐", "忙到现在，给自己片刻放松")
            in 14..17 -> listOf("下午好，让节奏轻一点", "午后好，给自己一点好心情")
            in 18..22 -> listOf("晚上好，今天辛苦啦", "一天慢慢收尾，照顾好自己")
            else -> listOf("夜深了，愿你今晚睡个好觉", "放慢一点，给自己一点休息")
        }
        val lines = timed + listOf("嗨，今天也好好照顾自己", "慢慢来，找到舒服的节奏", "吃好一点，心情也轻一点", "欢迎回来，今天过得怎么样") +
            if (date.dayOfMonth == 1) listOf("新的一个月，慢慢开始吧", "新的一页，愿你自在从容")
            else if (date.dayOfWeek in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) listOf("周末好，给自己一点闲暇", "周末的你，可以慢一点")
            else emptyList()
        return lines.mapIndexed { i, text -> HomeGreeting("daily:$text", text,
            listOf("Hello, take today at your own pace", "A little care for yourself today", "Welcome back, how is your day?", "Make room for a little joy")[i % 4]) }
    }
    fun next(date: LocalDate, hour: Int, previousId: String?): HomeGreeting {
        val pool = candidates(date, hour)
        return pool[(pool.indexOfFirst { it.id == previousId } + 1) % pool.size]
    }
}
