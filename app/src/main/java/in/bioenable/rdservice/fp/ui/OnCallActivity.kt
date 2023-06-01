package `in`.bioenable.rdservice.fp.ui

import `in`.bioenable.rdservice.fp.R
import `in`.bioenable.rdservice.fp.app.App
import `in`.bioenable.rdservice.fp.contracts.CallHandlerView
import `in`.bioenable.rdservice.fp.model.Config
import `in`.bioenable.rdservice.fp.model.ErrorCode
import `in`.bioenable.rdservice.fp.presenter.CallHandlerPresenter
import `in`.bioenable.rdservice.fp.service.ScannerService
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity

class OnCallActivity : AppCompatActivity(), ServiceConnection, CallHandlerView {

    private val TAG = "OnCallActivity"
    private lateinit var presenter : CallHandlerPresenter
    private lateinit var dialog : AlertDialog

    private var firstCapture = true
    private var timeout = 10000
    private var time = 10

    private lateinit var ivPosh : ImageView
    private lateinit var btnNext : Button
    private lateinit var tvTimeout : TextView
    private lateinit var tvPosh : TextView
    private lateinit var ui : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = createDialog()
        presenter = CallHandlerPresenter(this,applicationContext as App)

    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this,ScannerService::class.java),this, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(this)
        super.onStop()
    }

    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
        val service = (iBinder as ScannerService.ServiceBinder).getService(presenter)
        presenter.attachService(service)
        when(intent.action){
            Config.INFO_CALL -> presenter.infoCall()
            Config.INFO_CALL -> presenter.infoCall()
            Config.CAPTURE_CALL -> {
                initCaptureView()
                presenter.captureCall(intent.getStringExtra(Config.PID_OPTIONS))
            }
        }
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        presenter.detachService()
    }

    private fun initCaptureView(){
        setContentView(R.layout.layout_capture)
        ui = Handler(Looper.getMainLooper())
        ivPosh = findViewById(R.id.posh_preview)
        tvPosh = findViewById(R.id.posh)
        tvTimeout = findViewById(R.id.timeout_tv)
        btnNext = findViewById(R.id.next_capture)
    }

    private val timer : Runnable = object : Runnable {
        override fun run() {
            tvTimeout.text = time.toString()
            time--
            if (time>0) ui.postDelayed(this,1000)
            else ui.removeCallbacks(this)
        }
    }

//    override fun showProgress() {
//
//    }
//
//    override fun hideProgress() {
//
//    }

    override fun showFinger(index: Int,timeout:Int) {
        this.timeout = timeout
        time = timeout/1000
        ui.removeCallbacks(timer)
        tvTimeout.text = time.toString()
        val displayPosition = "Put ${ErrorCode.POSHES[index]} finger.".replace('_',' ').replace("UNKNOWN","ANY")
        tvPosh.text = displayPosition
        if(firstCapture){
            firstCapture = false
            capture()
        } else btnNext.visibility = View.VISIBLE
        when(index){
            2->ivPosh.setImageResource(R.drawable.fing_left_index)
            3->ivPosh.setImageResource(R.drawable.fing_left_little)
            4->ivPosh.setImageResource(R.drawable.fing_left_middle)
            5->ivPosh.setImageResource(R.drawable.fing_left_ring)
            6->ivPosh.setImageResource(R.drawable.fing_left_thumb)
            7->ivPosh.setImageResource(R.drawable.fing_right_index)
            8->ivPosh.setImageResource(R.drawable.fing_right_little)
            9->ivPosh.setImageResource(R.drawable.fing_right_middle)
            10->ivPosh.setImageResource(R.drawable.fing_right_ring)
            11->ivPosh.setImageResource(R.drawable.fing_right_thumb)
            12->ivPosh.setImageResource(R.drawable.fing_all)
        }
    }

    fun onNextClick(view: View){
        capture()
    }

    private fun capture(){
        btnNext.visibility = View.INVISIBLE
        ui.post(timer)
        presenter.proceedCapture(timeout)
    }

//    override fun showAllFingers() {
//
//    }
//
//    override fun showNextButton() {
//
//    }
//
//    override fun hideNextButton() {
//
//    }

    override fun onInfoResult(deviceInfoXML: String, rdServiceXML: String) {
        val data = Intent()
                .putExtra(Config.DEVICE_INFO,deviceInfoXML)
                .putExtra(Config.RD_SERVICE_INFO,rdServiceXML)
        setResult(AppCompatActivity.RESULT_OK,data)
        finish()
    }

    override fun onCaptureResult(pidDataXML: String) {
        val data = Intent()
                .putExtra(Config.PID_DATA,pidDataXML)
        setResult(AppCompatActivity.RESULT_OK,data)
        finish()
    }

//    override fun showPhoneVerificationDialog(phoneNumber:String) {
//        val view = LayoutInflater.from(this).inflate(R.layout.layout_phone_verification,null,false)
//        val etPhoneNumber = view.findViewById<EditText>(R.id.et_phone_verification_number)
//        etPhoneNumber.setText(phoneNumber)
//        AlertDialog.Builder(this)
//                .setView(view)
//                .setPositiveButton("SEND OTP", object:DialogInterface.OnClickListener {
//                    override fun onClick(p0: DialogInterface?, p1: Int) {
//                        Toast.makeText(this@OnCallActivity,etPhoneNumber.text,Toast.LENGTH_SHORT).show()
//                        presenter.submitPhoneNumber(etPhoneNumber.text.toString().trim())
//                    }
//                })
//                .setNegativeButton("CANCEL",object : DialogInterface.OnClickListener {
//                    override fun onClick(p0: DialogInterface?, p1: Int) {
//                        Toast.makeText(this@OnCallActivity,"method not set",Toast.LENGTH_SHORT).show()
//                    }
//                })
//                .show()
//    }
//
//    override fun showOTPValidationDialog(phoneNumber:String,seconds:Int) {
//        Log.e(TAG,"showOTPValidationDialog: ")
//        val view = LayoutInflater.from(this).inflate(R.layout.layout_otpv,null,false)
//        val etOtp = view.findViewById<EditText>(R.id.et_otpv)
//        AlertDialog.Builder(this)
//                .setView(view)
//                .setPositiveButton("VERIFY", object:DialogInterface.OnClickListener {
//                    override fun onClick(p0: DialogInterface?, p1: Int) {
//                        Toast.makeText(this@OnCallActivity,etOtp.text,Toast.LENGTH_SHORT).show()
//                        presenter.submitOtp(phoneNumber,etOtp.text.toString().trim())
//                    }
//                })
//                .setNegativeButton("CANCEL",object : DialogInterface.OnClickListener{
//                    override fun onClick(p0: DialogInterface?, p1: Int) {
//                        Toast.makeText(this@OnCallActivity,"method not set",Toast.LENGTH_SHORT).show()
//                    }
//                })
//                .show()
//    }

    private fun createDialog(): AlertDialog {
        return AlertDialog.Builder(this)
                .setCancelable(false)
                .create()
    }

    override fun showMessage(msg:String){
        dialog.setMessage(msg)
        dialog.show()
    }

    override fun hideMessage(){
        dialog.dismiss()
    }

    override fun onBackPressed() {
        presenter.cancelCapture()
    }
}
