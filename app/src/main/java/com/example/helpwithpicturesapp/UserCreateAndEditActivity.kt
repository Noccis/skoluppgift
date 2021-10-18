package com.example.helpwithpicturesapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.helpwithpicturesapp.databinding.ActivityUserCreateAndEditBinding
import android.net.Uri
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

const val REQUEST_CODE_IMAGE_PICK = 0


class UserCreateAndEditActivity: AppCompatActivity() {

    lateinit var binding: ActivityUserCreateAndEditBinding

    var curFile: Uri? = null
    val imageRef = Firebase.storage.reference

    lateinit var recyclerView: RecyclerView
    lateinit var imageViewUpload: ImageView
    lateinit var uploadButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCreateAndEditBinding.inflate(layoutInflater)
        setContentView(binding.root)






        listFiles()
    }
    /*

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                curFile = it
                imageViewUpload.setImageURI(it)
            }
        }

    }

    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            curFile?.let {
                imageRef.child("UploadedPictures/$filename").putFile(it).await()
                Toast.makeText(this@UserCreateAndEditActivity,"Bilden är sparad",Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

     */




    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {

            val images = imageRef.child("UploadedPictures/").listAll().await()
            val userImageUrl = mutableListOf<String>()
            for (image in images.items) {
                val url = image.downloadUrl.await()
                userImageUrl.add(url.toString())
            }
            withContext(Dispatchers.Main) {
                val imageAdapter = ImageAdapter(userImageUrl)
                recyclerView.apply {
                    adapter = imageAdapter
                    layoutManager = LinearLayoutManager(this@UserCreateAndEditActivity)
                }

            }


        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }



}



