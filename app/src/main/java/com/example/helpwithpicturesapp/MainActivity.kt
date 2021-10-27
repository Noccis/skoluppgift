package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    lateinit var auth : FirebaseAuth
    lateinit var textEmail : EditText
    lateinit var textPassword : EditText
    lateinit var userSeeInsrtuctionsView: TextView
   lateinit var password : String
    val TAG = "!!!"
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            val intent = Intent(this , WeekdaysActivity::class.java)
            startActivity(intent)
        }

        userSeeInsrtuctionsView = findViewById(R.id.instructions_Tv)


        auth = Firebase.auth

        textEmail = findViewById(R.id.textEmail)
        textPassword = findViewById(R.id.textPassword)

        val createButton = findViewById<Button>(R.id.createButton)
        createButton.setOnClickListener(::creatUser)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            loginUser()
        }


    }

    fun goToAddActivity() {
        val intent = Intent(this , WeekdaysActivity::class.java)
        intent.putExtra(Constants.PASSWORD, password)
        startActivity(intent)
    }

    fun loginUser() {
        val email = textEmail.text.toString()
        password = textPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Användarnamn och lösernord måste fyllas i!"
                , Toast.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener  { task ->
                if ( task.isSuccessful) {
                    Log.d(TAG, "loginUser: Success")
                    goToAddActivity()
                } else {
                    Log.d(TAG, "loginUser: user not loged in ${task.exception}")
                    Toast.makeText(this, "Användarnamn eller lösernord stämmer inte!"
                        , Toast.LENGTH_LONG).show()
                }
            }
    }

    fun creatUser(view : View) {
        val email = textEmail.text.toString()
        val password = textPassword.text.toString()

        val user = hashMapOf(
            "email" to email,
            "password" to password
        )
        Log.d(TAG, "onCreate: KÖrs")
        // Add a new document with a generated ID

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Användarnamn och lösernord måste fyllas i!"
                , Toast.LENGTH_LONG).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "creatUser: Success")
                    if (auth.currentUser != null) {
                        db.collection("users").document(auth.currentUser!!.uid)
                            .set(user)
                            .addOnSuccessListener { documentReference ->

                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: ${auth.currentUser!!.uid}"
                                )  // här är tillagt userID
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                        goToAddActivity()
                    } else {
                        Log.d(TAG, "creatUser: user not created ${task.exception}")
                        Toast.makeText(this, "Email addressen finns redan!", Toast.LENGTH_LONG)
                            .show()

                    }
                }
            }

    }

}

