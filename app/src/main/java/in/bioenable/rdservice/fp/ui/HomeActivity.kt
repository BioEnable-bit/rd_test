package `in`.bioenable.rdservice.fp.ui

 import `in`.bioenable.rdservice.fp.R
 import `in`.bioenable.rdservice.fp.contracts.App
 import `in`.bioenable.rdservice.fp.contracts.HomeView
 import `in`.bioenable.rdservice.fp.contracts.StatusItemView
 import `in`.bioenable.rdservice.fp.helper.CryptoUtil
 import `in`.bioenable.rdservice.fp.model.Config
 import `in`.bioenable.rdservice.fp.presenter.HomePresenter
 import `in`.bioenable.rdservice.fp.service.ScannerService
 import android.content.*
 import android.graphics.Bitmap
 import android.graphics.Canvas
 import android.graphics.Paint
 import android.net.Uri
 import android.os.Build
 import android.os.Bundle
 import android.os.Environment
 import android.os.IBinder
 import android.util.Log
 import android.view.Menu
 import android.view.MenuItem
 import android.view.View
 import android.widget.ImageView
 import android.widget.TextView
 import androidx.annotation.RequiresApi
 import androidx.appcompat.app.AlertDialog
 import androidx.appcompat.app.AppCompatActivity
 import androidx.core.content.ContextCompat
 import com.nitgen.SDK.AndroidBSP.BuildConfig
 import java.io.*


//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity(),
        HomeView,
        ServiceConnection
{

    private val TAG = "HomeActivity"
//    val CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"
    private lateinit var app : App

//    private lateinit var svInternet :StatusItemView
    private lateinit var svRoot :StatusItemView
    private lateinit var svConnection :StatusItemView
    private lateinit var svRegistration :StatusItemView
    private lateinit var presenter : HomePresenter

    private var isCapturing = false

//    private val REQ_CODE_IMEI = 57

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        //readDatFiles()



//        var loginToken: String = "[123456789]"
//        loginToken = loginToken.substring(1, loginToken.length - 1)
//        Log.e(TAG, "onCreate: $loginToken")
//
//        readFileFromExternalStorage()
    }

    fun readFileFromExternalStorage() : ByteArray?
    {
        var f: File = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "RDService/NitGen_1.min")
        Log.e(TAG, "onCreate: "+f.absolutePath)

        try {
            val br = BufferedReader(FileReader(f))
            var st: String?
            while (br.readLines().also { st = it.toString() } != null) {
                Log.e(TAG, "readFileFromExternalStorage: $st")
                return st!!.toByteArray()
            }

        } catch (e: Exception)
        {
            Log.e(TAG, "onCreate: $e")
        }
         return null
    }

    fun readDatFiles() : ByteArray?
    {
        var reader: BufferedReader? =  null;
        try {
//            reader = BufferedReader(
//                     InputStreamReader(assets.open("LeftIndex.dat"))
//            );

            reader = BufferedReader(
                InputStreamReader(assets.open("thumb.dat"), "UTF-8")
            )

            // do reading, usually loop until end of file reading
            var  mLine: String;
            while ((reader.readLine()) != null) {
                //Log.e(TAG, "readDatFiles: "+reader.readLine() )
                    val bytes = reader.readLine().toByteArray()
                return bytes;
                Log.e(TAG, "readDatFiles: $bytes")
            }
        } catch (e : IOException) {
            Log.e(TAG, "readDatFiles: $e")
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (e : IOException) {
                    Log.e(TAG, "readDatFiles: $e")
                }
            }
        }
        return null;
    }


    private fun initActivity() {

        Log.e(TAG, "initActivity URL: "+ CryptoUtil.getInstance().fullUrl)
        app = applicationContext as App
        presenter = HomePresenter(this,app)

        setContentView(R.layout.activity_home)

//        svInternet = StatusItemViewHolder(findViewById(R.id.sv_internet),"INTERNET",R.drawable.ic_swap_vertical_circle_black_24dp)
        svRoot = StatusItemViewHolder(findViewById(R.id.sv_root),"ROOT STATUS",R.drawable.ic_security_black_24dp)
        svConnection = StatusItemViewHolder(findViewById(R.id.sv_device_connection),"DEVICE CONNECTION",R.drawable.ic_usb_black_24dp)
        svRegistration = StatusItemViewHolder(findViewById(R.id.sv_device_registration),"DEVICE REGISTRATION",R.drawable.ic_offline_pin_black_24dp)

        svRoot.setAction(Runnable {
            presenter.checkForRoot()  //1
        })

        svConnection.setAction(Runnable {
            presenter.testScanner()
            isCapturing = true
        })

        svRegistration.setAction(Runnable{
            showRegisterDialog()
        })

//        readImei(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.action_clear_data -> {
                AlertDialog.Builder(this)
                        .setMessage("Are you sure to delete all app data?")
                        .setPositiveButton("YES") { p0, p1 ->
                            app.store().deleteAllEntries()
                            presenter.estimateStatus()
                        }
                        .setNegativeButton("CANCEL",null)
                        .show()
            }
            R.id.action_contact_us -> showContactUsDialog()
            R.id.action_about -> {
                val content = layoutInflater.inflate(R.layout.layout_about, null)
                AlertDialog.Builder(this)
                        .setView(content)
                        .setPositiveButton("OK", null)
                        .show()
                val iv = getString(R.string.internal_version)
                (content.findViewById<View>(R.id.internal_version) as TextView).text = iv
                (content.findViewById<View>(R.id.uidai_version_value) as TextView).text = Config.RDS_VER
            }
            R.id.action_help -> {
                redirectToWeb(getString(R.string.help_url))
            }
            R.id.action_status_activity -> startActivity(Intent(this,StatusActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun redirectToWeb(url:String){
        val opener = Intent(Intent.ACTION_VIEW)
        opener.data = Uri.parse(url)
        startActivity(opener)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this,ScannerService::class.java),this, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(this)
        super.onStop()
    }

    override fun onBackPressed() {
        if(isCapturing)presenter.cancelScannerTest()
        else super.onBackPressed()
    }

    private fun showContactUsDialog() {
        val dialog = AlertDialog.Builder(this)
                .setTitle("Contact us")
                .setView(layoutInflater.inflate(R.layout.contact_us, null))
                .setPositiveButton("EMAIL", null)
                .setNegativeButton("CALL", null)
                .setNeutralButton("CANCEL", null)
                .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setOnClickListener {
                    presenter.emailSupport()
                    dialog.dismiss()
                }
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setOnClickListener {
                    callSupport()
                    dialog.dismiss()
                }
    }

    fun callSupport() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + getString(R.string.support_phone_number))
        startActivity(intent)
    }

    override fun composeEmail(serial:String?, client:String?, phoneNumber:String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse(getString(R.string.support_email_id))
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email_id)))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Android App")
        var msg = "Device Serial No: $serial"
        if (phoneNumber != null) {
            msg+="\nPhone number: $phoneNumber"
        }
        if(client!=null){
            msg+="\nClient: $client"
        }
        intent.putExtra(Intent.EXTRA_TEXT, msg)
        intent.type = "message/rfc822"
        try {
            startActivity(Intent.createChooser(intent, "Email via"))
        } catch (e: Exception) {}
    }

    override fun showRegisterDialog() {
        val envs = arrayOf("P","PP","S")
        AlertDialog.Builder(this)
                .setTitle("Select P for actual UIDAI authentication purposes.")
                .setItems(envs) { _, position ->
                    presenter.registerOn(envs[position])
                }
                .show()
    }

    override fun navigateToInfoPage() {
        startActivityForResult(Intent(this,StatusActivity::class.java),999)
    }

    override fun log(log: String) {
        Log.e(TAG, "log: "+log)

    }

    override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
        Log.e(TAG,"onServiceConnected():")
        val serviceBinder = binder as ScannerService.ServiceBinder?
        Log.e(TAG,"onServiceConnected(): is binder null: ${serviceBinder==null}")
        presenter.attachService(serviceBinder?.getService(presenter.getServiceCallback()))
        presenter.estimateStatus()
