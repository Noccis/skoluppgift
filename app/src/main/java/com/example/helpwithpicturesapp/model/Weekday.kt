package com.example.helpwithpicturesapp.model

import com.example.helpwithpicturesapp.model.Actions

data class Weekday (var name: String? = null,
                    var color: Int = 0) {

    val listOfActions  = mutableListOf<Actions>()

}
