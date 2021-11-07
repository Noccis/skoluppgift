package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.Button
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore

class WeekdaysActivity : AppCompatActivity() {

    lateinit var mondayButton: Button
    lateinit var tuesdayButton: Button
    lateinit var wednesdayButton: Button
    lateinit var thursdayButton: Button
    lateinit var fridayButton: Button
    lateinit var saturdayButton: Button
    lateinit var sundayButton: Button
    var monday = "monday"
    var tuesday = "tuesday"
    var wednesday = "wednesday"
    var thursday = "thursday"
    var friday = "friday"
    var saturday = "saturday"
    var sunday = "sunday"
    var uid = ""
    var pinkod1 = ""
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekdays)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        mondayButton = findViewById(R.id.måndag_Button)
        tuesdayButton = findViewById(R.id.tisdag_Button)
        wednesdayButton = findViewById(R.id.onsdag_Button)
        thursdayButton = findViewById(R.id.torsdag_Button)
        fridayButton = findViewById(R.id.fredag_Button)
        saturdayButton = findViewById(R.id.lördag_Button)
        sundayButton = findViewById(R.id.söndag_Button)

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        uid = currentUser!!.uid
        Log.d("!!!", "onCreate: weekdays userId $uid")

            val user = Firebase.auth.currentUser
            val email = user?.email.toString()
            val usercollection = db.collection("users")

            val query = usercollection.whereEqualTo("email", email).get()
                .addOnSuccessListener {
                        document ->
                    if (document != null){
                        val userdocument = document.toObjects(Usuari::class.java)
                        pinkod1 = userdocument[0].pinkod
                        Log.i("user_pin",pinkod1)
                        Log.i("user email ", email)
                    }
                }


        mondayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, monday)
            intent.putExtra(Constants.PINKOD, pinkod1)
            startActivity(intent)
        }

        tuesdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, tuesday)
            intent.putExtra(Constants.PINKOD, pinkod1)
            startActivity(intent)
        }

        wednesdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, wednesday)
            intent.putExtra(Constants.PINKOD, pinkod1)
            startActivity(intent)
        }

        thursdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, thursday)
            intent.putExtra(Constants.PINKOD, pinkod1)
            startActivity(intent)
        }

        fridayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, friday)
            intent.putExtra(Constants.PINKOD, pinkod1)
            startActivity(intent)
        }

        saturdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, saturday)
            intent.putExtra(Constants.PINKOD, pinkod1)
            startActivity(intent)
        }

        sundayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, sunday)
            intent.putExtra(Constants.PINKOD, pinkod1)
            startActivity(intent)
        }
    }

}

