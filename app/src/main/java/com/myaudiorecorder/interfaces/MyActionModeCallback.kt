package com.myaudiorecorder.interfaces

import androidx.appcompat.view.ActionMode

abstract class MyActionModeCallback : ActionMode.Callback {
    var isSelectable = false
}
