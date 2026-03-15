package com.example.bluetoothintelligentaccesscontrolsystem.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.example.bluetoothintelligentaccesscontrolsystem.databinding.ListUserItemBinding
import com.example.bluetoothintelligentaccesscontrolsystem.db.UserDao
import com.example.bluetoothintelligentaccesscontrolsystem.entity.User
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.hid
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.receiveData
import com.example.bluetoothintelligentaccesscontrolsystem.utils.Common.registryFlag
import com.example.bluetoothintelligentaccesscontrolsystem.utils.CustomDialog
import com.example.bluetoothintelligentaccesscontrolsystem.utils.MToast

class UserListViewAdapter(private val context: Context, private val list: MutableList<Any>) :
    BaseAdapter() {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "local",
        AppCompatActivity.MODE_PRIVATE
    ) // 临时存储
    private var editor: SharedPreferences.Editor = sharedPreferences.edit() // 修改提交
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1
        val holder: ViewHolder
        if (view == null) {
            val binding = ListUserItemBinding.inflate(LayoutInflater.from(context), p2, false)
            view = binding.root
            holder = ViewHolder(binding)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        initViews(holder.binding, p0)

        return view
    }

    private fun initViews(binding: ListUserItemBinding, index: Int) {
        val user = list[index] as User
        binding.nameText.text = user.name
        binding.accountText.text = user.account
        binding.passwordText.text = user.password
        binding.createTimeText.text = user.createDateTime
        // binding.pwdText.text = user.pwd.toString()
        binding.faceText.text = if (user.fid != -1) user.fid.toString() else "无"
        binding.handText.text = if (user.hid != -1) user.hid.toString() else "无"
        binding.deleteButton.setOnClickListener {
            if (binding.faceText.text.toString() == "无" && binding.handText.text.toString() == "无") {
                runOnUiThread {
                    UserDao(context).delete(user.uid.toString())
                    list.removeAt(index)
                    notifyDataSetChanged()
                    MToast.mToast(context, "删除成功")
                }
            } else {
                MToast.mToast(context, "请先删除 人脸信息与指纹信息")
            }
        }
        binding.updateFaceButton.setOnClickListener {
            if (binding.faceText.text.toString() == "无") { // 执行录入
                insertFaceThread(user, index)
            } else { // 执行删除
                deleteFaceThread(user, index)
            }
        }

        binding.updateHandButton.setOnClickListener {
            if (binding.handText.text.toString() == "无") {// 执行录入
                insertHandThread(user, index)
            } else { // 执行删除
                deleteHandThread(user, index)
            }
        }
    }

    private class ViewHolder(val binding: ListUserItemBinding)


    /***
     * @brief 录入人脸
     */
    private fun insertFaceThread(user: User, index: Int) {
        if (user.fid == -1) {
            Common.sendMessage(context, 1, "1")
            val dialog = CustomDialog(context, "录入人脸")
            Thread {
                try {
                    runOnUiThread {
                        dialog.show()
                    }
                    var time = 0
                    while (true) {
                        if (time > 100) {
                            runOnUiThread {
                                MToast.mToast(context, "超时,录入失败")
                                dialog.dismiss()
                            }
                            break
                        } else {
                            if (registryFlag) {
                                runOnUiThread {
                                    registryFlag = false
                                    if (receiveData?.fid != null) {
                                        user.fid = receiveData!!.fid?.toInt()
                                        UserDao(context).update(user, user.uid.toString())
                                        (list[index] as User).fid = user.fid
                                        notifyDataSetChanged()
                                    } else {
                                        MToast.mToast(context, "录入失败")
                                    }
                                    dialog.dismiss()
                                    receiveData = null
                                }
                                break
                            }
                        }
                        time++
                        Thread.sleep(100)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    /***
     * @brief 录入指纹
     */
    private fun insertHandThread(user: User, index: Int) {
        if (user.hid == -1) {
            Common.sendMessage(context, 2, hid.toString())
            val dialog = CustomDialog(context, "录入指纹")
            Thread {
                try {
                    runOnUiThread {
                        dialog.show()
                    }
                    var time = 0
                    while (true) {
                        if (time > 100) {
                            runOnUiThread {
                                MToast.mToast(context, "超时,录入失败")
                                dialog.dismiss()
                            }
                            break
                        } else {
                            if (registryFlag) {
                                runOnUiThread {
                                    registryFlag = false
                                    if (receiveData?.handid != null) {
                                        user.hid = receiveData!!.handid?.toInt()
                                        UserDao(context).update(user, user.uid.toString())
                                        (list[index] as User).hid = user.hid
                                        notifyDataSetChanged()
                                        hid += 1
                                        editor.putInt("hidNum", hid)
                                        editor.commit()
                                    } else {
                                        MToast.mToast(context, "录入失败")
                                    }
                                    dialog.dismiss()
                                    receiveData = null
                                }

                                break
                            }
                        }
                        time++
                        Thread.sleep(100)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    /***
     * @brief 删除人脸
     */
    private fun deleteFaceThread(user: User, index: Int) {
        if (user.fid != -1) {
            Common.sendMessage(context, 4, user.fid.toString())
            val dialog = CustomDialog(context, "删除人脸")
            Thread {
                try {
                    runOnUiThread {
                        dialog.show()
                    }
                    var time = 0
                    while (true) {
                        if (time > 100) {
                            runOnUiThread {
                                MToast.mToast(context, "超时,删除失败")
                                dialog.dismiss()
                            }
                            break
                        } else {
                            if (registryFlag) {
                                runOnUiThread {
                                    registryFlag = false
                                    user.fid = -1
                                    UserDao(context).update(user, user.uid.toString())
                                    (list[index] as User).fid = -1
                                    notifyDataSetChanged()
                                    dialog.dismiss()
                                    receiveData = null
                                }
                                break
                            }
                        }
                        time++
                        Thread.sleep(100)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    /***
     * @brief 删除指纹
     */
    private fun deleteHandThread(user: User, index: Int) {
        if (user.hid != -1) {
            Common.sendMessage(context, 5, user.hid.toString())
            val dialog = CustomDialog(context, "删除指纹")
            Thread {
                try {
                    runOnUiThread {
                        dialog.show()
                    }
                    var time = 0
                    while (true) {
                        if (time > 100) {
                            runOnUiThread {
                                MToast.mToast(context, "超时,删除失败")
                                dialog.dismiss()
                            }
                            break
                        } else {
                            if (registryFlag) {
                                runOnUiThread {
                                    registryFlag = false
                                    user.hid = -1
                                    receiveData = null
                                    UserDao(context).update(user, user.uid.toString())
                                    (list[index] as User).hid = -1
                                    notifyDataSetChanged()
                                    dialog.dismiss()
                                    if (hid > 0) hid -= 1
                                    editor.putInt("hidNum", hid)
                                    editor.commit()
                                }
                                break
                            }
                        }
                        time++
                        Thread.sleep(100)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}