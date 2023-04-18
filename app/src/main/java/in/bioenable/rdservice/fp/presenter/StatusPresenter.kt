package `in`.bioenable.rdservice.fp.presenter

import `in`.bioenable.rdservice.fp.contracts.*
import `in`.bioenable.rdservice.fp.network.ActivationInfo
import `in`.bioenable.rdservice.fp.network.OtpvInfo
import `in`.bioenable.rdservice.fp.network.VersionInfo
import java.text.SimpleDateFormat
import java.util.*

class StatusPresenter(val view : StatusView,val app:App) : BasePresenter(view),
        WebService.PhoneVerificationInteractor.PhoneVerificationListener,
        WebService.OtpValidationInteractor.OtpVerificationListener,
        IScannerService.Callbacks,
        IPhoneVerificationPresenter
{

    private var service : IScannerService? = null
    // todo revert
    private var serial : String = ""
    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale("EN","IN"))

    private var phoneVerificationView : IPhoneVerificationView? = null

    fun attachService(service:IScannerService){
        this.service = service
    }

    fun detachService(){
        this.service = null
    }

    fun estimateStatus(){
        serial = service?.getSerial()?:""
        val activationInfo = app.store().getActivationInfo(serial) as ActivationInfo?
        val otpvInfo = app.store().getOtpvInfo(serial) as OtpvInfo?
        val versionInfo = app.store().getVersionInfo(serial) as VersionInfo?

        if(activationInfo==null)view.activationView().hide()
        else {
            view.activationView().show()
            var msg = "${activationInfo.client}\nValidity: from ${activationInfo.from} to ${activationInfo.to}"
            view.activationView().setMessage(msg)

            val visitUrl = "Visit below url to renew activation:\n${activationInfo.activationLink}"
            var canActivationBeRenewed = false
            if(isActivationValid(activationInfo)){
                if(isActivationOnWarning(activationInfo)){
                    canActivationBeRenewed = true
                    msg+="\n\n${activationInfo.warningMsg}\n\n$visitUrl"
                    view.activationView().setWarning(msg)
                } else {
                    canActivationBeRenewed = false
                }
            } else {
                canActivationBeRenewed = true
                msg+="\n\nYour device's activation has expired.\n\n$visitUrl"
                view.activationView().setError(msg)
            }

            if(canActivationBeRenewed){
                view.activationView().setButtonText("ACTIVATE")
                view.activationView().setAction(Runnable { view.redirectToUrl(serial,activationInfo.activationLink) })
                view.activationView().showButton()
            } else view.activationView().hideButton()
        }

        if(otpvInfo==null)view.otpvView().hide()
        else {
            view.otpvView().show()
            val msg = "Phone number: ${otpvInfo.phoneNo}\nValidity: from ${otpvInfo.from} to ${otpvInfo.to}"
            view.otpvView().setMessage(msg)
        }

        if(versionInfo==null)view.upgradationView().hide()
        else {
            view.upgradationView().show()
            if(versionInfo.versionCode<=app.store().getInternalVersionCode()){
                view.upgradationView().setMessage("Running on latest version.")
                view.upgradationView().hideButton()
            } else {
                view.upgradationView().setWarning(versionInfo.warningMsg)
                view.upgradationView().setButtonText("UPGRADE")
                view.upgradationView().showButton()
                view.upgradationView().setAction(Runnable { view.redirectToUrl(serial,versionInfo.path) })
            }
        }

        val error = app.store().getInitError(serial)?.toInt()?:0
        when(error){
            0 -> {

            }
            2->{
                view.activationView().show()
                val activationLink = app.store().getActivationLink()
                val msg="Activation required.\n\nVisit below url for activation:\n$activationLink"
                view.activationView().setError(msg)
                view.activationView().setButtonText("ACTIVATE")
                view.activationView().setAction(Runnable { view.redirectToUrl(serial,activationLink?:"") })
                view.activationView().showButton()
            }
            3 -> {
                view.activationView().show()
                val activationLink = app.store().getActivationLink()
                val msg="Public device.\n\nVisit below url for activation:\n$activationLink"
                view.activationView().setError(msg)
                view.activationView().setButtonText("ACTIVATE")
                view.activationView().setAction(Runnable { view.redirectToUrl(serial,activationLink?:"") })
                view.activationView().showButton()
            }
            151 -> {
                view.otpvView().show()
                view.otpvView().setError("Phone verification required.")
                view.otpvView().hideButton()
                phoneVerificationView = view.createPhoneVerificationView()
                askPhoneNumber(null)
            }
        }
    }

