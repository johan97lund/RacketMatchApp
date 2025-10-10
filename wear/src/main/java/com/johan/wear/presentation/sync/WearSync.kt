package com.johan.wear.presentation.sync

import android.content.Context
import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.tasks.await

object WearPaths {
    const val MATCH_START = "/match/start"
}

class WearSync(private val context: Context) {
    private val msgClient get() = Wearable.getMessageClient(context)
    private val nodeClient get() = Wearable.getNodeClient(context)

    suspend fun sendStart() {
        val nodes = nodeClient.connectedNodes.await()
        for (n in nodes) {
            msgClient.sendMessage(n.id, WearPaths.MATCH_START, ByteArray(0)).await()
        }
    }
}