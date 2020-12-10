package com.zk.soulierge.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zk.soulierge.OrganiseListActivity

import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.ApiClient.BASE_IMAGE_URL
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.row_event.view.*
import okhttp3.ResponseBody
import org.json.JSONObject

class HomeFragment : BaseFragment() {
    override fun getTagFragment(): String {
        return "home_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callHomeAPI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cv_event.tv_find.text = getString(R.string.ph_find_event)
        cv_organization.tv_find.text = getString(R.string.ph_find_organisation)
        cv_organization?.setOnClickListener { startActivity(Intent(context,OrganiseListActivity::class.java)) }
    }

    private fun callHomeAPI() {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java).getHomePage(),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    context?.loadingDialog(false)
                    try {
                        val temp = o.string()
                        var json = JSONObject(temp.replace(", }","}"))
                        if (json.has("success")){
                            if(json.has("event_banner")){
                                Glide.with(requireActivity()).load(BASE_IMAGE_URL+json.getString("event_banner"))
                                    .placeholder(R.drawable.event_smaple)
                                    .into(cv_event.row_event_image)
                            }
                            if(json.has("org_banner")){
                                Glide.with(requireActivity()).load(BASE_IMAGE_URL+json.getString("org_banner"))
                                    .placeholder(R.drawable.event_smaple)
                                    .into(cv_organization.row_event_image)
                            }
                            if(json.has("banner")){
                                Glide.with(requireActivity()).load(BASE_IMAGE_URL+json.getString("banner"))
                                    .placeholder(R.drawable.organisation_sample)
                                    .into(app_bar_image)
                            }
                        }else if(json.has("failure")){
                            context?.showAppDialog(json.getString("failure"))
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    context?.loadingDialog(false)
                    context?.simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    context?.loadingDialog(false)
                    message?.let {
                        context?.simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

}
