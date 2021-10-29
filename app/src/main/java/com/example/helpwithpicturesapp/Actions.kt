package com.example.helpwithpicturesapp

import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentId

data class Actions(@DocumentId var documentName: String? = null, // här kan man sätta dokument ID. ska vi ha det namnet?
                   var imageId: String? = null,
                   var checkBox: Boolean = false,
                   var imageText: String? = null,
                   var order : Long? = null,
                   var steps: Boolean = false
) {

    val listOfActionSteps = mutableListOf<Actions>()
}
