package com.example.helpwithpicturesapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.helpwithpicturesapp.databinding.ActivityUserCreateAndEditBinding
import android.net.Uri
import android.widget.Button
import android.widget.ImageButton
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

    var curFile: Uri? = null
    val imageRef = Firebase.storage.reference

    lateinit var recyclerView: RecyclerView
    lateinit var uploadButton: Button
    lateinit var downloadButton: Button
    lateinit var deleteButton: Button
    lateinit var imgeViewButton: ImageButton






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_create_and_edit)


        recyclerView = findViewById(R.id.recyclerView)
        uploadButton = findViewById(R.id.uploadButton)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)
        imgeViewButton = findViewById(R.id.imageViewButton)


        imgeViewButton.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }
        uploadButton.setOnClickListener {
            uploadImageToStorage("myImage")
        }

        downloadButton.setOnClickListener {
           downLoadImage("myImage")
        }
        deleteButton.setOnClickListener {
            deleteImage("myImage")
        }





        listFiles()
    }

    private fun deleteImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            imageRef.child("UploadedPictures/$filename").delete().await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, "Bilden är raderad", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message,Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun downLoadImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            val maxDownloadSize = 5L * 1024 * 1024
            val bytes = imageRef.child("UploadedPictures/$filename").getBytes(maxDownloadSize).await()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            withContext(Dispatchers.Main) {
                imgeViewButton.setImageBitmap(bmp)
            }


        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message,Toast.LENGTH_SHORT).show()
            }

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                curFile = it
                imgeViewButton.setImageURI(it)
            }
        }

    }

    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            curFile?.let {
                imageRef.child("UploadedPictures/$filename").putFile(it).await()
                Toast.makeText(this@UserCreateAndEditActivity,"Bilden är sparad",Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity,e.message,Toast.LENGTH_SHORT).show()
            }
        }
    }








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



