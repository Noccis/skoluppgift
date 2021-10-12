package com.example.helpwithpicturesapp

data class Weekday (var name: String? = null,
                    var imageId: Int = 0,
                    var imageText: String? = null,
                    var done: Boolean = false) {

    val listOfActions  = mutableListOf<Actions>()   // Kommer man åt listan härifrån när man ska in med den i adaptern eller måste man peta in en lista i constructorn? Fråga David.
}