package com.example.helpwithpicturesapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore

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

    val db = FirebaseFirestore.getInstance()

    val weekdayMonday = db.collection("Weekday").document("monday").collection("monday").toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekdays)
        mondayButton = findViewById(R.id.måndag_Button)
        mondayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, monday)

            intent.putExtra("Monday", weekdayMonday)

            startActivity(intent)
        }

        tuesdayButton = findViewById(R.id.tisdag_Button)
        tuesdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, tuesday)
            startActivity(intent)
        }

        wednesdayButton = findViewById(R.id.onsdag_Button)
        wednesdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, wednesday)
            startActivity(intent)
        }
        thursdayButton = findViewById(R.id.torsdag_Button)
        thursdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, thursday)
            startActivity(intent)
        }
        fridayButton = findViewById(R.id.fredag_Button)
        fridayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, friday)
            startActivity(intent)
        }
        saturdayButton = findViewById(R.id.lördag_Button)
        saturdayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, saturday)
            startActivity(intent)
        }
        sundayButton = findViewById(R.id.söndag_Button)
        sundayButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, sunday)
            startActivity(intent)
        }

    }

    }

