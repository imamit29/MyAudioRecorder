package com.myaudiorecorder.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import com.myaudiorecorder.R

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var hideNotification: Boolean
        get() = prefs.getBoolean(HIDE_NOTIFICATION, false)
        set(hideNotification) = prefs.edit().putBoolean(HIDE_NOTIFICATION, hideNotification).apply()

    var saveRecordingsFolder: String
        get() = prefs.getString(SAVE_RECORDINGS, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString()+"/AudioMemo")!!
        set(saveRecordingsFolder) = prefs.edit().putString(SAVE_RECORDINGS, saveRecordingsFolder).apply()

    var extension: Int
        get() = prefs.getInt(EXTENSION, EXTENSION_M4A)
        set(extension) = prefs.edit().putInt(EXTENSION, extension).apply()

    var bitrate: Int
        get() = prefs.getInt(BITRATE, DEFAULT_BITRATE)
        set(bitrate) = prefs.edit().putInt(BITRATE, bitrate).apply()

    fun getExtensionText() = context.getString(when (extension) {
        EXTENSION_M4A -> R.string.m4a
        EXTENSION_OGG -> R.string.ogg
        else -> R.string.mp3
    })

    fun getOutputFormat() = when (extension) {
        EXTENSION_OGG -> MediaRecorder.OutputFormat.OGG
        else -> MediaRecorder.OutputFormat.MPEG_4
    }

    fun getAudioEncoder() = when (extension) {
        EXTENSION_OGG -> MediaRecorder.AudioEncoder.OPUS
        else -> MediaRecorder.AudioEncoder.AAC
    }

    fun String.getFilenameFromPath() = substring(lastIndexOf("/") + 1)

    fun String.getParentPath() = removeSuffix("/${getFilenameFromPath()}")
}

