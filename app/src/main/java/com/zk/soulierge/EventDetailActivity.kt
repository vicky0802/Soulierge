package com.zk.soulierge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.UpEventResponseItem
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utilExt.toDisplayDateFormat
import com.zk.soulierge.support.utils.confirmationDialog
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.toola_bar.*

class EventDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        initToolbar(tool_bar, true, getString(R.string.event_details))
        if (intent.hasExtra("eventId")) {
            callEventDetailAPI(intent.getStringExtra("eventId"))
        }
        btnDeleteEvnet?.setOnClickListener {
            confirmationDialog(getString(R.string.app_name).toUpperCase(),
                getString(R.string.del_message), {
                    if (intent.hasExtra("eventId")) {
                        deleteEvent(intent.getStringExtra("eventId"))
                    }
                })
        }
    }

    private fun deleteEvent(eventId: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .delEvent(eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ) {
                        if (o.success?.isNotEmpty() == true) {
                            setResult(RESULT_OK);onBackPressed()
                        }
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

    private fun callEventDetailAPI(id: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .eventDetail(eventId = id),
            singleCallback = object : SingleCallback<UpEventResponseItem> {
                override fun onSingleSuccess(o: UpEventResponseItem, message: String?) {
                    loadingDialog(false)
                    setEventData(o)
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

    private fun setEventData(o: UpEventResponseItem) {
        txtEventName?.text = o.name
        txtEventDetail?.text = o.description
        txtAge?.text = getString(R.string.event_age_restriction) + ": " + o.ageRestriction
        txtEventLocation?.text = o.location
        txtEventStartDate?.text = o.date.toDisplayDateFormat("dd/MM/yyyy") + " | " + o.time
        txtEventEndDate?.text = o.endDate.toDisplayDateFormat("dd/MM/yyyy") + " | " + o.endTime
        txtTotalCapacity?.text = getString(R.string.total_capacity) + ": " + o.capacity
        txtAvailable?.text = getString(R.string.available) + ": " + o.availableCapacity

    }
}