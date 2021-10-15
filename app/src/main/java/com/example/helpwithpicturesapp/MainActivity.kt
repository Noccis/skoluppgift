package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    lateinit var userSeeInsrtuctionsView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        userSeeInsrtuctionsView = findViewById(R.id.instructions_Tv)
        val nextPageButton = findViewById<Button>(R.id.button_NextPage)
        nextPageButton.setOnClickListener {
            weekdayPage()
        }


    }
    fun weekdayPage() {
        val intent = Intent(this, WeekdaysActivity::class.java)
        startActivity(intent)
    }

}

