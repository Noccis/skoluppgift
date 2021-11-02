package com.example.helpwithpicturesapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Log
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class WeekdaysActivity : AppCompatActivity() {

    lateinit var mondayButton: Button
    lateinit var tuesdayButton: Button
    lateinit var wednesdayButton: Button
    lateinit var thursdayButton: Button
    lateinit var fridayButton: Button
    lateinit var saturdayButton: Button
    lateinit var sundayButton: Button
    lateinit var password : String
    var monday = "monday"
    var tuesday = "tuesday"
    var wednesday = "wednesday"
    var thursday = "thursday"
    var friday = "friday"
    var saturday = "saturday"
    var sunday = "sunday"
    var uid = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekdays)


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN




        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null)
         uid = currentUser!!.uid
        Log.d("!!!", "onCreate: weekdays userId $uid")








        password = intent.getStringExtra(Constants.PASSWORD).toString()


        mondayButton = findViewById(R.id.måndag_Button)
        mondayButton.setOnClickListener {



            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, monday)
            intent.putExtra(Constants.PASSWORD, password)
            startActivity(intent)
        }

        tuesdayButton = findViewById(R.id.tisdag_Button)
        tuesdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, tuesday)
            intent.putExtra(Constants.PASSWORD, password)
            startActivity(intent)
        }

        wednesdayButton = findViewById(R.id.onsdag_Button)
        wednesdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, wednesday)
            intent.putExtra(Constants.PASSWORD, password)
            startActivity(intent)
        }
        thursdayButton = findViewById(R.id.torsdag_Button)
        thursdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, thursday)
            intent.putExtra(Constants.PASSWORD, password)
            startActivity(intent)
        }
        fridayButton = findViewById(R.id.fredag_Button)
        fridayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, friday)
            intent.putExtra(Constants.PASSWORD, password)
            startActivity(intent)
        }
        saturdayButton = findViewById(R.id.lördag_Button)
        saturdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, saturday)
            intent.putExtra(Constants.PASSWORD, password)
            startActivity(intent)
        }
        sundayButton = findViewById(R.id.söndag_Button)
        sundayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, sunday)
            intent.putExtra(Constants.PASSWORD, password)
            startActivity(intent)
        }

    }



    }

