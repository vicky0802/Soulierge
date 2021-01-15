package com.zk.soulierge.support.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.zk.soulierge.R
import com.zk.soulierge.support.prettyDialog.PrettyDialog
import com.zk.soulierge.support.utilExt.string

fun Context.simpleAlert(msg: String, positiveButton: (() -> Unit)? = null) {
    simpleAlert(null, msg, positiveButton)
}

fun Context.simpleAlert(
    title: String?,
    msg: String?,
    positiveButton: (() -> Unit)? = null,
    icon: Int? = R.mipmap.ic_launcher_round
) {
    simpleAlert(title, msg, R.string.ok.string(this), positiveButton, icon)
}

fun Context.simpleAlert(
    title: String?,
    msg: String?,
    btnTitle: String,
    positiveButton: (() -> Unit)? = null,
    icon: Int? = R.mipmap.ic_launcher_round
) {
    val mDialog = PrettyDialog(this)
//    mDialog.setTypeface(this.font(R.font.montserrat_regular))
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(icon)
    mDialog.setTitle(title)
    msg?.let { mDialog.setMessage(it) }
    mDialog.addButton(btnTitle, R.color.white, R.color.app_blue) {
        positiveButton?.invoke()
        mDialog.dismiss()
    }.show()
}

fun Context.appForceUpdate(
    title: String,
    msg: String,
    btnTitle: String,
    positiveButton: (() -> Unit)? = null
) {
    val mDialog = PrettyDialog(this)
//    mDialog.setTypeface(this.font(R.font.montserrat_regular))
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.mipmap.ic_launcher_round)
        .setTitle(title)
        .setMessage(msg)
        .addButton(btnTitle, R.color.white, R.color.app_blue) {
            positiveButton?.invoke()
        }.show()
}

fun Context.confirmationDialog(
    msg: String,
    btnPositiveClick: (() -> Unit)? = null,
    btnNegativeClick: (() -> Unit)? = null
) {
    val mDialog = PrettyDialog(this)
//    mDialog.setTypeface(this.font(R.font.montserrat_regular))
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.mipmap.ic_launcher_round)
        //.setTitle(getString(R.string.application_name))
        .setMessage(msg)
        .addButton(getString(R.string.cancel), R.color.white, R.color.app_blue) {
            btnNegativeClick?.invoke()
            mDialog.dismiss()
        }
        .addButton(getString(R.string.yes), R.color.white, R.color.color_red) {
            btnPositiveClick?.invoke()
            mDialog.dismiss()
        }.show()
}

fun Context.confirmationDialog(
    title: String,
    msg: String,
    btnPositiveClick: (() -> Unit)? = null,
    btnNegativeClick: (() -> Unit)? = null
) {
    val mDialog = PrettyDialog(this)
//    mDialog.setTypeface(this.font(R.font.montserrat_regular))
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.mipmap.ic_launcher_round)
        .setTitle(title)
        .setMessage(msg)
        .addButton(getString(R.string.no), R.color.white, R.color.app_blue) {
            btnNegativeClick?.invoke()
            mDialog.dismiss()
        }.addButton(getString(R.string.yes), R.color.white, R.color.color_red) {
            btnPositiveClick?.invoke()
            mDialog.dismiss()
        }.show()
}

fun Context.markerDialog(
    title: String = getString(R.string.app_name).toUpperCase(),
    msg: String,
    btnPositiveClick: (() -> Unit)? = null,
    btnNegativeClick: (() -> Unit)? = null
) {
    val mDialog = PrettyDialog(this)
//    mDialog.setTypeface(this.font(R.font.montserrat_regular))
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(R.mipmap.ic_launcher_round)
        .setTitle(title)
        .setMessage(msg)
        .addButton(getString(R.string.view_details), R.color.white, R.color.app_blue) {
            btnNegativeClick?.invoke()
            mDialog.dismiss()
        }.addButton(getString(R.string.get_direction), R.color.white, R.color.btn_get_direction_color) {
            btnPositiveClick?.invoke()
            mDialog.dismiss()
        }.show()
}

