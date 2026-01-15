package com.johan.racketmatchapp.presentation.sync

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import timber.log.Timber

class PhoneSyncService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        when (messageEvent.path) {
            WearPaths.MATCH_START -> {
                Timber.d("johan MATCH_START")
                //val intent = Intent(this, MatchActivity::class.java)
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //startActivity(intent)
            }
        }
    }
}