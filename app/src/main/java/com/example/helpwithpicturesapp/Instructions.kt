package com.example.helpwithpicturesapp

class Instructions(
    var image: Int,
    var userInstructions: String,

    )

class InstructionList() {
    var listOfInstructions: MutableList<Instructions> = mutableListOf()


    init {
        val i1 = Instructions(
            R.drawable.borstatander,
            "Borsta, borsta, borsta, båda uppe och nere"

        )

        val i2 = Instructions(
            R.drawable.clean,
            "Plocka, plocka, plocka och lägg tillbaka saker på var sin plats"
        )
        val i3 = Instructions(
            R.drawable.dammsug,
            "Damsug ordentligt under soffan"
        )
        val i4 = Instructions(
            R.drawable.dinner,
            "Äta middag"
        )

        listOfInstructions.add(i1)
        listOfInstructions.add(i2)
        listOfInstructions.add(i3)
        listOfInstructions.add(i4)


    }


}