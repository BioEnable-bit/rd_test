package `in`.bioenable.rdservice.fp.model

import `in`.bioenable.rdservice.fp.contracts.App
import `in`.bioenable.rdservice.fp.contracts.CryptoHelper
import `in`.bioenable.rdservice.fp.contracts.IRootChecker
import `in`.bioenable.rdservice.fp.contracts.WebService
import `in`.bioenable.rdservice.fp.helper.CryptoUtil
import `in`.bioenable.rdservice.fp.helper.XMLHelper
import `in`.bioenable.rdservice.fp.network.ActivationInfo
import `in`.bioenable.rdservice.fp.network.OtpvInfo
import `in`.bioenable.rdservice.fp.network.VersionInfo
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*


class DomainModelImpl(val app : App,
                      val domainModelCallback:DomainModel.DomainModelCallback) :
    DomainModel,
    CryptoHelper.KeysCreatorInteractor.OnPublicKeyAvailableCallback,
    WebService.RegisterDeviceInteractor.Listener,
    WebService.InitInteractor.OnInitCompletedListener,
    IRootChecker.Callback
{

    private val TAG = "DomainModelImpl"
    private lateinit var rdData: RDData
    private lateinit var serial : String
    private var count = 0
    private var infoCall = true
    private lateinit var env: String

    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale("EN","IN"))

    private fun validatePidOptions(pidOptionsXML: String?){
        rdData = RDData()
        var errCode = 100
        var errInfo = ErrorCode.getErrorInfo(100)
        if(pidOptionsXML==null) {
            onValidationError(errCode,errInfo)
            return
        }
        try {
            val pidOptions = XMLHelper.getInstance().parsePidOptions(pidOptionsXML)
            if(pidOptions.areNotPidOptions()){
                onValidationError(errCode,errInfo)
                return
            }
            errCode = ErrorCode.getPidOptionsErrorCode(pidOptions)
            errInfo = ErrorCode.getErrorInfo(errCode)
            if(errCode==0) onValidationSuccess(pidOptions)
            else onValidationError(errCode,errInfo)
        } catch (e:Exception){

            e.printStackTrace()
            errCode = 100
            errInfo = ErrorCode.getErrorInfo(100)
            onValidationError(errCode,errInfo)
        }
    }

    private fun onValidationError(errCode:Int,errInfo:String){
        finalizeCaptureCall(errCode)
    }

    private fun onValidationSuccess(pidOptions:PidOptions){
        Log.e(TAG, "onValidationSuccess: $pidOptions")
        rdData.pidOptions = pidOptions
        onAttestationValid() //@yogesh added this method directly
        //checkRootStatus() //@yogesh changes regarding safetynet update 17/05/23 commented this method to resolve error
//        checkDeviceConnected()
    }

    private fun checkDeviceConnected(){
        domainModelCallback.onCheckDeviceReady()
    }

    private fun checkDeviceRegistered(env: String, serial: String) {
        Log.e(TAG,"checkDeviceRegistered: env: $env, serial: $serial")
        if(isDeviceRegistered(env,serial))onFoundRegistered(env,serial)
        else onFoundNotRegistered(env,serial)
    }

    private fun onFoundRegistered(env:String,serial:String){
        if(infoCall){
            finalizeInfoCall(true,serial)
        } else {
//            rdData.update(
//                    env,
//                    app.store().read(PersistentStore.Key.DC),
//                    app.store().read(app.store().getMcKeyForEnvironment(env)),
//                    app.store().read(app.store().getUidaiCertKeyForEnvironment(env)))
            rdData.update(
                env,
                app.store().getDc(serial),
                app.store().getMc(serial,env),
                app.store().getUidaiCertificate(env))
            checkToCallCapture()
        }
    }

    private fun onFoundNotRegistered(env:String,serial:String){
        this.env = env
        initDevice(serial)
//        registerDevice(env,serial)
    }

    private fun onNotRegistered(){
        finalizeCaptureCall(740)
    }

    private fun checkToCallCapture(){
        Log.e(TAG,"checkToCallCapture: $count")
        val fCount = Integer.parseInt(rdData.pidOptions.getfCount())
        val fType = Integer.parseInt(rdData.pidOptions.getfType())
        var to : String? = rdData.pidOptions.timeout
        if(to==null||to=="")to = "10000"
        val timeout = Integer.parseInt(to)
        Log.e(TAG,"checkToCallCapture: count: $count, fCount: $fCount, timeout: $timeout")
        if(count<fCount) {
            domainModelCallback.onIsoRequired(fType,timeout,ErrorCode.getPoshIndices()[count])
        } else finalizeCaptureCall()
    }

    private fun finalizeInfoCall(isReady:Boolean, serial:String){
        try {
            val deviceInfoXML = XMLHelper.DeviceInfoBuilder(isReady,serial)
                .setDpId(Config.DP_ID)
                .setMi(Config.MODEL_ID)
                .setRdsId(Config.RDS_ID)
                .setRdsVer(Config.RDS_VER)
//                    .setDc(if(isReady)app.store().read(PersistentStore.Key.DC) else "")
//                    .setMc(if(isReady)app.store().read(PersistentStore.Key.MC_P)else "")
                .setDc(if(isReady)app.store().getDc(serial) else "")
                .setMc(if(isReady)app.store().getMc(serial,"P") else "")
                .build()
            val rdServiceInfoXML = XMLHelper.getInstance().createRDServiceXML(if(isReady) Config.READY else Config.NOTREADY)
            domainModelCallback.onInfoXMLsPrepared(deviceInfoXML,rdServiceInfoXML)
        } catch (e:java.lang.Exception){
            e.printStackTrace()
            domainModelCallback.onInfoXMLsPrepared("","")
        }
    }

    private fun finalizeCaptureCall(){
        Log.e(TAG,"finalizeCaptureCall: ")
        try {
            CryptoUtil.getInstance().processAndSetPidData(rdData)
            val pidDataXML = XMLHelper.getInstance().createPidDataXML(rdData)
            domainModelCallback.onPidDataXMLPrepared(pidDataXML)
        } catch (e:java.lang.Exception){
            domainModelCallback.onPidDataXMLPrepared("")
        }
    }

    private fun finalizeCaptureCall(error:Int){
        rdData.errCode = error
        try {
            val pidDataXML = XMLHelper.getInstance().createPidDataXML(rdData)
            domainModelCallback.onPidDataXMLPrepared(pidDataXML)
        } catch (e:java.lang.Exception){
            domainModelCallback.onPidDataXMLPrepared("")
        }
    }

    private fun isDeviceRegistered(env:String,serial: String) : Boolean {
//        val storedSerial = app.store().read(PersistentStore.Key.SERIAL)
//        Log.e(TAG,"isDeviceRegistered: env: $env, stored serial: $storedSerial, new serial: $serial")
//        var isRegistered = storedSerial!=null && storedSerial == serial
//        if(isRegistered){
//            val mc : String? = app.store().read(app.store().getMcKeyForEnvironment(env))
//            Log.e(TAG,"isDeviceRegistered: mc: $mc")
//            val isMcValid = mc!=null&& CryptoUtil.getInstance().isMcValid(mc)
//            Log.e(TAG,"isDeviceRegistered: isMcValid: $isMcValid")
//            isRegistered = isMcValid
//        }

        val mc = app.store().getMc(serial,env)
        val isMcValid = mc!=null&& CryptoUtil.getInstance().isMcValid(mc)
        return isMcValid
    }

    private fun initDevice(serial:String){
        Log.e(TAG,"initDevice: ")
        domainModelCallback.onLongProcessingStarted("Initializing...")
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
            this)
    }

    private fun registerDevice(env:String,serial: String) {
        this.domainModelCallback.onLongProcessingStarted("Preparing to register device...")
        app.cryptoHelper().keysCreatorInteractor().createKeys(env,this)
    }

    override fun prepareInfoXMLs() {
        infoCall = true
        onAttestationValid() //@yogesh changes regarding safetynet update 17/05/23 this method was called in checkRootStatus(), called it directly
//        checkRootStatus() //@yogesh changes regarding safetynet update 17/05/23 commented this method to resolve error
//        checkDeviceConnected()
    }

    //@yogesh changes regarding safetynet update 17/05/23 commented this method to resolve error
