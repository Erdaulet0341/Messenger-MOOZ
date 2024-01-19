package com.messenger.data

@kotlinx.serialization.Serializable
data class User(
    val name: String= "",
    val surname: String= "",
    val uid: String= "",
    val phonemunber: String= "",
    ): java.io.Serializable