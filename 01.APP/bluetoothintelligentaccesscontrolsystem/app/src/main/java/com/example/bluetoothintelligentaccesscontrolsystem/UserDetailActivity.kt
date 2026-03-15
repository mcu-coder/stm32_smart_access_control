package com.example.bluetoothintelligentaccesscontrolsystem

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.bluetoothintelligentaccesscontrolsystem.adapter.UserListViewAdapter
import com.example.bluetoothintelligentaccesscontrolsystem.databinding.ActivityUserDetailBinding
import com.example.bluetoothintelligentaccesscontrolsystem.databinding.UpdateUserDetailBinding
import com.example.bluetoothintelligentaccesscontrolsystem.db.UserDao
import com.example.bluetoothintelligentaccesscontrolsystem.entity.Receive
import com.example.bluetoothintelligentaccesscontrolsystem.entity.User
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common
import com.example.bluetoothintelligentaccesscontrolsystem.utils.MToast
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Objects

class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private var isReceive = false
    private lateinit var dao: UserDao
    private lateinit var sharedPreferences: SharedPreferences // 临时存储
    private lateinit var editor: SharedPreferences.Editor // 修改提交
    private var time = "1"
    private var list: MutableList<Any>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("local", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        dao = UserDao(this)
        initViews()
//        EventBus.getDefault().register(this)
    }

    private fun initViews() {
        Common.registryFlag = false
        setSupportActionBar(binding.toolbar)
        ImmersionBar.with(this).init()
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true) //添加默认的返回图标
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        time = sharedPreferences.getString("openTime", "1").toString()
        list = dao.query()
        if (list == null || list!!.size <= 0) {
            isShowListView(false)
        }
        binding.assetsList.adapter = UserListViewAdapter(this, list!!)
        eventManager()
    }

    private fun eventManager() {
        binding.searchButton.setOnClickListener {
            val text = binding.searchText.text.toString()
            isShowListView(true)
            if (text != "") {
                list = dao.query(text, "name")
                if (list == null || list!!.size <= 0) {
                    isShowListView(false)
                }
            } else {
                list = dao.query()
                if (list == null || list!!.size <= 0) {
                    isShowListView(false)
                }
            }
            binding.assetsList.adapter = UserListViewAdapter(this, list!!)
        }

        binding.assetsList.setOnItemClickListener { adapterView, view, p2, l ->
            val builder = AlertDialog.Builder(this@UserDetailActivity)
            builder.setTitle("选择").setMessage("请选择")
                .setNegativeButton("取消") { _, _ ->
                    builder.create().dismiss()
                }.setPositiveButton("修改信息") { _, _ ->
                    builder.create().dismiss()
                    updateItemDialog(p2)
                }
            builder.create().show()
        }

    }

    /***
     * @brief 显示修改物品弹窗
     * @param p2 item在list中的索引
     */
    private fun updateItemDialog(p2: Int) {
        val t = list?.get(p2) as User
        val builder = AlertDialog.Builder(this)
        val view = UpdateUserDetailBinding.inflate(LayoutInflater.from(this))
        builder.setTitle("修改用户数据").setView(view.root)
        val dialog = builder.create()
        view.inputNameEdit.setText(t.name)
        view.inputPasswordEdit.setText(t.password)
        view.confirmButton.setOnClickListener {
            if (verifyData(view)) {
                val name = view.inputNameEdit.text.toString()
                val password = view.inputPasswordEdit.text.toString()
                t.name = name
                t.password = password
                dao.update(t, t.uid.toString())
                MToast.mToast(this, "修改成功")
                dialog.dismiss()
            }
        }
        view.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    /***
     * 数据验证
     */
    private fun verifyData(view: UpdateUserDetailBinding): Boolean {
        val name = view.inputNameEdit.text.toString()
        val password = view.inputPasswordEdit.text.toString()

        if (name.isEmpty()) {
            MToast.mToast(this, "用户名不能为空")
            return false
        }
        if (password.isEmpty()) {
            MToast.mToast(this, "密码不能为空")
            return false
        }
        return true
    }

    /***
     * @brief 是否显示数据列表
     * @param f true 显示
     */
    private fun isShowListView(f: Boolean) {
        binding.assetsList.visibility = if (f) View.VISIBLE else View.GONE
        binding.nullDataView.visibility = if (f) View.GONE else View.VISIBLE
    }

//    /**
//     * 解析数据
//     * @param data
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun receiveDataFormat(data: Receive) {
//        try {
//            if (data.fid != null && data.fid != "0" || data.handid != null && data.handid != "0" || data.did != null) {
//                Common.registryFlag = true
//                Common.receiveData = data
//            } else {
//                Common.registryFlag = false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("数据解析", e.message.toString())
//            MToast.mToast(this, "数据解析失败")
//        }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        EventBus.getDefault().unregister(this)
//    }
}