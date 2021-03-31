package com.ampnet.headlesscmsservice.enums

enum class Lang {
    EN,
    ES,
    EL,
    DE,
    IT,
    FR
}

fun Lang.name() = this.name.toLowerCase()
