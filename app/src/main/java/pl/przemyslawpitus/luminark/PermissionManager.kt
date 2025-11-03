package pl.przemyslawpitus.luminark

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val activity: Activity) {
    fun ensurePermissions(permissions: List<String>) {
        val permissionsToRequest = permissions.filter { !hasPermission(it) }
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(permissions: List<String>) {
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), 1)
    }
}