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
    val authid = ""
    val TAG = "!!!"

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
        if ( auth.currentUser?.uid != null ) {
            val intent = Intent ( this, WeekdaysActivity::class.java)
            startActivity(intent)
            finish()
        }

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
                    Log.d(TAG, "loginUser: Success")
                    val user = Firebase.auth.currentUser
                    val email = user?.email.toString()
                    val usercollection = db.collection("users")

                    val query = usercollection.whereEqualTo("email", email).get()
                        .addOnSuccessListener {
                                document ->
                            if (document != null){
                                val userdocument = document.toObjects(Usuari::class.java)
                                val pinkod = userdocument[0].pinkod
                                Log.i("user_pin",pinkod)

                                val intent =  Intent(this , WeekdaysActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                        }
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
        val pinkod = textPinkod.text.toString()

        val user = hashMapOf(
            "email" to email,
            "pinkod" to pinkod
        )
        Log.d(TAG, "onCreate: KÖrs")

        if (email.isEmpty() || password.isEmpty() || pinkod.isEmpty()) {
            Toast.makeText(this, "Användarnamn, lösernord & pinkod måste fyllas i!"
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
                                Log.d(TAG, "DocumentSnapshot added with ID: ${auth.currentUser!!.uid}"
                                )
                                uniqueUserList()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }

                        val intent = Intent(this , WeekdaysActivity::class.java)
                        intent.putExtra(Constants.PINKOD, pinkod)
                        startActivity(intent)
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
            val actionIdsWithSteps = mutableListOf<Actions>()

            for (action in actionsList){
                if (action.steps) {
                    actionId = action.documentName!!
                    Log.d("1111", "0 actionId: $actionId")  /// Här är det tre olika actionId

                    actionIdsWithSteps.add(action)

                    actionsRef.document(actionId!!).collection("steps").get() //
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
                                val x = 'y'
                                // for (action in actionIdsWithSteps) {
                                Log.d("peter","actionidwithsteps size ${actionIdsWithSteps.size}")

                                db.collection("users").document(uid!!).collection("weekday")
                                    .document(day).collection("action").document(action.documentName!!)
                                    .set(action)
                                    .addOnSuccessListener { docRef ->
                                        Log.d("peter", "actionid: ${action.documentName!!} day: $day")
                                        // for (actionId in actionIdsWithSteps)
                                        //  {
                                        Log.d("peter", "steplist: ${stepList.size}")
                                        for (step in stepList) {
                                            Log.d("peter", "step: ${step}")
                                            db.collection("users").document(uid)
                                                .collection("weekday")
                                                .document(day).collection("action")
                                                .document(action.documentName!!)
                                                .collection("steps").add(step)
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d(TAG, "uniqueUserList: Error: $e")
                                    }
                            }
                        }
                } else {
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

}
data class Usuari( var email: String="", var pinkod: String="", var password: String="")

