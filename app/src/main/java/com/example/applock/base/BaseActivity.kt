package com.example.athu.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.applock.R


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    lateinit var loading: Dialog
    lateinit var loading2: AlertDialog

    private var isCancelable = false

    lateinit var binding: T

    protected abstract fun getLayoutBinding(): T


    @LayoutRes
    open fun getLayoutIdLoading(): Int = -1

    open fun getThemResId(): Int = -1

    protected abstract fun updateUI(savedInstanceState: Bundle?)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getLayoutBinding()
        setContentView(binding.root)
        initDialog()
        updateUI(savedInstanceState)
    }


//    override fun onBackPressed() {
//        val count = supportFragmentManager.backStackEntryCount
//        if (count == 1) {
//            finish()
//        } else {
//            super.onBackPressed()
//        }
//    }

    @Throws
    open fun openFragment(
        toFragment: Fragment?,
        resId: Int,
        fragmentClazz: Class<*>,
        args: Bundle?,
        addBackStack: Boolean
    ) {
        val tag = fragmentClazz.simpleName
        try {
            val fragment: Fragment
            try {
                fragment = (fragmentClazz.asSubclass(Fragment::class.java)).newInstance()
                    .apply { arguments = args }

                val transaction = supportFragmentManager.beginTransaction()
//                transaction.setCustomAnimations(
//                    R.anim.enter_from_right,
//                    R.anim.exit_to_left,
//                    R.anim.enter_from_left,
//                    R.anim.exit_to_right
//                )
                if (addBackStack) {
                    transaction.addToBackStack(tag)
                }
                if (toFragment != null)
                    transaction.hide(toFragment)
                transaction.add(resId, fragment, tag)
                transaction.commit()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws
    open fun openFragment(
        resId: Int, fragmentClazz: Class<*>, args: Bundle?, addBackStack: Boolean,
        vararg aniInt: Int
    ) {
        val tag = fragmentClazz.simpleName
        try {
            val isExisted = supportFragmentManager.popBackStackImmediate(tag, 0)
            if (!isExisted) {
                val fragment: Fragment
                try {
                    fragment = (fragmentClazz.asSubclass(Fragment::class.java)).newInstance()
                        .apply { arguments = args }

                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(aniInt[0], aniInt[1], aniInt[2], aniInt[3])

                    transaction.add(resId, fragment, tag)

                    if (addBackStack) {
                        transaction.addToBackStack(tag)
                    }
                    transaction.commitAllowingStateLoss()

                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Show toast
     * @param msg
     */

    /**
     * Init dialog loading
     */
    private fun initDialog() {
        val builder: Dialog = if (getThemResId() != -1)
            Dialog(this, getThemResId()) else Dialog(this)
        if (getLayoutIdLoading() == -1)
            builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.setCancelable(isCancelable)
        builder.setContentView(if (getLayoutIdLoading() == -1) R.layout.layout_loading_dialog_default else getLayoutIdLoading())
        builder.create()
        loading = builder
    }

    /**
     * Show dialog loading
     */
    open fun showDialog() {
        runOnUiThread {
            if (!loading.isShowing) {
                loading.show()
            }
        }
    }

    /**
     * Hide dialog loading 2
     */
    open fun hideDialog2() {
        runOnUiThread {
            if (loading2.isShowing) {
                loading2.dismiss()
            }
        }
    }

    /**
     * Hide dialog loading
     */
    open fun hideDialog() {
        runOnUiThread {
            if (loading.isShowing) {
                loading.dismiss()
            }
        }
    }

    /**
     * Set cancelable dialog
     */


    open fun clearAllBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    open fun showAlertDialog(message: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setNeutralButton(
            "Đóng"
        ) { dialog, which -> dialog.dismiss() }
            .show()
    }


}