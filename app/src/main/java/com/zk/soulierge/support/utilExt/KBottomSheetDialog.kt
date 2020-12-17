package com.zk.soulierge.support.utilExt

import android.content.Context
import android.view.View
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.bottomSheetDialog(init: BottomSheetDialogBuilder.() -> Unit, @StyleRes theme: Int? = null): BottomSheetDialogBuilder? = activity?.bottomSheetDialog(init, theme)

fun Context.bottomSheetDialog(init: BottomSheetDialogBuilder.() -> Unit, @StyleRes theme: Int? = null): BottomSheetDialogBuilder = BottomSheetDialogBuilder(this, theme).apply(init)

class BottomSheetDialogBuilder(context: Context, @StyleRes theme: Int? = null) {

    var dialog: BottomSheetDialog? = null

    init {
        dialog = if (theme != null) {
            BottomSheetDialog(context, theme)
        } else {
            BottomSheetDialog(context)
        }
    }

    fun dismiss() = dialog?.dismiss()

    fun show(): BottomSheetDialogBuilder {
        dialog?.show()
        return this
    }

    fun customView(view: View) {
        dialog?.setContentView(view)
    }

    fun cancelable(value: Boolean = true) {
        dialog?.setCancelable(value)
    }

    fun cancelableTouchOutside(value: Boolean = true) {
        dialog?.setCanceledOnTouchOutside(value)
    }

}