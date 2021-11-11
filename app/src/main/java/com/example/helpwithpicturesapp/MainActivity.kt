package com.example.helpwithpicturesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var signInButton: Button
    lateinit var signUpButton: Button
    lateinit var loginButton : Button
    lateinit var createButton : Button
    lateinit var textEmail : EditText
    lateinit var textPassword : EditText
    lateinit var textPinkod : TextView
    val db = Firebase.firestore
    val week = listOf<String>("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
    val actionsList = mutableListOf<Actions>()
    val actionsRef = db.collection("Actions")
    var actionId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        signInButton = findViewById(R.id.signInButton)
        signUpButton = findViewById(R.id.signUpButton)
        textPinkod = findViewById(R.id.textPinkod)
        textEmail = findViewById(R.id.textEmail)
        textPassword = findViewById(R.id.textPassword)
        createButton = findViewById(R.id.createButton)
        loginButton = findViewById(R.id.loginButton)
        textEmail = findViewById(R.id.textEmail)
        textPassword = findViewById(R.id.textPassword)

        textPinkod.visibility = View.GONE
        createButton.visibility = View.GONE

        auth = Firebase.auth
       /* if ( auth.currentUser?.uid != null ) {
            val intent = Intent ( this, WeekdaysActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        */



        signUpButton.setOnClickListener {
            signup()
        }
        signInButton.setOnClickListener {
            signin()
        }

        createButton.setOnClickListener(::creatUser)

        loginButton.setOnClickListener {
            loginUser()
        }

    }

    fun signin() {
        textPinkod.visibility = View.GONE
        createButton.visibility = View.GONE
        loginButton.visibility = View.VISIBLE
    }

    fun signup() {
        textPinkod.visibility = View.VISIBLE
        createButton.visibility = View.VISIBLE
        loginButton.visibility = View.GONE
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
                    val user = Firebase.auth.currentUser
                    val email = user?.email.toString()
                    val usercollection = db.collection("users")

                    val query = usercollection.whereEqualTo("email", email).get()
                        .addOnSuccessListener {
                                document ->
                            if (document != null){
                                val userdocument = document.toObjects(Usuari::class.java)
                                val pinkod = userdocument[0].pinkod

                                val intent =  Intent(this , WeekdaysActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { exception ->
                        }
                } else {
                    Toast.makeText(this, "Användarnamn eller lösernord stämmer inte!"
                        , Toast.LENGTH_LONG).show()
                }
            }
    }

    fun creatUser(view : View) {
        val email = textEmail.text.toString()
        val password = textPassword.text.toString()
        val pinkod = textPinkod.text.toString()

        val user = hashMapOf(
            "email" to email,
            "pinkod" to pinkod
        )

        if (email.isEmpty() || password.isEmpty() || pinkod.isEmpty()) {
            Toast.makeText(this, "Användarnamn, lösernord & pinkod måste fyllas i!"
                , Toast.LENGTH_LONG).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser != null) {
                        db.collection("users").document(auth.currentUser!!.uid)
                            .set(user)
                            .addOnSuccessListener { documentReference ->

                                uniqueUserList()
                            }
                            .addOnFailureListener { e ->
                            }

                        val intent = Intent(this , WeekdaysActivity::class.java)
                        intent.putExtra(Constants.PINKOD, pinkod)
                        startActivity(intent)
                    } else {
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

            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

            var uid : String? = null
            if (currentUser != null) {
                uid = currentUser.uid
            }
            val actionIdsWithSteps = mutableListOf<Actions>()

            for (action in actionsList){
                if (action.steps) {
                    actionId = action.documentName!!

                    actionIdsWithSteps.add(action)

                    actionsRef.document(actionId!!).collection("steps").get()
                        .addOnSuccessListener { stepSnapshot ->
                            val stepList = mutableListOf<Actions>()
                            stepList.clear()
                            for (step in stepSnapshot.documents) {
                                val newStep = step.toObject(Actions::class.java)
                                if (newStep != null) {
                                    stepList.add(newStep)
                                }
                            }
                            for (day in week) {
                                if (uid != null) {
                                    db.collection("users").document(uid).collection("weekday")
                                        .document(day).collection("action")
                                        .document(action.documentName!!)
                                        .set(action)
                                        .addOnSuccessListener { docRef ->
                                            for (step in stepList) {
                                                db.collection("users").document(uid)
                                                    .collection("weekday").document(day)
                                                    .collection("action")
                                                    .document(action.documentName!!)
                                                    .collection("steps").add(step)
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                        }
                                }
                            }
                        }
                     } else {
                        for (day in week) {
                            if (uid != null) {
                                db.collection("users").document(uid).collection("weekday")
                                .document(day).collection("action").add(action)
                                .addOnSuccessListener { docRef ->
                                    }
                                .addOnFailureListener { e ->
                                }
                        }
                    }
                }
            }
        }
    }
}
data class Usuari( var email: String="", var pinkod: String="", var password: String="")

