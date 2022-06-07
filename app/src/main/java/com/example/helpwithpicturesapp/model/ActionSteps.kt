package com.example.helpwithpicturesapp.model

import com.google.firebase.firestore.DocumentId

data class ActionSteps (@DocumentId var documentName: String? = null,
                        var imageId: String? = null,
                        var checkBox: Boolean = false,
                        var imageText: String? = null,
                        var order : Long? = null) {


}