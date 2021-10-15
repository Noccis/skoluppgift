package com.example.helpwithpicturesapp

data class Weekday (var name: String? = null,
                    var color: Int = 0) {

    val listOfActions  = mutableListOf<Actions>()

}