fun Context.confirmationDialog(
    title: String,
    msg: String,
    btnPositive: String,
    btnNegative: String,
    btnPositiveClick: (() -> Unit)? = null,
    btnNegativeClick: (() -> Unit)? = null,
    icon: Int? = R.mipmap.ic_launcher_round
) {
    val mDialog = PrettyDialog(this)
//    mDialog.setTypeface(this.font(R.font.montserrat_regular))
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(icon)
        .setTitle(title)
        .setMessage(msg)
        .addButton(btnPositive, R.color.white, R.color.app_blue) {
            btnPositiveClick?.invoke()
            mDialog.dismiss()
        }.addButton(btnNegative, R.color.white, R.color.app_blue) {
            btnNegativeClick?.invoke()
            mDialog.dismiss()
        }.show()
}

fun Context.confirmationDialog(
    title: String,
    msg: String,
    btnPositive: String,
    btnPositiveClick: (() -> Unit)? = null,
    icon: Int? = R.mipmap.ic_launcher_round
) {
    val mDialog = PrettyDialog(this)
//    mDialog.setTypeface(this.font(R.font.montserrat_regular))
    mDialog.setCanceledOnTouchOutside(false)
    mDialog.setCancelable(false)
    mDialog.setIcon(icon)
        .setTitle(title)
        .setMessage(msg)
        .addButton(btnPositive, R.color.white, R.color.app_blue) {
            btnPositiveClick?.invoke()
            mDialog.dismiss()
        }.show()
}

fun Context.takePick(onGallery: (() -> Unit)? = null, onCamera: (() -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle("Select Picture")
        .setItems(R.array.select_image_from) { dialogInterface, which ->
            when (which) {
                0 -> {
                    dialogInterface.dismiss()
                    /**Open gallery*/
                    onGallery?.invoke()
                }
                1 -> {
                    dialogInterface.dismiss()
                    /**Open camera*/
                    onCamera?.invoke()
                }
            }
        }
        .create().show()
}

//fun Context.accountTypes(onItemSelected: ((item: String) -> Unit)? = null) {
//    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
//    val data = resources.getStringArray(R.array.accountTypes)
//    mDialog.setTitle("Account Type")
//        .setItems(data) { dialogInterface, which ->
//            dialogInterface.dismiss()
//            onItemSelected?.invoke(data[which])
//        }
//        .create().show()
//}

fun Context.showListDialog(
    title: String?,
    list: ArrayList<String>,
    onItemSelected: ((item: String) -> Unit)? = null
) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data = list.toTypedArray()
    mDialog.setTitle(title)
        .setItems(data) { dialogInterface, which ->
            dialogInterface.dismiss()
            onItemSelected?.invoke(data[which])
        }
        .create().show()
}

fun Context.showListDialog(
    title: String?,
    data: Array<String>,
    onItemSelected: ((item: String) -> Unit)? = null
) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
        .setItems(data) { dialogInterface, which ->
            dialogInterface.dismiss()
            onItemSelected?.invoke(data[which])
        }
        .create().show()
}

fun Context.showListDialog(
    @StringRes title: Int,
    @ArrayRes list: Int,
    onItemSelected: ((item: String) -> Unit)? = null
) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data = resources.getTextArray(list)
    mDialog.setTitle(title)
        .setItems(list) { dialogInterface, which ->
            dialogInterface.dismiss()
            onItemSelected?.invoke(data[which] as String)
        }
        .create().show()
}

fun Context.showListDialog(
    @StringRes title: Int,
    data: Array<String>,
    onItemSelected: ((item: String, which: Int) -> Unit)? = null
) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    mDialog.setTitle(title)
        .setItems(data) { dialogInterface, which ->
            dialogInterface.dismiss()
            onItemSelected?.invoke(data[which], which)
        }
        .create().show()
}


fun <T> Context.showCustomListDialog(
    title: String?,
    list: ArrayList<T>,
    onItemSelected: ((item: T) -> Unit)? = null
) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data: Array<String?> = arrayOfNulls(list.size)
    list.forEachIndexed { index, t -> data[index] = t.toString() }
    mDialog.setTitle(title)
        .setItems(data) { dialogInterface, which ->
            dialogInterface.dismiss()
            onItemSelected?.invoke(list[which])
        }
        .create().show()
}

