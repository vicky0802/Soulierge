package com.zk.soulierge

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.OrgUserModel
import com.zk.soulierge.support.api.model.OrganisationModalItem
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.BottomSheetDialogBuilder
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_update_org_user.*
import kotlinx.android.synthetic.main.dialog_filter.view.btnCancel
import kotlinx.android.synthetic.main.dialog_gender.view.*
import kotlinx.android.synthetic.main.toola_bar.*

class UpdateOrgUserActivity : AppCompatActivity() {
    var organisation: OrganisationModalItem? = null
    var user: OrgUserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_org_user)
        initToolbar(tool_bar, true, getString(R.string.lbl_organisation_user))

        if (intent.hasExtra("organisation")) {
            organisation = intent.getParcelableExtra<OrganisationModalItem>("organisation")
        }
        if (intent.hasExtra("user")) {
            user = intent.getParcelableExtra<OrgUserModel>("user")
            setData(user)
        }

        txtGender?.setOnClickListener { openBottomSheetDialog() }

        btnSaveOrgUser?.setOnClickListener {
            if (isValid()) {
                updateUserAPI()
            }
        }
    }

    private fun setData(user: OrgUserModel?) {
        edt_first_name?.setText(user?.name)
        txtPhone?.setText(user?.phoneNumber)
        txtEmail?.setText(user?.email)
        txtGender?.setText(user?.gender)
    }

    private fun openBottomSheetDialog() {
        val view = View.inflate(this, R.layout.dialog_gender, null)
        val builder = let { BottomSheetDialogBuilder(it) }
        builder?.customView(view)
        view?.btnMale?.setOnClickListener { txtGender.setText(view?.btnMale.text.toString());builder?.dismiss(); }
        view?.btnFeMale?.setOnClickListener { txtGender.setText(view?.btnFeMale?.text.toString());builder?.dismiss(); }
        view?.btnCancel?.setOnClickListener { builder?.dismiss(); }
        builder?.show()
    }

    private fun updateUserAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateUser(
                    gender = txtGender?.text.toString().trim(),
                    name = edt_first_name?.text.toString().trim(),
                    phone_number = txtPhone?.text.toString().trim(),
                    user_id = user?.id.toString()
                ),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    if (o.success?.isNotEmpty() == true) {
                        setResult(RESULT_OK)
                        onBackPressed()
                    } else {
                        showAppDialog(o.failure)
                    }
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


    private fun isValid(): Boolean {
        return when {
            edt_first_name.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_first_name)
                )
                false
            }
//            edt_last_name.text.toString().trim().isEmpty() -> {
//                showAppDialog(
//                    getString(R.string.warnning_enter_last_name)
//                )
//                false
//            }
            txtPhone.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_phone)
                )
                false
            }
            txtEmail.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_email)
                )
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(txtEmail.text.toString())
                .matches() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_valid_email)
                )
                false
            }
            else -> true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}