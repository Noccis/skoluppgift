package com.example.helpwithpicturesapp

import com.google.firebase.firestore.DocumentId

 data class ActionSteps (@DocumentId var documentName: String? = null,
                   var imageId: String? = null,
                   var checkBox: Boolean = false,
                   var imageText: String? = null) {

     
}