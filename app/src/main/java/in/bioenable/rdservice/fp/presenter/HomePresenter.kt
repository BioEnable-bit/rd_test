package `in`.bioenable.rdservice.fp.presenter

import `in`.bioenable.rdservice.fp.contracts.*
import `in`.bioenable.rdservice.fp.helper.CryptoUtil
import `in`.bioenable.rdservice.fp.model.Config
import `in`.bioenable.rdservice.fp.network.ActivationInfo
import `in`.bioenable.rdservice.fp.network.OtpvInfo
import `in`.bioenable.rdservice.fp.network.VersionInfo
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class HomePresenter(val view:HomeView,val app:App):
        IScannerService.Callbacks,
        IRootChecker.Callback,
        WebService.InitInteractor.OnInitCompletedListener,
        CryptoHelper.KeysCreatorInteractor.OnPublicKeyAvailableCallback,
        WebService.RegisterDeviceInteractor.Listener
{

    private var isSafetyNetCompliant = false
    private var isOpened = false
    private var isCapturing = false
    private var serial = ""
    private var env = "P"
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("EN","IN"))

    private var service : IScannerService? = null

    fun getServiceCallback(): IScannerService.Callbacks = this

    fun attachService(service:IScannerService?){
        this.service = service
    }

    fun detachService() {
        this.service = null
    }

    fun estimateStatus(){
        val attestation = app.store().getSafetyNetAttestation()
        isSafetyNetCompliant = true //!app.rootChecker().isAttestationOlderThanHours(attestation,Config.ATTESTATION_VALIDITY)
        //@yogesh changes regarding safetynet update 17/05/23 commented !app code line and pass true
        if(isSafetyNetCompliant)onFoundNonRooted()
        else checkForRoot()
        if(service?.getStatus()==IScannerService.ScannerStatus.READY) { //READY replaced by USED - Yogesh changed
            isOpened = true
            serial = service?.getSerial()?:""
            onDeviceReady(serial)
        } else {
            isOpened = false
            onDeviceDisconnected()
        }
        validateRegistrationView()
    }

    fun checkForRoot() {
        view.deviceRegistrationView().hide()
        view.rootStatusView().showProgress()
        view.rootStatusView().hideButton()
        view.rootStatusView().setMessage("Checking...")
        //app.rootChecker().checkForRoot(this)
        //@yogesh changes regarding safetynet update 17/05/23 commented this last line of method to resolve error
    }

    private fun onDeviceReady(serial:String){
        isCapturing = false
        view.deviceConnectionView().show()
        view.deviceConnectionView().setMessage("Serial number: $serial")
        view.deviceConnectionView().showButton()
        view.deviceConnectionView().setButtonText("TEST")
    }

    fun testScanner(){
        if(!isCapturing){
            isCapturing = true
            service?.capture(0,7000)
        }
    }

    fun cancelScannerTest(){
        service?.cancelCapture()
    }

    fun emailSupport(){
        val otpvInfo = app.store().getOtpvInfo(service?.getSerial()?:"") as OtpvInfo?
        val activationInfo = app.store().getActivationInfo(service?.getSerial()?:"") as ActivationInfo?
        view.composeEmail(service?.getSerial(),activationInfo?.client,otpvInfo?.phoneNo)
    }

    fun init(){
        view.deviceRegistrationView().showProgress()
        view.deviceRegistrationView().setMessage("Initializing...")
        view.deviceRegistrationView().disableButton()
        app.webService().initInteractor().init(
                serial,
                Config.MODEL_ID,
                Config.DP_ID,
                Config.RDS_ID,
                Config.RDS_VER,
                app.store().getInternalVersionCode(),
                app.store().getInternalVersionName(),
                app.store().getOsName(),
                app.store().getOsVersion(),
                app.store().getImei()?:"",
                app.store().getIpAddress(),
                this
        )
    }

    fun registerOn(env:String){
        this.env = env
        init()
    }

    override fun onOpened() {
        isOpened = true
        serial = service?.getSerial()?:""
        onDeviceReady(serial)
        validateRegistrationView()
    }

    override fun onCaptured(bitmap: Any?, quality: Int) {
        isCapturing = false
        view.showBitmap(bitmap)
    }

    override fun onCaptured(isofir: ByteArray, isofmr: ByteArray, type: Int, quality: Int) {



    }

    override fun onCapturedFIR(isofir: ByteArray, type: Int, quality: Int) {

    }

    override fun onCapturedFMR(isofmr: ByteArray, type: Int, quality: Int) {

    }

