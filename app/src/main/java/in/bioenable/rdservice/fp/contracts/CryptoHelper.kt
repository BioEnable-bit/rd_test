package `in`.bioenable.rdservice.fp.contracts

interface CryptoHelper {

    interface KeysCreatorInteractor {

        interface OnPublicKeyAvailableCallback {
            fun onError()
            fun onPublicKeyAvailable(env:String,publicKey:String)
        }
        fun createKeys(env:String,callback:OnPublicKeyAvailableCallback)
    }

    fun keysCreatorInteractor() : KeysCreatorInteractor

    fun encodeToBase64(bytes:ByteArray):ByteArray

    fun encodeToBase64String_NO_WRAP(bytes:ByteArray):String

    fun encodeToBase64String_DEFAULT(bytes:ByteArray):String

    fun decodeBase64(string:String):ByteArray

    fun decodeBase64(bytes:ByteArray):ByteArray
}