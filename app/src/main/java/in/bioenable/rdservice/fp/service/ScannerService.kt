package `in`.bioenable.rdservice.fp.service

import `in`.bioenable.rdservice.fp.R
import `in`.bioenable.rdservice.fp.contracts.IScanner
import `in`.bioenable.rdservice.fp.contracts.IScannerService
import `in`.bioenable.rdservice.fp.ui.HomeActivity
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.usb.UsbManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class ScannerService : Service(), IScannerService, IScanner.Callbacks {

    private val TAG = ScannerService::class.java.simpleName

    private val ui = Handler(Looper.getMainLooper())

    private val binder = ServiceBinder()

    private var callbacks : IScannerService.Callbacks? = null

    private var scanner : IScanner? = null

    private val USB_PERMISSION = "in.bioenabletech.rdservice.action.USB_MANUAL_PERMISSION"

    private var deviceStatus = IScannerService.ScannerStatus.NOT_READY // NOT_READY replaced by by USED for testing (Yogehsh Aswar)

    private var serial = ""

    inner class ServiceBinder : Binder() {

        fun getService(callbacks: IScannerService.Callbacks): IScannerService {
            this@ScannerService.callbacks = callbacks
            return this@ScannerService
        }
    }

    private val receiver = object : BroadcastReceiver(){

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent==null) return
            when(intent.action){
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    Log.e(TAG,"USB_DEVICE_ATTACHED")
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOmwnForeground()
//                    else startForeground(7, Notification())
                    Toast.makeText(context,"USB Device Attched",Toast.LENGTH_SHORT).show()
//                    scanner = NitgenScanner(this@ScannerService,this@ScannerService)
//                    Thread(Runnable { scanner.open() }).start()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    Log.e(TAG,"USB_DEVICE_DETACHED")
                    removeNotification()
                }
                USB_PERMISSION -> {

                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG,"onCreate()")
        val filter = IntentFilter(USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(receiver,filter)
//        scanner = NitgenScanner(this,this)
//        Thread(Runnable { scanner.open() }).start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG,"onStartCommand()")
     //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

            //startMyOmwnForeground() else startForeground(7, Notification())
     //   startMyOmwnForeground()
        scanner = NitgenScanner(this@ScannerService,this@ScannerService)
        try {
            Thread(Runnable {
                scanner?.open() }).start()
        }catch (e:Exception) {
            Log.e(TAG, "onStartCommand: $e")
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e(TAG,"onBind()")
        return binder
    }

    override fun capture(type: Int, timeout:Int) {
        Thread(Runnable{scanner?.capture(type,timeout)}).start()
    }

    override fun openDevice() {
        Thread(Runnable { scanner?.open() }).start()
    }

    override fun cancelCapture() {
        scanner?.cancelCapture()
    }

    @Synchronized
    override fun getStatus() = deviceStatus

    @Synchronized
    override fun getSerial() = serial

    override fun onOpened(serial:String) {
        Log.e(TAG,"onOpened(): serial: $serial")
        this.serial = serial
        this.deviceStatus = IScannerService.ScannerStatus.READY // Ready replaced by USED
        ui.post {
            callbacks?.onOpened()
        }
    }

    override fun onCaptured(bitmap: Any?, quality: Int) {
        ui.post{
            callbacks?.onCaptured(bitmap,quality)
        }
    }

    override fun onCaptured(isofir: ByteArray, isofmr: ByteArray, type: Int, quality: Int) {
        ui.post{
            callbacks?.onCaptured(isofir,isofmr,type,quality)
        }
    }

    override fun onCapturedFIR(isofir: ByteArray, type: Int, quality: Int) {
        ui.post{
            callbacks?.onCapturedFIR(isofir,type,quality)
        }
    }

    override fun onCapturedFMR(isofmr: ByteArray, type: Int, quality: Int) {
        ui.post{
            callbacks?.onCapturedFMR(isofmr,type,quality)
        }
    }

//    override fun onCaptured(iso: ByteArray, type: Int,quality:Int) {
//        ui.post {
//            callbacks?.onCaptured(iso,type,quality)
//        }
//    }



    override fun onCaptureCancelled() {
        ui.post {
            callbacks?.onCaptureCancelled()
        }
    }

    override fun onErrorOccurred(error: Int) {
        ui.post {
            callbacks?.onErrorOccurred(error)
        }
    }

    override fun onCaptureTimedOut() {
        ui.post{
            callbacks?.onCaptureTimedOut()
        }
    }

    override fun onDisconnected() {
        this.deviceStatus = IScannerService.ScannerStatus.NOT_READY // NOT_READY replaced by by USED for testing (Yogehsh Aswar)
        this.serial = ""
        ui.post {
            callbacks?.onDeviceDisconnected()
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private fun startMyOmwnForeground() {
//        val NOTIFICATION_CHANNEL_ID = "MKN"
//        val channelName = "My Background Service"
//        val pi = PendingIntent.getActivity(this, 45, Intent(this, HomeActivity::class.java), 0)
//        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
//        chan.lightColor = Color.BLUE
//        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
//        manager.createNotificationChannel(chan)
//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//        val notification = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, getString(R.string.channel_id)) else Notification.Builder(this))
//                .setContentTitle("BioEnable fingerprint scanner connected")
//                .setContentText("The device is in use. Tap to see status.")
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentIntent(pi)
//                .build()
//        startForeground(7, notification)
//    }

//    private fun showNotification(){
//        val pi = PendingIntent.getActivity(this,13,Intent(this,HomeActivity::class.java),0)
//        val notification = Notification.Builder(this)
//                .setContentTitle("BioEnable fingerprint scanner connected")
//                .setContentText("The device is in use. Tap to see status.")
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentIntent(pi)
//                .build()
//        startForeground(7,notification)
//    }

    private fun removeNotification() = stopForeground(true)

    override fun onDestroy() {
        Log.e(TAG,"onDestroy()")
        unregisterReceiver(receiver)
//        try{
//            scanner.dispose()
//        } catch (e:Exception){}
        super.onDestroy()
    }
}
