package com.example.helpwithpicturesapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
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



class UserCreateAndEditActivity : AppCompatActivity() {

    val TAG = "!!!"

    var curFile: Uri? = null
    val imageRef = Firebase.storage.reference
    val uniqeString = UUID.randomUUID().toString()
    val db = FirebaseFirestore.getInstance()
    var decision = ""
    val userImageUrl = mutableListOf<String>()



    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewImageButton: ImageButton
    lateinit var uploadButton: Button
    lateinit var deleteButton: Button
    lateinit var storeButton: Button
    lateinit var imgeViewButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_create_and_edit)

        recyclerView = findViewById(R.id.recyclerView)
        uploadButton = findViewById(R.id.uploadButton)
        storeButton = findViewById(R.id.storeButton)
        deleteButton = findViewById(R.id.deleteButton)
        imgeViewButton = findViewById(R.id.imageViewButton)

        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()

        val imageAdapter = ImageAdapter(this, userImageUrl)
        recyclerView.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(this@UserCreateAndEditActivity)
        }

        imgeViewButton.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }

        uploadButton.setOnClickListener {
            uploadImageToStorage("uniqeString")
        }

        storeButton.setOnClickListener {
            listFiles()
        }


        deleteButton.setOnClickListener {
            deleteImage("uniqeString")
        }

    }

    private fun deleteImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            imageRef.child("UploadedPictures/$filename").delete().await()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@UserCreateAndEditActivity, "Bilden är raderad", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }
        /*
    private fun downLoadImage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            val maxDownloadSize = 5L * 1024 * 1024
            val bytes =
                imageRef.child("UploadedPictures/$uniqeString").getBytes(maxDownloadSize).await()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            withContext(Dispatchers.Main) {
                imgeViewButton.setImageBitmap(bmp)
            }


        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message, Toast.LENGTH_SHORT).show()
            }

        }

    }

         */


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                curFile = it
                imgeViewButton.setImageURI(it)
            }
        }

    }


    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {

            curFile?.let {
                val uploadTask = imageRef.child("UploadedPictures/$uniqeString").putFile(it)

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.child("UploadedPictures/$uniqeString").downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Toast.makeText(this@UserCreateAndEditActivity, "Bilden är sparad", Toast.LENGTH_SHORT).show()

                        val action = Actions(null, downloadUri.toString(), false, "test")
                        db.collection("Weekday").document(decision).collection(decision).add(action)



                        Log.d(TAG, "uploadImageToStorage: ${downloadUri}")

                    } else {
                        // Handle failures
                        // ...
                    }
                }


            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {

            val images = imageRef.child("UploadedPictures/").listAll().await()

            for (image in images.items) {
                val url = image.downloadUrl.await()
                userImageUrl.add(url.toString())
            }
            withContext(Dispatchers.Main) {
               recyclerView.adapter?.notifyDataSetChanged()
                Log.d(TAG, "listFiles: ")

            }


        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun setImage(url: String){
        Glide.with(this).load(url).into(imgeViewButton)
    }


}



