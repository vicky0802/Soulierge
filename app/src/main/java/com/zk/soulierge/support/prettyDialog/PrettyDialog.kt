package com.zk.soulierge.support.prettyDialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialog
import com.zk.soulierge.R
import kotlinx.android.synthetic.main.dialog_pretty.*

class PrettyDialog(internal var context: Context) : AppCompatDialog(context) {

    var resources: Resources? = null
    var close_rotation_animation: RotateAnimation? = null
    var icon_animation = true
    var typeface: Typeface? = null
    var thisDialog: PrettyDialog

    init {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_pretty)
        setCancelable(true)
        resources = context.resources
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setFlags(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val displayMetrics = resources?.displayMetrics
        val pxWidth = displayMetrics?.widthPixels?.toFloat()
        if (isTablet(context)) {
            (pxWidth?.times(0.50))?.toInt()?.let { window?.setLayout(it, ViewGroup.LayoutParams.WRAP_CONTENT) }
        } else {
            (pxWidth?.times(0.75))?.toInt()?.let { window?.setLayout(it, ViewGroup.LayoutParams.WRAP_CONTENT) }
        }
        window?.attributes?.windowAnimations = R.style.pdlg_default_animation
        thisDialog = this
        setupViews_Base()
    }

    private fun isTablet(context: Context): Boolean {
        val xlarge = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == 4
        val large = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews_Base() {
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        resources?.getDimensionPixelSize(R.dimen.pretty_dialog_icon)?.div(2)?.let { lp.setMargins(0, it, 0, 0) }
        ll_content?.layoutParams = lp
        resources?.let {
            ll_content?.setPadding(0, (1.25 * it.getDimensionPixelSize(R.dimen.pretty_dialog_icon) / 2).toInt(), 0, it.getDimensionPixelSize(R.dimen._10sdp))
        }

        close_rotation_animation = RotateAnimation(0f, 180f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f)
        close_rotation_animation?.duration = 300
        close_rotation_animation?.repeatCount = Animation.ABSOLUTE
        close_rotation_animation?.interpolator = DecelerateInterpolator()
        close_rotation_animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                thisDialog.dismiss()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        iv_icon?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.alpha = 0.7f
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    v.alpha = 1.0f
                    if (icon_animation) {
                        v.startAnimation(close_rotation_animation)
                    }
                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false
            }
        }
        tv_title?.visibility = View.GONE
        tv_message?.visibility = View.GONE
    }

    fun setGravity(gravity: Int): PrettyDialog {
        window!!.setGravity(gravity)
        return this
    }

    fun addButton(text: String, textColor: Int?, backgroundColor: Int?, /*BUTTON_TYPE type,*/ callback: () -> Unit): PrettyDialog {
        resources?.let {
            val button = PrettyDialogButton(context, text, textColor, backgroundColor, typeface, callback)
            val margin = it.getDimensionPixelSize(R.dimen._10sdp)
            val marginTop = it.getDimensionPixelSize(R.dimen._8sdp)
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.setMargins(margin, marginTop, margin, 0)
            lp.weight = 1.0f
            button.layoutParams = lp
            ll_buttons?.addView(button)
        }
        return this
    }

    fun setTitle(text: String?): PrettyDialog {
        if (text?.trim { it <= ' ' }?.isNotEmpty() == true) {
            tv_title?.visibility = View.VISIBLE
            tv_title?.text = text
        } else {
            tv_title?.visibility = View.GONE
        }
        return this
    }

    fun setTitleColor(color: Int?): PrettyDialog {
        //tv_title.setTextColor(ContextCompat.getColor(context,color==null?R.color.pdlg_color_black : color));
        resources?.getColor(color ?: R.color.pdlg_color_black)?.let { tv_title?.setTextColor(it) }
        return this
    }

    fun setMessage(text: String): PrettyDialog {
        if (text.trim { it <= ' ' }.isNotEmpty()) {
            tv_message!!.visibility = View.VISIBLE
            tv_message!!.text = text
        } else {
            tv_message!!.visibility = View.GONE
        }
        return this
    }

    fun setMessageColor(color: Int?): PrettyDialog {
        //tv_message.setTextColor(ContextCompat.getColor(context,color==null?R.color.pdlg_color_black :color));
        resources?.getColor(color ?: R.color.pdlg_color_black)?.let { tv_message?.setTextColor(it) }
        return this
    }

    fun setIcon(icon: Int?): PrettyDialog {
        iv_icon?.setImageResource(icon ?: R.mipmap.ic_launcher)
        icon_animation = false
        iv_icon?.setOnTouchListener(null)
        return this
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setIconCallback(callback: () -> Unit): PrettyDialog {
        iv_icon?.setOnTouchListener(null)
        iv_icon?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.alpha = 0.7f
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    v.alpha = 1.0f
                    callback.invoke()
                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false
            }
        }
        return this
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setIcon(icon: Int?, callback: () -> Unit): PrettyDialog {
        icon_animation = false
        iv_icon?.setImageResource(icon ?: R.mipmap.ic_launcher)
        iv_icon?.setOnTouchListener(null)
        iv_icon?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.alpha = 0.7f
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    v.alpha = 1.0f
                    callback.invoke()
                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false
            }
        }
        return this
    }

    fun setTypeface(tf: Typeface?): PrettyDialog {
        typeface = tf
        tv_title?.typeface = tf
        tv_message?.typeface = tf

        for (i in 0 until ll_buttons?.childCount!!) {
            val button = ll_buttons?.getChildAt(i) as PrettyDialogButton
            tf?.let { button.setTypeface(it) }
            button.requestLayout()
        }

        return this
    }

    fun setAnimationEnabled(enabled: Boolean): PrettyDialog {
        if (enabled) {
            window?.attributes?.windowAnimations = R.style.pdlg_default_animation
        } else {
            window?.attributes?.windowAnimations = R.style.pdlg_no_animation
        }
        return this
    }

}