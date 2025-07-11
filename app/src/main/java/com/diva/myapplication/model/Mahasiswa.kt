package com.diva.myapplication.model

import java.io.Serializable

data class Mahasiswa(
    val nim: String = "",
    val username: String = "",
    var nilai: String = ""
) : Serializable