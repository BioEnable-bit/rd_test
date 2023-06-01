package `in`.bioenable.rdservice.fp.contracts

interface App {

//    interface GetImeiCallback {
//        fun onReadImei(imei:String)
//        fun onError(error:String)
//    }
//    fun getImei(callback:GetImeiCallback)
//
//    interface NitgenPermissionRequestCallback {
//        fun onPermissionGranted()
//        fun onPermissionDenied()
//    }
//    fun askNitgenPermission(callback:NitgenPermissionRequestCallback)

    fun store() : PersistentStore

    fun cryptoHelper() : CryptoHelper

    fun webService() : WebService

    fun notificationHelper() : NotificationHelper

    //fun rootChecker() : IRootChecker //@yogesh changes regarding safetynet update 17/05/23 - commented this function

    fun manager():Manager
}