package com.example.helpwithpicturesapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws
import kotlin.math.log

const val REQUEST_CODE_IMAGE_PICK = 0
const val CAMERA_REQUEST_CODE = 1
const val START_REQUEST_CAMERA = 2

class UserCreateAndEditActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    private var gridLayoutManager: GridLayoutManager? = null
    lateinit var uploadButton: Button
    lateinit var startCameraButton: Button
    lateinit var storeButton: Button
    lateinit var saveButton: Button
    lateinit var imageView_Button: ImageButton
    lateinit var imageAdapter: ImageAdapter
    lateinit var backImage: ImageView
    lateinit var editText: EditText
    lateinit var frameLayout: FrameLayout


    val TAG = "!!!"
    val imageRef = Firebase.storage.reference
    val db = FirebaseFirestore.getInstance()
    var decision = ""
    val userImageUrl = mutableListOf<String>()
    var choosenImageUrl: String? = null
    var choosenImageBitmap: Bitmap? = null
    var curFile: Uri? = null
    var actionId = ""
    var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_create_and_edit)


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        frameLayout = findViewById(R.id.framelayout)
        recyclerView = findViewById(R.id.recyclerView)
        uploadButton = findViewById(R.id.uploadButton)
        storeButton = findViewById(R.id.storeButton)
        startCameraButton = findViewById(R.id.startCameraButton)
        saveButton = findViewById(R.id.saveButton)
        editText = findViewById(R.id.userEditText)
        imageView_Button = findViewById(R.id.imageView_Button)
        backImage = findViewById(R.id.backImage)


        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()
        actionId = intent.getStringExtra(INSTRUCTIONS_POSITION_KEY).toString()



        gridLayoutManager =
            GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        imageAdapter = ImageAdapter(this, userImageUrl)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = imageAdapter


        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            uid = currentUser.uid
            Log.d(TAG, "onCreate: $uid")
        }

        backImage.setOnClickListener {
            finish()
        }

        imageView_Button.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }

        uploadButton.setOnClickListener {
            uploadImage()


        }
        //Lagra bilderknapp
        storeButton.setOnClickListener {
            listFiles()
        }

        startCameraButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
                Toast.makeText(this, "Kameran går ej att öppna", Toast.LENGTH_SHORT).show()
            } else {
                startCamera()

            }
        }
        //Lägg till i listanknapp
        saveButton.setOnClickListener {
            it.hideKeyboard()
            storeAction()
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

    fun uploadImage() {
        Log.d(TAG, "uploadImage: ")
        if(choosenImageUrl != null || choosenImageBitmap == null) {
            uploadImageToStorage("uniqueString")
            Log.d(TAG, "uploadImage: url")

        }
        else if(choosenImageBitmap != null || choosenImageUrl != null) {
           uploadImageAsBitmapToStorage()
            Log.d(TAG, "uploadImage: bitmap")


        }
    }







    fun uploadImageAsBitmapToStorage() {
        val bitmap = (imageView_Button.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uniqeString = UUID.randomUUID().toString()
        var uploadTask = imageRef.child("$uid/$uniqeString").putBytes(data)


        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.child("$uid/$uniqeString").downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result


                choosenImageUrl = downloadUri.toString()

                storeAction()


            }

        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name tillåtelse nekad", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(applicationContext, "$name tillåtelse godkänd", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        when (requestCode) {
            CAMERA_REQUEST_CODE -> innerCheck("kamera")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(
            TAG,
            "onActivityResult: 1 req$requestCode, res$resultCode, data$data, ${Activity.RESULT_OK}"
        )
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                curFile = it
                imageView_Button.setImageURI(it)
            }
        } else if (requestCode == START_REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            val takenImage = data.extras?.get("data") as Bitmap


            imageView_Button.setImageBitmap(takenImage)


            choosenImageBitmap = takenImage
           

        } else {
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
                    imageRef.child("$uid/$uniqeString").downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result


                        choosenImageUrl = downloadUri.toString()
                        Log.d(TAG, "uploadImageToStorage: $choosenImageUrl")


                        storeAction()


                        Log.d(TAG, "uploadImageToStorage: ${downloadUri}")


                    }
                    Log.d(TAG, "uploadImageToStorage: Loggen körs")
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

        if (choosenImageUrl != null && editText.text.toString() != "") {
            db.collection("users").document(uid).collection("weekday")
                .document(decision).collection("action").add(storageImage)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "storeAction: $storageImage")
                        Toast.makeText(
                            this@UserCreateAndEditActivity,
                            "Bild och instruktion är tillagd i ditt schema", Toast.LENGTH_SHORT
                        ).show()
                        editText.setText("")
                    }
                }
        } else {
            Toast.makeText(
                this@UserCreateAndEditActivity,
                "Välj bild och skriv en instruktion", Toast.LENGTH_SHORT
            ).show()
            Log.d(TAG, "storeAction: $choosenImageUrl")
            Log.d(TAG, "storeAction: ${editText.text.toString()}")
        }
    }

    private fun listFiles() = CoroutineScope(Dispatchers.IO).launch {
        try {

            val userImages = imageRef.child(uid).listAll().await()
            val publicImages = imageRef.child("UploadedPictures").listAll().await()

            // Laddar bilder från användarens folder på storage
            for (image in userImages.items) {
                val url = image.downloadUrl.await()
                userImageUrl.add(url.toString())

            }   // Laddar publika bilder från storage
            for (publicImage in publicImages.items) {
                val publicUrl = publicImage.downloadUrl.await()
                userImageUrl.add(publicUrl.toString())
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

    //Sätter vald bild från rcViewn till imageView
    fun setImage(url: String) {
        choosenImageUrl = url // <- adressen kommer in
        Glide.with(this).load(url).into(imageView_Button)
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
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
/*
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

*/



