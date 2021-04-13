package `in`.bioenable.rdservice.fp.contracts

interface IPhoneVerificationView {

    fun enablePhoneNumber()

    fun disablePhoneNumber()

    fun showOtp()

    fun hideOtp()

    fun enableOtp()

    fun disableOtp()

    fun enableButton()

    fun disableButton()

    fun setButtonText(text:String)

    fun startTimer(timeout:Int)

    fun stopTimer()

//    fun askPhoneNumber()
//
//    fun askOtp(timeout:Int)

    fun showProgress()

    fun hideProgress()

    fun setMessage(message:String?)

    fun setError(error:String)

    fun attachPresenter(presenter: IPhoneVerificationPresenter)

    fun clearMessage()

}