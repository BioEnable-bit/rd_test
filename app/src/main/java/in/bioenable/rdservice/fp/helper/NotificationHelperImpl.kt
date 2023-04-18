package `in`.bioenable.rdservice.fp.helper

import `in`.bioenable.rdservice.fp.R
import `in`.bioenable.rdservice.fp.contracts.NotificationHelper
import `in`.bioenable.rdservice.fp.ui.StatusActivity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast

class NotificationHelperImpl(val context: Context) : NotificationHelper {

    private val nm : NotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private val NOTIFICATION_PHONE_VERIFICATION = 4984
    private val NOTIFICATION_ACTIVATION = 8976
    private val NOTIFICATION_PUBLIC_DEVICE = 9715
    private val NOTIFICATION_VERSION_UPGRADE = 1636
    private val NOTIFICATION_DEVICE_REGISTRATION = 6748

    fun sendNotification(id:Int, title: String, msg: String){

        Toast.makeText(context, "$id,$title,$msg",Toast.LENGTH_SHORT).show()



//        val not = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Notification.Builder(context,context.getString(R.string.notification_channel_id))
//        } else {
//            Notification.Builder(context)
//                    .setSound(soundUri())
//        }
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentText(msg)
//                .setContentTitle(title)
//                .setAutoCancel(true)
//                .setContentIntent(
//                        PendingIntent.getActivity(
//                                context,
//                                454,
//                                Intent(context,StatusActivity::class.java),
//                                0))
//                .build()
//
//        nm.notify(id,not)

//        val pi = PendingIntent.getActivity(context,0, Intent(context, activityKlass),0)
//        val notification = Notification.Builder(context)
//                .setContentTitle(title)
//                .setContentText(msg)
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentIntent(pi)
//                .build()
//        nm.notify(id,notification)

    }

    fun removeNotification(id: Int) {
        nm.cancel(id)
    }

    private fun soundUri(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        RingtoneManager.getRingtone(this,rtUri).play()
    }

    override fun showPhoneVerificationRequiredNotification() {
        sendNotification(
                NOTIFICATION_PHONE_VERIFICATION,
                "Phone verification required",
                "Phone verification is required. Tap to verify your phone number.")
    }

    override fun showDeviceRegistrationRequiredNotification() {
        sendNotification(
                NOTIFICATION_DEVICE_REGISTRATION,
                "Device initialization required.",
                "Device is not initialized. Tap to see status."
        )
    }

    override fun showActivationRequiredNotification() {
        sendNotification(
                NOTIFICATION_ACTIVATION,
                "Device activation required.",
                "Device activation is required. Tap to see status."
        )
    }

    override fun showPublicDeviceNotification(){
        sendNotification(
                NOTIFICATION_PUBLIC_DEVICE,
                "Public device.",
                "Connected device is not allowed. Top to see more."
        )
    }

    override fun showVersionUpgradeNotification() {
        sendNotification(
                NOTIFICATION_VERSION_UPGRADE,
                "Upgrade RDService",
                "A newer version of RDService is available. Tap to upgrade RDService to the latest version."
        )
    }
}