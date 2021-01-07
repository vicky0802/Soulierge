package com.zk.soulierge

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.google.gson.JsonObject
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.CategoryItem
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.toJson
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utils.confirmationDialog
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.row_category.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.MediaType
import okhttp3.RequestBody

class CategoriesActivity : AppCompatActivity() {
    var categoryBuilder: RecyclerViewBuilder<CategoryItem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        initToolbar(tool_bar, true, getString(R.string.categories))

        callCategoriesListAPI()
        setupRecycleView(ArrayList<CategoryItem?>())
        fab?.setOnClickListener { showDialog() }

    }

    private fun setupRecycleView(o: ArrayList<CategoryItem?>) {
        categoryBuilder = rvCategory?.setUp(
            R.layout.row_category,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                view?.txtCategoryTitle.text = item.name
                view?.imgCategoryDelete.setOnClickListener {
                    confirmationDialog(getString(R.string.app_name).toUpperCase(),
                        getString(R.string.del_message), { callCategoryDelAPI(item, position) })
                }
                view?.setOnClickListener { showDialog(item.id, item.name) }
            }
            isNestedScrollingEnabled = false
        }
    }



    private fun callCategoriesListAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getCategories(),
            singleCallback = object : SingleCallback<ArrayList<CategoryItem?>> {
                override fun onSingleSuccess(o: ArrayList<CategoryItem?>, message: String?) {
                    loadingDialog(false)
                    if (o.size > 0) {
                        llNoData?.visibility = View.GONE
                    } else {
                        llNoData?.visibility = View.VISIBLE
                    }
                    setupRecycleView(o)
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    llNoData?.visibility = View.VISIBLE
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    llNoData?.visibility = View.VISIBLE
                    message?.let {
                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

    private fun callCategoryDelAPI(item: CategoryItem, position: Int) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .delCategory(item.id),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                        { callCategoriesListAPI() }
                    )
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    message?.let {
                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

    fun showDialog(categoryId: String? = null, category: String? = null) {
        val dialog = AppCompatDialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setFlags(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog?.btnCanccel?.setOnClickListener { dialog.dismiss() }
        if (categoryId != null) {
            dialog?.edtCategory?.setText(category)
        }
        dialog?.btnSave?.setOnClickListener {
            if (dialog?.edtCategory?.text?.toString()?.trim()?.isEmpty() == true) {
                showAppDialog(getString(R.string.category_message))
            } else {
                if (categoryId != null){
                    callUpdateCategoryAPI(categoryId,dialog?.edtCategory?.text?.toString()?.trim())
                }else{
                callAddCategoryAPI(dialog?.edtCategory?.text?.toString()?.trim())}
                dialog?.dismiss()
            }
        }
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun callAddCategoryAPI(category: String?) {
        val json = JsonObject()
        json.addProperty("name", category)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .addCategory(body),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    callCategoriesListAPI()
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    message?.let {
                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )

    }

    private fun callUpdateCategoryAPI(categoryId: String?, category: String?) {
        val json = JsonObject()
        json.addProperty("id", categoryId)
        json.addProperty("name", category)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateCategroy(body),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    callCategoriesListAPI()
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    message?.let {
                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}