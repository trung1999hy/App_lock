package com.example.login.base

import android.content.res.Resources
import android.os.Bundle
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.athu.base.BaseActivity


abstract class BaseFragment<T : ViewBinding> : Fragment(),
    ViewTreeObserver.OnGlobalLayoutListener {

    private var rootView: View? = null

    lateinit var binding: T


    protected abstract fun getLayoutBinding(inflater: LayoutInflater): T


    protected abstract fun initView(savedInstanceState: Bundle?)

    protected abstract fun initAction(savedInstanceState: Bundle?)

    protected abstract fun listerData(savedInstanceState: Bundle?)

    override fun onGlobalLayout() {
        rootView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView != null) {
            val parent = rootView!!.parent as ViewGroup?
            parent?.removeView(rootView)
        } else {
            try {
                binding = getLayoutBinding(inflater)
                rootView = binding.root
                rootView!!.viewTreeObserver.addOnGlobalLayoutListener(this)
            } catch (e: InflateException) {
                e.printStackTrace()
            }
        }
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initAction(savedInstanceState)
        listerData(savedInstanceState)

    }


    fun <VH : RecyclerView.ViewHolder> setUpRcv(
        rcv: RecyclerView,
        adapter: RecyclerView.Adapter<VH>
    ) {
        rcv.setHasFixedSize(true)
        rcv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        rcv.adapter = adapter
    }

    fun <VH : RecyclerView.ViewHolder> setUpRcv(
        rcv: RecyclerView, adapter:
        RecyclerView.Adapter<VH>,
        isHasFixedSize: Boolean,
        isNestedScrollingEnabled: Boolean
    ) {
        rcv.setHasFixedSize(isHasFixedSize)
        rcv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        rcv.adapter = adapter
        rcv.isNestedScrollingEnabled = isNestedScrollingEnabled
    }

    fun <VH : RecyclerView.ViewHolder> setUpRcv(
        rcv: RecyclerView, adapter:
        RecyclerView.Adapter<VH>,
        isNestedScrollingEnabled: Boolean
    ) {
        rcv.setHasFixedSize(true)
        rcv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        rcv.adapter = adapter
        rcv.isNestedScrollingEnabled = isNestedScrollingEnabled
    }

    fun openFragment(
        resId: Int,
        fragmentClazz: Class<*>,
        args: Bundle?,
        addBackStack: Boolean
    ) {
        activity?.let {
            if (it is BaseActivity<*>) {
                it.openFragment(resId, fragmentClazz, args, addBackStack)
            }
        }
    }


    open fun removeFragment(frag: Class<*>) {
        parentFragmentManager.fragments.findLast { it::class.java.simpleName == frag.simpleName }
            ?.let { fragment ->
                parentFragmentManager.beginTransaction().remove(fragment).commit()
                parentFragmentManager.popBackStack()
            }
    }

    fun popBackTo(clazz: Class<*>) {
        parentFragmentManager.popBackStack(clazz.simpleName, 0)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }


    fun showDialog() {
        activity?.let {
            if (it is BaseActivity<*>) {
                it.showDialog()
            }
        }
    }

    fun hideDialog() {
        activity?.let {
            if (it is BaseActivity<*>) {
                it.hideDialog()
            }
        }
    }

    fun onBackPressed() {
        activity?.onBackPressed()
    }

    open fun clearAllBackStack() {
        activity?.let {
            if (it is BaseActivity<*>) {
                it.clearAllBackStack()
            }
        }
    }

    fun finish() {
        activity?.finish()
    }

    fun runOnUiThread(action: () -> Unit) {
        activity?.runOnUiThread { action() } ?: action()
    }


}