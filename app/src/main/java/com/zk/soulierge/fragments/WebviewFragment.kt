package com.zk.soulierge.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zk.soulierge.R
import kotlinx.android.synthetic.main.fragment_webview.*


private const val ARG_TITLE = "param_title"
private const val ARG_URL = "param_url"


class WebviewFragment : BaseFragment() {

    private var title: String? = null
    private var url: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            url = it.getString(ARG_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_header_title.text = title
    }


    companion object {
        @JvmStatic
        fun newInstance(title: String, url: String) =
            WebviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_URL, url)
                }
            }
    }


    override fun getTagFragment(): String {
        return "webview_frag"
    }
}
