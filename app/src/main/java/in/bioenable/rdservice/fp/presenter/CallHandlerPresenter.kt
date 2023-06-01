package `in`.bioenable.rdservice.fp.presenter

import `in`.bioenable.rdservice.fp.contracts.App
import `in`.bioenable.rdservice.fp.contracts.BasePresenter
import `in`.bioenable.rdservice.fp.contracts.CallHandlerView
import `in`.bioenable.rdservice.fp.contracts.IScannerService
import `in`.bioenable.rdservice.fp.model.DomainModel
import `in`.bioenable.rdservice.fp.model.DomainModelImpl
import `in`.bioenable.rdservice.fp.ui.HomeActivity
import android.util.Log

class CallHandlerPresenter(private val view: CallHandlerView,
                           app:App) :
        BasePresenter(view),
        IScannerService.Callbacks,
        DomainModel.DomainModelCallback
{

    private val TAG = CallHandlerPresenter::class.java.simpleName

    private val domainModel : DomainModel = DomainModelImpl(app,this)

    private var service : IScannerService? = null

    private var type = 0

    internal fun attachService(service: IScannerService){
        Log.e(TAG,"attachService")
        this.service = service
    }

    internal fun detachService(){
        Log.e(TAG,"detachService")
        this.service = null
    }

    fun captureCall(pidOptionsXML: String?){
        Log.e(TAG,"captureCall: PidOptions: $pidOptionsXML")
        domainModel.preparePidDataXML(pidOptionsXML)
    }

    internal fun infoCall() {
        Log.e(TAG,"infoCall: ")
        domainModel.prepareInfoXMLs()
    }

    fun proceedCapture(timeout:Int){
        service?.capture(type,timeout)?:domainModel.submitCaptureError()
    }

    fun cancelCapture(){
        Log.e(TAG,"cancelCapture: ")
        service?.cancelCapture()
    }

    fun submitPhoneNumber(phoneNumber:String){
//        domainModel.submitPhoneNumber(phoneNumber)
    }

    fun submitOtp(phoneNumber:String,otp:String){
        Log.e(TAG,"submitOtp: $phoneNumber")
//        domainModel.submitOtp(phoneNumber,otp)
    }

    override fun onOpened() {
        Log.e(TAG,"onOpened")
    }

    override fun onCaptured(bitmap: Any?, quality: Int) {

    }

    override fun onCaptured(isofir: ByteArray, isofmr: ByteArray, type: Int, quality: Int) {
       domainModel.submitIso(isofir,isofmr,type,quality)
    }

    override fun onCapturedFIR(isofir: ByteArray, type: Int, quality: Int) {
        domainModel.submitIsoFIR(isofir,type,quality)
    }

    override fun onCapturedFMR(isofmr: ByteArray, type: Int, quality: Int) {


            domainModel.submitIsoFMR(isofmr,type,quality)

    }

//    override fun onCaptured(iso: ByteArray, type: Int,quality:Int) {
//        domainModel.submitIso(iso,type,quality)
//
//    }


    override fun onCaptureTimedOut() {
        domainModel.submitCaptureTimedOut()
    }

    override fun onCaptureCancelled() {
        domainModel.submitCaptureError()
    }

    override fun onErrorOccurred(error: Int) {
        domainModel.submitCaptureError()
    }

    override fun onDeviceDisconnected() {
        domainModel.submitDeviceNotReady()
//        domainModel.submitCaptureError()
    }


    override fun onCheckDeviceReady() {
        if(service?.getStatus()==IScannerService.ScannerStatus.READY)domainModel.submitDeviceReady() // changed
        else domainModel.submitDeviceNotReady()
    }

    override fun onSerialNumberRequired() {
        domainModel.submitSerialNumber(service?.getSerial()?:"")
    }

    override fun onIsoRequired(type: Int, timeout:Int,index:Int) {
        this.type = type
        view.showFinger(index,timeout)
    }

    override fun onPidDataXMLPrepared(pidDataXML: String) {
        view.onCaptureResult(pidDataXML)
    }

    override fun onInfoXMLsPrepared(deviceInfoXML: String,rdServiceInfoXML:String) {
        view.onInfoResult(deviceInfoXML,rdServiceInfoXML)
    }

    override fun onLongProcessingStarted(msg: String) {
        view.showMessage(msg)
    }

    override fun onLongProcessingCompleted() {
        view.hideMessage()
    }

}
