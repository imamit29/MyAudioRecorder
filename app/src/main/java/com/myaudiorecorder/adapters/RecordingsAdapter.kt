package com.myaudiorecorder.adapters

import android.graphics.Color
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myaudiorecorder.R
import com.myaudiorecorder.activities.SimpleActivity
import com.myaudiorecorder.extensions.formatDate
import com.myaudiorecorder.models.Recording
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import kotlinx.android.synthetic.main.item_recording.view.*
import java.util.*

class RecordingsAdapter(
    activity: SimpleActivity,
    var recordings: ArrayList<Recording>,
    recyclerView: RecyclerView,
    itemClick: (Any) -> Unit
) :
    MyRecyclerViewAdapter(activity, recyclerView, itemClick), RecyclerViewFastScroller.OnPopupTextUpdate {

    var currRecordingId = 0

    override fun getActionMenuId(): Int {
        TODO("Not yet implemented")
    }


    override fun prepareActionMode(menu: Menu) {
    }

    override fun actionItemPressed(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getSelectableItemCount() = recordings.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = recordings.getOrNull(position)?.id

    override fun getItemKeyPosition(key: Int) = recordings.indexOfFirst { it.id == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = createViewHolder(R.layout.item_recording, parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recording = recordings[position]
        holder.bindView(recording, true, true) { itemView, layoutPosition ->
            setupView(itemView, recording)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = recordings.size


    fun updateItems(newItems: ArrayList<Recording>) {
        if (newItems.hashCode() != recordings.hashCode()) {
            recordings = newItems
            notifyDataSetChanged()
            finishActMode()
        }
    }


    fun updateCurrentRecording(newId: Int) {
        val oldId = currRecordingId
        currRecordingId = newId
        notifyItemChanged(recordings.indexOfFirst { it.id == oldId })
        notifyItemChanged(recordings.indexOfFirst { it.id == newId })
    }

    private fun setupView(view: View, recording: Recording) {
        view.apply {
            recording_frame?.isSelected = selectedKeys.contains(recording.id)

            if (recording.id == currRecordingId) {
                recording_title.setTextColor(Color.BLACK)
                recording_playpause.setImageResource(R.drawable.ic_pause_vector)
            }else{
                recording_title.setTextColor(resources.getColor(R.color.purple_700))
                recording_playpause.setImageResource(R.drawable.ic_play_vector)
            }

            recording_title.text = recording.title
            recording_date.text = recording.timestamp.formatDate(context)
        }
    }

    override fun onChange(position: Int) = recordings.getOrNull(position)?.title ?: ""
}
