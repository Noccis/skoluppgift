package com.example.helpwithpicturesapp

import com.google.firebase.firestore.DocumentId

data class Users(@DocumentId var userId: String? = null, // här kan man sätta dokument ID. ska vi ha det namnet?
                   var email: String? = null,
                   var password: String? = null)
{
}