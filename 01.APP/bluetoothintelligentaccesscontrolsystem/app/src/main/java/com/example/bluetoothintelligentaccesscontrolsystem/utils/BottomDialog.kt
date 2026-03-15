package com.example.bluetoothintelligentaccesscontrolsystem.utils


import com.example.bluetoothintelligentaccesscontrolsystem.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.FrameLayout
import android.widget.ListView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.bluetoothintelligentaccesscontrolsystem.LoginActivity
import com.example.bluetoothintelligentaccesscontrolsystem.MainActivity
import com.example.bluetoothintelligentaccesscontrolsystem.adapter.BlueToothListViewAdapter
import com.example.bluetoothintelligentaccesscontrolsystem.bluetooth.BlueTooth
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.SCAN_START
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomDialog(private val mContext: Context, private val handler: Handler) :
    BottomSheetDialogFragment() {

    private var view: View? = null
    private val TAG = "底部弹窗"
    lateinit var matchedListView: ListView
    lateinit var scanListView: ListView

    init {
        Common.blueTooth = BlueTooth(mContext)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog: 创建底部弹出框")
        // 返回 BottomSheetDialog 的实例
        return BottomSheetDialog(requireContext())
    }

    override fun onStart() {
        Log.d(TAG, "onStart: ")
        super.onStart()
        // 获取 dialog 对象
        val dialog = dialog as? BottomSheetDialog
        dialog?.let {
            // 去掉默认的背景颜色，不然圆角显示不见
            it.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 获取 dialog 的根布局
            val bottomSheet: FrameLayout? =
                it.delegate.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            // 获取根布局的 LayoutParams 对象
            val layoutParams = bottomSheet?.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.height = getPeekHeight()
            // 修改弹窗的最大高度，不允许上滑（默认可以上滑）
            bottomSheet.layoutParams = layoutParams
            val behavior = BottomSheetBehavior.from(bottomSheet)
            // peekHeight 即弹窗的最大高度
            behavior.peekHeight = getPeekHeight()
            // 初始为展开状态
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    /**
     * 弹窗高度，默认为屏幕高度的四分之三
     * 子类可重写该方法返回 peekHeight
     *
     * @return height
     */
    protected fun getPeekHeight(): Int {
        val peekHeight = resources.displayMetrics.heightPixels
        // 设置弹窗高度为屏幕高度的 3/4
        return peekHeight - peekHeight / 4
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.e(TAG, "onCreateView ")
        view = inflater.inflate(R.layout.bluetooth_view, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View?) {
        Common.isCloseDialog = true
        matchedListView = view?.findViewById(R.id.matchedListView)!!
        scanListView = view.findViewById(R.id.scanListView)!!
        val map = Common.blueTooth?.ScanPairBlueTooth()
        matchedListView.adapter = BlueToothListViewAdapter(map, mContext)
        matchedListView.setOnItemClickListener { _, _, i, _ ->
            map?.let {
                Common.blueTooth?.btConnetOrServer(LoginActivity.mHandler, it[i]["btAddress"])
                Common.deviceAddress = it[i]["btAddress"].toString()
            }

        }

        view.findViewById<View>(R.id.scanButton).setOnClickListener {
            scanListView.visibility = View.VISIBLE
            handler.sendMessage(handler.obtainMessage(SCAN_START, ""))
        }
    }

    /**
     * 关闭窗口
     */
    override fun dismiss() {
        super.dismiss()
    }
}
