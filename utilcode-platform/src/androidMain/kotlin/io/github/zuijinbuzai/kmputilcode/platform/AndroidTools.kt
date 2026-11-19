package io.github.zuijinbuzai.kmputilcode.platform

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import kotlin.math.roundToInt

/** Context is supplied by the caller; these helpers never retain an Activity. */
object AndroidTools {
    fun dpToPx(context: Context, dp: Float): Int = (dp * context.resources.displayMetrics.density).roundToInt()
    fun pxToDp(context: Context, px: Int): Float = px / context.resources.displayMetrics.density
    fun hasPermission(context: Context, permission: String): Boolean = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    fun copyText(context: Context, text: String, label: String = "text") {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText(label, text))
    }
    /** Requires ACCESS_NETWORK_STATE. Validated means the OS recently validated internet access. */
    fun hasValidatedInternet(context: Context): Boolean {
        check(hasPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)) { "ACCESS_NETWORK_STATE is required" }
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    /** Opens the system browser; returns false if no handler exists. Call from the main thread. */
    fun openUrl(context: Context, url: String): Boolean {
        val uri = Uri.parse(url)
        require(uri.scheme in listOf("http", "https") && !uri.host.isNullOrBlank())
        return try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (_: android.content.ActivityNotFoundException) { false }
    }
    fun shareText(context: Context, text: String, title: String = "Share") {
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(send, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
