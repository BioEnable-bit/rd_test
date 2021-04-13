package `in`.bioenable.rdservice.fp.contracts

interface NotificationHelper {

//    fun sendNotification(id:Int,title:String,msg:String,activityKlass:Class<Any>,cancellable:Boolean)
//
//    fun removeNotification(type:Int)

    fun showPhoneVerificationRequiredNotification()

    fun showDeviceRegistrationRequiredNotification()

    fun showActivationRequiredNotification()

    fun showPublicDeviceNotification()

    fun showVersionUpgradeNotification()

}