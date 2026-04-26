package ug.ac.ndejje.cbc_teachers_toolkit.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun openUrl(context: Context, url: String) {
    val uri = Uri.parse(url)
    CustomTabsIntent.Builder().build().launchUrl(context, uri)
}
