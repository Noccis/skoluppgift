package com.example.helpwithpicturesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView


class UserCreateAndEditActivity(storage: Any) : AppCompatActivity() {

    lateinit var userUploadImageView: ImageView
    var storageRef = storage.javaClass



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_create_and_edit)
        userUploadImageView = findViewById(R.id.user_ImageView)


        val uploadButton = findViewById<Button>(R.id.upload_Button)
        uploadButton.setOnClickListener {

        }

    }
}



