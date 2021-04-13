package `in`.bioenable.rdservice.fp.contracts

interface IScanner {
    fun open()
    fun capture(type:Int,timeout:Int)
    fun cancelCapture()
//    fun dispose()

    interface Callbacks {
        fun onOpened(serial:String)
        fun onCaptured(bitmap:Any?,quality:Int)
        fun onCaptured(iso:ByteArray,type:Int,quality:Int)
        fun onCaptureCancelled()
        fun onErrorOccurred(error:Int)
        fun onCaptureTimedOut()
        fun onDisconnected()
    }
}