//    private fun checkRootStatus(){
//        if(app.rootChecker().isAttestationOlderThanHours(
//                app.store().getSafetyNetAttestation(),
//                Config.ATTESTATION_VALIDITY))onAttestationInvalid()
//        else onAttestationValid()
//    }

    private fun onAttestationValid(){
        checkDeviceConnected()
    }

    //@yogesh changes regarding safetynet update 17/05/23 commented this method to resolve error
//    private fun onAttestationInvalid(){
//        domainModelCallback.onLongProcessingStarted("Running security check...")
//        app.rootChecker().checkForRoot(this)
//    }

    override fun submitSerialNumber(serial: String) {
        this.serial = serial
        checkActivation(serial)
//        if(infoCall)checkDeviceRegistered("P",serial)
//        else {
//            rdData.serial = serial
//            checkDeviceRegistered(rdData.pidOptions.env,serial)
//        }
    }

    private fun checkActivation(serial:String){
        if(isActivationValid(serial))onActivationValid(serial)
        else onActivationInvalid()
    }

    private fun isActivationValid(serial:String):Boolean {
        val activationInfo = app.store().getActivationInfo(serial) as ActivationInfo?
        val to = activationInfo?.to
        return if(to==null) true else !Date().after(sdf.parse(to))
    }

    private fun onActivationValid(serial:String){
        checkOtpv(serial)
    }

    private fun onActivationInvalid(){
        onActivationRequired("")
    }

    private fun checkOtpv(serial:String){
        if(isOtpvValid(serial)) onOtpvValid(serial)
        else onOtpvInvalid()
    }

    private fun isOtpvValid(serial:String):Boolean{
        val otpvInfo = app.store().getOtpvInfo(serial) as OtpvInfo?
        val to = otpvInfo?.to
        return if(to==null) true else !Date().after(sdf.parse(to))
    }

    private fun onOtpvValid(serial:String) {
        if(infoCall)checkDeviceRegistered("P",serial)
        else {
            rdData.serial = serial
            checkDeviceRegistered(rdData.pidOptions.env,serial)
        }
    }

    private fun onOtpvInvalid() {
        onPhoneVerificationRequired()
//        if(infoCall) finalizeInfoCall(false,serial)
//        else finalizeCaptureCall(740)
    }

    override fun submitIso(isofir: ByteArray,isofmr: ByteArray,type:Int,quality:Int) {
        
        val nmPointsfmr: Int = isofmr.size / 5

        Log.e(TAG, "submitIso: $quality")


        rdData.pidData.pid.bios.add(
                    Bio()
                        .setPosh(ErrorCode.getPoshIndices()[count])
                        .setType("FMR")
                        .setqScore(quality.toString())
                        .setNmPoints(nmPointsfmr.toString())
                        .setEncodedBiometric(app.cryptoHelper().encodeToBase64String_DEFAULT(isofmr))

                )

                val nmPointsfir: Int = isofir.size / 5

                rdData.pidData.pid.bios.add(
                    Bio()
                        .setPosh(ErrorCode.getPoshIndices()[count])
                        .setType("FIR")
                        .setqScore(quality.toString())
                        .setNmPoints(nmPointsfir.toString())
                        .setEncodedBiometric(app.cryptoHelper().encodeToBase64String_DEFAULT(isofir))
                )

                 count++


        checkToCallCapture()
    }

    override fun submitIsoFIR(isofir: ByteArray, type: Int, quality: Int) {
        val nmPointsfir: Int = isofir.size / 5


        Log.e(TAG, "submitIsoFIR: $quality")



        rdData.pidData.pid.bios.add(
            Bio()
                .setPosh(ErrorCode.getPoshIndices()[count])
                .setType("FIR")
                .setqScore(quality.toString())
                .setNmPoints(nmPointsfir.toString())
                .setEncodedBiometric(app.cryptoHelper().encodeToBase64String_DEFAULT(isofir))
        )


        count++

        checkToCallCapture()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun submitIsoFMR(isofmr: ByteArray, type: Int, quality: Int) {


        val nmPointsfmr: Int = isofmr.size / 5

        Log.e(TAG, "submitIsoFMR: $quality")

        rdData.pidData.pid.bios.add(
            Bio()
                .setPosh(ErrorCode.getPoshIndices()[count])
                .setType("FMR")
                .setqScore(quality.toString())
                .setNmPoints(nmPointsfmr.toString())
                .setEncodedBiometric(app.cryptoHelper().encodeToBase64String_DEFAULT(isofmr)))
                //.setEncodedBiometric(base64))

        count++

        checkToCallCapture()
    }




    override fun submitDeviceReady() {
        domainModelCallback.onSerialNumberRequired()
    }

    override fun submitDeviceNotReady() {
        if(infoCall) finalizeInfoCall(false,"")
        else finalizeCaptureCall(720)
    }

    override fun submitCaptureTimedOut() {
        finalizeCaptureCall(700)
    }

    override fun submitCaptureError() {
        finalizeCaptureCall(730)
    }

    override fun preparePidDataXML(pidOptionsXML: String?) {
        infoCall = false
        validatePidOptions(pidOptionsXML)
    }

    override fun onError() {
        domainModelCallback.onLongProcessingCompleted()
    }

    override fun onPublicKeyAvailable(env:String,publicKey: String) {
        Log.e(TAG,"env: $env, public_key: $publicKey")
        domainModelCallback.onLongProcessingStarted("Registering device...")
        app.webService().registerDeviceInteractor()
            .registerDevice(
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

    override fun onRegistrationSuccess(env:String,dc: String, mc: String, uidaiCert: String) {
        Log.e(TAG,"onRegistrationSuccess:")
        Log.e(TAG,"env: $env, dc: $dc, mc_null?: ${mc.isEmpty()}, mc_length: ${mc.length}, uidai_cert_null?: ${uidaiCert.isEmpty()}, uidai_cert_length: ${uidaiCert.length}")
        domainModelCallback.onLongProcessingCompleted()
//        app.store().write(PersistentStore.Key.SERIAL,serial)
//        app.store().write(PersistentStore.Key.DC,dc)
//        app.store().write(app.store().getMcKeyForEnvironment(env),mc)
//        app.store().write(
//                app.store().getUidaiCertKeyForEnvironment(env),
//                uidaiCert.replace("-----BEGIN CERTIFICATE-----","")
//                        .replace("-----END CERTIFICATE-----","")
//                        .replace("\n",""))
//        app.store().write(PersistentStore.Key.SERIAL,serial)
//        app.store().putDc(serial,dc)
//        app.store().putMc(serial,env,mc)
//        app.store().putUidaiCertificate(env,
//                uidaiCert.replace("-----BEGIN CERTIFICATE-----","")
//                        .replace("-----END CERTIFICATE-----","")
//                        .replace("\n",""))
        onFoundRegistered(env,serial)
    }

    override fun onInvalidSerialNumber() {
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onActivationRequired(activationLink:String) {
        domainModelCallback.onLongProcessingCompleted()
        app.notificationHelper().showActivationRequiredNotification()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onPublicDevice(activationLink: String) {
        domainModelCallback.onLongProcessingCompleted()
        app.notificationHelper().showPublicDeviceNotification()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onInvalidDpId() {
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onInvalidMi() {
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onInvalidCombinationOfDpIdAndMi() {
        Log.e(TAG,"onInvalidCombinationOfDpIdAndMi")
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onInvalidUidaiRdsVer() {
        Log.e(TAG,"onInvalidUidaiRdsVer")
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onInternalRdsVerNotSupported() {
        Log.e(TAG,"onInternalRdsVerNotSupported:")
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onPhoneVerificationRequired() {
        Log.e(TAG,"onPhoneVerificationRequired:")
        domainModelCallback.onLongProcessingCompleted()
        app.notificationHelper().showPhoneVerificationRequiredNotification()
        if(infoCall)finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)

//        val otpvInfo = app.store().read(PersistentStore.Key.OTPV_INFO)
//        var phoneNumber = ""
//        if(otpvInfo!=null) phoneNumber = otpvInfo.split(GUARD)[0]
//        domainModelCallback.onPhoneVerificationRequired(phoneNumber)

    }

    override fun onEmailVerificationRequired() {
        Log.e(TAG,"onEmailVerificationRequired:")
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onPublicKeySigningError() {
        Log.e(TAG,"onPublicKeySigningError:")
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onCouldNotRegisterAtUidai() {
        Log.e(TAG,"onCouldNotRegisterAtUidai:")
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onInitSuccess(actInfo: ActivationInfo?, otpvInfo: OtpvInfo?, versionInfo: VersionInfo?) {

        Log.e(TAG,"onInitSuccess: ${actInfo.toString()}\n${otpvInfo.toString()}\n${versionInfo.toString()}")

        domainModelCallback.onLongProcessingCompleted()

//        if(actInfo!=null)app.store().write(PersistentStore.Key.ACTIVATION_INFO,
//                "${actInfo.client}$GUARD${actInfo.from}$GUARD${actInfo.to}$GUARD${actInfo.daysForWarning}$GUARD${actInfo.warningMsg}")
//
//        if(otpvInfo!=null)app.store().write(PersistentStore.Key.OTPV_INFO,
//                "${otpvInfo.phoneNo}$GUARD${otpvInfo.from}$GUARD${otpvInfo.to}$GUARD${otpvInfo.daysForWarning}$GUARD${otpvInfo.warningMsg}")
//
//        if(versionInfo!=null)app.store().write(PersistentStore.Key.VERSION_INFO,
//                "${versionInfo.versionCode}$GUARD${versionInfo.path}$GUARD${versionInfo.warningMsg}")

//        app.store().putInitError(serial,"0")
//        if(actInfo!=null)app.store().putActivationInfo(serial,actInfo)
//        if(otpvInfo!=null)app.store().putOtpvInfo(serial,otpvInfo)
//        if(versionInfo!=null)app.store().putVersionInfo(serial,versionInfo)

        registerDevice(env,serial)
    }

    override fun onUnknownError() {
        Log.e(TAG,"onUnknownError")
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onNotConnectedToInternetError() {
        Log.e(TAG,"onNotConnectedToInternetError")
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,serial)
        else finalizeCaptureCall(740)
    }

    override fun onErrorOccurred() {
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,"")
        else finalizeCaptureCall(999)
    }

    override fun onFoundRooted() {
        domainModelCallback.onLongProcessingCompleted()
        if(infoCall) finalizeInfoCall(false,"")
        else finalizeCaptureCall(999)
    }

    override fun onFoundNonRooted() {
        domainModelCallback.onLongProcessingCompleted()
        app.store().putSafetyNetAttestation(System.currentTimeMillis().toString())
        checkDeviceConnected()
    }
}