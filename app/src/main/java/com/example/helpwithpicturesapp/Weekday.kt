package com.example.helpwithpicturesapp

data class Weekday (var name: String? = null,
                    var color: Int = 0) {

    val listOfActions  = mutableListOf<Actions>()   // Kommer man åt listan härifrån när man ska in med den i adaptern eller måste man peta in en lista i constructorn? Fråga David.

}