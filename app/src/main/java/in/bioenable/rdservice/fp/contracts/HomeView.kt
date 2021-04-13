package `in`.bioenable.rdservice.fp.contracts

interface HomeView {

    fun rootStatusView() : StatusItemView

    fun deviceConnectionView() : StatusItemView

    fun deviceRegistrationView() : StatusItemView

    fun showBitmap(bitmap:Any?)

    fun onCaptureError()

    fun composeEmail(serial:String?, client:String?, phoneNumber:String?)

    fun showRegisterDialog()

    fun navigateToInfoPage()

    fun log(log: String)

}