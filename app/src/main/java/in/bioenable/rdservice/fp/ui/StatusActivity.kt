package `in`.bioenable.rdservice.fp.ui

import `in`.bioenable.rdservice.fp.R
import `in`.bioenable.rdservice.fp.contracts.App
import `in`.bioenable.rdservice.fp.contracts.IPhoneVerificationView
import `in`.bioenable.rdservice.fp.contracts.StatusItemView
import `in`.bioenable.rdservice.fp.contracts.StatusView
import `in`.bioenable.rdservice.fp.presenter.StatusPresenter
import `in`.bioenable.rdservice.fp.service.ScannerService
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.ActionBar
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
//import androidx.appcompat.app.ActionBar
//import androidx.appcompat.app.AppCompatActivity

open class StatusActivity : AppCompatActivity(),
        StatusView,
        ServiceConnection
{

    private lateinit var otpvView : StatusItemView
    private lateinit var activationView : StatusItemView
    private lateinit var upgradationView : StatusItemView

    private lateinit var presenter : StatusPresenter

    private var phoneVerificationView : PhoneVerificationFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
    }

    private fun initActivity(){
        presenter = StatusPresenter(this,applicationContext as App)

        setContentView(R.layout.activity_status)

        activationView = StatusItemViewHolder(findViewById(R.id.view_activation),"ACTIVATION INFO",R.drawable.ic_monetization_on_gray_24dp)
        otpvView = StatusItemViewHolder(findViewById(R.id.view_otpv),"PHONE VERIFICATION INFO",R.drawable.ic_call_blue_24dp)
        upgradationView = StatusItemViewHolder(findViewById(R.id.view_upgradation),"VERSION INFO",R.drawable.ic_new_releases_black_24dp)

//        val custTitle = "Info"
        if(supportActionBar!=null){
            val actionBar = supportActionBar as ActionBar
//            actionBar.title = custTitle
            actionBar.setDisplayHomeAsUpEnabled(true)
        } else{
//            title = custTitle
        }
        bindService(Intent(this,ScannerService::class.java),this, Context.BIND_AUTO_CREATE)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun otpvView(): StatusItemView {
        return otpvView
    }

    override fun activationView(): StatusItemView {
        return activationView
    }

    override fun upgradationView(): StatusItemView {
        return upgradationView
    }

    override fun createPhoneVerificationView(): IPhoneVerificationView {
        if(phoneVerificationView==null){
            phoneVerificationView = PhoneVerificationFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.view_otpv_required, phoneVerificationView!!)
                    .commitNow()
        }
        val pvfView = phoneVerificationView as IPhoneVerificationView
        pvfView.attachPresenter(presenter)
        return pvfView
    }

    override fun destroyPhoneVerificationView() {
        if(phoneVerificationView==null)return
        supportFragmentManager.beginTransaction()
                .remove(phoneVerificationView!!)
                .commitNow()
        phoneVerificationView = null
    }

    private fun redirectToWeb(url:String){
        try{
            val opener = Intent(Intent.ACTION_VIEW)
            opener.data = Uri.parse(url)
            startActivity(opener)
        } catch(e:Exception){}
    }

    override fun redirectToUrl(serial: String, activationLink: String) {
        redirectToWeb(activationLink)
    }

    override fun close() {
        setResult(AppCompatActivity.RESULT_OK)
        finish()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        presenter.detachService()
    }

    override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
        val serviceBinder = binder as ScannerService.ServiceBinder
        presenter.attachService(serviceBinder.getService(presenter))
        presenter.estimateStatus()
    }

    override fun onDestroy() {
        unbindService(this)
        super.onDestroy()
    }

    private fun function(){
        val view = LayoutInflater.from(this).inflate(R.layout.layout_phone_verification,null,false)
        AlertDialog.Builder(this)
                .setView(view)
    }
}