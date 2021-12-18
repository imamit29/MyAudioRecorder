package com.myaudiorecorder.utils

import android.content.Context
import android.text.format.DateFormat
import com.myaudiorecorder.R
import com.myaudiorecorder.extensions.baseConfig
import com.myaudiorecorder.extensions.getInternalStoragePath
import com.myaudiorecorder.extensions.getSharedPrefs
import com.myaudiorecorder.extensions.getStorageDirectories
import java.io.File
import java.text.SimpleDateFormat
import java.util.regex.Pattern

open class BaseConfig(val context: Context) {
    protected val prefs = context.getSharedPrefs()

    companion object {
        fun newInstance(context: Context) = BaseConfig(context)
    }

    var textColor: Int
        get() = prefs.getInt(
            TEXT_COLOR, context.resources.getColor(
                R.color.white))
        set(textColor) = prefs.edit().putInt(TEXT_COLOR, textColor).apply()

    var appId: String
        get() = prefs.getString(APP_ID, "")!!
        set(appId) = prefs.edit().putString(APP_ID, appId).apply()

    var sdTreeUri: String
        get() = prefs.getString(SD_TREE_URI, "")!!
        set(uri) = prefs.edit().putString(SD_TREE_URI, uri).apply()

    var sdCardPath: String
        get() = prefs.getString(SD_CARD_PATH, getDefaultSDCardPath())!!
        set(sdCardPath) = prefs.edit().putString(SD_CARD_PATH, sdCardPath).apply()

    private fun getDefaultSDCardPath() = if (prefs.contains(SD_CARD_PATH)) "" else context.getSDCardPath()

    fun Context.getSDCardPath(): String {
        val directories = getStorageDirectories().filter {
            !it.equals(getInternalStoragePath()) && !it.equals(
                "/storage/emulated/0",
                true
            )
        }

        val fullSDpattern = Pattern.compile(SD_OTG_PATTERN)
        var sdCardPath = directories.firstOrNull { fullSDpattern.matcher(it).matches() }
            ?: directories.firstOrNull { !physicalPaths.contains(it.toLowerCase()) } ?: ""

        // on some devices no method retrieved any SD card path, so test if its not sdcard1 by any chance. It happened on an Android 5.1
        if (sdCardPath.trimEnd('/').isEmpty()) {
            val file = File("/storage/sdcard1")
            if (file.exists()) {
                return file.absolutePath
            }

            sdCardPath = directories.firstOrNull() ?: ""
        }

        if (sdCardPath.isEmpty()) {
            val SDpattern = Pattern.compile(SD_OTG_SHORT)
            try {
                File("/storage").listFiles()?.forEach {
                    if (SDpattern.matcher(it.name).matches()) {
                        sdCardPath = "/storage/${it.name}"
                    }
                }
            } catch (e: Exception) {
            }
        }

        val finalPath = sdCardPath.trimEnd('/')
        baseConfig.sdCardPath = finalPath
        return finalPath
    }

    var use24HourFormat: Boolean
        get() = prefs.getBoolean(USE_24_HOUR_FORMAT, DateFormat.is24HourFormat(context))
        set(use24HourFormat) = prefs.edit().putBoolean(USE_24_HOUR_FORMAT, use24HourFormat).apply()

    var dateFormat: String
        get() = prefs.getString(DATE_FORMAT, getDefaultDateFormat())!!
        set(dateFormat) = prefs.edit().putString(DATE_FORMAT, dateFormat).apply()

    private fun getDefaultDateFormat(): String {
        val format = DateFormat.getDateFormat(context)
        val pattern = (format as SimpleDateFormat).toLocalizedPattern()
        return when (pattern.toLowerCase().replace(" ", "")) {
            "d.M.y" -> DATE_FORMAT_ONE
            "dd/mm/y" -> DATE_FORMAT_TWO
            "mm/dd/y" -> DATE_FORMAT_THREE
            "y-mm-dd" -> DATE_FORMAT_FOUR
            "dmmmmy" -> DATE_FORMAT_FIVE
            "mmmmdy" -> DATE_FORMAT_SIX
            "mm-dd-y" -> DATE_FORMAT_SEVEN
            "dd-mm-y" -> DATE_FORMAT_EIGHT
            else -> DATE_FORMAT_ONE
        }
    }

    var primaryColor: Int
        get() = prefs.getInt(PRIMARY_COLOR, context.resources.getColor(R.color.colorPrimary))
        set(primaryColor) = prefs.edit().putInt(PRIMARY_COLOR, primaryColor).apply()

    var backgroundColor: Int
        get() = prefs.getInt(BACKGROUND_COLOR, context.resources.getColor(R.color.background))
        set(backgroundColor) = prefs.edit().putInt(BACKGROUND_COLOR, backgroundColor).apply()

    var accentColor: Int
        get() = prefs.getInt(ACCENT_COLOR, context.resources.getColor(R.color.colorPrimary))
        set(accentColor) = prefs.edit().putInt(ACCENT_COLOR, accentColor).apply()


}
