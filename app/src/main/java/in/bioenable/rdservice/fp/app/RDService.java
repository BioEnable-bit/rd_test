//package in.bioenable.rdservice.fp.app;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbManager;
//import android.net.ConnectivityManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.Messenger;
//import android.os.RemoteException;
//import android.support.annotation.Nullable;
//import android.util.Base64;
//import android.util.Log;
//
//import com.nitgen.SDK.AndroidBSP.NBioBSPJNI;
//import com.nitgen.SDK.AndroidBSP.StaticVals;
//
//import java.security.KeyPair;
//import java.security.PrivateKey;
//
//import in.bioenable.rdservice.fp.BuildConfig;
//import in.bioenable.rdservice.fp.R;
//import in.bioenable.rdservice.fp.helper.AndroidSecurityUtil;
//import in.bioenable.rdservice.fp.helper.CryptoUtil;
//import in.bioenable.rdservice.fp.helper.InternetChecker;
//import in.bioenable.rdservice.fp.helper.KeysCreator;
//import in.bioenable.rdservice.fp.helper.RootChecker;
//import in.bioenable.rdservice.fp.helper.XMLHelper;
//import in.bioenable.rdservice.fp.model.BioEnablePacketRequest;
//import in.bioenable.rdservice.fp.model.BioEnablePacketResponse;
//import in.bioenable.rdservice.fp.network.RetrofitClient;
//import in.bioenable.rdservice.fp.ui.BioEnableActivity;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class RDService extends Service implements NBioBSPJNI.CAPTURE_CALLBACK, Handler.Callback,
//        RootChecker.Callback,InternetChecker.Callback, KeysCreator.OnKeysCreatedCallback {
//
//    public static final String USB_MANUAL_PERMISSION = "in.bioenabletech.nitgen.MANUAL_PERMISSION";
//    public final String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
//    private static final String TAG = RDService.class.getSimpleName();
//    private InternetChecker.Status internetStatus = InternetChecker.Status.NOT_AVAILABLE;
//    private RootChecker.Status rootStatus = RootChecker.Status.UNKNOWN;
//    private UsbConnState connStt = UsbConnState.NOT_CONNECTED;
//    private RegProgress regProgress = RegProgress.IDLE;
//
//    private String env;
//    private String serial;
//
//    private static PrivateKey privateKey = CryptoUtil.loadPrivateKey();
//
//    public static PrivateKey getPrivateKey() {
//        if(privateKey==null)CryptoUtil.loadPrivateKey();
//        return privateKey;
//    }
//
//    private static final String SERIAL_CODE = "010701-613E5C7F4CC7C4B0-72E340B47E034015";
//    private final int NO_POSH_INPUT = 19;
//    private final int INVALID_POSHES = 29;
//
//    private NBioBSPJNI bsp;
//    private int err = 0;
//    private int timeout = 7000;
//    private int count = 0;
//    private int[] poshes;
//    private String[] fmrs;
//    private String fmr;
//    private int quality;
//
//    private void setQuality(int quality){
//        this.quality = quality;
//    }
//
//    @Override
//    public void onInternetChecked(InternetChecker.Status status) {
//        Log.e(TAG, "onInternetChecked: "+status);
//        this.internetStatus = status;
//        try {
//            Message msg = Message.obtain(
//                    null,
//                    What.UPD_INTERNET_AVAILABILITY.ordinal(),
//                    status.ordinal(),
//                    0
//            );
//            if(RDService.this.client!=null)RDService.this.client.send(msg);
//            if(status== InternetChecker.Status.AVAILABLE &&
//                    (rootStatus== RootChecker.Status.UNKNOWN||
//                            rootStatus== RootChecker.Status.ERROR)) new RootChecker(this,this).checkForRoot();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onKeysCreationStarted() {
//        RDService.this.regProgress = RDService.RegProgress.REGISTERING;
//        if(client!=null) try {
//            client.send(Message.obtain(null, RDService.What.UPD_REG_PROGRESS.ordinal(), RDService.RegProgress.REGISTERING.ordinal(),0));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onKeysCreated(KeyPair keyPair) {
//
//        final PersistentStore ps = App.store();
//        String clientInfo = ps.read(PersistentStore.Key.CLIENT_INFO);
//        String[] clientInfoArray = new String[5];
//        if(clientInfo!=null){
//            String[] split = clientInfo.split("\n");
//            System.arraycopy(split, 0, clientInfoArray, 0, split.length);
//        }
//
//        String publicKey = Base64.encodeToString(keyPair.getPublic().getEncoded(),Base64.DEFAULT);
//
//        BioEnablePacketRequest request = new BioEnablePacketRequest()
//                .setEnvironment(env)
//                .setDcIeSerial(serial)
//                .setDevicePublicKey(publicKey)
//                .setClientname(clientInfoArray[0])
//                .setAddress(clientInfoArray[1])
//                .setPincode(clientInfoArray[2])
//                .setMobileNum(clientInfoArray[3])
//                .setImei(clientInfoArray[4])
//                .setOsVer(Build.VERSION.RELEASE)
//                .setIv(BuildConfig.VERSION_CODE);
//        String requestXml;
//
//        try {
//            requestXml = XMLHelper.getInstance().createBioEnablePacketRequestXML(request);
//
//            RetrofitClient.getApi().registerDevice(requestXml).enqueue(new Callback<String>() {
//                @Override
//                public void onResponse(Call<String> call, Response<String> resp) {
//                    Log.e(TAG, "onResponse: ");
//                    if(resp.body()==null)return;
//                    try {
//                        final BioEnablePacketResponse response = XMLHelper.getInstance()
//                                .parseBioEnablePacketResponse(resp.body());
//
////                        ps.write(PersistentStore.Key.ENV,env);
//                        ps.write(PersistentStore.Key.SERIAL,serial);
//                        ps.write(PersistentStore.Key.DC,response.getUuid_value());
//                        ps.write(ps.getMcKeyForEnvironment(env),response.getSigned_device_public_key());
//                        ps.write(PersistentStore.Key.TOKEN,response.getToken());
//                        ps.write(PersistentStore.Key.IV,String.valueOf(BuildConfig.VERSION_CODE));
//
//                    } catch (Exception/*IOException | ParserConfigurationException | SAXException*/ e) {
//                        e.printStackTrace();
//                    }
//
//                    RDService.this.regProgress = RDService.RegProgress.IDLE;
//                    try {
//                        client.send(Message.obtain(
//                                null,
//                                What.UPD_REG_PROGRESS.ordinal(),
//                                RegProgress.IDLE.ordinal(),
//                                RegResponse.SUCCESS.ordinal()));
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<String> call, Throwable t) {
//                    Log.e(TAG, "onFailure: "+t.getMessage());
//                    RDService.this.regProgress = RDService.RegProgress.IDLE;
//                    try {
//                        client.send(Message.obtain(
//                                null,
//                                What.UPD_REG_PROGRESS.ordinal(),
//                                RegProgress.IDLE.ordinal(),
//                                RegResponse.ERR_NETWORK.ordinal()));
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onFailure() {
//        RDService.this.regProgress = RDService.RegProgress.IDLE;
//        if(client!=null) try {
//            client.send(Message.obtain(
//                    null,
//                    RDService.What.UPD_REG_PROGRESS.ordinal(),
//                    RDService.RegProgress.IDLE.ordinal(),
//                    RDService.RegResponse.ERR_CREATE_KEYS.ordinal()));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public enum UsbConnState {
//        NOT_CONNECTED,
//        NOT_OPENED,
//        OPENING,
//        OPENED,
//        CAPTURING,
//        CAPTURED
//    }
//
//    public enum RegProgress {
//        IDLE,
//        REGISTERING
//    }
//
//    public enum RegResponse {
//        ERR_CREATE_KEYS,
//        ERR_BIO_REQ_PARSE,
//        ERR_NETWORK,
//        ERR_BIO_RES_DECRYPT,
//        ERR_BIO_RES_PARSE,
//        SUCCESS
//    }
//
//    public enum What {
//        REQ_SERVICE_TEST,
//        RES_SERVICE_TEST,
//        REQ_OPEN,
//        REQ_CAPTURE,
//        REQ_CAPTURE_CANCEL,
//        UPD_CONN_STATE,
//        UPD_USB_ERR,
//        UPD_FMR,
//        REQ_ROOT_CHECK,
//        UPD_ROOT_STATUS,
//        UPD_INTERNET_AVAILABILITY,
//        REQ_REGISTER,
//        UPD_REG_PROGRESS,
//        NULLIFY_CLIENT
//    }
//
//    private Messenger service,client;
//
//    private BroadcastReceiver usbReceiver = new BroadcastReceiver(){
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent==null||intent.getAction()==null)return;
//            switch (intent.getAction()){
//                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
//                    showNotification();
//                    updateService();
//                    onConnStateChanged(null,null);
//                    break;
//                case UsbManager.ACTION_USB_DEVICE_DETACHED:
//                    hideNotification();
//                    Log.e(TAG, "onReceive: DETACHED");
////                    bsp = null;
//                    serial = null;
//                    connStt = UsbConnState.NOT_CONNECTED;
//                    onConnStateChanged(null,null);
//                    break;
//                case CONNECTIVITY_CHANGE:
//                    if(isNetworkAvailable(RDService.this))new InternetChecker(RDService.this).execute();
//                    else onInternetChecked(InternetChecker.Status.NOT_AVAILABLE);
//                    break;
//            }
//        }
//    };
//
//    private void showNotification() {
//        PendingIntent pi = PendingIntent.getActivity(this,13,new Intent(this, BioEnableActivity.class),0);
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle("BioEnable fingerprint scanner connected")
//                .setContentText("The device is in use. Tap to see status.")
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentIntent(pi)
//                .build();
//        startForeground(7,notification);
//    }
//
//    private void hideNotification(){
//        stopForeground(true);
//    }
//
//    private void testGitOnFileChanged(){}
//
//    @Override
//    public void onCreate(){
//        Log.e(TAG, "onCreate: ");
//        initService();
//        initNitgenIfConnected();
//        openNitgen();
//        new InternetChecker(this).execute();
//        RootChecker rc = new RootChecker(this);
//        String att = App.store().read(PersistentStore.Key.ATTESTATION);
//        if(rc.isAttestationOlderThanHours(att,8)){
//            rc.checkForRoot();
//        } else {
//            rootStatus = RootChecker.Status.NON_ROOTED;
//        }
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId){
//        Log.e(TAG, "onStartCommand: ");
//        updateService();
//        return START_STICKY;
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent){
//        Log.e(TAG, "onBind: ");
//        updateService();
//        return service.getBinder();
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        Log.e(TAG, "onTaskRemoved: ");
//    }
//
//    @Override
//    public void onDestroy() {
//        Log.e(TAG, "onDestroy: ");
//        hideNotification();
////        try {
//        if(bsp!=null)bsp.dispose();
//        unregisterReceiver(usbReceiver);
////        } catch (Exception e){}
//        super.onDestroy();
//    }
//
//    @Override
//    public void onRootChecked(RootChecker.Status status) {
//        if(status== RootChecker.Status.NON_ROOTED){
//            App.store()
//                    .write(PersistentStore.Key.ATTESTATION,String.valueOf(System.currentTimeMillis()));
//        }
//        updateOnRootChecked(status);
//    }
//
//    private void updateService(){
//        if(connStt==UsbConnState.NOT_CONNECTED) initNitgenIfConnected();
//        if(connStt==UsbConnState.NOT_OPENED) openNitgen();
//        if(rootStatus==RootChecker.Status.UNKNOWN){
//            new RootChecker(this,this).checkForRoot();
//        }
//    }
//
//    private void initService() {
//
//        Log.e(TAG,"initService: ");
//
//        service = new Messenger(new Handler(this));
//
//        IntentFilter filter = new IntentFilter();
//
//        filter.addAction(CONNECTIVITY_CHANGE);
//        filter.addAction(USB_MANUAL_PERMISSION);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        registerReceiver(usbReceiver,filter);
//    }
//
//    public static UsbDevice scanNitgenScanner(Context context) {
//        UsbManager usbManager = (UsbManager) context.getSystemService(USB_SERVICE);
//        if (usbManager == null) return null;
//        for (UsbDevice device : usbManager.getDeviceList().values()) {
//            if (device.getVendorId() == 2694 && device.getProductId() == 1616) {
//                return device;
//            }
//        }
//        return null;
//    }
//
//    private void initNitgenIfConnected(){
//        Log.e(TAG, "initNitgenIfConnected: ");
//        if(scanNitgenScanner(this)!=null){
//            showNotification();
//            if(bsp==null){
//                NBioBSPJNI.CURRENT_PRODUCT_ID = 0;
//                bsp = new NBioBSPJNI(SERIAL_CODE,this,this);
//            }
//            this.connStt = UsbConnState.NOT_OPENED;
//        } else {
////            bsp=null;
//            serial=null;
//            this.connStt = UsbConnState.NOT_CONNECTED;
//        }
//    }
//
//    private void open(){
//        Log.e(TAG, "openDevice: ");
//        int openedId = bsp.GetOpenedDeviceID();
//        int errorCode = bsp.GetErrorCode();
//        Log.e(TAG, "openDevice: id: "+openedId+" errorCode: "+errorCode);
//        if(openedId==0&&errorCode == NBioBSPJNI.ERROR.NBioAPIERROR_NONE||
//                openedId!=0&&errorCode != NBioBSPJNI.ERROR.NBioAPIERROR_NONE){
//            bsp.OpenDevice();
//        } else if(openedId!=0){
//            OnConnected();
//        }
//        int err;
//        if((err = bsp.GetErrorCode())!=0)onErrorOccurred(err);
//    }
//
//    void setCaptureQualityInfo() {
//        NBioBSPJNI.CAPTURE_QUALITY_INFO mCAPTURE_QUALITY_INFO = bsp.new CAPTURE_QUALITY_INFO();
//        mCAPTURE_QUALITY_INFO.EnrollCoreQuality = 80;
//        mCAPTURE_QUALITY_INFO.EnrollFeatureQuality = 25;
//        mCAPTURE_QUALITY_INFO.VerifyCoreQuality = 80;
//        mCAPTURE_QUALITY_INFO.VerifyFeatureQuality = 25;
//        bsp.SetCaptureQualityInfo(mCAPTURE_QUALITY_INFO);
//    }
//
//    private byte[] capture(int timeout,int type) {
//        Log.e(TAG, "capture: "+timeout);
//        setQuality(0);
//        this.connStt = UsbConnState.CAPTURING;
//        onConnStateChanged(this.serial,null);
//
//        int quality = 0;//,count = 3;
//        byte[] isoData = null;
//        NBioBSPJNI.FIR_HANDLE captureHandle = bsp.new FIR_HANDLE();
//        NBioBSPJNI.FIR_HANDLE auditHandle = bsp.new FIR_HANDLE();
//        NBioBSPJNI.CAPTURED_DATA capturedData = bsp.new CAPTURED_DATA();
//        NBioBSPJNI.INPUT_FIR inputFir = bsp.new INPUT_FIR();
//        NBioBSPJNI.Export export = bsp.new Export();
//
////        long timeline = System.currentTimeMillis()+timeout;
////        int remainingTimeout = timeout;
////        do{
////            count--;
//
//        bsp.Capture(NBioBSPJNI.FIR_PURPOSE.ENROLL,captureHandle,timeout,auditHandle,capturedData);
//
////            bsp.Capture(NBioBSPJNI.FIR_PURPOSE.ENROLL,captureHandle,remainingTimeout,auditHandle,capturedData);
////            remainingTimeout = (int)(timeline - System.currentTimeMillis());
//
//        if (bsp.IsErrorOccured()){
//            onErrorOccurred(bsp.GetErrorCode());
//            return null;
////                continue;
//        }
//
//        quality = capturedData.getImageQuality();
//
//        if(type==1){
//            inputFir.SetFIRHandle(auditHandle);
//            NBioBSPJNI.Export.AUDIT exportAudit = export.new AUDIT();
//            export.ExportAudit(inputFir, exportAudit);
//            if (bsp.IsErrorOccured()) return null;
//            NBioBSPJNI.ISOBUFFER ISOBuffer = bsp.new ISOBUFFER();
//            bsp.ExportRawToISOV1(exportAudit,ISOBuffer,false,NBioBSPJNI.COMPRESS_MODE.WSQ);
//            if (bsp.IsErrorOccured()) return null;
//            isoData = ISOBuffer.Data;
//
////                String wh = "width: "+exportAudit.ImageWidth+"\nheight: "+exportAudit.ImageHeight;
////                Toast.makeText(this,wh+"\nbytes: "+isoData.length, Toast.LENGTH_LONG).show();
//
//        } else {
//            inputFir.SetFIRHandle(captureHandle);
//            NBioBSPJNI.Export.DATA exportData = export.new DATA();
//            export.ExportFIR(inputFir,exportData,NBioBSPJNI.EXPORT_MINCONV_TYPE.ISO);
//            isoData = exportData.FingerData[0].Template[0].Data;
//        }
//
////            if(bsp.IsErrorOccured())continue;
////        } while(remainingTimeout>0&&count>0&&quality<80);
//
//        setQuality(quality);
//        return isoData;
//    }
//
//    @Override
//    public void OnConnected(){
//        Log.e(TAG, "OnConnected: ");
//        this.serial = readSerial();
//        this.connStt = UsbConnState.OPENED;
//        onConnStateChanged(this.serial,null);
//    }
//
//    @Override
//    public int OnCaptured(final NBioBSPJNI.CAPTURED_DATA captured_data) {
//        Log.e(TAG, "OnCaptured: ");
//        this.connStt=UsbConnState.CAPTURED;
//        onConnStateChanged(this.serial,captured_data.getImage());
//        return 0;
//    }
//
//    @Override
//    public void OnDisConnected(){
//        Log.e(TAG, "OnDisConnected: ");
//        this.serial = null;
//        if(this.connStt!=UsbConnState.NOT_CONNECTED) {
//            this.connStt= UsbConnState.NOT_OPENED;
//            onConnStateChanged(this.serial,null);
//        }
//    }
//
//    private void onConnStateChanged(String serial, Bitmap image) {
//        if(client!=null){
//            try {
//                Message msg = Message.obtain(
//                        null,
//                        What.UPD_CONN_STATE.ordinal(),
//                        connStt.ordinal(),
//                        0,
//                        image
//                );
//
//                Bundle bundle = new Bundle();
//                bundle.putString("serial",serial);
//                msg.setData(bundle);
//                this.client.send(msg);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        } else Log.e(TAG, "onConnStateChanged: "+connStt.name()+", null client");
//        if(connStt==UsbConnState.CAPTURED)this.connStt = UsbConnState.OPENED;
//    }
//
////    private void recurrentCapture(int timeout){
////        Log.e(TAG, "recurrentCapture: count: "+count);
////        if(count>0){
////            byte[] fmr = capture(timeout);
////            fmrs[count-1] = fmr==null?null: Base64.encodeToString(fmr,Base64.DEFAULT);
////            Log.e(TAG, "recurrentCapture: "+fmrs[count-1]);
////            count--;
////            recurrentCapture(timeout);
////        }
////    }
//
//    private void onErrorOccurred(int err){
//        String msg = "UNKNOWN";
//        this.err = err;
//        switch (err){
//            case NBioBSPJNI.ERROR.NBioAPIERROR_DEVICE_NOT_CONNECTED:
//                msg = "Device not connected";
//                Thread.currentThread().interrupt();
//                break;
//            case NBioBSPJNI.ERROR.NBioAPIERROR_CAPTURE_TIMEOUT:
//                msg = "Capture timeout";
//                Thread.currentThread().interrupt();
//                break;
//            case NBioBSPJNI.ERROR.NBioAPIERROR_DEVICE_ALREADY_OPENED:
//                msg = "Device already opened";
//                break;
//            case NBioBSPJNI.ERROR.NBioAPIERROR_DEVICE_NOT_OPENED:
//                msg = "Device not opened";
//                break;
//            case NBioBSPJNI.ERROR.NBioAPIERROR_DEVICE_NOT_PERMISSION:
//                msg = "Device does not have permission";
//                Thread.currentThread().interrupt();
//                break;
//            case NBioBSPJNI.ERROR.NBioAPIERROR_DEVICE_LOST_DEVICE:
//                break;
//            // worker was not started for these errors
//            case NO_POSH_INPUT:
//                msg = "No posh input";
//                break;
//            case INVALID_POSHES:
//                msg = "Invalid poshes";
//                break;
//            default:
//                msg = msg+"-"+err;
//                break;
//        }
//
//        final String finalMsg = "Error: "+msg;
//        Bundle bundle = new Bundle();
//        bundle.putString("err",finalMsg);
//        if(client!=null){
//            try {
//                this.client.send(Message.obtain(
//                        null,
//                        What.UPD_USB_ERR.ordinal(),err,0,bundle));
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        } else Log.e(TAG, "onErrorOccurred: null client");
//
//        Log.e(TAG, "onErrorOccurred: "+msg);
//    }
//
//    private void openNitgen(){
//        Log.e(TAG, "openNitgen:");
//
//        final Thread bspThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(connStt==UsbConnState.NOT_OPENED){
//                    connStt=UsbConnState.OPENING;
//                    open();
//                }
//            }
//        });
//
//        bspThread.start();
//
//        if(this.client!=null) {
//            Message msg = Message.obtain(
//                    null,
//                    What.UPD_CONN_STATE.ordinal(),
//                    connStt.ordinal(),
//                    0,
//                    0
//            );
//            try {
//                this.client.send(msg);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public boolean handleMessage(Message msg) {
//        Log.e(TAG, "handleMessage: "+msg.what);
//        try {
//            switch (What.values()[msg.what]){
//                case REQ_SERVICE_TEST:
//                    this.client = msg.replyTo;
//                    Bundle bundle = new Bundle();
//                    bundle.putIntArray(
//                            "IORtReg",
//                            new int[]{
//                                    this.internetStatus.ordinal(),
//                                    this.connStt.ordinal(),
//                                    this.rootStatus.ordinal(),
//                                    this.regProgress.ordinal()
//                            });
//                    bundle.putString("serial",this.serial);
//                    Message reply = Message.obtain(null, What.RES_SERVICE_TEST.ordinal());
//                    reply.setData(bundle);
//                    this.client.send(reply);
//                    break;
//                case REQ_ROOT_CHECK:
//                    if(this.rootStatus!= RootChecker.Status.CHECKING){
//                        new RootChecker(this,this).checkForRoot();
//                    }
//                    break;
//                case REQ_OPEN:
//                    updateService();
//                    break;
//                case REQ_CAPTURE:
//                    int timeout = msg.getData().getInt("timeout",10000);
//                    int type = msg.getData().getInt("ftype",0);
//                    byte[] iso = capture(timeout,type);
//                    Bundle captRet = new Bundle();
//                    captRet.putString("FMR",iso==null?null:Base64.encodeToString(iso,Base64.DEFAULT));
//                    captRet.putByteArray("ISO",iso);
//                    captRet.putInt("QUALITY",this.quality);
//                    captRet.putInt("FTYPE",type);
//                    if(this.client!=null){
//                        Message captRetMsg = Message.obtain(null,What.UPD_FMR.ordinal());
//                        captRetMsg.setData(captRet);
//                        this.client.send(captRetMsg);
//                    }
//                    break;
//                case REQ_REGISTER:
//                    Bundle data = msg.getData();
//                    this.env = data.getString("env");
//                    this.serial = data.getString("serial");
//
//                    if(rootStatus== RootChecker.Status.NON_ROOTED){
////                        new KeysCreator(env,serial,Build.VERSION.RELEASE,BuildConfig.VERSION_CODE).execute();
//                        new KeysCreator(AndroidSecurityUtil.getSpec(this,env),this).execute();
//                    }
//                    break;
//                case REQ_CAPTURE_CANCEL:
//                    Thread.currentThread().interrupt();
//                    Log.e(TAG, "handleMessage: capture canceled");
//                    if(bsp!=null)bsp.CaptureCancel();
//                    break;
//                case NULLIFY_CLIENT:
//                    this.client = null;
//                    break;
//            }
//        } catch (RemoteException e){
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    private void updateOnRootChecked(RootChecker.Status status){
//        this.rootStatus = status;
//        if(client!=null){
//            try {
//                this.client.send(Message.obtain(
//                        null,
//                        What.UPD_ROOT_STATUS.ordinal(),
//                        status.ordinal(),0));
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        } else Log.e(TAG, "updateOnRootChecked: null client");
//    }
//
//    public String readSerial(){
//        String strSerial = "FAILED_SERIAL";
//        try {
//            byte[] idBytes = new byte[StaticVals.wLength_GET_ID];
//            bsp.getDeviceID(idBytes);
//            if (bsp.IsErrorOccured()) {
//                int errorCode = bsp.GetErrorCode();
//            } else {
//
//                strSerial = "";
//
//                for (int i = 0; i < StaticVals.wLength_GET_ID; i++){
//                    if (idBytes[i] == 0 || idBytes[i] == '\0') break;
//                    strSerial += String.format("%02X", idBytes[i]);
//                }
//
//                if (strSerial.length() > 11) strSerial = strSerial.substring(0, 11);
//
//                if (strSerial.contains("3200000") || strSerial.equals("FAILED_SERIAL") || strSerial.contains("FFFF") || strSerial.length() < 11 || strSerial.contains("?????")) {
//                    byte[] value = new byte[StaticVals.wLength_GET_VALUE];
//                    bsp.getValue(value);
//                    if (bsp.IsErrorOccured()) {
//                        int errorCode = bsp.GetErrorCode();
////                        this.setErrorCode(getContractErrorCode(errorCode,FAILED_SERIAL));
//                        System.out.println("Error: "+errorCode);
//                    }
//                    strSerial = new String(value);
//                    if (strSerial.length() > 11) strSerial = strSerial.substring(0, 11);
//                }
//            }
//        } catch (Exception ex) {
//            System.out.println("GetDeviceSerialNumber: "+ex.getMessage());
////            setErrorCode(FAILED_SERIAL);
//            ex.printStackTrace();
//        }
//
//        strSerial = strSerial.replace("\0","");
////        setErrorCode(getContractErrorCode(bsp.GetErrorCode(),FAILED_SERIAL));
//        return strSerial;
//    }
//
//    private static boolean isNetworkAvailable(Context context) {
//        final ConnectivityManager connectivityManager =
//                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
//        return connectivityManager != null &&
//                connectivityManager.getActiveNetworkInfo() != null &&
//                connectivityManager.getActiveNetworkInfo().isConnected();
//    }
//
//
//
////    public RegResponse registerDevice(final String env, final String serial, String osVer, final int iv) {
////        Log.e(TAG, "requestRegisterDevice: Registering device...");
////        final PersistentStore ps = new PersistentStore(this);
////        String clientInfo = ps.read(PersistentStore.Key.CLIENT_INFO);
////        String[] clientInfoArray = new String[5];
////        if(clientInfo!=null){
////            String[] split = clientInfo.split("\n");
////            System.arraycopy(split, 0, clientInfoArray, 0, split.length);
////        }
//
////        KeyPair keyPair;
////        try {
////            keyPair = CryptoUtil.getInstance().createKeys(AndroidSecurityUtil.getSpec(this,env));
////            privateKey = keyPair.getPrivate();
////        } catch (Exception e) {
////            e.printStackTrace();
////            return RegResponse.ERR_CREATE_KEYS;
////        }
//
////        String publicKey = Base64.encodeToString(keyPair.getPublic().getEncoded(),Base64.DEFAULT);
////
////        BioEnablePacketRequest request = new BioEnablePacketRequest()
////                .setEnvironment(env)
////                .setDcIeSerial(serial)
////                .setDevicePublicKey(publicKey)
////                .setClientname(clientInfoArray[0])
////                .setAddress(clientInfoArray[1])
////                .setPincode(clientInfoArray[2])
////                .setMobileNum(clientInfoArray[3])
////                .setImei(clientInfoArray[4])
////                .setOsVer(osVer)
////                .setIv(iv);
////        String requestXml;
//
////        try {
////            requestXml = XMLHelper.getInstance().createBioEnablePacketRequestXML(request);
////        } catch (Exception e){
////            e.printStackTrace();
////            return RegResponse.ERR_BIO_REQ_PARSE;
////        }
////
////        String path = CryptoUtil.getInstance().getSubUrl();
////        Log.e(TAG, "registerDevice: "+path);
////        RetrofitClient.getApi().registerDevice(requestXml).enqueue(new Callback<String>() {
////            @Override
////            public void onResponse(Call<String> call, Response<String> resp) {
////                try {
////                    final BioEnablePacketResponse response = XMLHelper.getInstance()
////                            .parseBioEnablePacketResponse(resp.body());
////
////                    ps.write(PersistentStore.Key.ENV,env);
////                    ps.write(PersistentStore.Key.SERIAL,serial);
////                    ps.write(PersistentStore.Key.DC,response.getUuid_value());
////                    ps.write(PersistentStore.Key.MC,response.getSigned_device_public_key());
////                    ps.write(PersistentStore.Key.TOKEN,response.getToken());
////                    ps.write(PersistentStore.Key.IV,String.valueOf(iv));
////
////                    return RegResponse.SUCCESS;
////                } catch (Exception/*IOException | ParserConfigurationException | SAXException*/ e) {
////                    return RegResponse.ERR_BIO_RES_PARSE;
////                }
////            }
////
////            @Override
////            public void onFailure(Call<String> call, Throwable t) {
////                Log.e(TAG, "onFailure: "+t.getMessage());
////            }
////        });
//
////        if(requestXml!=null)return RegResponse.ERR_NETWORK;
//
////        String rawResponse;
////        try {
////            rawResponse = NetOps.getInstance().requestRegisterDevice(requestXml);
////        } catch (Exception/*IOException*/ e){
////            return RegResponse.ERR_NETWORK;
////        }
////
////        String bioPacketResponseXML;
////        try {
////            CryptoUtil util = CryptoUtil.getInstance();
////            bioPacketResponseXML = util.decryptAES256(rawResponse, util.getEncryptionKey(util.createGUID()));
////        } catch (Exception e){
////            e.printStackTrace();
////            return RegResponse.ERR_BIO_RES_DECRYPT;
////        }
////
////        try {
////            final BioEnablePacketResponse response = XMLHelper.getInstance()
////                    .parseBioEnablePacketResponse(bioPacketResponseXML);
////
////            ps.write(PersistentStore.Key.ENV,env);
////            ps.write(PersistentStore.Key.SERIAL,serial);
////            ps.write(PersistentStore.Key.DC,response.getUuid_value());
////            ps.write(PersistentStore.Key.MC,response.getSigned_device_public_key());
////            ps.write(PersistentStore.Key.TOKEN,response.getToken());
////            ps.write(PersistentStore.Key.IV,String.valueOf(iv));
////
////            return RegResponse.SUCCESS;
////        } catch (Exception/*IOException | ParserConfigurationException | SAXException*/ e) {
////            return RegResponse.ERR_BIO_RES_PARSE;
////        }
////    }
//
////    public static String writeToFile(byte[] data,String fileName){
////
////        try {
////            File path = new File("/storage/emulated/0/BioEnable/");
////            if(!path.exists())path.mkdir();
////            File file = new File(path,fileName);
////            FileOutputStream fos = new FileOutputStream(file);
////            fos.write(data);
////            fos.flush();
////            fos.close();
////            return "SUCCESS";
////        } catch (IOException e) {
////            e.printStackTrace();
////            return e.toString();
////        }
////    }
////
////    private byte[] readFromFile(){
//////        StringBuilder sb = new StringBuilder();
////        byte[] bytes = null;
////        try{
////            File path = new File("/storage/emulated/0/BioEnable/");
////            File file = new File(path,"fir.txt");
////            bytes = IOUtils.readInputStreamFully(new FileInputStream(file));
////        } catch (Exception e){
////            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
////        }
////        return bytes;
////    }
//}
