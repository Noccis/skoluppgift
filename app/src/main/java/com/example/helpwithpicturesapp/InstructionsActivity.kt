package com.example.helpwithpicturesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InstructionsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)

        val returnButton: Button = findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {

            finish()
        }


    }
}