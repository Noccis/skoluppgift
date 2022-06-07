package com.example.helpwithpicturesapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpwithpicturesapp.*
import com.example.helpwithpicturesapp.adapters.ImageAdapter2
import com.example.helpwithpicturesapp.model.ActionSteps
import com.example.helpwithpicturesapp.model.Actions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

class CreateAndEditActionSteps : AppCompatActivity() {

    lateinit var uploadButton: Button
    lateinit var startCameraButton: Button
    lateinit var storeButton: Button
    lateinit var saveButton: Button
    lateinit var imgeViewButton: ImageButton
    lateinit var backImage: ImageView
    lateinit var editText: EditText
    lateinit var recyclerView: RecyclerView
    lateinit var imageAdapter2: ImageAdapter2
    var choosenImageUrl: String? = null
    var gridLayoutManager: GridLayoutManager? = null
    var curFile: Uri? = null
    val TAG = "!!!"
    var uid = ""
    var actionId = ""
    var decision = ""
    val db = FirebaseFirestore.getInstance()
    val userImageUrl = mutableListOf<String>()
    val uniqeString = UUID.randomUUID().toString()
    val imageRef = Firebase.storage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_and_edit_action_steps)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        recyclerView = findViewById(R.id.recyclerView)
        gridLayoutManager = GridLayoutManager(applicationContext, 3,LinearLayoutManager.VERTICAL, false)
        uploadButton = findViewById(R.id.uploadButton)
        storeButton = findViewById(R.id.storeButton)
        startCameraButton = findViewById(R.id.startCameraButton)
        saveButton = findViewById(R.id.saveButton)
        editText = findViewById(R.id.userEditText)
        imgeViewButton = findViewById(R.id.imageViewButton)
        backImage = findViewById(R.id.backImage)

        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()
        actionId = intent.getStringExtra(INSTRUCTIONS_POSITION_KEY).toString()

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if(currentUser != null) {
            uid = currentUser.uid
        }

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        imageAdapter2 = ImageAdapter2(this, userImageUrl)
        recyclerView.adapter = imageAdapter2

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

        startCameraButton.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                Toast.makeText(this, "Kameran går ej att öppna", Toast.LENGTH_SHORT).show()
            }else {
                startCamera()
            }
        }

        saveButton.setOnClickListener {
            storeAction()
            it.hideKeyboard()
        }
    }

    override fun onResume() {
        recyclerView.adapter?.notifyDataSetChanged()
        super.onResume()
    }

    fun startCamera() {
        val takePicturesIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicturesIntent, START_REQUEST_CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name tillåtelse nekad", Toast.LENGTH_SHORT).show()

            }else {
                Toast.makeText(applicationContext, "$name tillåtelse godkänd", Toast.LENGTH_SHORT).show()
            }
        }
        when(requestCode) {
            CAMERA_REQUEST_CODE -> innerCheck("kamera")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                curFile = it
                imgeViewButton.setImageURI(it)
            }
        }
        else if(requestCode == START_REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            val takenImage = data.extras?.get("data") as Bitmap
            imgeViewButton.setImageBitmap(takenImage)
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            curFile?.let {
                val uniqeString = UUID.randomUUID().toString()
                val uploadTask = imageRef.child("$uid/$uniqeString").putFile(it) // skapar en unik folder för inloggad användare

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.child(uniqeString).downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Toast.makeText(this@CreateAndEditActionSteps, "Bilden är sparad", Toast.LENGTH_SHORT).show()

                        val actionsteps = ActionSteps(null, downloadUri.toString(), false, editText.text.toString())
                        db.collection("users").document(uid).collection("weekday").
                        document(decision).collection("action").document(uid).
                        collection("steps").add(actionsteps)
                        storeAction()
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@CreateAndEditActionSteps, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun storeAction() {
        //val storageImage = Actions(null, choosenImageUrl, false, editText.text.toString())
        val actionstepsImage = Actions(null, choosenImageUrl, false, editText.text.toString())
        db.collection("users").document(uid).collection("weekday")
            .document(decision).collection("action").document(actionId)
            .collection("steps")
            .add(actionstepsImage)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "storeAction: $actionstepsImage")
                    Toast.makeText(
                        this@CreateAndEditActionSteps,
                        "Bilden och instruktionen är tillagda i listan", Toast.LENGTH_SHORT
                    ).show()
                    editText.setText("")
                    val stepRef = db.collection("users").document(uid).collection("weekday")
                        .document(decision).collection("action").document(actionId)

                    stepRef.update("steps", true)

                } else {
                    Toast.makeText(this@CreateAndEditActionSteps,
                        "Välj en bild och skriv instruktionen", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val userImages = imageRef.child(uid).listAll().await()
            val publicImages = imageRef.child("UploadedPictures").listAll().await()

            for (image in userImages.items) {
                val url = image.downloadUrl.await()
                userImageUrl.add(url.toString())
            }
            for(publicImage in publicImages.items ) {
                val publicUrl = publicImage.downloadUrl.await()
                userImageUrl.add(publicUrl.toString())
            }
            withContext(Dispatchers.Main) {
                recyclerView.adapter?.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@CreateAndEditActionSteps, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setImage(url: String) {
        choosenImageUrl = url
        Glide.with(this).load(url).into(imgeViewButton)
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}




