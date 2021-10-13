package com.example.helpwithpicturesapp

import com.google.firebase.firestore.DocumentId

class ActionSteps (@DocumentId var documentName: String? = null,
                   var imageId: String? = null,
                   var checkBox: Boolean = false,
                   var imageText: String? = null) {
}