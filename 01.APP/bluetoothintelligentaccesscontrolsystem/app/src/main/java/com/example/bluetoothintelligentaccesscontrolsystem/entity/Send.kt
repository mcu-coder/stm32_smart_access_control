package com.example.bluetoothintelligentaccesscontrolsystem.entity

data class Send(
    var cmd: Int,
    var en_face: Int? = null,//
    var en_handid: Int? = null,//
    var door_time: Int? = null,
    var door: Int? = null,
    var did: Int? = null,//
    var time: Int? = null,//

    var pwd: Int? = null//
)
