package `in`.bioenable.rdservice.fp.contracts

interface StatusView : BaseView {

    fun toast(msg:String)

    fun otpvView () : StatusItemView

    fun activationView() : StatusItemView

    fun upgradationView() : StatusItemView

    fun createPhoneVerificationView() : IPhoneVerificationView

    fun destroyPhoneVerificationView()

    fun redirectToUrl(serial:String, activationLink:String)

    fun close()
}