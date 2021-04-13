package `in`.bioenable.rdservice.fp.helper

import `in`.bioenable.rdservice.fp.contracts.CryptoHelper
import android.content.Context
import android.util.Base64
import java.security.KeyPair

class CryptoHelperImpl(val context:Context) : CryptoHelper {

    private var keysCreatorInteractor: CryptoHelper.KeysCreatorInteractor? = null

    override fun keysCreatorInteractor(): CryptoHelper.KeysCreatorInteractor {
        if (keysCreatorInteractor == null) keysCreatorInteractor = newKeysCreatorInteractor()
        return keysCreatorInteractor as CryptoHelper.KeysCreatorInteractor
    }

    private fun newKeysCreatorInteractor(): CryptoHelper.KeysCreatorInteractor {
        return object : CryptoHelper.KeysCreatorInteractor, KeysCreator.OnKeysCreatedCallback {
            private lateinit var callback: CryptoHelper.KeysCreatorInteractor.OnPublicKeyAvailableCallback
            private lateinit var env : String
            override fun createKeys(env: String, callback: CryptoHelper.KeysCreatorInteractor.OnPublicKeyAvailableCallback) {
                this.env = env
                this.callback = callback
                KeysCreator(AndroidSecurityUtil.getSpec(context, env), this).create()
            }

            override fun onFailure() {
                callback.onError()
            }

            override fun onKeysCreated(keyPair: KeyPair) {
                callback.onPublicKeyAvailable(env,encodeToBase64String_NO_WRAP(keyPair.public.encoded))
            }
        }
    }

    override fun encodeToBase64(bytes: ByteArray): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun encodeToBase64String_NO_WRAP(bytes: ByteArray): String {
        return Base64.encodeToString(bytes,Base64.NO_WRAP)
    }

    override fun encodeToBase64String_DEFAULT(bytes: ByteArray): String {
        return Base64.encodeToString(bytes,Base64.DEFAULT)
    }

    override fun decodeBase64(string: String): ByteArray {
        return Base64.decode(string,Base64.DEFAULT)
    }

    override fun decodeBase64(bytes: ByteArray): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}