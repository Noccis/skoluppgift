package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class WeekdaysActivity : AppCompatActivity() {

    lateinit var mondayButton: Button
    lateinit var tuesdayButton: Button
    lateinit var wednesdayButton: Button
    lateinit var thursdayButton: Button
    lateinit var fridayButton: Button
    lateinit var saturdayButton: Button
    lateinit var sundayButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekdays)
        mondayButton = findViewById(R.id.måndag_Button)
        mondayButton.setOnClickListener {
            toDoActivityPage()
        }
        tuesdayButton = findViewById(R.id.tisdag_Button)
        tuesdayButton.setOnClickListener {
            toDoActivityPage()
        }
        wednesdayButton = findViewById(R.id.onsdag_Button)
        wednesdayButton.setOnClickListener {
            toDoActivityPage()
        }
        thursdayButton = findViewById(R.id.torsdag_Button)
        thursdayButton.setOnClickListener {
            toDoActivityPage()
        }
        fridayButton = findViewById(R.id.fredag_Button)
        fridayButton.setOnClickListener {
            toDoActivityPage()
        }
        saturdayButton = findViewById(R.id.lördag_Button)
        saturdayButton.setOnClickListener {
            toDoActivityPage()
        }
        sundayButton = findViewById(R.id.söndag_Button)
        sundayButton.setOnClickListener {
            toDoActivityPage()
        }

    }

    fun toDoActivityPage() {
        val intent = Intent(this,ToDoActivity::class.java)
        startActivity(intent)
    }

}