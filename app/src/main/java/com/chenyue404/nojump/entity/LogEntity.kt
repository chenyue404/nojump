package com.chenyue404.nojump.entity

data class LogEntity(
    val time: Long,
    val callPackage: String,
    val dataString: String,
    val blocked: Boolean = false
)
