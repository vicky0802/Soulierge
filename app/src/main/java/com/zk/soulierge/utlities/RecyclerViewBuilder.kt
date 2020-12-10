package com.example.parth.worldz_code.utils.RecyckerViewBuilder

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLayoutManager.*
import com.zk.soulierge.utlities.RecyclerViewLinearLayout


fun <T : Any> androidx.recyclerview.widget.RecyclerView.setUp(
    @LayoutRes layoutResID: Int,
    itemList: ArrayList<T?>,
    @RecyclerViewLayoutManager.LayoutManager layoutManager: Int,
    @RecyclerViewLinearLayout.Orientation orientation: Int,
    builder: RecyclerViewBuilder<T>.() -> Unit
) = RecyclerViewBuilder<T>(
    this,
    layoutResID,
    itemList,
    layoutManager,
    orientation
).apply(builder)

class RecyclerViewBuilder<T : Any>
    (
    val recyclerView: androidx.recyclerview.widget.RecyclerView,
    val layoutResID: Int,
    val mItems: ArrayList<T?>,
    @RecyclerViewLayoutManager.LayoutManager val layoutManager: Int,
    @RecyclerViewLinearLayout.Orientation val orientation: Int
) : RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 1012

    var itemView: Int = 0

    var spanCount: (() -> Int)? = { 1 }
        set(value) {
            (recyclerView.layoutManager as androidx.recyclerview.widget.GridLayoutManager).spanCount =
                value!!.invoke()
            field = value
        }

    var staggeredSpanCount: (() -> Int)? = { 1 }
        set(value) {
            (recyclerView.layoutManager as androidx.recyclerview.widget.StaggeredGridLayoutManager).spanCount =
                value!!.invoke()
            field = value
        }

    var gapStrategy: (() -> Int)? =
        { androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_NONE }
        set(value) {
            (recyclerView.layoutManager as androidx.recyclerview.widget.StaggeredGridLayoutManager).gapStrategy =
                value!!.invoke()
            field = value
        }

    var isNestedScrollingEnabled: Boolean = false
        set(value) {
            recyclerView.isNestedScrollingEnabled = value
            field = value
        }

    var contentBindingListener: ((T, View, Int) -> Unit)? = null

    init {
        setHasStableIds(true)
        if (layoutManager == LINEAR)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                recyclerView.context,
                orientation,
                false
            )
        else if (layoutManager == GRID)
            recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
                recyclerView.context,
                spanCount!!.invoke()
            )
        else if (layoutManager == STAGGERED) {
            recyclerView.layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(
                staggeredSpanCount!!.invoke(),
                orientation
            )
            gapStrategy!!.invoke()
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = this
    }

    fun contentBinder(l: ((T, View, Int) -> Unit)?) {
        contentBindingListener = l
    }

    fun hasFixedSize(isFix: Boolean) {
        recyclerView.setHasFixedSize(isFix)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(layoutResID, parent, false)
        return CustomViewHolder(v)
    }

    override fun getItemCount(): Int = mItems.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onBindViewHolder(
        holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder is RecyclerViewBuilder<*>.CustomViewHolder) {
            mItems?.get(position)?.let { contentBindingListener?.invoke(it, holder.itemView, position) }
        }
    }

    fun removeItem(item: T) {
        val itemPosition = mItems.indexOf(item)
        mItems.remove(item)
        notifyItemRemoved(itemPosition)
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_ITEM
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setSwipeToRefresh(
        activity: Activity,
        swipeToRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout,
        onRefreshListener: () -> Unit
    ) {
        swipeToRefresh.setOnRefreshListener {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            onRefreshListener.invoke()
        }
    }

    fun stopSwipeRefresh(
        activity: Activity,
        swipeToRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    ) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        swipeToRefresh.isRefreshing = false
    }

    fun removeAll() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun addItem(item: T) {
        mItems.add(item)
        notifyItemInserted(mItems.size - 1)
    }

    fun addAll(list: ArrayList<T>) {
        val position = itemCount
        mItems.addAll(position, list)
        notifyDataSetChanged()
    }
}