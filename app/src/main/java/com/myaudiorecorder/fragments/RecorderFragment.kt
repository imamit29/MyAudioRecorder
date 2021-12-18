package com.myaudiorecorder.fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import com.myaudiorecorder.R
import com.myaudiorecorder.extensions.*
import com.myaudiorecorder.models.Events
import com.myaudiorecorder.services.RecorderService
import com.myaudiorecorder.utils.*
import kotlinx.android.synthetic.main.fragment_recorder.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class RecorderFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet) {
    private var status = RECORDING_STOPPED
    private var pauseBlinkTimer = Timer()
    private var bus: EventBus? = null

    override fun onResume() {
        if (!RecorderService.isRunning) status = RECORDING_STOPPED
        refreshView()
    }

    override fun onDestroy() {

        //unregistering the EventBus
        bus?.unregister(this)
        pauseBlinkTimer.cancel()
    }


    //this function used for attach the fragment and initiate the view
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        recorder_visualizer.recreate()

        //unregistering the EventBus
        bus = EventBus.getDefault()
        bus!!.register(this)

        updateRecordingDuration(0)
        toggle_recording_button.setOnClickListener {
            toggleRecording()
        }

        toggle_pause_button.setOnClickListener {
            Intent(context, RecorderService::class.java).apply {
                action = TOGGLE_PAUSE
                context.startService(this)
            }
        }

        Intent(context, RecorderService::class.java).apply {
            action = GET_RECORDER_INFO
            try {
                context.startService(this)
            } catch (e: Exception) {
            }
        }
    }

    //this function is used for update the duration with selected format
    private fun updateRecordingDuration(duration: Int) {
        recording_duration.text = duration.getFormattedDuration()
    }

    //This function is used to change runtime Recording button drawable
    private fun getToggleButtonIcon(): Drawable {
        val drawable = if (status == RECORDING_RUNNING || status == RECORDING_PAUSED) R.drawable.ic_stop_vector else R.drawable.ic_microphone_vector
        return resources.getColoredDrawableWithColor(drawable, context.getAdjustedPrimaryColor().getContrastColor())
    }

    //This function check the recording processes
    private fun toggleRecording() {
        status = if (status == RECORDING_RUNNING || status == RECORDING_PAUSED) {
            RECORDING_STOPPED
        } else {
            RECORDING_RUNNING
        }

        toggle_recording_button.setImageDrawable(getToggleButtonIcon())

        if (status == RECORDING_RUNNING) {
            startRecording()
        } else {
            toggle_pause_button.beGone()
            stopRecording()
        }
    }

    //This function give command to start recording
    private fun startRecording() {
        Intent(context, RecorderService::class.java).apply {
            context.startService(this)
        }
        recorder_visualizer.recreate()
    }

    //This function give command to stop recording
    private fun stopRecording() {
        Intent(context, RecorderService::class.java).apply {
            context.stopService(this)
        }
    }

    //Pause button blink task
    private fun getPauseBlinkTask() = object : TimerTask() {
        override fun run() {
            if (status == RECORDING_PAUSED) {
                // update just the alpha so that it will always be clickable
                Handler(Looper.getMainLooper()).post {
                    toggle_pause_button.alpha = if (toggle_pause_button.alpha == 0f) 1f else 0f
                }
            }
        }
    }

    //Refresh the view for play pause action
    private fun refreshView() {
        toggle_recording_button.setImageDrawable(getToggleButtonIcon())
        toggle_pause_button.beVisibleIf(status != RECORDING_STOPPED && isNougatPlus())
        if (status == RECORDING_PAUSED) {
            pauseBlinkTimer = Timer()
            pauseBlinkTimer.scheduleAtFixedRate(getPauseBlinkTask(), 500, 500)
        } else {
            pauseBlinkTimer.cancel()
        }

        if (status == RECORDING_RUNNING) {
            toggle_pause_button.alpha = 1f
        }
    }

    //Mandatory for EventBus Subscription
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotDurationEvent(event: Events.RecordingDuration) {
        updateRecordingDuration(event.duration)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotStatusEvent(event: Events.RecordingStatus) {
        status = event.status
        refreshView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotAmplitudeEvent(event: Events.RecordingAmplitude) {
        val amplitude = event.amplitude
        if (status == RECORDING_RUNNING) {
            recorder_visualizer.update(amplitude)
        }
    }
}
