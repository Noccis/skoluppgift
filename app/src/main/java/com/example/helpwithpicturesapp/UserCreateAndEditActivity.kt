package com.example.helpwithpicturesapp

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.helpwithpicturesapp.databinding.ActivityUserCreateAndEditBinding
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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


class UserCreateAndEditActivity: AppCompatActivity() {

    val TAG = "!!!"

    var curFile: Uri? = null
    val imageRef = Firebase.storage.reference
    val uniqeString = UUID.randomUUID().toString()
    val db = FirebaseFirestore.getInstance()





    lateinit var recyclerView: RecyclerView
    lateinit var uploadButton: Button
    lateinit var downloadButton: Button
    lateinit var deleteButton: Button
    lateinit var imgeViewButton: ImageButton
    lateinit var saveButton: Button





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_create_and_edit)

        recyclerView = findViewById(R.id.recyclerView)
        uploadButton = findViewById(R.id.uploadButton)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)
        saveButton = findViewById(R.id.saveButton)
        imgeViewButton = findViewById(R.id.imageViewButton)



        imgeViewButton.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }
        uploadButton.setOnClickListener {
            uploadImageToStorage("uniqeString")
        }

        downloadButton.setOnClickListener {
           downLoadImage("uniqeString")
        }
        deleteButton.setOnClickListener {
            deleteImage("uniqeString")
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
            val bytes = imageRef.child("UploadedPictures/$uniqeString").getBytes(maxDownloadSize).await()
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

   /*
    private fun uuploadImageToStorage(filename: String) {
        val ref = imageRef.child("UploadedPictures/$uniqeString")
        if (curFile != null) {
            val uploadTask = ref.putFile(curFile)
        }
    }
    */

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
                        Toast.makeText(this@UserCreateAndEditActivity,"Bilden är sparad",Toast.LENGTH_SHORT).show()

                        val action = Actions(null, downloadUri.toString(),false,"test")

                        db.collection("Weekday").document("monday").collection("monday").add(action)


                        Log.d(TAG, "uploadImageToStorage: ${downloadUri}")

                    } else {
                        // Handle failures
                        // ...
                    }
                }

                



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



