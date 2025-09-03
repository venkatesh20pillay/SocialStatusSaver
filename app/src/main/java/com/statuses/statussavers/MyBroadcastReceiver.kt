package com.statuses.statussavers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "SUBSCRIPTION") {
            Toast.makeText(context, "Received custom broadcast", Toast.LENGTH_SHORT).show()
        }
    }
}