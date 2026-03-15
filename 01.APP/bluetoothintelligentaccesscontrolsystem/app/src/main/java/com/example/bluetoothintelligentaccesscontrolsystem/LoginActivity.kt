package com.example.bluetoothintelligentaccesscontrolsystem

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bluetoothintelligentaccesscontrolsystem.adapter.BlueToothListViewAdapter
import com.example.bluetoothintelligentaccesscontrolsystem.bluetooth.BlueTooth
import com.example.bluetoothintelligentaccesscontrolsystem.databinding.ActivityLoginBinding
import com.example.bluetoothintelligentaccesscontrolsystem.db.UserDao
import com.example.bluetoothintelligentaccesscontrolsystem.entity.Receive
import com.example.bluetoothintelligentaccesscontrolsystem.entity.User
import com.example.bluetoothintelligentaccesscontrolsystem.utils.BottomDialog
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.deviceAddress
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.hid
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.isCloseDialog
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.isFirstStart
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.pa
import com.example.bluetoothintelligentaccesscontrolsystem.utils.CustomDialog
import com.example.bluetoothintelligentaccesscontrolsystem.utils.MToast
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import pub.devrel.easypermissions.EasyPermissions

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val listMap = mutableListOf<HashMap<String?, String?>>()
    private var lodDialog: CustomDialog? = null
    private var isDebugView = false //是否显示debug界面
    val REQUEST_CODE = 0
    private var bluestr = ""
    private var bluestate = false
    private var bottomDialog: BottomDialog? = null
    private lateinit var sharedPreferences: SharedPreferences // 临时存储
    private lateinit var editor: SharedPreferences.Editor // 修改提交
    private lateinit var dao: UserDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dao = UserDao(this)
        sharedPreferences = getSharedPreferences("local", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        initViews()
        Message()
        getPermission()
    }

    private fun initViews() {
        Common.registryFlag = false
        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = "登录"
        ImmersionBar.with(this).init()
        pa = sharedPreferences.getString("openPassword", "1234").toString()
        hid = sharedPreferences.getInt("hidNum", 0)
        binding.loginBtn.setOnClickListener {
            if (Common.blueTooth == null) {
                MToast.mToast(this, "请先建立连接")
//                setBlueTooth()
            }else if (Common.blueTooth!!.adapter == null) {  //判断是否建立连接
                if (Common.blueTooth!!.adapter != null && Common.blueTooth!!.adapter.isEnabled) {
                    val connectedDevices: List<BluetoothDevice> =
                        Common.blueTooth!!.adapter.bondedDevices.filter { device ->
                            device.bondState == BluetoothDevice.BOND_BONDED
                        }
                    if (connectedDevices.isEmpty()) {
                        MToast.mToast(this, "请先建立连接")
                    }
                } else {
                    MToast.mToast(this, "蓝牙不可用或未打开")
                }
            } else {
                verifyData()
            }
        }

        /***
         * 跳转注册
         */
        binding.skipRegisterBtn.setOnClickListener { view ->
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun verifyData() {
        val account = binding.inputNameEdit.text.toString()
        val password = binding.inputPasswordEdit.text.toString()
        if (account.isEmpty()) {
            MToast.mToast(this, "用户名不能为空")
            return
        }
        if (password.isEmpty()) {
            MToast.mToast(this, "密码不能为空")
            return
        }
        if (account == "admin" && password == "123456") {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        } else {
            val objects: List<Any>? = dao.query(account, password)
            if (objects!!.isEmpty()) {
                MToast.mToast(this, "账号或密码错误")
                return
            }
            val user: User = objects[0] as User
            if (user.account == account && user.password == password) {
                Common.user = user
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                MToast.mToast(this, "账号或密码错误")
            }
        }
    }

    /**
     * 动态获取权限
     */
    private fun getPermission() {
        val perms = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(
                this, "这是必要的权限", 100, *perms
            )
        } else {
            if (Common.blueTooth != null) {
                if (Common.btConnetThread == null) {
                    MToast.mToast(this, "测试2")
                } else {
                    MToast.mToast(this, "测试1")
                }
            }
            bottomDialog = BottomDialog(this, LoginActivity.mHandler)
            lodDialog = CustomDialog(this, "扫描中")
            if (isFirstStart) {
                isFirstStart = false
                setBlueTooth()
            }
        }
    }

    /**
     * 蓝牙配置
     */
    private fun setBlueTooth() {
        if (Common.blueTooth == null) {
            Common.blueTooth = BlueTooth(this)
        }
        if (!Common.blueTooth!!.isEnabled) {
            MToast.mToast(this, "请先打开蓝牙")
            //用于启动蓝牙功能的 Intent
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                MToast.mToast(this, "无权限")
                return
            }
            startActivityForResult(enableBtIntent, REQUEST_CODE)
        } else {
            if (Common.btConnetThread == null) {
//                isCloseDialog = true
                Common.blueTooth?.btConnetOrServer(LoginActivity.mHandler, null)
            } else {
//                MToast.mToast(this, "测试")
                isCloseDialog = false
//                Common.blueTooth?.btConnetOrServer(LoginActivity.mHandler, deviceAddress)
            }

        }
    }

    /**
     * 信息处理
     */
    private fun Message() {
        LoginActivity.mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    Common.SCAN_START -> Common.blueTooth?.ScanBlueTooth(broadcastReceiver)
                    Common.MSG_CONNET -> {
                        Log.d("蓝牙连接", "连接成功")
                        if (isCloseDialog) {
                            bottomDialog?.dismiss()
                        }
                        MToast.mToast(this@LoginActivity, "连接成功")
                    }

                    Common.MSG_ERROR -> {
                        Log.d("连接错误", msg.obj.toString())
                        MToast.mToast(this@LoginActivity, "连接错误")
                        Common.ISBTCONNECT = false
                    }

                    Common.MSG_GET_DATA -> {
//                        Log.d("收到数据", msg.obj.toString())
                        bluestr += msg.obj.toString()
                        bluestate = bluestr.contains("}")
                        if (bluestate) {
                            try {
                                Log.e("收到完整数据", bluestr)
                                val data = Gson().fromJson(bluestr, Receive::class.java)
                                dataAnalysis(data)
                                // 通过EventBus传递数据
//                                EventBus.getDefault()
//                                    .post(data)
                            } catch (e: Exception) {
                                MToast.mToast(this@LoginActivity, "错误")
                            }
                            bluestr = ""
                            bluestate = false
                        }
                    }

                    Common.SEND_OK -> {
                        Log.d("发送数据", msg.obj.toString())
                    }

                    Common.SEND_ERROR -> {
                        Log.d("发送错误", msg.obj.toString())
                        MToast.mToast(this@LoginActivity, "发送失败")
                    }
                }
            }
        }
    }

    /**
     * @brief 解析接受到的数据
     */
    private fun dataAnalysis(data: Receive?) {
        //这里只处理开锁信息
        if (data != null) {
            if (data.fid != null && data.fid != "0" || data.handid != null && data.handid != "0" || data.did != null) { // 注册和删除指令
                Common.registryFlag = true
                Common.receiveData = data
            } else {
                Common.registryFlag = false
                val list = dao.query()!!
                if (data.face_id != null && data.face_id != "0") {
                    for (d in list) {
                        val da = d as User
                        if (da.fid.toString() == data.face_id) {
                            Common.sendMessage(this, 3, "1")
                            break
                        }
                    }
                }

                if (data.hand_id != null && data.hand_id != "0") {
                    for (d in list) {
                        val da = d as User
                        if (da.hid.toString() == data.hand_id) {
                            Common.sendMessage(this, 3, "1")
                            break
                        }
                    }
                }

                if (data.pwd != null && data.pwd != "0") {
                    if (data.pwd == pa) {
                        Common.sendMessage(this, 3, "1")
                    }
                }
            }
        }
    }

    /***
     * 广播接收器
     */
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent
                        .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (ActivityCompat.checkSelfPermission(
                            this@LoginActivity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        MToast.mToast(this@LoginActivity, "无权限")
                        return
                    }
                    //排除未知设备
                    if (device!!.name != null && device.name.isNotEmpty()) {
                        val str = "名称:" + device.name + "地址:" + device.address
                        val map = HashMap<String?, String?>()
                        map["btName"] = device.name
                        map["btAddress"] = device.address
                        listMap.add(map)
                        bottomDialog?.scanListView!!.adapter = BlueToothListViewAdapter(
                            listMap, this@LoginActivity
                        )
                        bottomDialog?.scanListView!!.setOnItemClickListener { _, _, i, _ ->
                            if (listMap.size > 0) {
                                Common.blueTooth?.btConnetOrServer(
                                    LoginActivity.mHandler,
                                    listMap[i]["btAddress"]
                                )
                            }
                        }
                        Log.d("蓝牙设备", str)
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    lodDialog?.show()
                    Log.d("蓝牙", "设备扫描中")
                    MToast.mToast(this@LoginActivity, "蓝牙设备扫描中")
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    lodDialog?.dismiss()
                    Log.d("蓝牙", "扫描完成")
                    MToast.mToast(this@LoginActivity, "扫描完成....")
                    unregisterReceiver(this) //注销广播
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_settings -> {
                bottomDialog!!.show(supportFragmentManager, "dialog")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        lateinit var mHandler: Handler
    }
}