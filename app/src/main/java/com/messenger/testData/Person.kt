package com.messenger.testData
import java.io.Serializable

data class Person(
    val id: Int = 0,
    val name: String = "",
    val imgText:String = ""
): Serializable

val personList = listOf(
    Person(
        1,
        "Pranav",
        "PR"
    ),
    Person(
        2,
        "Ayesha",
        "PR"
    ),
    Person(
        3,
        "Roshini",
        "PR"
    ),
    Person(
        4,
        "Kaushik",
        "PR"
    ),
    Person(
        5,
        "Dad",
        "PR"
    ),
    Person(
        6,
        "Pranav",
        "PR"
    ),
)
