package com.example.helpwithpicturesapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
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
    var choosenImageUrl: String? = null
    var actionId = ""


    lateinit var recyclerView: RecyclerView
    private var gridLayoutManager : GridLayoutManager? = null
    lateinit var uploadButton: Button
    lateinit var deleteButton: Button
    lateinit var storeButton: Button
    lateinit var saveButton: Button
    lateinit var editText: EditText
    lateinit var imgeViewButton: ImageButton
    lateinit var imageAdapter: ImageAdapter
    lateinit var backImage : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_create_and_edit)

        recyclerView = findViewById(R.id.recyclerView)
        gridLayoutManager = GridLayoutManager(applicationContext, 3,LinearLayoutManager.VERTICAL, false)
        uploadButton = findViewById(R.id.uploadButton)
        storeButton = findViewById(R.id.storeButton)
        deleteButton = findViewById(R.id.deleteButton)
        saveButton = findViewById(R.id.saveButton)
        editText = findViewById(R.id.userEditText)
        imgeViewButton = findViewById(R.id.imageViewButton)
        backImage = findViewById(R.id.backImage)

        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()
        actionId = intent.getStringExtra(INSTRUCTIONS_POSITION_KEY).toString()

        recyclerView.layoutManager= gridLayoutManager
        recyclerView.setHasFixedSize(true)

        imageAdapter = ImageAdapter(this, userImageUrl)

        recyclerView.adapter = imageAdapter

        backImage.setOnClickListener {
            finish()
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

        saveButton.setOnClickListener {
            storeAction()
        }
    }
        override fun onResume() {
            recyclerView.adapter?.notifyDataSetChanged()
            super.onResume()
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

                        val action = Actions(null, downloadUri.toString(), false, editText.text.toString())
                        val actionsteps = ActionSteps(null,downloadUri.toString(),false,editText.text.toString() )
                        db.collection("Weekday").document(decision).collection(decision).add(action)
                        db.collection("Weekday").document(decision).collection(decision).document(actionId)
                            .collection(actionId).add(actionsteps)




                        Log.d(TAG, "uploadImageToStorage: ${downloadUri}")

                    }
                }


            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun storeAction() {
        val storageImage = Actions(null, choosenImageUrl, false, editText.text.toString())
        if (choosenImageUrl != null && editText.text.toString() != "" ) {
            db.collection("Weekday").document(decision).collection(decision).add(storageImage)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "storeAction: $storageImage")
                        Toast.makeText(
                            this@UserCreateAndEditActivity,
                            "Bilden och instruktionen är tillagda i listan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else Toast.makeText(
            this@UserCreateAndEditActivity,
            "Välj en bild och skriv instruktionen",
            Toast.LENGTH_SHORT
        ).show()
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


            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserCreateAndEditActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun setImage(url: String) {
        choosenImageUrl = url // adressen kommer in
        Glide.with(this).load(url).into(imgeViewButton)
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


}



