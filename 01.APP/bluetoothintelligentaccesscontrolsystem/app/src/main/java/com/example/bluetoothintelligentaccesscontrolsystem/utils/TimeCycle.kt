package com.example.bluetoothintelligentaccesscontrolsystem.utils

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间工具
 */
object TimeCycle {

    /**
     * 获取当前时间
     * return yyyy-MM-dd HH:mm:ss
     */
    fun getDateTime() : String{
        //转换为中国时区
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-8"))
        return transToString(System.currentTimeMillis())
    }
    /**
     * 时间戳转字符串
     * @param Timestamp
     * @return String
     */
    private fun transToString(time:Long):String{
        val date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
//        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time)
    }
    /**
     * 字符串转时间戳
     * @param String
     * @return Timestamp
     */
    fun transToTimeStamp(date:String):Long{
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date, ParsePosition(0)).time
    }
}
