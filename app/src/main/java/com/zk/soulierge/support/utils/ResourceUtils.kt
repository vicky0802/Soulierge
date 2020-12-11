package com.zk.soulierge.support.utils

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.zk.soulierge.support.base.CoreApp
import java.util.*

object ResourceUtils {
    @JvmStatic
    fun getString(@StringRes stringId: Int): String {
        return CoreApp.INSTANCE.getString(stringId)
    }

    fun getDrawable(@DrawableRes drawableId: Int): Drawable? {
        return ContextCompat.getDrawable(CoreApp.INSTANCE, drawableId)
    }

    @JvmStatic
    fun getColor(@ColorRes colorId: Int): Int {
        return ContextCompat.getColor(CoreApp.INSTANCE, colorId)
    }

    fun getDimen(@DimenRes dimenId: Int): Float {
        return CoreApp.INSTANCE.getResources().getDimension(dimenId)
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun getStringArrayList(@ArrayRes stringArrayID: Int): ArrayList<String> {
        val strings = ArrayList<String>()
        val stringArray: Array<String> =
            CoreApp.INSTANCE.getResources().getStringArray(stringArrayID)
        Collections.addAll(strings, *stringArray)
        return strings
    }

    fun getIntArrayList(@ArrayRes stringArrayID: Int): ArrayList<Int> {
        val integers = ArrayList<Int>()
        val intArray: IntArray = CoreApp.INSTANCE.getResources().getIntArray(stringArrayID)
        for (anIntArray in intArray) {
            integers.add(anIntArray)
        }
        return integers
    }

    fun getThemeName(context: Context, theme: Theme): String {
        return try {
            val mThemeResId: Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val fThemeImpl =
                    theme.javaClass.getDeclaredField("mThemeImpl")
                if (!fThemeImpl.isAccessible) fThemeImpl.isAccessible = true
                val mThemeImpl = fThemeImpl[theme]
                val fThemeResId =
                    mThemeImpl.javaClass.getDeclaredField("mThemeResId")
                if (!fThemeResId.isAccessible) fThemeResId.isAccessible = true
                mThemeResId = fThemeResId.getInt(mThemeImpl)
            } else {
                val fThemeResId =
                    theme.javaClass.getDeclaredField("mThemeResId")
                if (!fThemeResId.isAccessible) fThemeResId.isAccessible = true
                mThemeResId = fThemeResId.getInt(theme)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                theme.resources.getResourceEntryName(mThemeResId)
            } else context.resources.getResourceEntryName(mThemeResId)
        } catch (e: Exception) {
            // Theme returned by application#getTheme() is always Theme.DeviceDefault
            "AppTheme.ToolBar.White"
        }
    }

    fun getThemeName(@StyleRes theme: Int): String {
        return CoreApp.INSTANCE.getResources().getResourceEntryName(theme)
    }
}