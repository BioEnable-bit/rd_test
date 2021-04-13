package `in`.bioenable.rdservice.fp.contracts

interface CallHandlerView : BaseView {
    fun showMessage(msg:String)
    fun hideMessage()
    fun showFinger(index:Int,timeout:Int)
    fun onInfoResult(deviceInfoXML:String,rdServiceXML:String)
    fun onCaptureResult(pidDataXML:String)

//    fun hideProgress()

    //    fun showAllFingers()

    //    fun showProgress()
//    fun showNextButton()
//    fun hideNextButton()


//    fun showPhoneVerificationDialog(phoneNumber:String)
//    fun showOTPValidationDialog(phoneNumber:String,seconds:Int)
}