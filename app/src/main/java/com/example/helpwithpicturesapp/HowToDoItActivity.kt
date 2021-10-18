package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

const val ACTIONS_POSITION_KEY = "ACTION_KEY"
const val INSTRUCTIONS_POSITION_KEY = "INSTRUCTION_KEY"
const val POSITION_NOT_SET = -1


class HowToDoItActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var textView: TextView
    lateinit var previousButton: Button

    val diffrentInstructions = InstructionList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_do_it)


        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.userSeeInstructions_Tv)
        previousButton = findViewById(R.id.prevoius_Button)

        previousButton.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            startActivity(intent)
        }


        val actionsPosition = intent.getIntExtra(ACTIONS_POSITION_KEY, POSITION_NOT_SET)

        if(actionsPosition != POSITION_NOT_SET) {
            displayInstruction(actionsPosition)
        }



    }

    fun displayInstruction(position: Int) {
        val i = diffrentInstructions.listOfInstructions[position]
        imageView.setImageResource(i.image)
        textView.setText(i.userInstructions)


    }






}