fun <T> Context.showCustomListDialog(
    @StringRes title: Int,
    list: ArrayList<T>,
    onItemSelected: ((item: T) -> Unit)? = null
) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this)
    val data: Array<String?> = arrayOfNulls(list.size)
    list.forEachIndexed { index, t -> data[index] = t.toString() }
    mDialog.setTitle(title)
        .setItems(data) { dialogInterface, which ->
            dialogInterface.dismiss()
            onItemSelected?.invoke(list[which])
        }
        .create().show()
}

fun Context.showCustomAlert(alertDialog: AlertDialog.Builder.() -> Unit) =
    AlertDialog.Builder(this).apply(alertDialog)


fun Context.selectFileFrom(onFileClick: (() -> Unit)? = null, onImage: (() -> Unit)? = null) {
    val mDialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
    mDialog.setTitle(getString(R.string.select_file))
        .setItems(R.array.filefrom) { dialogInterface, which ->
            when (which) {
                0 -> {
                    dialogInterface.dismiss()
                    /**Open gallery*/
                    onFileClick?.invoke()
                }
                1 -> {
                    dialogInterface.dismiss()
                    /**Open camera*/
                    onImage?.invoke()
                }
            }
        }
        .create().show()
}

fun Context.showAppDialog(message: String?, buttonClick: (() -> Unit)? = null) {
    this.simpleAlert(
        getString(R.string.app_name).toUpperCase(),
        message,
        positiveButton = {
            buttonClick?.invoke()
        })
}

var dialog: Dialog? = null
fun Context.loadingDialog(b: Boolean?) {
    if (dialog == null || b == true) {
        if (dialog != null)
            if (dialog?.isShowing == true)
                dialog?.dismiss()
        dialog = AppCompatDialog(this/*, R.style.progress_bar_style*/)
        dialog?.setContentView(R.layout.layout_progressbar)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }
    if (b == true) {
        dialog?.show()
    } else {
        if (dialog?.isShowing == true)
            dialog?.dismiss()
    }
}
//fun Context.showTermsDialog(
//    url: String?,
//    onBtnAcceptClick: (Dialog) -> Unit,
//    onDialogShow: () -> Unit,
//    onPageLoad: () -> Unit,
//    hideBtn: Boolean? = false
//) {
//    val dialog = Dialog(this)
//    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//    dialog.window?.setFlags(
//        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
//        WindowManager.LayoutParams.FLAG_FULLSCREEN
//    )
//    dialog.setCancelable(false)
//    dialog.setContentView(R.layout.terms_and_conditions_dialog)
//    loadWebView(dialog.web_view_terms, url, onPageLoad = {
//        onPageLoad.invoke()
//    })
//    dialog.img_close_terms_condition?.setOnClickListener { dialog.dismiss() }
//    if (hideBtn == true) {
//        dialog.rl_bottom?.visibility = View.GONE
//    } else {
//        dialog.btn_accept?.setOnClickListener {
//            onBtnAcceptClick.invoke(dialog)
//            dialog.dismiss()
//        }
//    }
//    dialog.btn_dont_accept?.setOnClickListener { dialog.dismiss() }
//    dialog.window?.setLayout(
//        ViewGroup.LayoutParams.MATCH_PARENT,
//        ViewGroup.LayoutParams.WRAP_CONTENT
//    )
//    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.show()
//    onDialogShow.invoke()
//}

fun loadWebView(
    webViewTerms: WebView?,
    url: String?,
    onPageLoad: () -> Unit
) {
    val settings = webViewTerms?.settings
    settings?.javaScriptEnabled = true
    settings?.loadWithOverviewMode = true
    settings?.useWideViewPort = true
    settings?.builtInZoomControls = false
    settings?.displayZoomControls = false
    webViewTerms?.webChromeClient = WebChromeClient()
    webViewTerms?.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

    webViewTerms?.webViewClient = object : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onPageLoad.invoke()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
//                webViewTerms?.loadUrl(request?.url.toString())
            return false

        }
    }
    url?.let { webViewTerms?.loadUrl(it) }
}