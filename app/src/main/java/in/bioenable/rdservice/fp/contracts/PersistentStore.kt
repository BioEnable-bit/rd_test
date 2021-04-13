package `in`.bioenable.rdservice.fp.contracts

interface PersistentStore {

    fun getInternalVersionName():String

    fun getInternalVersionCode():Int

    fun getOsName():String

    fun getOsVersion():String

    fun getImei():String?

    fun getIpAddress():String

    fun getMc(serial:String,env:String):String?

    fun putMc(serial:String,env:String,mc:String)

    fun getDc(serial:String):String?

    fun putDc(serial:String,dc:String)

    fun getUidaiCertificate(env:String):String?

    fun putUidaiCertificate(env:String,cert:String)

    fun putOtpvInfo(serial:String,otpvInfo: Any)

    fun getOtpvInfo(serial:String):Any?

    fun putVersionInfo(serial:String,versionInfo:Any)

    fun getVersionInfo(serial:String):Any?

    fun putActivationInfo(serial:String,activationInfo:Any)

    fun getActivationInfo(serial:String):Any?

    fun putSafetyNetAttestation(attestation:String)

    fun getSafetyNetAttestation():String?

    fun putInitError(serial:String,errorCode:String)

    fun getInitError(serial:String):String?

    fun deleteInitError(serial:String)

    fun deleteOtpvInfo(serial:String)

    fun deleteActivationInfo(serial:String)

    fun deleteAllEntries()

    fun putActivationLink(activationLink:String)

    fun getActivationLink():String?

    fun putImei(imei:String)
}
