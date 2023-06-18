package com.example.LockPro.base

import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.android.billingclient.BuildConfig
import com.example.athu.base.BaseActivity
import java.util.Locale

abstract class PermissionActivity<T : ViewBinding> : BaseActivity<T>() {
    private var invokedCallback: (() -> Unit)? = null
    fun checkPermission(
        invokedCallback: () -> Unit,
    ) {
        this.invokedCallback = invokedCallback
        if (!isAutostartPermissionGranted(this, this.packageName)) {
            openAutostartPermissionSettings()
        } else {
            if (!isUsageAccessGranted(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent =
                        Intent(
                            Settings.ACTION_USAGE_ACCESS_SETTINGS
                        )
                    usageAccessActivityResultLauncher.launch(intent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                    this
                )
            ) {
                showPopupWindow()
            } else {
                this.invokedCallback?.invoke()
            }
        }

    }

    private fun showPopupWindow(
    ) {
        if ("xiaomi".equals(Build.MANUFACTURER.toLowerCase(Locale.ROOT))) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            );
            intent.putExtra("extra_pkgname", packageName);
            AlertDialog.Builder(this)
                .setTitle("Please Enable the additional permissions")
                .setMessage("You will not receive  while the app is in background if you disable these permissions")
                .setPositiveButton(
                    "Go to Settings"
                ) { dialog, which ->
                    systemAlertActivityResultLauncher.launch(intent)
                }
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .show();
        } else {
            var overlaySettings: Intent? = null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                overlaySettings = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                );
            } else {
                overlaySettings = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                )
            }
            systemAlertActivityResultLauncher.launch(overlaySettings)

        }
    }

    private val usageAccessActivityResultLauncher =
        this.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                showPopupWindow()
            } else {
                invokedCallback?.invoke()
            }

        }

    private val systemAlertActivityResultLauncher =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            invokedCallback?.invoke()
        }

    private val autostartActivityResultLauncher =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isAutostartPermissionGranted(
                    applicationContext,
                    applicationContext.packageName
                )
            ) {
                if (!isUsageAccessGranted(this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val intent =
                            Intent(
                                Settings.ACTION_USAGE_ACCESS_SETTINGS
                            )
                        usageAccessActivityResultLauncher.launch(intent)
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                        this
                    )
                ) {
                    showPopupWindow()
                }
            } else {
                invokedCallback?.invoke()
            }

        }


    fun isUsageAccessGranted(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun isAutostartPermissionGranted(context: Context, packageName: String): Boolean {
        return try {
            val autostartSetting = context.packageManager.getComponentEnabledSetting(
                ComponentName(packageName, "$packageName.YourAutostartReceiver")
            )
            (autostartSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    || autostartSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        } catch (e: Exception) {
            false
        }
    }

    fun openAutostartPermissionSettings(
    ) {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER.lowercase(Locale.getDefault())
            if ("xiaomi" == manufacturer) {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            } else if ("oppo" == manufacturer) {
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            } else if ("vivo" == manufacturer) {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            } else if ("huawei" == manufacturer) {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            } else if ("samsung" == manufacturer) {
                intent.component = ComponentName(
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.battery.BatteryActivity"
                )
            } else {
                if (!isUsageAccessGranted(this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val intent =
                            Intent(
                                Settings.ACTION_USAGE_ACCESS_SETTINGS,
                            )
                        usageAccessActivityResultLauncher.launch(intent)
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                        this
                    )
                ) {
                    showPopupWindow()
                } else {

                    invokedCallback?.invoke()
                }
            }
            autostartActivityResultLauncher.launch(intent)
        } catch (e: java.lang.Exception) {

        }
    }
}