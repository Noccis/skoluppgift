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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    lateinit var auth : FirebaseAuth
    lateinit var textEmail : EditText
    lateinit var textPassword : EditText
    lateinit var userSeeInsrtuctionsView: TextView
    val TAG = "!!!"
    val db = Firebase.firestore

    val week = listOf<String>("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")


    val actionsList = mutableListOf<Actions>()
    val actionsRef = db.collection("Actions")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





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



    fun loginUser() {
        val email = textEmail.text.toString()
        val password = textPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Användarnamn och lösernord måste fyllas i!"
                , Toast.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener  { task ->
                if ( task.isSuccessful) {
                   // Log.d(TAG, "loginUser: $uid Success")
                    val intent = Intent(this , WeekdaysActivity::class.java)
                    intent.putExtra(Constants.PASSWORD, password)
                    startActivity(intent)
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
                                uniqueUserList()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }

                    val intent = Intent(this , WeekdaysActivity::class.java)
                    intent.putExtra(Constants.PASSWORD, password)
                    startActivity(intent)
                         // <-------------------

                } else {
                    Log.d(TAG, "creatUser: user not created ${task.exception}")
                    Toast.makeText(this, "Email addressen finns redan!", Toast.LENGTH_LONG).show()

                }
            }

        }

    }

    fun uniqueUserList(){
        actionsRef.get().addOnSuccessListener { snapshot ->
            if (snapshot != null){
                actionsList.clear()
                for (document in snapshot.documents) {
                    val newDocument = document.toObject(Actions::class.java)
                    if (newDocument != null) {
                        actionsList.add(newDocument)
                    }

                }
            }
            Log.d("!!!", "1 onCreate: ${actionsList.size}")

            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser // Henrik ny härifrån

            var uid : String? = null
            if (currentUser != null) {
                 uid = currentUser.uid
                Log.d("!!!", "onCreate: userId $uid")
            }


            for (action in actionsList){
                for (day in week) {
                    db.collection("users").document(uid!!).collection("weekday")
                        .document(day).collection("action").add(action)
                        .addOnSuccessListener { docRef ->

                            Log.d(TAG, "uniqueUserList: uid: $uid, day: $day Success!!!! ${docRef.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "uniqueUserList: Error: $e")
                        }

                }
            }
        }
    }


}


