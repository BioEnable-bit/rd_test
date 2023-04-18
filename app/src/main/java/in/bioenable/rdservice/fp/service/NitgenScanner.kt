package `in`.bioenable.rdservice.fp.service

import `in`.bioenable.rdservice.fp.contracts.IScanner
import android.content.Context
import android.util.Log
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI
import com.nitgen.SDK.AndroidBSP.StaticVals
import java.nio.charset.StandardCharsets

class NitgenScanner(context:Context,val callbacks: IScanner.Callbacks) : IScanner,NBioBSPJNI.CAPTURE_CALLBACK {

    private val TAG = NitgenScanner::class.java.simpleName
    private val SERIAL_CODE = "010701-613E5C7F4CC7C4B0-72E340B47E034015"

    private var isOpened = false

    private val bsp = NBioBSPJNI(SERIAL_CODE,context,this)

    override fun OnDisConnected() {
        isOpened = false
        callbacks.onDisconnected()
        bsp.dispose()
    }

    override fun OnConnected() {
        val serial = readSerial()
        callbacks.onOpened(serial)
    }

    override fun OnCaptured(p0: NBioBSPJNI.CAPTURED_DATA?): Int {
        callbacks.onCaptured(p0?.image,p0?.imageQuality?:0)
        return 0
    }

    override fun open() {
        Log.e(TAG,"open(): opened device id: ${bsp.GetOpenedDeviceID()}, error code: ${bsp.GetErrorCode()}")
        if(!isOpened){
            isOpened = true
            bsp.OpenDevice()
        }
    }

    override fun capture(type: Int, timeout: Int) {

        Log.e(TAG, "capture: $timeout")

        var quality = 0//,count = 3;
        //var isoData: ByteArray? = null Commented on 06-01-2022
        var isoDatafir: ByteArray? = null
        var isoDatafmr: ByteArray? = null
        var isoData: ByteArray? = null
        val captureHandle = bsp.FIR_HANDLE()
        val auditHandle = bsp.FIR_HANDLE()
        val capturedData = bsp.CAPTURED_DATA()
        val inputFir = bsp.INPUT_FIR()
        val export = bsp.Export()

        bsp.Capture(NBioBSPJNI.FIR_PURPOSE.ENROLL, captureHandle, timeout, auditHandle, capturedData)


        if (errorOccurredAndActed()) return

        when (type) {
            1 ->
            {
                Log.e(TAG, "capture: FIR")
                inputFir.SetFIRHandle(auditHandle)
                val exportAudit = export.AUDIT()
                export.ExportAudit(inputFir, exportAudit)

                if (errorOccurredAndActed()) return

                val ISOBuffer = bsp.ISOBUFFER()
                bsp.ExportRawToISOV1(exportAudit, ISOBuffer, false, NBioBSPJNI.COMPRESS_MODE.WSQ)
                if (errorOccurredAndActed()) return

                isoDatafir = ISOBuffer.Data

                callbacks.onCapturedFIR(isoDatafir,type,quality)
            }
            0 ->
            {
                Log.e(TAG, "capture: FMR")
                inputFir.SetFIRHandle(captureHandle)
                val exportData = export.DATA()
                export.ExportFIR(inputFir, exportData, NBioBSPJNI.EXPORT_MINCONV_TYPE.ISO)
                if (errorOccurredAndActed()) return

                isoDatafmr = exportData.FingerData[0].Template[0].Data

               // Log.e(TAG, "capture: ${String(isoDatafmr, StandardCharsets.UTF_8)}")


                callbacks.onCapturedFMR(isoDatafmr,type,quality)
            }
            else -> {
                Log.e(TAG, "capture: FIR+FMR")

                inputFir.SetFIRHandle(auditHandle)
                val exportAudit = export.AUDIT()
                export.ExportAudit(inputFir, exportAudit)

                if (errorOccurredAndActed()) return

                val ISOBuffer = bsp.ISOBUFFER()
                bsp.ExportRawToISOV1(exportAudit, ISOBuffer, false, NBioBSPJNI.COMPRESS_MODE.WSQ)
                if (errorOccurredAndActed()) return

                isoDatafir = ISOBuffer.Data

                inputFir.SetFIRHandle(captureHandle)
                val exportData = export.DATA()
                export.ExportFIR(inputFir, exportData, NBioBSPJNI.EXPORT_MINCONV_TYPE.ISO)
                if (errorOccurredAndActed()) return

                isoDatafmr = exportData.FingerData[0].Template[0].Data

                callbacks.onCaptured(isoDatafir!!, isoDatafmr!!,type,quality)
            }
        }




     }

    override fun cancelCapture() {
        Log.e(TAG,"cancelCapture: ")
        bsp.CaptureCancel()
    }

//    override fun dispose() {
//        bsp.dispose()
//    }

    fun readSerial(): String {
        var strSerial = "FAILED_SERIAL"
        try {
            val idBytes = ByteArray(StaticVals.wLength_GET_ID)
            bsp.getDeviceID(idBytes)
            if (bsp.IsErrorOccured()) {
                val errorCode = bsp.GetErrorCode()
            } else {

                strSerial = ""

                for (i in 0 until StaticVals.wLength_GET_ID) {
                    if (idBytes[i].toInt() == 0 || idBytes[i] == '\u0000'.toByte()) break
                    strSerial += String.format("%02X", idBytes[i])
                }

                if (strSerial.length > 11) strSerial = strSerial.substring(0, 11)

                if (strSerial.contains("3200000") || strSerial == "FAILED_SERIAL" || strSerial.contains("FFFF") || strSerial.length < 11 || strSerial.contains("?????")) {
                    val value = ByteArray(StaticVals.wLength_GET_VALUE)
                    bsp.getValue(value)
                    if (bsp.IsErrorOccured()) {
                        val errorCode = bsp.GetErrorCode()
                        //                        this.setErrorCode(getContractErrorCode(errorCode,FAILED_SERIAL));
                        println("Error: $errorCode")
                    }
                    strSerial = String(value)
                    if (strSerial.length > 11) strSerial = strSerial.substring(0, 11)
                }
            }
        } catch (ex: Exception) {
            println("GetDeviceSerialNumber: " + ex.message)
            //            setErrorCode(FAILED_SERIAL);
            ex.printStackTrace()
        }

        strSerial = strSerial.replace("\u0000", "")
        //        setErrorCode(getContractErrorCode(bsp.GetErrorCode(),FAILED_SERIAL));
        return strSerial
    }

    private fun errorOccurredAndActed():Boolean{



        when(bsp.GetErrorCode()){
            NBioBSPJNI.ERROR.NBioAPIERROR_NONE -> {
                return false
            }
            NBioBSPJNI.ERROR.NBioAPIERROR_USER_CANCEL -> {
                callbacks.onCaptureCancelled()
                return true
            }
            NBioBSPJNI.ERROR.NBioAPIERROR_CAPTURE_TIMEOUT -> {
                callbacks.onCaptureTimedOut()
                return true
            }
            NBioBSPJNI.ERROR.NBioAPIERROR_DEVICE_NOT_CONNECTED -> {
                callbacks.onDisconnected()
                return true
            }
            else -> {
                callbacks.onErrorOccurred(bsp.GetErrorCode())
                return true
            }
        }
    }

}