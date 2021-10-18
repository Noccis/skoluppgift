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



    lateinit var userUploadImageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCreateAndEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImageButton.setOnClickListener {
            selectImage()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage()
        }

        binding.getImageButton.setOnClickListener {
            getImage()
        }

    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            binding.userImageView.setImageURI(imageUri)

        }
    }


    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Upploading File....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formater = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val filename = formater.format(now)
        val storageRef = FirebaseStorage.getInstance().getReference("UploadedPictures/$filename")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {

                binding.userImageView.setImageURI(null)
                Toast.makeText(this@UserCreateAndEditActivity,"Successfully uploaded",Toast.LENGTH_SHORT).show()
                if(progressDialog.isShowing) {
                    progressDialog.dismiss()

                }

            }
            .addOnCanceledListener {

                if(progressDialog.isShowing) {
                    progressDialog.dismiss()
                    Toast.makeText(this@UserCreateAndEditActivity,"Failed",Toast.LENGTH_SHORT).show()

                }

            }


    }
   private fun getImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Fetching image......")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val imageName = binding.userNameOfImageEt.text.toString()

        val storageRef = FirebaseStorage.getInstance().getReference("UploadedPictures/$imageName.jpg")
        val localfile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localfile).addOnSuccessListener {

            if(progressDialog.isShowing)
                progressDialog.dismiss()

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.userImageView.setImageBitmap(bitmap)

        }
            .addOnFailureListener{
                if(progressDialog.isShowing)
                    progressDialog.dismiss()
                Toast.makeText(this,"Failed to retrive the image", Toast.LENGTH_SHORT).show()
            }
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
                val ActionsRecycleViewAdapter = Actions()
            }


        }catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

}

