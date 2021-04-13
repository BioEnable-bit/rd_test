package `in`.bioenable.rdservice.fp.contracts

interface StatusItemView {

    fun setMessage(msg:String)
    fun setValidity( validity: String)

    fun setError(error:String)

    fun setWarning(warning:String)

    fun showButton()

    fun setButtonText(text:String)

    fun enableButton()

    fun disableButton()

    fun hideButton()

    fun showProgress()

    fun hideProgress()

    fun hide()

    fun show()

    fun setAction(runnable:Runnable)
}