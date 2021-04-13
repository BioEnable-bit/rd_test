package `in`.bioenable.rdservice.fp.contracts

interface IScannerService {

    enum class ScannerStatus {
        READY,NOT_READY
    }

    interface Callbacks {
        fun onOpened()
        fun onCaptured(bitmap:Any?,quality:Int)
        fun onCaptured(iso:ByteArray,type:Int,quality:Int)
        fun onCaptureTimedOut()
        fun onCaptureCancelled()
        fun onErrorOccurred(error:Int)
        fun onDeviceDisconnected()
    }

    fun capture(type:Int, timeout:Int)
    fun openDevice()
    fun cancelCapture()
    fun getStatus (): ScannerStatus
    fun getSerial():String
}