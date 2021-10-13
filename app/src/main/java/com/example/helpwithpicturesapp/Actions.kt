package com.example.helpwithpicturesapp

import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentId

data class Actions(@DocumentId var documentName: String? = null, // här kan man sätta dokument ID. ska vi ha det namnet?
                   var imageId: String? = null,
                   var checkBox: Boolean = false,
                   var imageText: String? = null,
) {

    val listOfActionSteps = mutableListOf<ActionSteps>()
}

// Den här klassen nedan behöver tas bort och ersättas med listOfActions i Weekday klassen
/*
class ActionsList() {

    var listOfActions: MutableList<Actions> = mutableListOf()

        init {

            val brushTeeth = Actions(
                                    "https://firebasestorage.googleapis.com/v0/b/helpwithpicturesapp-f9c12.appspot.com/o/borstatander.jpg?alt=media&token=5efb925b-76d9-48db-98ea-ba6dee1739eb",
                                    false
            )

            val clean = Actions(
                                    "https://firebasestorage.googleapis.com/v0/b/helpwithpicturesapp-f9c12.appspot.com/o/clean.jpg?alt=media&token=15355b0b-68ad-4f8f-abbb-9abfa6673388",
                                    false
            )

            val vacumClean = Actions(
                                    "https://firebasestorage.googleapis.com/v0/b/helpwithpicturesapp-f9c12.appspot.com/o/dammsug.jpg?alt=media&token=95e00a58-c822-4a2c-b678-2018d55fb49e",
                                    false
            )
            val dinner = Actions(
                                    "https://firebasestorage.googleapis.com/v0/b/helpwithpicturesapp-f9c12.appspot.com/o/dinner.jpg?alt=media&token=55f478b8-b416-4345-8629-0b877b676443",
                                    false
            )

            listOfActions.add(brushTeeth)
            listOfActions.add(clean)
            listOfActions.add(vacumClean)
            listOfActions.add(dinner)

        }
    }

 */

