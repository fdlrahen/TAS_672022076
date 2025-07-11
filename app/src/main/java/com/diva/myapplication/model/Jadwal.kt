package com.diva.myapplication.model

import java.io.Serializable


data class Jadwal(
    val kodeMatkul: String = "",
    val namaMatkul: String = "",
    val namaDosen: String = "",
    val hari: String = "",
    val jam: String  = "",
    val ruangan: String = "",
    val mahasiswa: List<String> = listOf(),
    var key: String? = null
): Serializable