//        refreshView()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.e(TAG,"onServiceDisconnected():")
        presenter.detachService()
    }

    override fun rootStatusView(): StatusItemView {
        return svRoot
    }

    override fun deviceConnectionView(): StatusItemView {
        return svConnection
    }

    override fun deviceRegistrationView(): StatusItemView {
        return svRegistration
    }

    override fun showBitmap(bitmap: Any?) {
        isCapturing = false
        val nettedBitmap = nettedBitmap(bitmap as Bitmap?)
        showImageDialog(nettedBitmap)
    }

    override fun onCaptureError() {
        isCapturing = false
    }

    private fun showImageDialog(bitmap: Bitmap?) {
        val iv = ImageView(this)
        iv.adjustViewBounds = true
        iv.setImageBitmap(bitmap)
        AlertDialog.Builder(this)
                .setView(iv)
                .setCancelable(true)
                .show()
    }

    private fun nettedBitmap(input: Bitmap?): Bitmap? {
        if (input == null) return null
        val mutable = Bitmap.createBitmap(input)
        val canvas = Canvas(mutable)
        val paint = Paint()
        paint.strokeWidth = 4f
        paint.color = ContextCompat.getColor(this, R.color.fp_mask)
        val w = mutable.width
        val h = mutable.height
        var y = 10
        while (y < h) {
            canvas.drawLine(0f, y.toFloat(), h.toFloat(), y.toFloat(), paint)
            y += 12
        }
        var x = 10
        while (x < w) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), h.toFloat(), paint)
            x += 12
        }
        return mutable
    }

//    private fun refreshView(){
//        if(isBound)presenter.estimateStatus()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==999&&resultCode== AppCompatActivity.RESULT_OK){
            presenter.init()
        }
//        super.onActivityResult(requestCode, resultCode, data)
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when(requestCode){
//            REQ_CODE_IMEI-> readImei(false)
//        }
//    }

//    @SuppressLint("HardwareIds")
//    private fun readImei(requestPermission: Boolean) {
//        val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            var imei: String? = null
//            imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (manager.phoneType == TelephonyManager.PHONE_TYPE_CDMA) manager.meid else manager.imei
//            } else {
//                manager.deviceId
//            }
//            if (imei != null) onImeiRead(imei) else finish()
//        } else {
//            if (requestPermission) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQ_CODE_IMEI)
//            } else finish()
//        }
//    }

//    private fun onImeiRead(imei: String) {
//        app.store().putImei(imei)
//        Log.e(TAG,"imei: $imei")
//    }
}
