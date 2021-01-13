package com.zk.soulierge

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.OrganisationModalItem
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_add_org_user.*
import kotlinx.android.synthetic.main.toola_bar.*

class AddOrgUserActivity : AppCompatActivity() {
    var organisation: OrganisationModalItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_org_user)
        initToolbar(tool_bar, true, getString(R.string.lbl_organisation_user))

        if (intent.hasExtra("organisation")) {
            organisation = intent.getParcelableExtra<OrganisationModalItem>("organisation")
        }

        btnSaveOrgUser?.setOnClickListener {
            if (isValid()) {
                callRegisterUserAPI()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callRegisterUserAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getApiClient().create(WebserviceBuilder::class.java)
                .onOrgUserCreate(
                    email = txtEmail?.text.toString().trim(),
                    password = txtPassword?.text.toString().trim(),
                    name = edt_first_name?.text.toString()
                        .trim() + " " + edt_last_name?.text.toString()
                        .trim(),
                    phone_number = txtPhone?.text.toString().trim(),
                    organization_id = organisation?.id
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
            edt_last_name.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_last_name)
                )
                false
            }
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
            txtPassword.text.toString().trim().isEmpty() -> {
                showAppDialog(
                    getString(R.string.warnning_enter_password)
                )
                false
            }
            else -> true
        }
    }
}