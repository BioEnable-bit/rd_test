package `in`.bioenable.rdservice.fp.contracts

interface IScanner {
    fun open()
    fun capture(type:Int,timeout:Int)
    fun cancelCapture()
//    fun dispose()

    interface Callbacks {
        fun onOpened(serial:String)
        fun onCaptured(bitmap:Any?,quality:Int)
       fun onCaptured(isofir:ByteArray,isofmr:ByteArray,type:Int,quality:Int)
         fun onCapturedFIR(isofir:ByteArray,type:Int,quality:Int)
        fun onCapturedFMR(isofmr:ByteArray,type:Int,quality:Int)

        fun onCaptureCancelled()
        fun onErrorOccurred(error:Int)
        fun onCaptureTimedOut()
        fun onDisconnected()

    }
}