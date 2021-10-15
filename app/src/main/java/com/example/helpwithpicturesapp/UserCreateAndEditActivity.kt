package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.helpwithpicturesapp.databinding.ActivityUserCreateAndEditBinding
import java.net.URI
import android.R
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*


class UserCreateAndEditActivity: AppCompatActivity() {

    lateinit var binding: ActivityUserCreateAndEditBinding
    lateinit var imageUri: Uri

    lateinit var userUploadImageView: ImageView






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCreateAndEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // userUploadImageView.setImageURI(Uri.parse("file://mnt/sdcard/d2.jpg"))

        binding.selectImgeButton.setOnClickListener {
            selectImage()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage()
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
}

