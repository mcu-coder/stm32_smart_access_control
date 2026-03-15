package com.example.bluetoothintelligentaccesscontrolsystem.entity

data class User(
    var uid: Int? = null,
    var account: String? = null, // 账号
    var name: String? = null, // 姓名
    var password: String? = null, // 密码
    var role: Int? = null, // 角色 0为管理员 1为普通用户
    var fid: Int? = null, // 人脸ID
    var hid: Int? = null, // 指纹ID
    var pwd: Int? = null, // 普通密码
    var createDateTime: String? = null // 创建时间

)
