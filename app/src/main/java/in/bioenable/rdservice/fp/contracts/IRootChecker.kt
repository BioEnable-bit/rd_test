package `in`.bioenable.rdservice.fp.contracts

interface IRootChecker {

    interface Callback {
        fun onErrorOccurred()
        fun onFoundRooted()
        fun onFoundNonRooted()
    }

    fun checkForRoot(callback:Callback)
    fun isAttestationOlderThanHours(attestation:String?,hours:Int) : Boolean
}