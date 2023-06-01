package `in`.bioenable.rdservice.fp.contracts

interface IScannerService {

    enum class ScannerStatus {
        READY,NOT_READY,USED // new variable added (Yogesh) for test purpose
    }

    interface Callbacks {
        fun onOpened()
        fun onCaptured(bitmap:Any?,quality:Int)
       // fun onCaptured(iso:ByteArray,type:Int,quality:Int)
        fun onCaptured(isofir:ByteArray,isofmr:ByteArray,type:Int,quality:Int)
        fun onCapturedFIR(isofir:ByteArray,type:Int,quality:Int)
        fun onCapturedFMR(isofmr:ByteArray,type:Int,quality:Int)
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