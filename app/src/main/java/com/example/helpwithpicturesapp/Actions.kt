package com.example.helpwithpicturesapp

import com.google.firebase.firestore.DocumentId

data class Actions(@DocumentId var documentName: String? = null,
                   var imageId: String? = null,
                   var checkBox: Boolean = false,
                   var imageText: String? = null,
                   var order : Long? = null,
                   var steps: Boolean = false
)
