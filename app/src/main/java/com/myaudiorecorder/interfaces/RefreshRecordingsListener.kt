package com.myaudiorecorder.interfaces

import com.myaudiorecorder.models.Recording


interface RefreshRecordingsListener {
    fun refreshRecordings()

    fun playRecording(recording: Recording, playOnPrepared: Boolean)
}
