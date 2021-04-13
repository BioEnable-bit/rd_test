package `in`.bioenable.rdservice.fp.ui

import `in`.bioenable.rdservice.fp.R
import `in`.bioenable.rdservice.fp.contracts.IPhoneVerificationPresenter
import `in`.bioenable.rdservice.fp.contracts.IPhoneVerificationView
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
//import androidx.fragment.app.Fragment

class PhoneVerificationFragment : androidx.fragment.app.Fragment(), IPhoneVerificationView {

    private lateinit var etPhoneNumber : EditText
    private lateinit var etOtp : EditText
    private lateinit var tvTimeout : TextView
    private lateinit var progress : ProgressBar
    private lateinit var button : Button

    private lateinit var presenter: IPhoneVerificationPresenter

    private val ui = Handler(Looper.getMainLooper())
    private var timeout : Int = 60

    private var enabled = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.layout_phone_verification,container,false) ?: return null
        etPhoneNumber = view.findViewById(R.id.et_phone_number)
        etOtp = view.findViewById(R.id.et_otp)
        tvTimeout = view.findViewById(R.id.tv_remaining_time)
        progress = view.findViewById(R.id.progress_phone_verification)
        button = view.findViewById(R.id.button_phone_verification)
        button.setOnClickListener(object:View.OnClickListener {
            override fun onClick(p0: View?) {
                val button : Button = p0 as Button
                when(button.text){
                    "VERIFY" -> {
                        presenter.verifyOtp(etPhoneNumber.text.toString().trim(),etOtp.text.toString().trim())
                    }
                    else -> {
                        presenter.sendOtp(etPhoneNumber.text.toString().trim())
                    }
                }
            }
        })
        return view
    }

    override fun enablePhoneNumber() {
        etPhoneNumber.isEnabled = true
    }

    override fun disablePhoneNumber() {
        etPhoneNumber.isEnabled = false
    }

    override fun showOtp() {
        etOtp.visibility = View.VISIBLE
    }

    override fun hideOtp() {
        etOtp.visibility = View.GONE
    }

    override fun enableOtp() {
        etOtp.text = null
        etOtp.isEnabled = true
    }

    override fun disableOtp() {
        etOtp.isEnabled = false
    }

    override fun enableButton() {
        button.isEnabled = true
    }

    override fun disableButton() {
        button.isEnabled = false
    }

    override fun setButtonText(text: String) {
        button.text = text
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.INVISIBLE
    }

    override fun setMessage(message: String?) {
        tvTimeout.text = message
    }

    override fun setError(error: String) {
        tvTimeout.text = error
        tvTimeout.error = ""
    }

    override fun attachPresenter(presenter: IPhoneVerificationPresenter) {
        this.presenter = presenter
    }

    override fun clearMessage() {
        tvTimeout.text = null
    }

    private val timer = object : Runnable {
        override fun run() {
            if(!enabled)return
            val remainingTime = "Remaining time : $timeout"
            setMessage(remainingTime)
            timeout--
            if(timeout>=0)ui.postDelayed(this,1000)
            else onTimedOut()
        }
    }

    private fun onTimedOut(){
        setError("Timed out.")
        enablePhoneNumber()
        etOtp.isEnabled = false
        button.isEnabled = true
        button.text = "RESEND OTP"
    }

    override fun startTimer(timeout:Int){
        this.timeout = timeout
        this.enabled = true
        ui.post(timer)
    }

    override fun stopTimer(){
        this.enabled = false
        this.timeout = 0
        ui.removeCallbacks(null)
        tvTimeout.text = null
    }
}