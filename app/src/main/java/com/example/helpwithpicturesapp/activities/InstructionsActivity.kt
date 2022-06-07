package com.example.helpwithpicturesapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.helpwithpicturesapp.R

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