//    private fun isOlderVersion():Boolean {
//        val versionInfo = app.store().getVersionInfo(serial) as VersionInfo?
//        val latest = versionInfo?.versionCode?:0
//        return app.store().getInternalVersionCode()<latest
//    }

    private fun isActivationValid(activationInfo:ActivationInfo):Boolean {
        val to = activationInfo.to
        return !Date().after(sdf.parse(to))
    }

    private fun isActivationOnWarning(activationInfo: ActivationInfo):Boolean {
        val to = sdf.parse(activationInfo.to)
        val daysMillis = activationInfo.daysForWarning*24*60*60*1000L
        val warningDate = Date(to.time-daysMillis)
        return !Date().before(warningDate)
    }

    private fun askPhoneNumber(error:String?){
        phoneVerificationView?.enablePhoneNumber()
        phoneVerificationView?.hideProgress()
        if(error==null){
            phoneVerificationView?.setMessage(null)
            phoneVerificationView?.hideOtp()
        } else {
            phoneVerificationView?.setError(error)
            phoneVerificationView?.disableOtp()
        }
        phoneVerificationView?.enableButton()
        phoneVerificationView?.setButtonText(if(error==null) "SEND OTP" else "RESEND OTP")
    }

    override fun onOtpSendingError() {
        askPhoneNumber("Unable to send OTP to given number.")
    }

    override fun onOtpSentSuccess(timeout:Int) {
//        phoneVerificationView?.askOtp(timeout)
        phoneVerificationView?.disablePhoneNumber()
        phoneVerificationView?.showOtp()
        phoneVerificationView?.enableOtp()
        phoneVerificationView?.hideProgress()
        phoneVerificationView?.startTimer(timeout)
        phoneVerificationView?.enableButton()
        phoneVerificationView?.setButtonText("VERIFY")
    }

    override fun onIncorrectOtpError() {
        phoneVerificationView?.stopTimer()
        askPhoneNumber("Incorrect OTP.")
    }

    override fun onOtpExpiredError() {
        phoneVerificationView?.stopTimer()
        askPhoneNumber("OTP expired.")
    }

    override fun onInvalidOtpVerificationRequest() {
        phoneVerificationView?.stopTimer()
        askPhoneNumber("Invalid request.")
    }

    override fun onOtpVerificationSuccess() {
        view.destroyPhoneVerificationView()
        view.close()
    }

    override fun onUnknownError() {
        view.toast("Unknown error.")
    }

    override fun onNotConnectedToInternetError() {
        view.toast("Not connected to internet.")
    }

    override fun onOpened() {
        estimateStatus()
    }

    override fun onCaptured(bitmap: Any?, quality: Int) {

    }

    override fun onCaptured(isofir: ByteArray, isofmr: ByteArray, type: Int, quality: Int) {
        TODO("Not yet implemented")
    }

    override fun onCapturedFIR(isofir: ByteArray, type: Int, quality: Int) {
        TODO("Not yet implemented")
    }

    override fun onCapturedFMR(isofmr: ByteArray, type: Int, quality: Int) {
        TODO("Not yet implemented")
    }

//    override fun onCaptured(iso: ByteArray, type: Int, quality: Int) {
//
//    }

//    override fun onCaptured2(iso: ByteArray, type: Int, quality: Int) {
//
//    }

    override fun onCaptureTimedOut() {

    }

    override fun onCaptureCancelled() {

    }

    override fun onErrorOccurred(error: Int) {

    }

    override fun onDeviceDisconnected() {
        view.close()
    }

    override fun sendOtp(phoneNumber: String) {
        phoneVerificationView?.disablePhoneNumber()
        phoneVerificationView?.disableOtp()
        phoneVerificationView?.disableButton()
        phoneVerificationView?.showProgress()
        phoneVerificationView?.setMessage("Sending OTP...")
        app.webService().phoneVerificationInteractor().verifyPhoneNumber(serial,phoneNumber,this)
    }

    override fun verifyOtp(phoneNumber: String, otp: String) {
        phoneVerificationView?.stopTimer()
        phoneVerificationView?.disablePhoneNumber()
        phoneVerificationView?.disableOtp()
        phoneVerificationView?.disableButton()
        phoneVerificationView?.showProgress()
        phoneVerificationView?.setMessage("Verifying...")
        app.webService().otpValidationInteractor().validateOtp(serial,phoneNumber,otp,this)
    }
}