package com.zk.soulierge

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
    var event: UpEventResponseItem? = null
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

    fun setupMap(){
        (map_view as SupportMapFragment)?.getMapAsync {
            val latLng = event?.longitude?.toDouble()?.let { it1 ->
                event?.latitude?.toDouble()?.let { it2 ->
                    LatLng(
                        it2,
                        it1
                    )
                }
            }
            if (it != null && latLng != null) {
                it?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng, 18F
                    )
                )

                it?.addMarker(
                    latLng?.let { it1 ->
                        MarkerOptions()
                            .position(it1).title(event?.name)
                            .icon(BitmapDescriptorFactory.defaultMarker())
                            .visible(true)
                    }
                )
            }
        }
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
                    event = o
                    setupMap()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            val intent = Intent(this, AddEventActivity::class.java)
            if (event != null)
                intent.putExtra("event", event)
            startActivityForResult(intent, 1004)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1004) {
            if (resultCode == RESULT_OK) {
                if (intent.hasExtra("eventId")) {
                    callEventDetailAPI(intent.getStringExtra("eventId"))
                }
            }
        }
    }
}