package com.wirepn.android.vpn

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wirepn.android.MainActivity
import com.wirepn.android.R

object VpnNotificationManager {

    const val CHANNEL_ID = "wirepn_vpn_status"
    private const val NOTIFICATION_ID = 0x7767

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = android.app.NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_vpn_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_vpn_description)
            setShowBadge(false)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun update(context: Context, state: VpnConnectionState, profileName: String?) {
        val nm = NotificationManagerCompat.from(context)
        when (state) {
            is VpnConnectionState.Connected -> {
                val disconnect = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(VpnControlReceiver.ACTION_DISCONNECT_VPN).setPackage(context.packageName),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                val openApp = PendingIntent.getActivity(
                    context,
                    1,
                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                val name = profileName?.takeIf { it.isNotBlank() }
                    ?: context.getString(R.string.none_selected)
                val text = context.getString(R.string.notification_vpn_connected_text, name)
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.notification_vpn_title))
                    .setContentText(text)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(openApp)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(
                        0,
                        context.getString(R.string.disconnect),
                        disconnect,
                    )
                    .build()
                nm.notify(NOTIFICATION_ID, notification)
            }
            is VpnConnectionState.Connecting -> {
                val openApp = PendingIntent.getActivity(
                    context,
                    1,
                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                val label = context.getString(R.string.status_connecting)
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.notification_vpn_title))
                    .setContentText(label)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(openApp)
                    .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                    .build()
                nm.notify(NOTIFICATION_ID, notification)
            }
            is VpnConnectionState.Disconnected,
            is VpnConnectionState.Error,
            -> nm.cancel(NOTIFICATION_ID)
        }
    }
}
