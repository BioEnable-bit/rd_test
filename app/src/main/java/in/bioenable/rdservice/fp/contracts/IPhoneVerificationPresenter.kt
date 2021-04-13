package `in`.bioenable.rdservice.fp.contracts

interface IPhoneVerificationPresenter {
    fun sendOtp(phoneNumber:String)
    fun verifyOtp(phoneNumber: String,otp:String)
}