package `in`.bioenable.rdservice.fp.contracts

import `in`.bioenable.rdservice.fp.network.ActivationInfo
import `in`.bioenable.rdservice.fp.network.OtpvInfo
import `in`.bioenable.rdservice.fp.network.VersionInfo

interface WebService {

    interface CommonResponses {
        fun onUnknownError()
        fun onNotConnectedToInternetError()
    }

    fun initInteractor() : InitInteractor

    interface InitInteractor {

        interface OnInitCompletedListener : CommonResponses {
            fun onInvalidSerialNumber()
            fun onActivationRequired(activationLink:String)
            fun onPublicDevice(activationLink:String)
            fun onInvalidDpId()
            fun onInvalidMi()
            fun onInvalidCombinationOfDpIdAndMi()
            fun onInvalidUidaiRdsVer()
            fun onInternalRdsVerNotSupported()
            fun onPhoneVerificationRequired()
            fun onEmailVerificationRequired()
            fun onInitSuccess(actInfo:ActivationInfo?, otpvInfo:OtpvInfo?, versionInfo: VersionInfo?)
        }

        fun init(serial:String,
                 mi:String,
                 dpId:String,
                 rdsId:String,
                 rdsVer:String,
                 versionCode:Int,
                 versionName:String,
                 osName:String,
                 osVer:String,
                 machineId:String,
                 machineIp:String,
                 listener:OnInitCompletedListener)
    }

    fun registerDeviceInteractor() : RegisterDeviceInteractor

    interface RegisterDeviceInteractor {

        fun registerDevice(serial:String,
                           mi:String,
                           dpId:String,
                           rdsId:String,
                           rdsVer:String,
                           versionCode:Int,
                           versionName:String,
                           osName:String,
                           osVer:String,
                           machineId:String,
                           machineIp:String,
                           env:String,
                           publicKey:String,
                           listener:Listener)

        interface Listener : InitInteractor.OnInitCompletedListener {
            fun onPublicKeySigningError()
            fun onCouldNotRegisterAtUidai()
            fun onRegistrationSuccess(env:String,dc:String,mc:String,uidaiCert:String)
        }
    }

    fun phoneVerificationInteractor() : PhoneVerificationInteractor

    interface PhoneVerificationInteractor {

        fun verifyPhoneNumber(serial:String,number:String,listener:PhoneVerificationListener)

        interface PhoneVerificationListener : CommonResponses {
            fun onOtpSendingError()
            fun onOtpSentSuccess(timeout:Int)
        }
    }

    fun otpValidationInteractor() : OtpValidationInteractor

    interface OtpValidationInteractor {

        fun validateOtp(serial:String,phoneNumber:String,otp:String,listener:OtpVerificationListener)

        interface OtpVerificationListener : CommonResponses {
            fun onIncorrectOtpError()
            fun onOtpExpiredError()
            fun onInvalidOtpVerificationRequest()
            fun onOtpVerificationSuccess()
        }
    }
}