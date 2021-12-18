package com.myaudiorecorder.activities

import android.content.Intent
import android.os.Bundle
import com.myaudiorecorder.R
import com.myaudiorecorder.adapters.ViewPagerAdapter
import com.myaudiorecorder.helpers.*
import com.myaudiorecorder.services.RecorderService
import com.myaudiorecorder.utils.PERMISSION_RECORD_AUDIO
import com.myaudiorecorder.utils.PERMISSION_WRITE_STORAGE
import com.myaudiorecorder.utils.STOP_AMPLITUDE_UPDATE
import com.myaudiorecorder.utils.isQPlus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : SimpleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //taking user permission for record the audio
        handlePermission(PERMISSION_RECORD_AUDIO) {
            if (it) {
                tryInitVoiceRecorder()
            } else {
                finish()
            }
        }
    }

    private fun tryInitVoiceRecorder() {
        if (isQPlus()) {
            setupViewPager()
        } else {

            //handle user permission for writing storage
            handlePermission(PERMISSION_WRITE_STORAGE) {
                if (it) {
                    setupViewPager()
                } else {
                    finish()
                }
            }
        }
    }

    //Settings up viewpager
    private fun setupViewPager() {
        view_pager.adapter = ViewPagerAdapter(this)
        view_pager.onPageChangeListener {
            main_tabs_holder.getTabAt(it)?.select()
            (view_pager.adapter as ViewPagerAdapter).finishActMode()
        }

        main_tabs_holder.onTabSelectionChanged(
            tabUnselectedAction = {
            },
            tabSelectedAction = {
                view_pager.currentItem = it.position
            }
        )
    }

    //getting the pagerAdapter
    private fun getPagerAdapter() = (view_pager.adapter as? ViewPagerAdapter)

    override fun onResume() {
        super.onResume()
        getPagerAdapter()?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        getPagerAdapter()?.onDestroy()

        Intent(this@MainActivity, RecorderService::class.java).apply {
            action = STOP_AMPLITUDE_UPDATE
            try {
                startService(this)
            } catch (ignored: Exception) {
            }
        }
    }

}