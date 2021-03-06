package com.zk.soulierge.support.utilExt

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.zk.soulierge.R
import com.zk.soulierge.support.base.CoreApp
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun String.showToast() {
    Toast.makeText(CoreApp.INSTANCE, this, Toast.LENGTH_SHORT).show()
}

fun String.showSnack(view: View, buttonText: String = "", length: Int = Snackbar.LENGTH_LONG) {
    val snackBar = Snackbar.make(view, this, Snackbar.LENGTH_LONG)
    if (buttonText.isNotBlank()) {
        snackBar.setAction(buttonText) {

        }
        snackBar.setActionTextColor(R.color.white.color())
    }
    snackBar.show()
}

fun String.logMsg(tag: String) {
    Log.e(tag, this)
}

fun Int.color(): Int = ContextCompat.getColor(CoreApp.INSTANCE, this)

fun Int.drawable(): Drawable? = ContextCompat.getDrawable(CoreApp.INSTANCE, this)

fun Int.string(): String = CoreApp.INSTANCE.getResources().getString(this)

fun Int.string(context: Context): String = context.getResources().getString(this)

@RequiresApi(Build.VERSION_CODES.M)
fun Activity.isPermissionGranted(permission: String, requestCode: Int): Boolean {
    var isGranted = true
    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        if (this.shouldShowRequestPermissionRationale(permission)) {

        } else {
            this.requestPermissions(arrayOf(permission), requestCode)
        }
        isGranted = false
    }
    return isGranted
}

@RequiresApi(Build.VERSION_CODES.M)
fun Fragment.isPermissionGranted(permission: String, requestCode: Int): Boolean {
    var isGranted = true
    if (context?.let { ContextCompat.checkSelfPermission(it, permission) } != PackageManager.PERMISSION_GRANTED) {
        if (this.shouldShowRequestPermissionRationale(permission)) {

        } else {
            this.requestPermissions(arrayOf(permission), requestCode)
        }
        isGranted = false
    }
    return isGranted
}

fun Activity.hideSoftKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = this.currentFocus
    if (view == null)
        view = View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun reformatDate(currentFormat: String, newFormat: String, givenDate: String): String {
    val date: Date = java.text.SimpleDateFormat(currentFormat).parse(givenDate)
    val cal = Calendar.getInstance()
    cal.timeZone = TimeZone.getTimeZone("UTC")
    cal.time = date

    val sdf = SimpleDateFormat(newFormat, Locale.getDefault())
    return sdf.format(cal.time)
}

fun String?.toCamelCase(): String? {
    if (this == null) return null
    val ret = StringBuilder(this.length)
    for (word in this.split(" ").toTypedArray()) {
        if (!word.isEmpty()) {
            ret.append(Character.toUpperCase(word[0]))
            ret.append(word.substring(1).toLowerCase())
        }
        if (ret.length != this.length) ret.append(" ")
    }
    return ret.toString()
}


fun AppCompatActivity?.initToolbar(
    toolbar: Toolbar?,
    isEnable: Boolean? = true,
    title: String? = ""
) {
    if (toolbar != null) {
        this?.setSupportActionBar(toolbar)
        isEnable?.let { this?.supportActionBar?.setHomeButtonEnabled(it) }
        isEnable?.let { this?.supportActionBar?.setDisplayHomeAsUpEnabled(it) }
        isEnable?.let { this?.supportActionBar?.setDisplayShowHomeEnabled(it) }
        if (this?.supportActionBar != null && title?.isNotEmpty() == true) {
            this.supportActionBar?.title = title
//        this.toolbar?.title = title
        }
    }
}

fun AppCompatActivity?.changeTitle(title: String?) {
    if (this?.supportActionBar != null && title?.isNotEmpty() == true) {
        this.supportActionBar?.title = title
//        this.toolbar?.title = title
    }
}

fun File.toMultipartBody(key: String, mediaType: String = "image/*"): MultipartBody.Part {
    val requestFile = RequestBody.create(MediaType.parse(mediaType), this)
    return MultipartBody.Part.createFormData(key, name, requestFile)
}

fun String.toReqBoday() = RequestBody.create(MediaType.parse("text/plain"), this)