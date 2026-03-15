package com.example.bluetoothintelligentaccesscontrolsystem

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.bluetoothintelligentaccesscontrolsystem.databinding.ActivityRegisterBinding
import com.example.bluetoothintelligentaccesscontrolsystem.db.UserDao
import com.example.bluetoothintelligentaccesscontrolsystem.entity.User
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.pa
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.registryFlag
import com.example.bluetoothintelligentaccesscontrolsystem.utils.MToast
import com.gyf.immersionbar.ImmersionBar
import java.util.Objects

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dao: UserDao
    private lateinit var sharedPreferences: SharedPreferences // 临时存储
    private lateinit var editor: SharedPreferences.Editor // 修改提交
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("local", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        dao = UserDao(this)
        initViews()
    }

    private fun initViews() {
        registryFlag = false
        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = "注册用户"
        ImmersionBar.with(this).init()
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true) //添加默认的返回图标
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        pa = sharedPreferences.getString("openPassword", "1234").toString()
        binding.registerBtn.setOnClickListener { verifyData() }
    }


    /***
     * 数据验证
     */
    private fun verifyData() {

        val account = binding.inputAccountEdit.text.toString()
        val name = binding.inputNameEdit.text.toString()
        val password = binding.inputPasswordEdit.text.toString()
        if (account.isEmpty()) {
            MToast.mToast(this, "账号不能为空")
            return
        }
        if (!isValidInput(account)) {
            MToast.mToast(this, "账号格式错误")
            return
        }

        if (name.isEmpty()) {
            MToast.mToast(this, "用户名不能为空")
            return
        }
        if (password.isEmpty()) {
            MToast.mToast(this, "密码不能为空")
            return
        }

        val objects: List<Any>? = dao.query(account, "account")
        if (objects!!.isNotEmpty()) {
            MToast.mToast(this, "已有该用户，请直接登录")
            return
        }
        val user = User(
            account = account,
            name = name,
            password = password,
            pwd = pa.toInt()
        )

        dao.insert(user)
        MToast.mToast(this, "添加成功")

    }

    /**
     * @brief 匹配正则 只能输入数字
     */
    private fun isValidInput(input: String): Boolean {
        val regex = "^[1-9][0-9]*$".toRegex()   // 创建正则表达式对象
        return regex.matches(input)  // 检查输入是否匹配正则表达式
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


}