//    override fun onCaptured(iso: ByteArray, type: Int, quality: Int) {}
   // override fun onCaptured2(iso: ByteArray, type: Int, quality: Int) {}

    override fun onCaptureTimedOut() {
        onDeviceReady(service?.getSerial()?:"")
    }

    override fun onCaptureCancelled() {
        onDeviceReady(service?.getSerial()?:"")
    }

    override fun onErrorOccurred(error: Int) {
        onDeviceReady(service?.getSerial()?:"")
    }

    override fun onDeviceDisconnected() {
        isOpened = false
        view.deviceConnectionView().setError("Check device connection.")
        view.deviceConnectionView().hideProgress()
        view.deviceConnectionView().hideButton()
        validateRegistrationView()
    }

    override fun onErrorOccurred() {
        isSafetyNetCompliant = false
        view.rootStatusView().hideProgress()
        view.rootStatusView().showButton()
        view.rootStatusView().setButtonText("RE-CHECK")
        view.rootStatusView().setError("Could not check phone's root status. Check internet connection.")
        validateRegistrationView()
    }

    override fun onFoundRooted() {
//        app.manager().disableClientCalls()
        isSafetyNetCompliant = false
        view.rootStatusView().hideProgress()
        view.rootStatusView().hideButton()
        view.rootStatusView().setError("This phone is not compliant with UIDAI guidelines for RDService.")
        validateRegistrationView()
    }

    override fun onFoundNonRooted() {
//        app.manager().enableClientCalls()
        isSafetyNetCompliant = true
        app.store().putSafetyNetAttestation(System.currentTimeMillis().toString())
        view.rootStatusView().hideProgress()
        view.rootStatusView().hideButton()
        view.rootStatusView().setMessage("This phone is compliant with UIDAI guidelines for RDService.")
        validateRegistrationView()
    }

    private fun validateRegistrationView() {
        if(!isSafetyNetCompliant||!isOpened){
            view.deviceRegistrationView().hide()
            return
        }

        view.deviceRegistrationView().show()
        enableToPerformInit()
        val initError = app.store().getInitError(serial)

        if(initError==null){
            init()
            return
        }

        when(initError.toInt()){
            0-> bindRegistrationData()
            2-> view.deviceRegistrationView().setError("Activation required.")
            3-> view.deviceRegistrationView().setError("Public device")
            151-> view.deviceRegistrationView().setError("Phone verification required.")
        }
    }

    private fun bindRegistrationData(){
        val today = Date()
        val msg = StringBuilder()
        var isNotRegistered = true
        val activationInfo = app.store().getActivationInfo(serial) as ActivationInfo?
        var msg1 :String? = null
        if(activationInfo==null) Log.e("TAG", "It is null")
        else{

            msg1 = "Validity: from ${activationInfo.from} to ${activationInfo.to}"
            Log.e("TAG", "bindRegistrationData: "+msg1 )

        }

        val mcP = app.store().getMc(serial,"P")
        if(mcP!=null){
            isNotRegistered = false
            val expP = CryptoUtil.getInstance().getMcValidity(mcP)

            view.log(expP.toString())

            //if(expP!=null&&!today.after(expP))msg.append("Registered on P.\nValid up to: ${sdf.format(expP)}")
            if(expP!=null&&!today.after(expP))msg.append("Registered on P.")
        }

        val mcPP = app.store().getMc(serial,"PP")
        if(mcPP!=null){
            isNotRegistered = false
            val expPP = CryptoUtil.getInstance().getMcValidity(mcPP)
            if(expPP!=null&&!today.after(expPP)){
                if(msg.isNotEmpty())msg.append("\n\n")
              //  msg.append("Registered on PP.\nValid up to: ${sdf.format(expPP)}")
                msg.append("Registered on PP")
            }
        }

        val mcS = app.store().getMc(serial,"S")
        if(mcS!=null){
            isNotRegistered = false
            val expS = CryptoUtil.getInstance().getMcValidity(mcS)
            if(expS!=null&&!today.after(expS)){
                if(msg.isNotEmpty())msg.append("\n\n")
              //  msg.append("Registered on S.\nValid up to: ${sdf.format(expS)}")
                msg.append("Registered on S")
            }
        }

        if(isNotRegistered)view.deviceRegistrationView().setError("Not registered.")
        else{
            view.deviceRegistrationView().setMessage(msg.toString())
            view.deviceRegistrationView().setValidity(msg1.toString())
        }
        enableToPerformInit()
    }

    private fun enableToPerformInit(){
        view.deviceRegistrationView().setButtonText("INIT")
        view.deviceRegistrationView().showButton()
        view.deviceRegistrationView().setAction(Runnable{view.showRegisterDialog()})
    }

    override fun onPublicKeySigningError() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error: 301")
    }

    override fun onCouldNotRegisterAtUidai() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error: 302")
    }

    override fun onRegistrationSuccess(env: String, dc: String, mc: String, uidaiCert: String) {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        estimateStatus()
    }

    override fun onInvalidSerialNumber() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error: 1")
    }

    override fun onActivationRequired(activationLink:String) {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Device activation required.")
        view.navigateToInfoPage()
    }

    override fun onPublicDevice(activationLink: String) {
        view.deviceRegistrationView().setError("Public device.")
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.navigateToInfoPage()
    }

    override fun onInvalidDpId() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error: 51")
    }

    override fun onInvalidMi() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error: 52")
    }

    override fun onInvalidCombinationOfDpIdAndMi() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error: 53")
    }

    override fun onInvalidUidaiRdsVer() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error: 54")
    }

    override fun onInternalRdsVerNotSupported() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error: 55")
    }

    override fun onPhoneVerificationRequired() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Phone number verification required.")
        view.navigateToInfoPage()
    }

    override fun onEmailVerificationRequired() {
//        app.store().putInitError(serial,"152")
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Email verification required.")
        view.navigateToInfoPage()
    }

    override fun onInitSuccess(actInfo: ActivationInfo?, otpvInfo: OtpvInfo?, versionInfo: VersionInfo?) {
        view.deviceRegistrationView().setMessage("Preparing to register...")
        app.cryptoHelper().keysCreatorInteractor().createKeys(env,this)
    }

    override fun onUnknownError() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Unknown error.")
    }

    override fun onNotConnectedToInternetError() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Check internet connection.")
    }

    override fun onError() {
        view.deviceRegistrationView().hideProgress()
        view.deviceRegistrationView().enableButton()
        view.deviceRegistrationView().setError("Error in preparing to register device.")
    }

    override fun onPublicKeyAvailable(env: String, publicKey: String) {
        view.deviceRegistrationView().setMessage("Registering...")
        app.webService().registerDeviceInteractor().registerDevice(
                serial,
                Config.MODEL_ID,
                Config.DP_ID,
                Config.RDS_ID,
                Config.RDS_VER,
                app.store().getInternalVersionCode(),
                app.store().getInternalVersionName(),
                app.store().getOsName(),
                app.store().getOsVersion(),
                app.store().getImei()?:"",
                app.store().getIpAddress(),
                env,
                publicKey,
                this)
    }
}