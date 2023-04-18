package `in`.bioenable.rdservice.fp.model

interface DomainModel {

    interface DomainModelCallback {
        fun onLongProcessingStarted(msg:String)
        fun onLongProcessingCompleted()
        fun onCheckDeviceReady()
        fun onSerialNumberRequired()
//        fun onPhoneVerificationRequired(phoneNumber:String)
//        fun onEmailVerificationRequired(email: String)
//        fun onOtpRequired(phoneNumber:String,seconds:Int)
//        fun onActivationRequired()
        fun onIsoRequired(type:Int,timeout:Int,index:Int)
        fun onInfoXMLsPrepared(deviceInfoXML:String,rdServiceInfoXML:String)
        fun onPidDataXMLPrepared(pidDataXML:String)
    }

    fun prepareInfoXMLs()
    fun preparePidDataXML(pidOptionsXML:String?)
    fun submitSerialNumber(serial:String)
//    fun submitPhoneNumber(phoneNumber:String)
//    fun submitOtp(phoneNumber:String,otp:String)
   // fun submitIso(iso:ByteArray,type:Int,quality:Int)
    fun submitIso(isofir:ByteArray,isofmr:ByteArray,type:Int,quality:Int)
    fun submitIsoFIR(isofir:ByteArray,type:Int,quality:Int)
    fun submitIsoFMR(isofmr:ByteArray,type:Int,quality:Int)

    fun submitDeviceReady()
    fun submitDeviceNotReady()
    fun submitCaptureTimedOut()
    fun submitCaptureError()

}
