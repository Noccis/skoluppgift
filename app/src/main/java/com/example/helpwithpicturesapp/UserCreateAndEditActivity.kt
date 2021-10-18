package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.helpwithpicturesapp.databinding.ActivityUserCreateAndEditBinding
import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class UserCreateAndEditActivity: AppCompatActivity() {

    lateinit var binding: ActivityUserCreateAndEditBinding
    lateinit var imageUri: Uri
    val imageRef = Firebase.storage.reference
    lateinit var myAdapter: ActionsRecycleViewAdapter



    lateinit var userUploadImageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCreateAndEditBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }






    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {

            val images = imageRef.child("UploadedPictures/").listAll().await()
            val userImageUrl = mutableListOf<String>()
            for(image in images.items) {
                val url = image.downloadUrl.await()
                userImageUrl.add(url.toString())
            }
            withContext(Dispatchers.Main) {
                val actionsRecycleViewAdapter = ActionsRecycleViewAdapter()
                userUploadImageView.apply {
                myAdapter = actionsRecycleViewAdapter
                }
            }


        }catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }




}

