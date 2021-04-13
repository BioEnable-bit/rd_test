package `in`.bioenable.rdservice.fp.app

import `in`.bioenable.rdservice.fp.contracts.Manager
import `in`.bioenable.rdservice.fp.ui.OnCallActivity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

class AppManager(val appContext:Context) : Manager {

    override fun disableClientCalls() {
        appContext.packageManager.setComponentEnabledSetting(
                ComponentName(appContext,OnCallActivity::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

    override fun enableClientCalls() {
        appContext.packageManager.setComponentEnabledSetting(
                ComponentName(appContext,OnCallActivity::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }
}