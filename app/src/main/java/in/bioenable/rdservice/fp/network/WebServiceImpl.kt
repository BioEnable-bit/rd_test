package `in`.bioenable.rdservice.fp.network

import `in`.bioenable.rdservice.fp.contracts.App
import `in`.bioenable.rdservice.fp.contracts.WebService
import `in`.bioenable.rdservice.fp.helper.CryptoUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

class WebServiceImpl(val app:App) : WebService {

    private val api = RetrofitClient.getApi()

    private val initInteractor = object : WebService.InitInteractor {
        override fun init(serial: String,
                          mi: String,
                          dpId: String,
                          rdsId: String,
                          rdsVer: String,
                          versionCode: Int,
                          versionName: String,
                          osName: String,
                          osVer: String,
                          machineId: String,
                          machineIp: String,
                          listener: WebService.InitInteractor.OnInitCompletedListener) {
            api.init(Init.Request(Info(
                    DeviceInfo(serial),
                    RdsInfo(mi,dpId,rdsId,rdsVer,versionName,versionCode),
                    MachineInfo(machineId,machineIp,osName,osVer)
            )),CryptoUtil.getInstance().fullUrl)
                    .enqueue(object:Callback<Init.Response>{

                        override fun onResponse(call: Call<Init.Response>, response: Response<Init.Response>) {
                            if(response.body()==null){
                                listener.onUnknownError()
                            } else {
                                val resp : Init.Response = response.body() as Init.Response
                                app.store().putInitError(serial,resp.resultCode.toString())
                                when(resp.resultCode){
                                    0 -> {
                                        val actInfo = resp.data.activationInfo
                                        val otpvInfo = resp.data.otpvInfo
                                        val versionInfo = resp.data.versionInfo
                                        if(actInfo!=null)app.store().putActivationInfo(serial,actInfo)
                                        if(otpvInfo!=null)app.store().putOtpvInfo(serial,otpvInfo)
                                        if(versionInfo!=null)app.store().putVersionInfo(serial,versionInfo)

                                        listener.onInitSuccess(
                                                actInfo,
                                                otpvInfo,
                                                versionInfo)
                                    }
                                    1-> listener.onInvalidSerialNumber()
                                    2-> {
                                        app.store().deleteActivationInfo(serial)
                                        app.store().putActivationLink(resp.activationLink)
                                        listener.onActivationRequired(resp.activationLink)
                                    }
                                    3-> {
                                        app.store().deleteActivationInfo(serial)
                                        app.store().putActivationLink(resp.activationLink)
                                        listener.onPublicDevice(resp.activationLink)
                                    }
                                    51-> listener.onInvalidDpId()
                                    52-> listener.onInvalidMi()
                                    53-> listener.onInvalidCombinationOfDpIdAndMi()
                                    54-> listener.onInvalidUidaiRdsVer()
                                    55-> listener.onInternalRdsVerNotSupported()
                                    151-> {
                                        app.store().deleteOtpvInfo(serial)
                                        listener.onPhoneVerificationRequired()
                                    }
                                    152-> {
                                        //todo app.store().deleteEmailVerificationInfo(serial)
                                        listener.onEmailVerificationRequired()
                                    }
                                    else -> listener.onUnknownError()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Init.Response>, t: Throwable?) {
                            when (t) {
                                null -> listener.onUnknownError()
                                is UnknownHostException -> listener.onNotConnectedToInternetError()
                                else -> listener.onUnknownError()
                            }
                        }
                    })
        }
    }

    private val registerDeviceInteractor = object : WebService.RegisterDeviceInteractor {
        override fun registerDevice(serial: String,
                                    mi: String,
                                    dpId: String,
                                    rdsId: String,
                                    rdsVer: String,
                                    versionCode: Int,
                                    versionName: String,
                                    osName: String,
                                    osVer: String,
                                    machineId: String,
                                    machineIp: String,
                                    env: String,
                                    publicKey: String,
                                    listener: WebService.RegisterDeviceInteractor.Listener) {


            api.registerDevice(RegisterDevice.Request(Info(
                    DeviceInfo(serial),
                    RdsInfo(mi,dpId,rdsId,rdsVer,versionName,versionCode),
                    MachineInfo(machineId,machineIp,osName,osVer)
            ),env,publicKey),CryptoUtil.getInstance().fullUrl)
                    .enqueue(object:Callback<RegisterDevice.Response>{

                        override fun onResponse(call: Call<RegisterDevice.Response>, response: Response<RegisterDevice.Response>) {
                            if(response.body()==null){
                                listener.onUnknownError()
                            } else {
                                val resp : RegisterDevice.Response = response.body() as RegisterDevice.Response
                                when(resp.getResultCode()){
                                    0-> {
                                        val uidaiCert = resp.uidaiCert.replace("-----BEGIN CERTIFICATE-----","")
                                                .replace("-----END CERTIFICATE-----","")
                                                .replace("\n","")
                                        app.store().putDc(serial,resp.dc)
                                        app.store().putMc(serial,env,resp.mc)
                                        app.store().putUidaiCertificate(env,uidaiCert)
                                        listener.onRegistrationSuccess(resp.env,resp.dc,resp.mc,uidaiCert)
                                    }
                                    1-> listener.onInvalidSerialNumber()
                                    //todo remove passing empty string as activation link
                                    2-> listener.onActivationRequired("")
                                    3-> listener.onPublicDevice("")
                                    51-> listener.onInvalidDpId()
                                    52-> listener.onInvalidMi()
                                    53-> listener.onInvalidCombinationOfDpIdAndMi()
                                    54-> listener.onInvalidUidaiRdsVer()
                                    55-> listener.onInternalRdsVerNotSupported()
                                    151-> listener.onPhoneVerificationRequired()
                                    152-> listener.onEmailVerificationRequired()
                                    301-> listener.onPublicKeySigningError()
                                    302-> listener.onCouldNotRegisterAtUidai()
                                    else -> listener.onUnknownError()
                                }
                            }
                        }

                        override fun onFailure(call: Call<RegisterDevice.Response>, t: Throwable?) {
                            when (t) {
                                null -> listener.onUnknownError()
                                is UnknownHostException -> listener.onNotConnectedToInternetError()
                                else -> listener.onUnknownError()
                            }
                        }
                    })
        }
    }

    private val phoneVerificationInteractor = object : WebService.PhoneVerificationInteractor {
        override fun verifyPhoneNumber(serial:String,number: String, listener: WebService.PhoneVerificationInteractor.PhoneVerificationListener) {
            api.requestOtp(PhoneVerification.Request(serial,number),CryptoUtil.getInstance().fullUrl)
                    .enqueue(object:Callback<PhoneVerification.Response>{

                        override fun onResponse(call: Call<PhoneVerification.Response>, response: Response<PhoneVerification.Response>) {
                            if(response.body()==null)listener.onUnknownError()
                            else {
                                val resp = response.body() as PhoneVerification.Response
                                when(resp.resultCode){
                                    0 -> listener.onOtpSentSuccess(resp.timeout)
                                    201 -> listener.onOtpSendingError()
                                    else -> listener.onUnknownError()
                                }
                            }
                        }

                        override fun onFailure(call: Call<PhoneVerification.Response>, t: Throwable?) {
                            when(t){
                                null -> listener.onUnknownError()
                                is UnknownHostException -> listener.onNotConnectedToInternetError()
                                else ->listener.onUnknownError()
                            }
                        }
                    })
        }
    }

    private val otpValidationInteractor = object : WebService.OtpValidationInteractor {
        override fun validateOtp(serial:String,phoneNumber: String, otp: String, listener: WebService.OtpValidationInteractor.OtpVerificationListener) {
            api.validateOtp(OtpValidation.Request(serial,phoneNumber,otp),CryptoUtil.getInstance().fullUrl)
                    .enqueue(object:Callback<OtpValidation.Response>{
                        override fun onResponse(call: Call<OtpValidation.Response>, response: Response<OtpValidation.Response>) {
                            if(response.body()==null)listener.onUnknownError()
                            else {
                                val resp = response.body() as OtpValidation.Response
                                when(resp.resultCode){
                                    0 -> listener.onOtpVerificationSuccess()
                                    251 -> listener.onIncorrectOtpError()
                                    252 -> listener.onOtpExpiredError()
                                    253 -> listener.onInvalidOtpVerificationRequest()
                                    else -> listener.onUnknownError()
                                }
                            }
                        }

                        override fun onFailure(call: Call<OtpValidation.Response>, t: Throwable?) {
                            when(t){
                                null -> listener.onUnknownError()
                                is UnknownHostException -> listener.onNotConnectedToInternetError()
                                else ->listener.onUnknownError()
                            }
                        }
                    })
        }
    }

    override fun initInteractor(): WebService.InitInteractor {
        return initInteractor
    }

    override fun registerDeviceInteractor(): WebService.RegisterDeviceInteractor {
        return registerDeviceInteractor
    }

    override fun phoneVerificationInteractor(): WebService.PhoneVerificationInteractor {
        return phoneVerificationInteractor
    }

    override fun otpValidationInteractor(): WebService.OtpValidationInteractor {
        return otpValidationInteractor
    }
}